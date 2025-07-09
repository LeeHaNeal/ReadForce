import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation, useParams } from 'react-router-dom';
import './css/UniversalQuestionPage.css';
import fetchWithAuth from '../../utils/fetchWithAuth';

const UniversalQuestionPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();

  const [quizList, setQuizList] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [selected, setSelected] = useState(null);
  const [passage, setPassage] = useState(null);
  const [error, setError] = useState(null);
  const [answers, setAnswers] = useState([]);

  useEffect(() => {
    const loadedPassage = location.state?.passage || {
      passageNo: Number(id),
      title: '',
      summary: '',
      content: '',
      language: '한국어',
    };

    if (!loadedPassage.passageNo) {
      setError("뉴스 또는 퀴즈 정보를 불러오지 못했습니다.");
      return;
    }

    setPassage(loadedPassage);

    fetchWithAuth(`/multiple_choice/get-multiple-choice-question-list?passageNo=${loadedPassage.passageNo}`)
      .then(res => res.json())
      .then(data => {
        console.log("퀴즈 로딩 성공:", data);
        setQuizList(data);
      })
      .catch(err => {
        console.error("퀴즈 로딩 실패:", err);
        setError("퀴즈 로딩 중 오류 발생");
      });
  }, [id, location.state]);

  const currentQuiz = quizList[currentIndex];

  const handleNext = () => {
    if (selected === null) return;

    const updatedAnswers = [...answers, { questionNo: currentQuiz.questionNo, selected }];
    setAnswers(updatedAnswers);
    setSelected(null);

    if (currentIndex < quizList.length - 1) {
      setCurrentIndex(currentIndex + 1);
    } else {
      handleSubmit(updatedAnswers);
    }
  };

  const handleSubmit = async (finalAnswers) => {
    try {
      const categoryPath = passage.category === 'NEWS'
        ? 'article'
        : passage.category === 'NOVEL'
        ? 'novel'
        : 'fairytale';

      navigate(`/${categoryPath}/result`, {
        state: {
          passage,
          total: finalAnswers.length,
          answers: finalAnswers,
          category: passage.category
        },
      });
    } catch (err) {
      console.error('퀴즈 저장 실패:', err);
      alert('퀴즈 결과 저장 중 오류가 발생했습니다. 다시 시도해주세요.');
    }
  };

  if (error) return <div className="PassageQuestion-container">{error}</div>;
  if (!passage || quizList.length === 0 || !currentQuiz) return <div className="PassageQuestion-container">로딩 중...</div>;

  return (
    <div className="page-container quiz-layout">
      <div className="quiz-passage">
        <h3 className="passage-title">{passage.title}</h3>
        <p className="passage-text">{passage.summary}</p>
        <p className="passage-text">{passage.content}</p>
      </div>

      <div className="quiz-box">
        <h4 className="question-heading">💡 문제 {currentIndex + 1}</h4>
        <p className="question-text">{currentQuiz.question}</p>

        <div className="quiz-options">
          {currentQuiz.choiceList.map((choice, idx) => (
            <button
              key={idx}
              className={`quiz-option ${selected === idx ? 'selected' : ''}`}
              onClick={() => setSelected(idx)}
            >
              {String.fromCharCode(65 + idx)}. {choice.content}
            </button>
          ))}
        </div>

        <div className="quiz-button-container">
          <button
            className="submit-button"
            disabled={selected === null}
            onClick={handleNext}
          >
            {currentIndex < quizList.length - 1 ? '다음 문제' : '제출'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default UniversalQuestionPage;
