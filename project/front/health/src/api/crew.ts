// import axios from 'axios';
import exportAxios from './axiosInstance';
const baseURL = import.meta.env.VITE_APP_BASE_URL;

//추천 크루 리스트 및 유저 점수
export const getCrewRecommendList = async () => {
  const response = await exportAxios.get(`${baseURL}/users/recommend-crew`);
  console.log('크루 추천 리스트', response);
  return response.data;
};

// 크루 추천 상세 모달
export const getCrewRecommendModal = async (crew_id: number) => {
  const response = await exportAxios.get(`${baseURL}/users/crew-recommendation/${crew_id}`);
  console.log('크루 추천 상세 모달', response);
  return response.data;
};

// 크루 상세보기
export const getCrewDetail = async (crew_id: number) => {
  const response = await exportAxios.get(`${baseURL}/crew/${crew_id}/detail`);
  console.log('크루 상세', response);
  return response.data;
};

// 크루 내 랭킹 조회
export const getCrewRanking = async (crew_id: number) => {
  const response = await exportAxios.get(`${baseURL}/crew/${crew_id}/ranking`);
  return response.data;
};

// 운동별 크루 랭킹 조회
export const getExerciseCrewRanking = async (exercise_id: number) => {
  const response = await exportAxios.get(`${baseURL}/crew/ranking/${exercise_id}`);
  return response.data;
};

// 크루 가입 신청
export const joinToCrew = async (crew_id: number) => {
  const response = await exportAxios.post(`${baseURL}/crew/${crew_id}/join`);
  return response.data;
};
// 크루 배틀 현황 조회
export interface CrewBattleStatusResponse {
  crewId: number;
  battleId: number;
  myCrewName: string;
  myCrewScore: number;
  opponentCrewName: string;
  opponentCrewScore: number;
  exerciseName: string;
  dday: number;
  battleStatus: string;
  buttonState: string;
}
//크루 배틀 현황
export const fetchCrewBattleStatus = async (crew_id: number) => {
  const response = await exportAxios.get(`${baseURL}/crew/${crew_id}/battle`);
  return response.data;
};

// 가입된 크루 조회
export const getUserCrewList = async (userId: number) => {
  // console.log('userId', userId);
  const response = await exportAxios.get(`${baseURL}/users/${userId}/crew-list`);
  console.log('가입된 크루 조회', response);
  return response.data;
};

// 크루 랜덤 매칭 동의여부
export const agreeRandomMatching = async (crew_id: number) => {
  const response = await exportAxios.post(`${baseURL}/crew/${crew_id}/battle/ready`);
  return response.data;
};

export interface sendCoin {
  crew_id: number;
  coin_count: number;
}
export interface ResponseData {
  message: string;
}
// 크루 코인 모금하기
export const collectCrewCoin = async ({ crew_id, coin_count }: sendCoin): Promise<ResponseData> => {
  const response = await exportAxios.post(`${baseURL}/crew/${crew_id}/coin/${coin_count}`);
  return response.data;
};
//크루 베틀 현황
export const crewBattleStatus = async (crew_id: number) => {
  const response = await exportAxios.get(`${baseURL}/crew/${crew_id}/battle`);
  return response.data;
};

// 배틀 랜덤 매칭
export const randomMatching = async (crew_id: number) => {
  const response = await exportAxios.post(`${baseURL}/crew/${crew_id}/battle`);
  return response.data;
};

// 실시간 크루 배틀 기여도 랭킹
export const getBattleRanking = async (battle_id: number, crew_id: number) => {
  const response = await exportAxios.get(`${baseURL}/battle/${battle_id}/${crew_id}`);
  return response.data;
};
// 크루명 중복 체크
export const checkCrewName = async (crewName: string) => {
  const response = await exportAxios.get(`${baseURL}/crew/${crewName}/validate`);
  return response.data;
};
// 크루 생성
export const createCrew = async (formData: FormData) => {
  const response = await exportAxios.post(`${baseURL}/crew`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return response.data;
};

// 크루원들의 오늘 운동 시간과 캐릭터 조회
export const crewMemberDailyExerciseTime = async (crew_id: number) => {
  console.log('크루 요청 운동 시간 크루 아이디', crew_id);
  const response = await exportAxios.get(`${baseURL}/crew/${crew_id}/exercise-time/daily`);
  return response.data;
};
