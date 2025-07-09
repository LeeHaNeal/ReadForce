import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation, useParams } from 'react-router-dom';
// import api from '../../api/axiosInstance';
import './css/ArticleQuestionPage.css';
import fetchWithAuth from '../../utils/fetchWithAuth';

const ArticleQuestionPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();

  const [quiz, setQuiz] = useState(null);
  const [article, setArticle] = useState(null);
  const [selected, setSelected] = useState(null);
  const [error, setError] = useState(null);

  // useEffect(() => {
  //   const loadedArticle = location.state?.article || {
  //     news_no: Number(id),
  //     title: '',
  //     summary: '',
  //     content: '',
  //     language: '한국어',
  //   };

  //   if (!loadedArticle.news_no) {
  //     setError("뉴스 또는 퀴즈 정보를 불러오지 못했습니다.");
  //     return;
  //   }

  //   setArticle(loadedArticle);

  //   console.log("🔍 요청할 news_no:", loadedArticle.news_no);  // 디버그용 로그

  //   api.get('/news/get-news-quiz-object', {
  //     params: { news_no: loadedArticle.news_no }
  //   })
  //     .then(res => {
  //       console.log("✅ 퀴즈 로딩 성공:", res.data);
  //       setQuiz(res.data);
  //     })
  //     .catch(err => {
  //       console.error("❌ 퀴즈 로딩 실패:", err);
  //       setError("퀴즈 로딩 중 오류 발생");
  //     });
  // }, [id, location.state]);

  // refresh-token 적용
  useEffect(() => {
    const loadedArticle = location.state?.article || {
      news_no: Number(id),
      title: '',
      summary: '',
      content: '',
      language: '한국어',
    };

    if (!loadedArticle.news_no) {
      setError("뉴스 또는 퀴즈 정보를 불러오지 못했습니다.");
      return;
    }

    setArticle(loadedArticle);


    console.log("🔍 요청할 news_no:", loadedArticle.news_no);

    fetchWithAuth(`/news/get-news-quiz-object?news_no=${loadedArticle.news_no}`)
      .then(res => res.json())
      .then(data => {
        console.log("퀴즈 로딩 성공:", data);
        setQuiz(data);
      })
      .catch(err => {
        console.error("퀴즈 로딩 실패:", err);
        setError("퀴즈 로딩 중 오류 발생");
      });
  }, [id, location.state]);

  const options = quiz ? [
    { text: quiz.choice1 },
    { text: quiz.choice2 },
    { text: quiz.choice3 },
    { text: quiz.choice4 },
  ] : [];

  // const handleSubmit = () => {
  //   if (selected === null) return;
  //   navigate('/question-result', {
  //     state: {
  //       isCorrect: selected === quiz.correct_answer_index,
  //       explanation: quiz.explanation,
  //       language: article.language || '한국어',
  //     },
  //   });
  // };
  const handleSubmit = async () => {
    if (selected === null) return;

    try {
      const res = await fetchWithAuth('/news/save-member-solved-news-quiz', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          news_quiz_no: quiz.news_quiz_no,
          selected_option_index: selected,
        }),
      });

      if (!res.ok) throw new Error('서버 응답 오류');

      // 저장 성공 시 결과 페이지로 이동
      navigate('/question-result', {
        state: {
          isCorrect: selected === quiz.correct_answer_index,
          explanation: quiz.explanation,
          language: article.language || '한국어',
        },
      });
    } catch (err) {
      console.error('퀴즈 저장 실패:', err);
      alert('퀴즈 결과 저장 중 오류가 발생했습니다. 다시 시도해주세요.');
    }
  };

  if (error) return <div className="ArticleQuestion-container">{error}</div>;
  if (!article || !quiz) return <div className="ArticleQuestion-container">로딩 중...</div>;

  // return (
  //   <div className="ArticleQuestion-container">
  //     <div className="ArticleQuestion-article">
  //       <h3 className="ArticleQuestion-title">{article.title}</h3>
  //       <p className="ArticleQuestion-summary">{article.summary}</p>
  //       <p className="ArticleQuestion-content">{article.content}</p>
  //     </div>

  //     <div className="ArticleQuestion-quiz">
  //       <h4>💡 문제</h4>
  //       <p>{quiz.question_text}</p>
  //       <div className="ArticleQuestion-options">
  //         {[quiz.choice1, quiz.choice2, quiz.choice3, quiz.choice4].map((opt, idx) => (
  //           <button
  //             key={idx}
  //             className={selected === idx ? 'selected' : ''}
  //             onClick={() => setSelected(idx)}
  //           >
  //             {String.fromCharCode(65 + idx)}. {opt}
  //           </button>
  //         ))}
  //       </div>
  //       <button disabled={selected === null} onClick={handleSubmit}>정답 제출</button>
  //     </div>
  //   </div>
  // );
  return (
    <div className="page-container quiz-layout">
      <div className="quiz-passage">
        <h3 className="passage-title">{article.title}</h3>
        <p className="passage-text">{article.summary}</p>
        <p className="passage-text">{article.content}</p>
      </div>


      <div className="quiz-box">
        <h4 className="question-heading">💡 문제</h4>
        <p className="question-text">{quiz.question_text}</p>
        <div className="quiz-options">
          {options.map((opt, idx) => (
            <button
              key={idx}
              className={`quiz-option ${selected === idx ? 'selected' : ''}`}

              onClick={() => setSelected(idx)}
            >
              {String.fromCharCode(65 + idx)}. {opt.text}
            </button>
          ))}
        </div>


        <div className="quiz-button-container">
          <button
            className="submit-button"
            disabled={selected === null}
            onClick={handleSubmit}
          >
            정답 제출
          </button>
        </div>

      </div>
    </div>
  );
};

export default ArticleQuestionPage;