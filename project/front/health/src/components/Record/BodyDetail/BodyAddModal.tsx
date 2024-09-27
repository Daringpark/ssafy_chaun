import { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import GeneralButton from '@/components/Button/GeneralButton';
import xCircle from '@/assets/svg/xCircle.svg';
import BodyType from '@/components/Survey/BodyType';
import EatingHabits from '@/components/Survey/EatingHabits';
import './BodyAddModal.scss';

interface BodyAddModalProps {
  onClose: () => void;
}

type FormData = {
  mealsPerDay: string;
  foodType: string;
  snacksPerDay: string;
  drinksPerDay: string;
};

export default function BodyAddModal({ onClose }: BodyAddModalProps) {
  const { register, watch } = useForm<FormData>();
  const [isButtonDisabled, setIsButtonDisabled] = useState(true);

  const [bodyData, setBodyData] = useState({
    height: 0,
    weight: 0,
    skeletalMuscleMass: null as number | null,
    bodyFat: null as number | null,
    bodyMuscle: false,
    bodyShape: 0,
  });

  // 체형 입력 검증 (필수 항목만 확인)
  const isRequiredBodyDataComplete = () => {
    const requiredBodyData = {
      height: bodyData.height,
      weight: bodyData.weight,
      bodyMuscle: bodyData.bodyMuscle,
      bodyShape: bodyData.bodyShape,
    };

    // 필수 데이터가 모두 입력되었는지 확인 (0이 아닌 경우 또는 값이 true여야 함)
    return Object.values(requiredBodyData).every((value) => {
      return value !== 0 && value !== false;
    });
  };

  // 식습관 입력 검증
  const isEatingHabitsComplete = () => {
    const mealsPerDay = watch('mealsPerDay');
    const foodType = watch('foodType');
    const snacksPerDay = watch('snacksPerDay');
    const drinksPerDay = watch('drinksPerDay');

    return mealsPerDay && foodType && snacksPerDay && drinksPerDay;
  };

  const checkDataCompletion = () => {
    const isBodyDataComplete = isRequiredBodyDataComplete();
    const isEatingComplete = isEatingHabitsComplete();
    setIsButtonDisabled(!(isBodyDataComplete && isEatingComplete));
  };

  useEffect(() => {
    checkDataCompletion();
  }, [bodyData, watch('mealsPerDay'), watch('foodType'), watch('snacksPerDay'), watch('drinksPerDay')]);

  const handleComplete = () => {
    if (!isButtonDisabled) {
      // POST 요청
      onClose();
    }
  };

  return (
    <div className="bodyAddModal">
      <hr className="divider" />
      <img src={xCircle} alt="Close" className="closeIcon" onClick={onClose} />
      <div>
        <h1 className="title">체형 입력</h1>
        <p className="description">보다 정확한 체형 분석 및 예측을 위해 체형과 식습관 정보를 입력해주세요.</p>
      </div>
      <div className="scrollableContent">
        <BodyType onBodyDataChange={setBodyData} />
        <EatingHabits register={register} />
      </div>
      <GeneralButton
        buttonStyle={{ style: 'floating', size: 'semiTiny' }}
        onClick={handleComplete}
        className="completedButton"
        disabled={isButtonDisabled}>
        완료
      </GeneralButton>
    </div>
  );
}
