import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import fetchWithAuth from '../../utils/fetchWithAuth'; 
import './AdaptiveQuizPage.css'; 

const AdaptiveQuizPage = () => {
  const navigate = useNavigate();
  const [quiz, setQuiz] = useState(null);
  const [selected, setSelected] = useState(null);
  const [notFound] = useState(false);

  useEffect(() => {
    const fetchQuiz = async () => {
      try {
        const res = await fetchWithAuth('/adaptive/get-question');
        const data = await res.json();

        // 데이터가 없거나 이상하면 더미 보여주기
        if (!data || !data.question) {
          console.warn('문제 없음: 더미 데이터로 대체');
          setQuiz({
            passage: '그는 고요한 밤에도 불을 켜고 책을 읽었다.',
            question: '다음 중 이 문장의 뜻과 가장 가까운 것은?',
            options: ['밤에 잠을 자지 않았다', '낮에 책을 읽었다', 'TV를 보았다', '도서관에 갔다'],
            correct_answer: '밤에 잠을 자지 않았다',
            explanation: '밤새워 책을 읽었다는 의미입니다.',
          });
        } else {
          setQuiz(data);
        }
      } catch (err) {
        console.error('API 오류: 더미 데이터로 대체', err);
        setQuiz({
          passage: '그는 고요한 밤에도 불을 켜고 책을 읽었다.',
          question: '다음 중 이 문장의 뜻과 가장 가까운 것은?',
          options: ['밤에 잠을 자지 않았다', '낮에 책을 읽었다', 'TV를 보았다', '도서관에 갔다'],
          correct_answer: '밤에 잠을 자지 않았다',
          explanation: '밤새워 책을 읽었다는 의미입니다.',
        });
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

  const options = quiz.options || []; 

  const handleSubmit = () => {
    if (!selected) return;

    const correct = quiz.correct_answer === selected;

    navigate('/adaptive-learning/result', {
      state: {
        isCorrect: correct,
        explanation: quiz.explanation || "해설 없음",
        next: '/adaptive-learning/start', 
      },
    });
  };

  return (
    <div className="quiz-layout">
      <div className="quiz-passage">
        <h3 className="passage-title">🤖 적응력 문제</h3>
        <p className="passage-text">{quiz.passage || '※ 추가 지문 없음'}</p>
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
          >정답 제출</button>
        </div>
      </div>
    </div>
  );
};

export default AdaptiveQuizPage;
