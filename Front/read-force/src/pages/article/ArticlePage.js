import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import UniversalList from '../../components/universal/UniversalList';
import { fetchPassageList } from '../../api/newsApi';
import debounce from 'lodash/debounce';
import NewsCategory from '../../components/NewsCategory';

// Level 매핑
const reverseLevelMap = {
  '1': 1,
  '2': 2,
  '3': 3,
  '4': 4,
  '5': 5,
  '6': 6,
  '7': 7,
  '8': 8,
  '9': 9,
  '10': 10,
};

// ✅ CategoryEnum 매핑
const reverseCategoryMap = {
  '뉴스': 'NEWS',
  '소설': 'NOVEL',
  '동화': 'FAIRY_TALE',
};

// ✅ TypeEnum 매핑 (category 별)
const reverseTypeMap = {
  '뉴스': {
    '정치': 'POLITICS',
    '경제': 'ECONOMY',
    '사회': 'SOCIETY',
    '생활/문화': 'LIFE_AND_CULTURE',
    'IT/과학': 'IT_AND_SCIENCE',
    '세계': 'WORLD',
    '스포츠': 'SPORTS',
    '연예': 'ENTERTAINMENT',
  },
  '소설': {
    '추리/미스테리': 'MYSTERY',
    '과학': 'SCIENCE_FICTION',
    '판타지': 'FANTASY',
    '로맨스': 'ROMANCE',
    '역사': 'HISTORICAL',
    '모험': 'ADVENTURE',
    '스릴러': 'THRILLER',
  },
  '동화': {
    '환상': 'FANTASY',
    '생활': 'SLICE_OF_LIFE',
    '전래': 'TRADITIONAL',
    '정보': 'INFORMATIONAL',
  },
};

const defaultSubType = {
  '뉴스': '정치',
  '소설': '추리/미스테리',
  '동화': '환상',
};

const ArticlePage = () => {
  const navigate = useNavigate();
  const [newsItems, setNewsItems] = useState([]);
  const [language] = useState('KOREAN');
  const [level, setLevel] = useState('');
  const [category, setCategory] = useState('뉴스');   // 기본값: 뉴스
  const [subType, setSubType] = useState(defaultSubType['뉴스']); // 기본 서브타입
  const [orderBy, setOrderBy] = useState('latest');
  const [classification] = useState('NORMAL');

  const handleSolve = (item) => {
    navigate(`/question/${item.news_no}`, {
      state: { article: item }
    });
  };

  const fetchNews = useCallback(async (params) => {
    try {
      return await fetchPassageList(params);
    } catch (err) {
      console.error('지문 목록 불러오기 실패:', err);
      return [];
    }
  }, []);

  const debouncedFetch = useMemo(() => debounce(async (params) => {
    const data = await fetchNews(params);
    setNewsItems(data);
  }, 300), [fetchNews]);

  const fetchData = useCallback(() => {
    const apiLevel = reverseLevelMap[level] || undefined;
    const apiCategory = reverseCategoryMap[category] || 'NEWS';
    const apiType = reverseTypeMap[category]?.[subType] || 'INFORMATIONAL';

    debouncedFetch({
      language,
      classification,
      category: apiCategory,
      type: apiType,
      level: apiLevel,
      orderBy
    });
  }, [debouncedFetch, language, classification, category, subType, level, orderBy]);

  useEffect(() => {
    fetchData();
    return () => debouncedFetch.cancel();
  }, [fetchData, debouncedFetch]);

  // 카테고리 변경 시 서브타입도 기본값으로 리셋
  const handleCategoryChange = (newCategory) => {
    setCategory(newCategory);
    setSubType(defaultSubType[newCategory]); 
  };

  return (
    <div className="page-container">
      <UniversalList
        items={newsItems}
        language={language}
        level={level}
        setLevel={setLevel}
        category={category}
        setCategory={handleCategoryChange}
        subType={subType}
        setSubType={setSubType}
        order_by={orderBy}
        setOrderBy={setOrderBy}
        categoryOptions={NewsCategory}
        subTypeOptions={Object.keys(reverseTypeMap[category])} // 카테고리별 서브타입 표시
        onSolve={handleSolve}
      />
    </div>
  );
};

export default ArticlePage;
