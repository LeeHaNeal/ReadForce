import React, { useEffect, useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import ChallengeStartModal from './ChallengeStartModal';
import './challengepage.css';
import api from '../../api/axiosInstance';

const ChallengePage = () => {
  const navigate = useNavigate();
  const [showModal, setShowModal] = useState(false);
  const [wrongQuestions, setWrongQuestions] = useState([]);
  const [top5Data, setTop5Data] = useState([]);
  const [selectedLanguage, setSelectedLanguage] = useState('KOREAN');
  const debounceRef = useRef(null);

  const handleRankingClick = () => navigate('/ranking');
  const handleStartChallenge = () => setShowModal(true);
  const handleSolveClick = (quizNo) => navigate(`/question/${quizNo}`);

  useEffect(() => {
    const fetchWrongQuestions = async () => {
      try {
        const res = await api.get('/quiz/get-most-incorrected-quiz');
        setWrongQuestions(res.data);
      } catch (error) {
        console.error('가장 많이 틀린 문제 불러오기 실패:', error);
      }
    };
    fetchWrongQuestions();
  }, []);

  useEffect(() => {
    if (debounceRef.current) clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(async () => {
      try {
        const res = await api.get(`/ranking/get-ranking-list?category=NEWS&language=${selectedLanguage}`);
        setTop5Data(res.data.slice(0, 5));
      } catch (err) {
        console.error('TOP 5 뉴스 랭킹 불러오기 실패:', err);
        setTop5Data([]);
      }
    }, 500);
    return () => clearTimeout(debounceRef.current);
  }, [selectedLanguage]);

  return (
    <div className="ChallengePage-container">
      <h2 className="ChallengePage-title">
        “ 오늘 <span className="ChallengePage-highlight">812명</span>이 도전했어요! ”
      </h2>

      <div className="ChallengePage-top-container">
        <div className="ChallengePage-stats-box">
          <h3>실시간 통계</h3>
          <div className="ChallengePage-grid-4x2">
            <div><div className="label">응시자 수</div><div className="number">20,180</div></div>
            <div><div className="label">총 문제</div><div className="number">100</div></div>
            <div><div className="label">푸는 문제</div><div className="number">23</div></div>
            <div><div className="label">정답률</div><div className="number">74%</div></div>
          </div>
          <button className="ChallengePage-btn" onClick={handleStartChallenge}>
            문제 도전하기
          </button>
        </div>

        <div className="ChallengePage-top5-box">
          <div className="ChallengePage-top5-header">
            <h3>뉴스 주간 TOP 5</h3>
            <button className="ChallengePage-ranking-more-btn" onClick={handleRankingClick}>＋</button>
          </div>

          <div className="ChallengePage-top5-tabs">
            {['KOREAN', 'JAPANESE', 'ENGLISH'].map((lang) => (
              <button
                key={lang}
                className={selectedLanguage === lang ? 'active' : ''}
                onClick={() => setSelectedLanguage(lang)}
              >
                {lang === 'KOREAN' ? '한국' : lang === 'JAPANESE' ? '일본' : '미국'}
              </button>
            ))}
          </div>

          <ol>
            {top5Data.map((user, index) => (
              <li key={user.nickname}>
                <span className={`rank ${index === 0 ? 'gold' : index === 1 ? 'silver' : index === 2 ? 'bronze' : ''}`}>
                  {index + 1}
                </span>
                {user.nickname}
                <span className="point">{user[`${selectedLanguage.toLowerCase()}_news`] ?? 0}p</span>
              </li>
            ))}
          </ol>
        </div>
      </div>

      <div className="ChallengePage-wrong-section">
        <div className="ChallengePage-wrong-header-row">
          <h3>가장 많이 틀린 문제</h3>
          <div className="ChallengePage-right-label">정답률</div>
          <div></div>
        </div>

        {wrongQuestions.map((item, index) => {
          const correctRate = 100 - item.percentage;
          return (
            <div className="ChallengePage-wrong-item" key={item.news_quiz_no ?? item.literature_quiz_no ?? `wrong-${index}`}>
              <p>{item.question_text}</p>
              <span>{correctRate}%</span>
              <button onClick={() => handleSolveClick(item.news_quiz_no ?? item.literature_quiz_no)}>문제풀기</button>
            </div>
          );
        })}
      </div>

      {showModal && <ChallengeStartModal onClose={() => setShowModal(false)} />}
    </div>
  );
};

export default ChallengePage;
