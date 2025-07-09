import React, { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import UniversalList from '../../components/universal/UniversalList';
import { debouncedFetchPassageList } from '../../api/passageApi';
import { novelTypeOptions } from '../../components/TypeOptions';

const reverseLevelMap = {
  '1': 'LEVEL_1',
  '2': 'LEVEL_2',
  '3': 'LEVEL_3',
  '4': 'LEVEL_4',
  '5': 'LEVEL_5',
  '6': 'LEVEL_6',
  '7': 'LEVEL_7',
  '8': 'LEVEL_8',
  '9': 'LEVEL_9',
  '10': 'LEVEL_10',
};

const reverseCategoryMap = {
  '추리 / 미스테리': 'MYSTERY',
  '과학': 'SCIENCE',
  '판타지': 'FANTASY',
  '로맨스': 'ROMANCE',
  '역사': 'HISTORICAL',
  '모험': 'ADVENTURE',
  '스릴러': 'THRILLER',
  '기타': 'ETC',
};

const NovelPage = () => {
  const navigate = useNavigate();
  const [items, setItems] = useState([]);
  const [language] = useState('KOREAN');
  const classification = 'NORMAL';
  const category = 'NOVEL';

  const [type, setType] = useState('');
  const [level, setLevel] = useState('');
  const [orderBy, setOrderBy] = useState('latest');

  const fetchData = useCallback(() => {
    const apiLevel = reverseLevelMap[level] || '';
    const apiType = reverseCategoryMap[type] || '';

    debouncedFetchPassageList(
      {
        language,
        classification,
        category,
        type: apiType,
        level: apiLevel,
        orderBy,
      },
      (data) => {
        setItems(data);
      }
    );
  }, [language, classification, category, type, level, orderBy]);

  useEffect(() => {
    fetchData();
    return () => {
      debouncedFetchPassageList.cancel();
    };
  }, [fetchData]);

  const handleSolve = (item) => {
    navigate(`/literature-quiz/${item.passage_no}`);
  };

  return (
    <div className="page-container">
      <UniversalList
        items={items}
        level={level}
        setLevel={setLevel}
        type={type}
        setType={setType}
        orderBy={orderBy}
        setOrderBy={setOrderBy}
        typeOptions={novelTypeOptions}
        onSolve={handleSolve}
      />
    </div>
  );
};

export default NovelPage;
