import React, { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import UniversalList from '../../components/universal/UniversalList';
import { debouncedFetchPassageList } from '../../api/passageApi';
import { newsTypeOptions } from '../../components/TypeOptions';

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

const ArticlePage = () => {
  const navigate = useNavigate();
  const [newsItems, setNewsItems] = useState([]);
  const [language] = useState('KOREAN');
  const [level, setLevel] = useState('');
  const [type, setType] = useState('');
  const [orderBy, setOrderBy] = useState('latest');

  const category = 'NEWS';
  const classification = 'NORMAL';

  const fetchData = useCallback(() => {
    const apiLevel = reverseLevelMap[level] || '';

    debouncedFetchPassageList({
      language,
      classification,
      category,
      type: type,
      level: apiLevel,
      orderBy,
    }, (data) => {
      setNewsItems(data);
    });
  }, [language, category, type, level, orderBy]);

  useEffect(() => {
    fetchData();
    return () => {
      debouncedFetchPassageList.cancel();
    };
  }, [fetchData]);

  const handleSolve = (item) => {
    navigate(`/question/${item.passage_no}`, {
      state: { article: item }
    });
  };

  return (
    <div className="page-container">
      <UniversalList
        items={newsItems}
        level={level}
        setLevel={setLevel}
        type={type}
        setType={setType}
        orderBy={orderBy}
        setOrderBy={setOrderBy}
        typeOptions={newsTypeOptions}
        onSolve={handleSolve}
      />
    </div>
  );
};

export default ArticlePage;
