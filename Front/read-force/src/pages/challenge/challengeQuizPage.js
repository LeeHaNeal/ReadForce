import React, { useEffect, useRef, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import api from '../../api/axiosInstance';
import './ChallengeQuizPage.css';

const ChallengeQuizPage = () => {
  const navigate = useNavigate();
  const location = useLocation();

  // language, category, type 쿼리 파라미터로 받음
  const params = new URLSearchParams(location.search);
  const language = params.get('language') || 'KOREAN';
  const category = params.get('category') || 'NEWS';
  const type = params.get('type') || 'ECONOMY';  // 기본값 ECONOMY로 설정

  const [quizzes, setQuizzes] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [selectedAnswer, setSelectedAnswer] = useState(null);
  const [answerList, setAnswerList] = useState([]);
  const [timeLeft, setTimeLeft] = useState(30 * 60);  // 30분 타이머
  const [loading, setLoading] = useState(true);
  const timerRef = useRef(null);

  useEffect(() => {
    // 문제 불러오기 (type 포함)
    api.get('/challenge/get-challenge-question-list', {
      params: { language, category, type }
    })
      .then(res => setQuizzes(res.data))
      .catch(() => alert('문제 불러오기 실패'))
      .finally(() => setLoading(false));

    // 타이머 시작
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
  }, [language, category, type]);  // type도 의존성에 추가

  const handleSelect = (index) => setSelectedAnswer(index);

  const handleNext = () => {
    if (selectedAnswer === null) return alert('답을 선택해주세요.');
    setAnswerList(prev => [...prev, { [quizzes[currentIndex].questionNo]: selectedAnswer }]);
    setSelectedAnswer(null);
    setCurrentIndex(prev => prev + 1);
  };

  const handleSubmit = async () => {
    if (selectedAnswer !== null && currentIndex < quizzes.length) {
      setAnswerList(prev => [...prev, { [quizzes[currentIndex].questionNo]: selectedAnswer }]);
    }

    clearInterval(timerRef.current);

    const payload = {
      selecetedIndexList: answerList,
      totalQuestionSolvingTime: 1800 - timeLeft,
      language,
      category,
      type  // 제출할 때도 type 전달
    };

    try {
      const res = await api.post('/challenge/submit-challenge-result', payload);
      alert(`오늘의 도전 완료! 점수: ${res.data.SCORE}`);
      navigate('/challenge');
    } catch {
      alert('결과 제출 실패');
      navigate('/challenge');
    }
  };

  const formatTime = (seconds) => `${String(Math.floor(seconds / 60)).padStart(2, '0')}:${String(seconds % 60).padStart(2, '0')}`;

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

        <div className="ChallengeQuizPage-question">{currentQuiz.question}</div>

        {currentQuiz.choiceList?.map((choice, idx) => (
          <button
            key={idx}
            className={`ChallengeQuizPage-option ${selectedAnswer === idx ? 'selected' : ''}`}
            onClick={() => handleSelect(idx)}
          >
            {choice.content}
          </button>
        ))}

        {currentIndex === quizzes.length - 1
          ? <button className="ChallengeQuizPage-submit" onClick={handleSubmit}>제출하기</button>
          : <button className="ChallengeQuizPage-submit" onClick={handleNext}>다음 문제</button>}
      </div>
    </div>
  );
};

export default ChallengeQuizPage;
