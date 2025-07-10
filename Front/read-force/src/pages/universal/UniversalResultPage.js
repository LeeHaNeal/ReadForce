import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import './css/UniversalResultPage.css';
import clockImg from '../../assets/image/clock.png';
import correctAnim from '../../assets/correct.json';
import incorrectAnim from '../../assets/incorrect.json';
import Lottie from 'lottie-react';

const UniversalResultPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [showExplanation, setShowExplanation] = useState(false);


  const isCorrect = location.state?.isCorrect;
  const explanation = location.state?.explanation || '해설이 제공되지 않았습니다.';
  const language = location.state?.language || '한국어';
  const resultMessage = isCorrect ? '정답입니다!' : '오답입니다.';
  const resultSubText = isCorrect
    ? '대단해요! 문맥을 잘 파악하셨네요.'
    : '조금만 더 집중해볼까요? 누구나 틀릴 수 있어요!';

const category = location.state?.category || 'NEWS';
const elapsedTime = location.state?.elapsedTime;

const formatTime = (totalSeconds) => {
  const minutes = String(Math.floor(totalSeconds / 60)).padStart(2,'0');
  const seconds = String(totalSeconds % 60).padStart(2, '0');
  return `${minutes}:${seconds}`;
};

const getBackPath = () => {
  switch (category.trim()) {
    case 'NEWS':
      return '/article';
    case 'NOVEL':
      return '/novel';
    case 'FAIRY_TALE':
      return '/fairytale';
    default:
      return '/article';
  }
};

  return (
    <div className="ArticleResult-wrapper">
      <div className="ArticleResult-card">
          <Lottie
            animationData={isCorrect ? correctAnim : incorrectAnim}
            loop={false}
            style={{ width: 90, height: 90, margin: '0 auto' }}
          />
        <h2> {resultMessage}</h2>
        <p className="ArticleResult-subtext">{resultSubText}</p>

        {elapsedTime !== undefined && (
          <p className="ArticleResult-time"><img src={clockImg} alt="clock" className="clock-icon" />총 소요시간: {formatTime(elapsedTime)}</p>
        )}

        <div className="ArticleResult-buttons">
          <button onClick={() => setShowExplanation(!showExplanation)}>해설보기</button>
          <button onClick={() => navigate(-1)}>다시 도전하기</button>
          <button onClick={() => navigate(getBackPath())}>그만하기</button>
        </div>

        {showExplanation && (
          <div className="ArticleResult-explanation">
            <h3>📝 해설</h3>
            <p>{explanation}</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default UniversalResultPage;
