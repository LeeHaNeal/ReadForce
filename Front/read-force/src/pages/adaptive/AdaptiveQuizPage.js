import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import fetchWithAuth from '../../utils/fetchWithAuth';
import './AdaptiveQuizPage.css';

const AdaptiveQuizPage = () => {
  const navigate = useNavigate();
  const [quiz, setQuiz] = useState(null);
  const [selected, setSelected] = useState(null);
  const [notFound, setNotFound] = useState(false);

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

        if (!data || !data.question) {
          console.warn('백엔드에 문제 없음:', data);
          setNotFound(true);
          return;
        }

        setQuiz(data);
      } catch (err) {
        console.error('API 통신 오류:', err);
        setNotFound(true);
      }
    };

    fetchQuiz();
  }, []);

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

  // choiceList를 content 배열로 변환
  const options = quiz.choiceList ? quiz.choiceList.map(choice => choice.content) : [];

  const handleSubmit = () => {
    if (!selected) return;

    // 정답 선택지 찾기
    const correctChoice = quiz.choiceList.find(choice => choice.isCorrect);
    const correct = correctChoice && correctChoice.content === selected;

    navigate('/adaptive-learning/result', {
      state: {
        isCorrect: correct,
        explanation: quiz.explanation || "해설 없음", // 백엔드에 explanation 추가 안 된 경우
        next: '/adaptive-learning/start',
      },
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
            </button>
          ))}
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
