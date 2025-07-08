import React, { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import UniversalList from '../../components/universal/UniversalList';
import { debouncedFetchPassageList } from '../../api/passageApi';
import NewsCategory from '../../components/NewsCategory';

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
    debouncedFetchPassageList({
      language,
      classification,
      category,
      type: type,
      level,
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
        typeOptions={NewsCategory}
        onSolve={handleSolve}
      />
    </div>
  );
};

export default ArticlePage;
