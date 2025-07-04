import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { fetchLiteratureParagraphList } from '../../api/literatureApi'; 
import debounce from 'lodash/debounce';
import { novelCategoryOptions } from '../../components/LiteratureCategory';
import UniversalList from '../../components/universal/UniversalList';
import { useNavigate } from 'react-router-dom';

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
  '역사': 'HISTORY',
  '모험': 'ADVENTURE',
  '스릴러': 'THRILLER',
  '기타': 'ETC',
};

const NovelPage = () => {
  const [items, setItems] = useState([]);
  const type = 'NOVEL'; 
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
      console.error('소설 목록 불러오기 실패:', err);
      return [];
    }
  }, [type]);

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
        categoryOptions={novelCategoryOptions}
        onSolve={handleSolve}
        />
    </div>
  );
};

export default NovelPage;
