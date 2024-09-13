import './Record.scss';
import GeneralButton from '@/components/Button/GeneralButton';
import PredictionGraph from '@/components/Record/PredictionGraph';
import InputField from '@/components/Input/Input';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ExerciseModal from '@/components/Exercise/ExerciseModal';
import DropDown from '@/assets/svg/dropDown.svg'

export default function RecordPage() {
  const navigate = useNavigate();
  const [exerciseType, setExerciseType] = useState('');
  const [duration, setDuration] = useState('');
  const [showExerciseModal, setShowExerciseModal] = useState(false);
  const [day, setDay] = useState('');
  const [showDayDropdown, setShowDayDropdown] = useState(false);

  // 그래프에 사용할 데이터 정의
  const data = [
    { name: '현재', weight: 62.0 },
    { name: '1달 후', weight: 61.3 },
    { name: '3달 후', weight: 58.6 },
  ];

  // ExerciseModal에서 선택한 운동 종목을 처리하는 함수
  const handleExerciseSelect = (selected: string | string[]) => {
    // selected가 string 배열일 경우 첫 번째 항목을 사용
    const exercise = Array.isArray(selected) ? selected[0] : selected;
    setExerciseType(exercise);
    setShowExerciseModal(false);
  };

  // 모달을 닫는 함수
  const handleCloseModal = () => {
    setShowExerciseModal(false);
  };

  return (
    <div className="recordsContainer">
      <GeneralButton
        buttonStyle={{ style: 'primary', size: 'large' }}
        onClick={() => navigate('/record/bodyDetail')}
      >
        상세 체형 기록 조회
      </GeneralButton>

      <div className="currentPrediction">
        <p className="predictionText">
          <strong>민영님</strong>의 이번주 운동을 유지했을 때, 체형 예측 결과예요
        </p>

        <div className="predictionImages">
          <div className="predictionImage">
            <p>현재</p>
          </div>
          <div className="predictionImage">
            <p>1달 후</p>
          </div>
          <div className="predictionImage">
            <p>3달 후</p>
          </div>
        </div>

        <div className="predictionGraph">
          <PredictionGraph data={data} />
        </div>
      </div>

      <div className="exercisePrediction">
        <p className="predictionText">
          <strong>민영님</strong>의 운동 강도를 높였을 때, 체형 예측 결과를 조회해보세요
        </p>
        <div className="exerciseInput">
          <div className="inputBox">
            <button
              className="inputField semiMedium"
              onClick={() => setShowExerciseModal(true)}
            >
              {exerciseType || '운동 종목'}
            </button>
            {showExerciseModal && (
              <ExerciseModal 
                onSelectExercise={handleExerciseSelect} 
                multiple={false} 
                onClose={handleCloseModal}
              />
            )}

            <button
              className="inputField semiSmall"
              onClick={() => setShowDayDropdown(!showDayDropdown)}
            >
              {day || ''}
              <img src={DropDown} alt="dropDown" className="dropDown"/>
            </button>
            {showDayDropdown && (
              <div className="dayDropdown">
                <div onClick={() => setDay('1')}>1</div>
                <div onClick={() => setDay('2')}>2</div>
              </div>
            )}
            일

            <InputField
              placeholder=""
              value={duration}
              onChange={(e) => setDuration(e.target.value)}
              size="semiTiny"
            />
            분
        </div>
        <GeneralButton buttonStyle={{ style: 'primary', size: 'medium' }}>
          체형 예측
        </GeneralButton>          
        </div>
      </div>
    </div>
  );
}
