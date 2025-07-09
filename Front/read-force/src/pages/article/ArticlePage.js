import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import UniversalList from '../../components/universal/UniversalList';
import { fetchPassageList } from '../../api/newsApi';
import debounce from 'lodash/debounce';
import NewsCategory from '../../components/NewsCategory';

// 레벨 매핑
const reverseLevelMap = { '1': 1, '2': 2, '3': 3, '4': 4, '5': 5, '6': 6, '7': 7, '8': 8, '9': 9, '10': 10 };

// 카테고리 매핑
const reverseCategoryMap = { '뉴스': 'NEWS', '소설': 'NOVEL', '동화': 'FAIRY_TALE' };

// 타입 매핑
const reverseTypeMap = {
  '뉴스': {
    '정치': 'POLITICS', '경제': 'ECONOMY', '사회': 'SOCIETY',
    '생활/문화': 'LIFE_AND_CULTURE', 'IT/과학': 'IT_AND_SCIENCE',
    '세계': 'WORLD', '스포츠': 'SPORTS', '연예': 'ENTERTAINMENT',
  },
  '소설': {
    '추리/미스테리': 'MYSTERY', '과학': 'SCIENCE_FICTION',
    '판타지': 'FANTASY', '로맨스': 'ROMANCE', '역사': 'HISTORICAL',
    '모험': 'ADVENTURE', '스릴러': 'THRILLER',
  },
  '동화': {
    '환상': 'FANTASY', '생활': 'SLICE_OF_LIFE', '전래': 'TRADITIONAL', '정보': 'INFORMATIONAL',
  },
};

// 카테고리별 기본 subType 설정 → 여기서 '뉴스' 기본값을 '경제'로 수정
const defaultSubType = { '뉴스': '경제', '소설': '추리/미스테리', '동화': '환상' };

const ArticlePage = () => {
  const navigate = useNavigate();

  // 상태 정의
  const [newsItems, setNewsItems] = useState([]);
  const [language] = useState('KOREAN'); // 고정
  const [level, setLevel] = useState('');
  const [category, setCategory] = useState('뉴스');
  const [subType, setSubType] = useState(defaultSubType['뉴스']); // 기본 '경제'
  const [orderBy, setOrderBy] = useState('latest');
  const [classification] = useState('NORMAL'); // 고정

  // 문제 풀기 버튼 클릭 시 이동
  const handleSolve = (item) => {
    navigate(`/question/${item.news_no}`, { state: { article: item } });
  };

  // passage 리스트 조회 API 호출
  const fetchNews = useCallback(async (params) => {
    try {
      return await fetchPassageList(params);
    } catch (err) {
      console.error('지문 목록 불러오기 실패:', err);
      return [];
    }
  }, []);

  // 호출 최적화 (debounce 적용)
  const debouncedFetch = useMemo(() => debounce(async (params) => {
    const data = await fetchNews(params);
    setNewsItems(data);
  }, 300), [fetchNews]);

  // 실제 데이터 조회 함수
  const fetchData = useCallback(() => {
    const apiLevel = reverseLevelMap[level] || undefined;
    const apiCategory = reverseCategoryMap[category] || 'NEWS';
    const apiType = reverseTypeMap[category]?.[subType] || 'INFORMATIONAL';

    console.log('[passage 요청 파라미터]', { language, classification, category: apiCategory, type: apiType, level: apiLevel, orderBy });

    debouncedFetch({ language, classification, category: apiCategory, type: apiType, level: apiLevel, orderBy });
  }, [debouncedFetch, language, classification, category, subType, level, orderBy]);

  // 초기 렌더링 및 상태 변경 시 조회
  useEffect(() => {
    fetchData();
    return () => debouncedFetch.cancel();
  }, [fetchData, debouncedFetch]);

  // 카테고리 변경 시 subType 초기화
  const handleCategoryChange = (newCategory) => {
    setCategory(newCategory);
    setSubType(defaultSubType[newCategory]); 
  };

  return (
    <div className="page-container">
      <UniversalList
        items={newsItems}
        language={language}
        level={level} setLevel={setLevel}
        category={category} setCategory={handleCategoryChange}
        subType={subType} setSubType={setSubType}
        order_by={orderBy} setOrderBy={setOrderBy}
        categoryOptions={NewsCategory}
        subTypeOptions={Object.keys(reverseTypeMap[category])}
        onSolve={handleSolve}
      />
    </div>
  );
};

export default ArticlePage;
