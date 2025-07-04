import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { useNavigate } from 'react-router-dom'; 
import debounce from 'lodash/debounce';

import UniversalList from '../../components/universal/UniversalList';
import { fetchLiteratureParagraphList } from '../../api/literatureApi';
import { fairytaleCategoryOptions } from '../../components/LiteratureCategory';

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
  const [items, setItems] = useState([]);
  const type = 'FAIRYTALE'; 
  const [level, setLevel] = useState('');
  const [category, setCategory] = useState('');
  const [order_by, setOrderBy] = useState('latest');

  const navigate = useNavigate(); 

  const handleSolve = (item) => {
    navigate(`/literature-quiz/${item.literature_paragraph_no}`);
  };

  const fetchItems = useCallback(async (params) => {
    try {
      return await fetchLiteratureParagraphList({
        type,
        level: params.level,
        category: params.category,
        order_by: params.order_by,
      });
    } catch (err) {
      console.error('동화 목록 불러오기 실패:', err);
      return [];
    }
  }, []);

  const debouncedFetch = useMemo(() => debounce(async (params) => {
    const data = await fetchItems(params);
    setItems(data);
  }, 300), [fetchItems]);

  const fetchData = useCallback(() => {
    const apiLevel = reverseLevelMap[level] || '';
    const apiCategory = reverseCategoryMap[category] || '';
    debouncedFetch({
      level: apiLevel,
      category: apiCategory,
      order_by,
    });
  }, [debouncedFetch, level, category, order_by]);

  useEffect(() => {
    fetchData();
    return () => {
      debouncedFetch.cancel();
    };
  }, [fetchData, debouncedFetch]);

  return (
    <div className="page-container">
      <UniversalList
        items={items}
        level={level}
        setLevel={setLevel}
        category={category}
        setCategory={setCategory}
        order_by={order_by}
        setOrderBy={setOrderBy}
        categoryOptions={fairytaleCategoryOptions}
        onSolve={handleSolve} 
      />
    </div>
  );
};

export default FairyTalePage;
