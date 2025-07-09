import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation, useParams } from 'react-router-dom';
import './css/ArticleQuestionPage.css';
import fetchWithAuth from '../../utils/fetchWithAuth';

const ArticleQuestionPage = () => {
  const { id } = useParams(); // passageNo
  const navigate = useNavigate();
  const location = useLocation();

  const [quizList, setQuizList] = useState([]);
  const [currentQuizIndex, setCurrentQuizIndex] = useState(0);
  const [article, setArticle] = useState(null);
  const [selected, setSelected] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    const loadedArticle = location.state?.article || {
      passage_no: Number(id),
      title: '',
      content: '',
      language: '한국어',
    };

    if (!loadedArticle.passage_no) {
      setError("지문 또는 문제 정보를 불러오지 못했습니다.");
      return;
    }

    setArticle(loadedArticle);

    fetchWithAuth(`/multiple_choice/get-multiple-choice-question-list?passageNo=${loadedArticle.passage_no}`)
      .then(res => res.json())
      .then(data => {
        if (!data || data.length === 0) throw new Error('문제가 없습니다.');
        setQuizList(data);
      })
      .catch(err => {
        console.error("퀴즈 로딩 실패:", err);
        setError("문제 로딩 중 오류 발생");
      });
  }, [id, location.state]);

  const currentQuiz = quizList[currentQuizIndex];
  const options = currentQuiz?.choiceList || [];

  const handleSubmit = async () => {
    if (selected === null) return;

    try {
      const res = await fetchWithAuth('/multiple_choice/save-solved', { // 수정 필요 시 경로 변경
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          multiple_choice_no: currentQuiz.multipleChoiceNo,
          selected_choice_no: options[selected]?.choiceNo,
        }),
      });

      if (!res.ok) throw new Error('서버 응답 오류');

      // 다음 문제 있으면 이동, 없으면 결과 페이지
      if (currentQuizIndex + 1 < quizList.length) {
        setSelected(null);
        setCurrentQuizIndex(prev => prev + 1);
      } else {
        navigate('/question-result', {
          state: {
            solvedCount: quizList.length,
            language: article.language,
          },
        });
      }
    } catch (err) {
      console.error('정답 제출 실패:', err);
      alert('결과 저장 중 오류 발생. 다시 시도해주세요.');
    }
  };

  if (error) return <div className="ArticleQuestion-container">{error}</div>;
  if (!article || quizList.length === 0) return <div className="ArticleQuestion-container">로딩 중...</div>;

  return (
    <div className="page-container quiz-layout">
      <div className="quiz-passage">
        <h3 className="passage-title">{article.title}</h3>
        <p className="passage-text">{article.content}</p>
      </div>

      <div className="quiz-box">
        <h4 className="question-heading">💡 문제 {currentQuizIndex + 1}</h4>
        <p className="question-text">{currentQuiz.questionContent}</p>
        <div className="quiz-options">
          {options.map((opt, idx) => (
            <button
              key={opt.choiceNo}
              className={`quiz-option ${selected === idx ? 'selected' : ''}`}
              onClick={() => setSelected(idx)}
            >
              {String.fromCharCode(65 + idx)}. {opt.choiceContent}
            </button>
          ))}
        </div>

        <div className="quiz-button-container">
          <button
            className="submit-button"
            disabled={selected === null}
            onClick={handleSubmit}
          >
            {currentQuizIndex + 1 < quizList.length ? '다음 문제' : '제출 완료'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ArticleQuestionPage;
