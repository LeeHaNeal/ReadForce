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
  const [answerList, setAnswerList] = useState([]);
  const [timeLeft, setTimeLeft] = useState(30 * 60); // 30 minutes
  const [loading, setLoading] = useState(true);
  const timerRef = useRef(null);

  /** 문제 가져오기 + 타이머 시작 */
  useEffect(() => {
    api.get('/challenge/get-challenge-question-list', {
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
  }, [language, category]);

  /** 보기 선택 */
  const handleSelect = (index) => {
    setSelectedAnswer(index);
  };

  /** 다음 문제로 이동 */
  const handleNext = () => {
    if (selectedAnswer === null) {
      alert('답을 선택해주세요.');
      return;
    }

    const currentQuiz = quizzes[currentIndex];
    setAnswerList(prev => [...prev, { [currentQuiz.questionNo]: selectedAnswer }]);
    setSelectedAnswer(null);
    setCurrentIndex(prev => prev + 1);
  };

  /** 결과 제출 */
  const handleSubmit = async () => {
    if (selectedAnswer !== null && currentIndex < quizzes.length) {
      const currentQuiz = quizzes[currentIndex];
      setAnswerList(prev => [...prev, { [currentQuiz.questionNo]: selectedAnswer }]);
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
      alert(`오늘의 도전 완료! 점수: ${res.data.SCORE}`);
      navigate('/challenge');
    } catch (error) {
      console.error(error);
      alert('결과 제출 실패');
      navigate('/challenge');
    }
  };

  /** 남은 시간 포맷팅 */
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


        {currentIndex === quizzes.length - 1 ? (
          <button className="ChallengeQuizPage-submit" onClick={handleSubmit}>제출하기</button>
        ) : (
          <button className="ChallengeQuizPage-submit" onClick={handleNext}>다음 문제</button>
        )}
      </div>
    </div>
  );
};

export default ChallengeQuizPage;
