import React, { useEffect, useState } from 'react';
import './testquestionpage.css';
import { useLocation, useNavigate } from 'react-router-dom';
import api from '../../api/axiosInstance';

const TestQuestionPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const language = location.state?.language || 'KOREAN';

  const [question, setQuestion] = useState(null);
  const [selected, setSelected] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    api
      .get(`/test/start?language=${language}`)
      .then((res) => {
        const raw = res.data;

        // ✅ 여기서 정확히 변환해야 함
        const formatted = {
          article: {
            title: raw.title,
            content: raw.content,
          },
          quiz: {
            questionNo: raw.questionNo,
            questionText: raw.question,
            choices: raw.choiceList.map((c) => c.content),
          },
          testerId: raw.testerId,
        };

        setQuestion(formatted);
      })
      .catch(() => {
        alert('문제를 불러오지 못했습니다.');
        navigate('/');
      });
  }, [language, navigate]);

  const handleSelect = (idx) => {
    if (isSubmitting) return;
    setSelected(idx);
  };

  const handleSubmit = async () => {
    if (selected === null) {
      alert('보기를 선택하세요.');
      return;
    }

    setIsSubmitting(true);

    try {
      const res = await api.post('/test/submit-vocabulary-result', {
        testerId: question.testerId,
        questionNo: question.quiz.questionNo,
        selectedIndex: selected,
        questionSolvingTime: 10,
        language,
      });

      if (res.data?.choiceList) {
        const raw = res.data;

        const formatted = {
          article: {
            title: raw.title,
            content: raw.content,
          },
          quiz: {
            questionNo: raw.questionNo,
            questionText: raw.question,
            choices: raw.choiceList.map((c) => c.content),
          },
          testerId: raw.testerId,
        };

        setQuestion(formatted);
        setSelected(null);
      } else {
        // 최종 결과
        navigate('/test-result', { state: { result: res.data } });
      }
    } catch (err) {
      alert('제출 중 오류가 발생했습니다.');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!question) return <div>문제를 불러오는 중...</div>;

  return (
    <div className="TestQuestion-layout">
      <div className="TestQuestion-article-box">
        <h3 className="TestQuestion-article-title">{question.article.title}</h3>
        <p className="TestQuestion-article-content">{question.article.content}</p>
      </div>

      <div className="TestQuestion-right-container">
        <div className="TestQuestion-quiz-box">
          <h4 className="TestQuestion-quiz-title">문제</h4>
          <p className="TestQuestion-quiz-question">{question.quiz.questionText}</p>

          {question.quiz.choices.map((opt, idx) => (
            <button
              key={idx}
              className={`TestQuestion-quiz-option ${selected === idx ? 'selected' : ''}`}
              onClick={() => handleSelect(idx)}
              disabled={isSubmitting}
            >
              {String.fromCharCode(65 + idx)}. {opt}
            </button>
          ))}
        </div>

        <div className="TestQuestion-controls">
          <button
            className="TestQuestion-submit"
            onClick={handleSubmit}
            disabled={selected === null || isSubmitting}
          >
            제출
          </button>
        </div>
      </div>
    </div>
  );
};

export default TestQuestionPage;
