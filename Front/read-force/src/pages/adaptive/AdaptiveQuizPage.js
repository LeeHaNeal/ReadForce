import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import fetchWithAuth from '../../utils/fetchWithAuth';
import './AdaptiveQuizPage.css';

const AdaptiveQuizPage = () => {
  const navigate = useNavigate();
  const [quiz, setQuiz] = useState(null);
  const [selected, setSelected] = useState(null);
  const [notFound, setNotFound] = useState(false);
  const [startTime, setStartTime] = useState(Date.now());

  useEffect(() => {
    const fetchQuiz = async () => {
      try {
        const res = await fetchWithAuth('/recommend/get-recommend?language=KOREAN');

        if (!res.ok) {
          console.error('백엔드 에러 상태 코드:', res.status);
          setNotFound(true);
          return;
        }

        const data = await res.json();

        if (!data || !data.question || !data.choiceList) {
          console.warn('데이터 불완전:', data);
          setNotFound(true);
          return;
        }

        setQuiz(data);
        setStartTime(Date.now());
      } catch (err) {
        console.error('API 통신 오류:', err);
        setNotFound(true);
      }
    };

    fetchQuiz();}, []);

  if (notFound) {
    return (
      <div className="page-container quiz-notfound-container">
        <div className="warning">❗ 제공된 문제가 없습니다.</div>
        <div className="description">다른 문제를 시도해 주세요.</div>
        <button className="go-back-button" onClick={() => navigate(-1)}>🔙 돌아가기</button>
      </div>
    );
  }

  if (!quiz) return <div className="page-container">로딩 중...</div>;

  const options = quiz.choiceList.map(choice => choice.content);
  const selectedChoice = quiz.choiceList.find(choice => choice.content === selected);

  const handleSubmit = async () => {
    if (!selectedChoice) return;

    const solvingTime = Math.max(10, Math.floor((Date.now() - startTime) / 1000)); 
    const payload = {
      questionNo: quiz.questionNo,
      selectedIndex: selectedChoice.choiceNo, 
      questionSolvingTime: solvingTime,
      isFavorit: false
    };

    try {
      const res = await fetchWithAuth('/learning/save-multiple-choice', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!res.ok) {
        throw new Error('정답 기록 실패');
      }
    } catch (err) {
      console.error('🚨 제출 실패:', err);
    }

    const correct = selectedChoice.isCorrect;

    navigate('/adaptive-learning/result', {
      state: {
        isCorrect: correct,
        explanation: quiz.explanation || "해설 없음",
        next: '/adaptive-learning/start'
      }
    });
  };

  return (
    <div className="quiz-layout">
      <div className="quiz-passage">
        <h3 className="passage-title">🤖 적응력 문제</h3>
        <p className="passage-text">{quiz.content || '※ 추가 지문 없음'}</p>
      </div>

      <div className="quiz-box">
        <h4 className="question-heading">💡 문제</h4>
        <p className="question-text">{quiz.question}</p>

        <div className="quiz-options">
          {options.map((opt, idx) => (
            <button
              key={idx}
              className={`quiz-option ${selected === opt ? 'selected' : ''}`}
              onClick={() => setSelected(opt)}
            >
              {String.fromCharCode(65 + idx)}. {opt}
            </button>))}
        </div>

        <div className="quiz-button-container">
          <button
            className="submit-button"
            disabled={!selected}
            onClick={handleSubmit}
          >
            정답 제출
          </button>
        </div>
      </div>
    </div>
  );
};

export default AdaptiveQuizPage;
