import React, { useState } from 'react';
import ChallengeStartModal from './ChallengeStartModal';
import './challengepage.css';
import trophyAnimation from '../../assets/trophy.json';
import Lottie from 'lottie-react';

const ChallengePage = () => {
  const [showModal, setShowModal] = useState(false);

  return (
    <div className="challenge-wrapper">
      <Lottie animationData={trophyAnimation} loop autoplay className="trophy-animation" />

      <button
        className="challenge-btn"
        onClick={() => setShowModal(true)}
      >
        문해력 도전
      </button>

      {showModal && <ChallengeStartModal onClose={() => setShowModal(false)} />}
    </div>
  );
};

export default ChallengePage;
