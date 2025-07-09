import './css/UniversalCard.css';
import React, { useEffect } from 'react';

const UniversalCard = React.memo(({ data, onSolve, typeOptions = [] }) => {
  const getTypeLabel = (value) => {
    const trimmed = value?.trim();
    const found = typeOptions.find(opt => opt.value === trimmed);
    return found ? found.label : trimmed;
  };

  const getLevelLabel = (level) => {
    if (level <= 3) return '초급';
    if (level <= 6) return '중급';
    return '고급';
  };

  const type = getTypeLabel(data.type);
  const level = data.level;
  const levelLabel = getLevelLabel(level);

  return (
    <div className="UniversalCard-card">
      <div className="UniversalCard-header">
        <h3 className="UniversalCard-title">
          {data.title}
          <span className="UniversalCard-subtitle">
            - {data.literature_paragraph_no}
          </span>
        </h3>
        <span className={`UniversalCard-badge UniversalCard-${levelLabel}`}>
          {level}단계
        </span>
      </div>

      <p className="UniversalCard-content">{data.content}</p>

      <div className="UniversalCard-footer">
        <p className="UniversalCard-category"># {type}</p>
        <button
          onClick={() => onSolve && onSolve(data)}
          className="UniversalCard-button"
        >
          문제 풀기
        </button>
      </div>
    </div>
  );
});


export default UniversalCard;
