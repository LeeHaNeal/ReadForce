import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import './css/UniversalResultPage.css';

const ArticleResultPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [showExplanation, setShowExplanation] = useState(false);

  const isCorrect = location.state?.isCorrect;
  const explanation = location.state?.explanation || '해설이 제공되지 않았습니다.';
  const language = location.state?.language || '한국어';
  const resultMessage = isCorrect ? '정답입니다!' : '오답입니다.';
  const resultEmoji = isCorrect ? '🎉' : '❌';
  const resultSubText = isCorrect
    ? '👏 대단해요! 문맥을 잘 파악하셨네요.'
    : '😢 조금만 더 집중해볼까요? 누구나 틀릴 수 있어요!';

  const getBackPath = () => {
    switch (language.trim()) {
      case '한국어':
        return '/korea';
      case '일본어':
        return '/japan';
      case '영어':
        return '/usa';
      default:
        return '/korea';
    }
  };

  return (
    <div className="ArticleResult-wrapper">
      <div className="ArticleResult-card">
        <h2>{resultEmoji} {resultMessage}</h2>
        <p className="ArticleResult-subtext">{resultSubText}</p>

        <div className="ArticleResult-buttons">
          <button onClick={() => setShowExplanation(!showExplanation)}>해설보기</button>
          <button onClick={() => navigate(-1)}>다시 도전하기</button>
          <button onClick={() => navigate(getBackPath())}>닫기</button>
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

export default ArticleResultPage;
