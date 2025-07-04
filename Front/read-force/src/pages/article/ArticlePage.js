import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import UniversalList from '../../components/universal/UniversalList';
import {
  fetchNewsListByLanguage,
  fetchNewsListByLanguageAndLevel,
  fetchNewsListByLanguageAndLevelAndCategory
} from '../../api/newsApi';
import debounce from 'lodash/debounce';
import NewsCategory from '../../components/NewsCategory';

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
  '정치': 'POLITICS',
  '경제': 'ECONOMY',
  '사회': 'SOCIETY',
  '생활/문화': 'CULTURE',
  'IT/과학': 'SCIENCE',
  '기타': 'ETC',
};

const ArticlePage = () => {
  const navigate = useNavigate();
  const [newsItems, setNewsItems] = useState([]);
  const [language] = useState('KOREAN');
  const [level, setLevel] = useState('');
  const [category, setCategory] = useState('');
  const [order_by, setOrderBy] = useState('latest');

  const handleSolve = (item) => {
    navigate(`/question/${item.news_no}`, {
      state: { article: item }
    });
  };

  const fetchNews = useCallback(async (params) => {
    try {
      if (!params.level && !params.category) {
        return await fetchNewsListByLanguage({ language: params.language, order_by: params.order_by });
      } else if (params.level && !params.category) {
        return await fetchNewsListByLanguageAndLevel({ language: params.language, level: params.level, order_by: params.order_by });
      } else {
        return await fetchNewsListByLanguageAndLevelAndCategory({ language: params.language, level: params.level, category: params.category, order_by: params.order_by });
      }
    } catch (err) {
      console.error('뉴스 목록 불러오기 실패:', err);
      return [];
    }
  }, []);

  const debouncedFetch = useMemo(() => debounce(async (params) => {
    const data = await fetchNews(params);
    setNewsItems(data);
  }, 300), [fetchNews]);

  const fetchData = useCallback(() => {
    const apiLevel = reverseLevelMap[level] || '';
    const apiCategory = reverseCategoryMap[category] || '';
    debouncedFetch({ language, level: apiLevel, category: apiCategory, order_by });
  }, [debouncedFetch, language, level, category, order_by]);

  useEffect(() => {
    fetchData();
    return () => debouncedFetch.cancel();
  }, [fetchData, debouncedFetch]);

  return (
    <div className="page-container">
      <UniversalList
        items={newsItems}
        language={language}
        level={level}
        setLevel={setLevel}
        category={category}
        setCategory={setCategory}
        order_by={order_by}
        setOrderBy={setOrderBy}
        categoryOptions={NewsCategory}
        onSolve={handleSolve}
      />
    </div>
  );
};

export default ArticlePage;
