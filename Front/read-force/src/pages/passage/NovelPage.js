import React, { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import UniversalList from '../../components/universal/UniversalList';
import { debouncedFetchPassageList } from '../../api/passageApi';
import { novelTypeOptions } from '../../components/TypeOptions';

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
    const apiLevel = level || '';
    const apiType = type || '';

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
    navigate(`/novel/quiz/${item.passageNo}`, {
      state: { article: item }
    });
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
