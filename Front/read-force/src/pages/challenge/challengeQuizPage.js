import React, { useEffect, useRef, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import api from '../../api/axiosInstance';
import './ChallengeQuizPage.css';

const ChallengeQuizPage = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const params = new URLSearchParams(location.search);
  const language = params.get('language') || 'KOREAN';
  const category = params.get('category') || 'NEWS';

  const [quizzes, setQuizzes] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [selectedAnswer, setSelectedAnswer] = useState(null);
  const [answerList, setAnswerList] = useState([]); // 제출할 답안 목록
  const [timeLeft, setTimeLeft] = useState(30 * 60);
  const [loading, setLoading] = useState(true);
  const timerRef = useRef(null);

  useEffect(() => {
    if (!language || !category) {
      alert('language와 category 파라미터가 필요합니다.');
      navigate('/');
      return;
    }

    api.get(`/challenge/get-challenge-question-list`, {
      params: { language, category }
    })
      .then(res => setQuizzes(res.data))
      .catch(() => alert('문제 불러오기 실패'))
      .finally(() => setLoading(false));

    timerRef.current = setInterval(() => {
      setTimeLeft(prev => {
        if (prev <= 1) {
          clearInterval(timerRef.current);
          handleSubmit();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timerRef.current);
  }, [language, category, navigate]);

  const handleSelect = (index) => {
    setSelectedAnswer(index);
  };

  const handleNext = () => {
    if (selectedAnswer === null) {
      alert('답을 선택해주세요.');
      return;
    }

    const currentQuiz = quizzes[currentIndex];
    setAnswerList(prev => [
      ...prev,
      { [currentQuiz.questionNo]: selectedAnswer }
    ]);
    setSelectedAnswer(null);
    setCurrentIndex(prev => prev + 1);
  };

  const handleSubmit = async () => {
    if (selectedAnswer !== null && currentIndex < quizzes.length) {
      const currentQuiz = quizzes[currentIndex];
      setAnswerList(prev => [
        ...prev,
        { [currentQuiz.questionNo]: selectedAnswer }
      ]);
    }

    clearInterval(timerRef.current);

    const payload = {
      selecetedIndexList: answerList,
      totalQuestionSolvingTime: 1800 - timeLeft,
      language,
      category
    };

    try {
      const res = await api.post('/challenge/submit-challenge-result', payload);
      alert(`도전 완료! 점수: ${res.data.SCORE}`);
      navigate('/challenge');
    } catch (error) {
      console.error(error);
      alert('결과 제출 실패');
      navigate('/challenge');
    }
  };

  const formatTime = (seconds) => {
    const m = Math.floor(seconds / 60).toString().padStart(2, '0');
    const s = (seconds % 60).toString().padStart(2, '0');
    return `${m}:${s}`;
  };

  if (loading) return <div>문제 로딩중...</div>;
  if (quizzes.length === 0) return <div>문제가 없습니다.</div>;

  const currentQuiz = quizzes[currentIndex];

  return (
    <div className="ChallengeQuizPage-layout">
      <div className="ChallengeQuizPage-article">
        <h3>{currentQuiz.title}</h3>
        <p>{currentQuiz.content}</p>
      </div>

      <div className="ChallengeQuizPage-quiz-box">
        <div className="ChallengeQuizPage-header">
          <strong>문제 {currentIndex + 1} / {quizzes.length}</strong>
          <span className="ChallengeQuizPage-timer">남은 시간: {formatTime(timeLeft)}</span>
        </div>

        <div className="ChallengeQuizPage-question">{currentQuiz.questionText}</div>
        {currentQuiz.choices.map((choice, idx) => (
          <button
            key={idx}
            className={`ChallengeQuizPage-option ${selectedAnswer === idx ? 'selected' : ''}`}
            onClick={() => handleSelect(idx)}
          >
            {choice}
          </button>
        ))}

        {currentIndex === quizzes.length - 1 ? (
          <button className="ChallengeQuizPage-submit" onClick={handleSubmit}>
            제출하기
          </button>
        ) : (
          <button className="ChallengeQuizPage-submit" onClick={handleNext}>
            다음 문제
          </button>
        )}
      </div>
    </div>
  );
};

export default ChallengeQuizPage;
