import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './ChallengeStartModal.css';

const ChallengeStartModal = ({ onClose }) => {
  const navigate = useNavigate();

  const [category, setCategory] = useState('NEWS');
  const [language, setLanguage] = useState('KOREAN');
  const [literatureType, setLiteratureType] = useState('NOVEL');

  const handleStartClick = () => {
    if (category === 'NEWS') {
      navigate(`/challenge/quiz?language=${language}&category=NEWS`);
    } else {
      navigate(`/challenge/quiz?language=${language}&category=${literatureType}`);
    }
    onClose();
};


  return (
    <div className="ChallengeStartModal-overlay">
      <div className="ChallengeStartModal-content">
        <h2 className="ChallengeStartModal-title">도전 유형 선택</h2>

        <div className="ChallengeStartModal-category">
          <button className={category === 'NEWS' ? 'selected' : ''} onClick={() => setCategory('NEWS')}>기사</button>
          <button className={category === 'LITERATURE' ? 'selected' : ''} onClick={() => setCategory('LITERATURE')}>문학</button>
        </div>

        {category === 'NEWS' && (
          <div className="ChallengeStartModal-option-group">
            <p>언어 선택</p>
            <button className={language === 'KOREAN' ? 'selected' : ''} onClick={() => setLanguage('KOREAN')}>한국어</button>
            <button className={language === 'ENGLISH' ? 'selected' : ''} onClick={() => setLanguage('ENGLISH')}>English</button>
            <button className={language === 'JAPANESE' ? 'selected' : ''} onClick={() => setLanguage('JAPANESE')}>일본어</button>
          </div>
        )}

        {category === 'LITERATURE' && (
          <div className="ChallengeStartModal-option-group">
            <p>문학 종류 선택</p>
            <button className={literatureType === 'NOVEL' ? 'selected' : ''} onClick={() => setLiteratureType('NOVEL')}>소설</button>
            <button className={literatureType === 'FAIRYTALE' ? 'selected' : ''} onClick={() => setLiteratureType('FAIRYTALE')}>동화</button>
          </div>
        )}

        <div className="ChallengeStartModal-actions">
          <button onClick={onClose}>취소</button>
          <button onClick={handleStartClick}>시작하기</button>
        </div>
      </div>
    </div>
  );
};

export default ChallengeStartModal;
