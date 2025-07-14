import React, { useState, useEffect } from 'react';
import api from '../../api/axiosInstance';
import './RankingPage.css';

const categories = [
  { label: '뉴스(한국어)', category: 'NEWS', language: 'KOREAN', scoreKey: 'korean_news' },
  { label: '뉴스(영어)', category: 'NEWS', language: 'ENGLISH', scoreKey: 'english_news' },
  { label: '뉴스(일본어)', category: 'NEWS', language: 'JAPANESE', scoreKey: 'japanese_news' },
  { label: '소설', category: 'NOVEL', language: 'KOREAN', scoreKey: 'novel' },
  { label: '동화', category: 'FAIRY_TALE', language: 'KOREAN', scoreKey: 'fairytale' },
];

const RankingPage = () => {
  const [selectedCategory, setSelectedCategory] = useState(categories[0]);
  const [rankingData, setRankingData] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchRanking = async () => {
      setIsLoading(true);
      setError(null);

      const { category, language } = selectedCategory;

      try {
        const response = await api.get('/ranking/get-ranking-list', {
          params: { category, language },
        });
        setRankingData(response.data);
      } catch (err) {
        console.error('❌ 랭킹 API 오류:', err);
        setError('랭킹 정보를 불러오지 못했습니다.');
        setRankingData([]);
      } finally {
        setIsLoading(false);
      }
    };

    fetchRanking();
  }, [selectedCategory]);

  const renderScore = (user) => user[selectedCategory.scoreKey] ?? user.score ?? 0;

  return (
    <div className="RankingPage-container">
      <h2 className="RankingPage-title">🏆 문해력 랭킹</h2>

      <div className="RankingPage-category-buttons">
        {categories.map((cat) => (
          <button
            key={cat.label}
            className={selectedCategory.label === cat.label ? 'active' : ''}
            onClick={() => setSelectedCategory(cat)}
          >
            {cat.label}
          </button>
        ))}
      </div>

      {isLoading ? (
        <p className="RankingPage-loading">랭킹 정보를 불러오는 중입니다...</p>
      ) : error ? (
        <p className="RankingPage-error">{error}</p>
      ) : rankingData.length === 0 ? (
        <p className="RankingPage-no-data">해당 카테고리의 랭킹 정보가 없습니다.</p>
      ) : (
        <div className="RankingPage-list">
          {rankingData.map((user, idx) => (
            <div key={user.email} className="RankingPage-item">
              <span className="RankingPage-rank">{idx + 1}위</span>
              <span className="RankingPage-nickname">{user.nickname}</span>
              <span className="RankingPage-score">{renderScore(user)}점</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default RankingPage;
