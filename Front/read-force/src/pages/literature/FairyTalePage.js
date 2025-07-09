import React, { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import UniversalList from '../../components/universal/UniversalList';
import { debouncedFetchPassageList } from '../../api/passageApi';
import { fairytaleTypeOptions } from '../../components/TypeOptions';

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
  '전래': 'TRADITIONAL',
  '환상': 'FANTASY',
  '생활': 'LIFE',
  '정보': 'INFORMATION',
  '기타': 'ETC',
};

const FairyTalePage = () => {
  const navigate = useNavigate();
  const [items, setItems] = useState([]);
  const [language] = useState('KOREAN');
  const classification = 'NORMAL';
  const category = 'FAIRY_TALE';

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
        typeOptions={fairytaleTypeOptions}
        onSolve={handleSolve}
      />
    </div>
  );
};

export default FairyTalePage;
