# 웹처리
import requests
from fastapi import FastAPI, HTTPException
from contextlib import asynccontextmanager

# Uvicorn 라이브러리
import uvicorn
from typing import List, Optional
from pydantic import BaseModel
from bson import ObjectId

# DB timezone UTC 저장
from datetime import datetime

# MongoDB 관련 라이브러리
import pymongo
from pymongo import MongoClient

# 데이터 처리 및 예측, 추천 라이브러리
import numpy as np
from copy import deepcopy as dp
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import LSTM, Dense, Input, LayerNormalization
from tensorflow.keras.optimizers import Adam
from sklearn.preprocessing import MinMaxScaler
import joblib

### AI 회귀 모델 처리 ###
# 모델 구조 정의 - 기존과 똑같은 구조를 불러오기
def build_model(input_shape, forecast_steps):
    model = Sequential()
    model.add(Input(shape=input_shape))
    model.add(LSTM(units=32, dropout=0.5, return_sequences=True))
    model.add(LSTM(units=32, dropout=0.5))
    model.add(Dense(32, activation='tanh'))
    model.add(LayerNormalization())
    model.add(Dense(units=forecast_steps, activation='sigmoid'))  # 90일 예측
    
    return model

version = 'v12'

# 모델에 따른 가중치 불러오기
def load_model_weights(model, weights_path):
    model.load_weights(weights_path)
    return model

# 모델 로드 함수
@asynccontextmanager
async def load_model_startup(app: FastAPI):
    global model, encoder, scaler_bmi, scaler_weight, scaler_calories 

    timesteps = 7
    features = 6 # [sex_1, sex_2, age, BMI, weight, comsumed_cal] = 5 features
    forecast_steps = 90 # 최대 90일까지의 예측을 진행
    input_shape = (timesteps, features)

    model = build_model(input_shape, forecast_steps)
    model = load_model_weights(model, f"./DOCK/models/model{version}.weights.h5")

    # Load the saved MinMaxScaler and OneHotEncoder
    scaler_bmi = joblib.load('./DOCK/models/minmax_scaler_bmi.pkl')
    scaler_weight = joblib.load('./DOCK/models/minmax_scaler_weight.pkl')
    scaler_calories = joblib.load('./DOCK/models/minmax_scaler_calories.pkl')
    encoder = joblib.load('./DOCK/models/onehot_encoder_v2.pkl')

    yield

    print("Application shutdown.")

# 예측 수행
def make_predictions(model, X_test):
    try:
        predictions = model.predict(X_test)
    except Exception as e:
        raise HTTPException(status_code=500, detail = f'Model Prediciton : {e}')
    
    return predictions


# 모델 수행 이후 처리 함수
def model_predict(data_test):
    global scaler_weight

    predictions = make_predictions(model, data_test)  # 7일 입력 X -> 그 다음 1일 부터 ~ 90일 앞까지 값을 Y
    # 체중 값을 역변환 (age, BMI, calories는 0으로 두고, weight 값만 역변환)
    inverse_weight_predictions = scaler_weight.inverse_transform(
        np.hstack([np.zeros((predictions.shape[1], 2)),  # 나이, BMI 0
                   predictions.reshape(-1, 1),           # weight 예측값 (역변환 대상)
                   np.zeros((predictions.shape[1], 1))])  # 칼로리 0
    )[:, 2]  # weight만 역변환

    return round(inverse_weight_predictions[29], 2), round(inverse_weight_predictions[89], 2)

# APP 정의
app = FastAPI(lifespan=load_model_startup)

# MongoClient 생성
try:
    client = MongoClient("mongodb://localhost:27017", serverSelectionTimeoutMS = 5000) # EC2 주소 + 포트로 바꾸기
    # 서버 상태 확인
    server_status = client.admin.command("ping")
    db = client['Health']
    predict_basic = db['predict_basic']
    predict_extra = db['predict_extra']
    crew_recommend = db['crew_recommend']
    print("MongoDB 서버에 성공적으로 연결되었습니다:", server_status)
except pymongo.errors.ServerSelectionTimeoutError as e:
    print("MongoDB에 연결할 수 없습니다:", e)

### 모델 정의 부분 ###
class ExerciseData(BaseModel):
    sex: int
    age: int
    bmi: float
    weight: float
    calories: float

class ExerciseDetail(BaseModel):
    exercise_id: int
    duration: int
    count: int

class UserExerciseRequest(BaseModel):
    exercise_detail: Optional[ExerciseDetail] = None # 객체 형태
    exercise_data: List[ExerciseData]  # 7일간의 운동 정보 리스트
    extra_exercise_data: Optional[List[ExerciseData]] = None

    def average_calories(self) -> float:
        if not self.exercise_data:
            return 0.0
        total_calories = sum(data.calories for data in self.exercise_data)
        return total_calories / len(self.exercise_data)

# object id convergence
def convert_objectid(data):
    if isinstance(data, dict):
        for key, value in data.items():
            if isinstance(value, ObjectId):
                data[key] = str(value)
            elif isinstance(value, list):
                data[key] = [convert_objectid(item) for item in value]
            elif isinstance(value, dict):
                data[key] = convert_objectid(value)
    elif isinstance(data, list):    
        data = [convert_objectid(item) for item in data]
    return data

# 데이터 전처리 함수
def preprocess_data(exercise_data):
    global encoder, scaler_bmi, scaler_weight, scaler_calories

    # 입력으로 받은 데이터를 어레이로 저장
    exercise_data = np.array([[data.sex, data.age, data.bmi, data.weight, data.calories] for data in exercise_data])
    # 역 연산처리
    # 성별 피처 - 인코더 적용
    sex_encoded = encoder.transform(exercise_data[:, [0]])
    # 수치형 데이터 - 스케일러 적용
    bmi_scaled = scaler_bmi.transform(exercise_data[:, [2]])  # remaining columns: 나이, BMI, 몸무게, 칼로리
    weight_scaled = scaler_bmi.transform(exercise_data[:, [3]])  # remaining columns: 나이, BMI, 몸무게, 칼로리
    calories_scaled = scaler_bmi.transform(exercise_data[:, [4]])  # remaining columns: 나이, BMI, 몸무게, 칼로리
    # 성별 + 수치형 데이터
    processed_data = np.hstack([sex_encoded, exercise_data[:, [1]], bmi_scaled, weight_scaled, calories_scaled])

    return processed_data

# 보정 함수
def make_confirmed_weight(days_30, days_90, data, p30, p90):
    last_weight = data[-1].weight
    cal_average = UserExerciseRequest(exercise_data=data).average_calories()

    calculate_flag = 0
    # 기본적인 보정 가중치 정의
    if days_30 > 10 or days_90 > 15:  # 오차율이 크다면 큰 보정
        if days_30 > 10:
            print('30일 예측 - 10kg 이상 차이남, 오차 큼')
        if days_90 > 15:
            print('90일 예측 - 15kg 이상 차이남, 오차 큼')
        weight_adjustment_factor_30 = 0.1
        weight_adjustment_factor_90 = 0.15
        calculate_flag = 1
    elif days_30 > 5 or days_90 > 10:  # 중간 정도의 오차율 보정
        if days_30 > 5:
            print('30일 예측 - 5kg 이상 차이남, 오차 보통')
        if days_90 > 10:
            print('90일 예측 - 10kg 이상 차이남, 오차 보통')
        weight_adjustment_factor_30 = 0.35
        weight_adjustment_factor_90 = 0.4
        calculate_flag = 1
    else:  # 오차가 작을 경우 보정 없이
        print('30일 예측, 90일 예측, 오차 작음')
        weight_adjustment_factor_30 = 0.8
        weight_adjustment_factor_90 = 0.8

    # 칼로리 소모량에 따라 추가 가중치 적용
    if calculate_flag:
        if cal_average >= 500:
            print("운동량이 많음 - 체중 감소 가중치 적용")
            pred_30_adjustment = np.random.uniform(-1.5, -0.5)  # 체중 감소 가중치
            pred_90_adjustment = np.random.uniform(-0.5, 0)
        elif cal_average >= 350:
            print("운동량이 보통")
            pred_30_adjustment = np.random.uniform(-0.5, 0.5)  # 체중 증가 가중치
            pred_90_adjustment = np.random.uniform(-1, -0.5)
        else:
            print("운동량이 적음 - 체중 증가 가중치 적용")
            pred_30_adjustment = np.random.uniform(0, 1)  # 체중 증가 가중치
            pred_90_adjustment = np.random.uniform(1, 2.5)
    else:
        pred_30_adjustment = 0
        pred_90_adjustment = 0

    # 예측 값 보정
    pred_30_corrected = last_weight * (1-weight_adjustment_factor_30) + (p30 * weight_adjustment_factor_30) + pred_30_adjustment
    pred_90_corrected = pred_30_corrected * (1-weight_adjustment_factor_90) + (p90 * weight_adjustment_factor_90) + pred_90_adjustment

    # 현재 체중을 고려하여 최종 보정된 예측 값을 계산
    pred_30_final = round((last_weight + pred_30_corrected) / 2, 2)
    pred_90_final = round((pred_30_final + pred_90_corrected) / 2, 2)

    return pred_30_final, pred_90_final

# 루트 라우터
@app.get("/")
def root():
    return {"message": "MongoDB와 FastAPI 연결 성공"}

### 운동 예측 기능 ###
# API :: 종합 체중 예측 => spring에서 스케쥴러를 통한 예측 후 MongoDB 저장
@app.post("/api/v1/users/{user_id}/body/prediction/fast-api")
async def predict(user_id: int, request: UserExerciseRequest):
    try:
        # 1. request를 통해 exercise_data를 받는다.
        exercise_data = request.exercise_data # exercise_data

        # 2. exercise_data를 길이를 맞춰 전처리 코드
        dummy_count = 7 - len(exercise_data)
        height_sqr = exercise_data[-1].weight / exercise_data[-1].bmi

        for exercise_obj in exercise_data:
            exercise_obj.calories += np.random.normal(250,15)
        for _ in range(dummy_count):
            last_data = dp(exercise_data[-1])
            last_data.calories = np.random.normal(250, 15) # 평균 걸음으로도 250에서 300 칼로리를 소모한다.
            last_data.weight = last_data.weight + round(np.random.uniform(-0.1, 0.2), 2) # 하지만, 식습관으로 인해서 체중이 찌거나 유지되는 중..
            last_data.bmi = last_data.weight / height_sqr
            exercise_data.append(last_data)

        # 3. 전처리 데이터 np 배열 변환
        X_test = preprocess_data(exercise_data) # (7, 5)
        X_test = X_test.reshape(1, 7, -1)  # 한 차원 늘려서, 하나의 입력으로, 7일간의 운동 정보(5개의 feature)를 timesteps=7, features=5

        # 4. model.predict 예측한 결과를 만들어서 DB에 저장하고, user_id랑 예측 값 보내주기
        pred_30_d, pred_90_d = model_predict(X_test)

        # 4-1. weight와 p30, p90과 차이가 많이 날 때, 예측 값 보정
        last_weight = exercise_data[-1].weight
        p30_diff = abs(last_weight - pred_30_d)
        p90_diff = abs(last_weight - pred_90_d)
        print(last_weight, pred_30_d, pred_90_d, p30_diff, p90_diff)
        pred_30_d, pred_90_d = make_confirmed_weight(p30_diff, p90_diff, exercise_data, pred_30_d, pred_90_d)


        # 5. 예측 DB 변수 정의
        new_prediction = {
            "user_id": user_id,
            "current": round(exercise_data[-1].weight, 2),
            "p30": pred_30_d,
            "p90": pred_90_d,
            "created_at": datetime.utcnow()
        }

        # 6. 종합 예측 Predict_basic document에 MongoDB 저장
        predict_basic.insert_one(new_prediction)

        # 7. 재확인 코드
        new_prediction = convert_objectid(new_prediction)  # ObjectId 변환
        return new_prediction
    except Exception as e:
        raise HTTPException(status_code=500, detail=f'Error : {e}')

# API :: 추가 운동 예측 -> 요청시 
@app.post("/api/v1/users/{user_id}/body/prediction/extra/fast-api")
async def extra_predict(user_id: int, request: UserExerciseRequest):
    try:
        # 1. exercise_data들 받기
        exercise_data = request.exercise_data # List Exercise_data
        extra_exercise_data = request.extra_exercise_data # List Extra_Exercise_data
        exercise_data = exercise_data + extra_exercise_data

        # 2. exercise_data를 길이를 맞춰 전처리 코드
        dummy_count = 7 - len(exercise_data)
        height_sqr = exercise_data[-1].weight / exercise_data[-1].bmi
        for _ in range(dummy_count):
            last_data = dp(exercise_data[-1])
            last_data.calories = np.random.normal(250, 15) # 평균 걸음으로도 250에서 300 칼로리를 소모한다.
            last_data.weight = last_data.weight + round(np.random.uniform(-0.1, 0.2), 2) # 하지만, 식습관으로 인해서 체중이 찌거나 유지되는 중..
            last_data.bmi = last_data.weight / height_sqr
            exercise_data.append(last_data)

        # 3. 전처리 데이터 np 배열 변환
        X_test = preprocess_data(exercise_data) # (7, 5)
        X_test = X_test.reshape(1, 7, -1)  # 한 차원 늘려서, 1개의 데이터에 7일간의 운동 정보(5개의 feature)를 timesteps=7, features=5

        # 4. model.predict 예측한 결과를 만들어서 DB에 저장하고, user_id랑 예측 값 보내주기
        pred_30_d, pred_90_d = model_predict(X_test)

        # 4-1. weight와 p30, p90과 차이가 많이 날 때, 예측 값 보정
        last_weight = exercise_data[-1].weight
        ### 정량적 오차
        # p30_diff = abs(last_weight - pred_30_d)
        # p90_diff = abs(last_weight - pred_90_d)
        # if p30_diff >= 4 or p90_diff >= 6:
        #     print(1)
        #     print(p30_diff, p90_diff)
        #     cal_average = UserExerciseRequest(exercise_data=exercise_data).average_calories()
        #     if cal_average >= 500:
        #         if pred_30_d - last_weight > 0: # 예측이 더 클 경우
        #             cal_weight = last_weight + np.random.uniform(-2, -1)
        #         else:
        #             cal_weight = last_weight + np.random.uniform(0, 1)
        #     else: # 운동량이 많지 않으면, 몸무게가 찌는게 더 당연하다.
        #         if pred_30_d - last_weight > 0: # 예측이 더 클 경우
        #             cal_weight = last_weight + np.random.uniform(1, 2)
        #         else:
        #             cal_weight = last_weight + np.random.uniform(-1.5, 0)
        #     pred_30_d = round((last_weight + cal_weight + pred_30_d) / 3, 2)
        #     pred_90_d = round((((cal_weight + pred_30_d + pred_90_d) / 3) + np.random.uniform(-2, -1)), 2)
        p30_diff = abs(last_weight - pred_30_d)
        p90_diff = abs(last_weight - pred_90_d)
        pred_30_d, pred_90_d = make_confirmed_weight(p30_diff, p90_diff, exercise_data, pred_30_d, pred_90_d)


        # 5. 예측 DB 변수 정의
        new_prediction = {
            "user_id": user_id,
            "current": round(exercise_data[-1].weight, 2),
            "p30": pred_30_d,
            "p90": pred_90_d,
            "exercise" : {
                "exercise_id": request.exercise_detail.exercise_id,
                "count": request.exercise_detail.count,
                "duration": request.exercise_detail.duration
            },
            "created_at": datetime.utcnow()
        }

        # 6. 종합 예측 MongoDB 저장
        predict_extra.insert_one(new_prediction)

        # 7. 재확인 코드
        new_prediction = convert_objectid(new_prediction)  # ObjectId 변환
        return new_prediction
    except Exception as e:
        raise HTTPException(status_code=500, detail=f'Error : {e}, "extra_data" : "is_not_found"')

# CLI 실행을 main 함수에서 실행
if __name__ == "__main__":
    uvicorn.run("predict:app", host="0.0.0.0", port=8000, reload=True)