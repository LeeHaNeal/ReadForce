// import api from './axiosInstance';
// import debounce from 'lodash/debounce';

// // ENUM 변환 매핑
// const levelMap = {
//   '초급': 'BEGINNER',
//   '중급': 'INTERMEDIATE',
//   '고급': 'ADVANCED',
// };

// const categoryMap = {
//   '정치': 'POLITICS',
//   '경제': 'ECONOMY',
//   '사회': 'SOCIETY',
//   '생활/문화': 'CULTURE',
//   'IT/과학': 'SCIENCE',
//   '기타': 'ETC',
// };

// const orderByMap = {
//   'latest': 'DESC',
//   'oldest': 'ASC',
//   'DESC': 'DESC',
//   'ASC': 'ASC',
// };

// // 1. 언어만 있을 때
// export const fetchNewsListByLanguage = async ({ language, orderBy }) => {
//   try {
//     const res = await api.get('/passage/get-news-list-by-language', {
//       params: {
//         language,
//         orderBy: orderByMap[orderBy],
//       },
//     });
//     return res.data;
//   } catch (err) {
//     console.error('뉴스 전체 목록 불러오기 실패:', err);
//     return [];
//   }
// };

// export const debouncedFetchNewsListByLanguage = debounce(fetchNewsListByLanguage, 300);

// // 2. 언어 + 난이도
// export const fetchNewsListByLanguageAndLevel = async ({ language, level, orderBy }) => {
//   try {
//     const res = await api.get('/passage/get-news-list-by-language-and-level', {
//       params: {
//         language,
//         level: levelMap[level] || level,
//         orderBy: orderByMap[orderBy],
//       },
//     });
//     return res.data;
//   } catch (err) {
//     console.error('뉴스 난이도 목록 불러오기 실패:', err);
//     return [];
//   }
// };

// export const debouncedFetchNewsListByLanguageAndLevel = debounce(fetchNewsListByLanguageAndLevel, 300);

// // 3. 언어 + 난이도 + 카테고리
// export const fetchNewsListByLanguageAndLevelAndCategory = async ({ language, level, category, orderBy }) => {
//   try {
//     const res = await api.get('/passage/get-news-list-by-language-and-level-and-category', {
//       params: {
//         language,
//         level: levelMap[level] || level,
//         category: categoryMap[category] || category,
//         orderBy: orderByMap[orderBy],
//       },
//     });
//     return res.data;
//   } catch (err) {
//     console.error('뉴스 카테고리 목록 불러오기 실패:', err);
//     return [];
//   }
// };

// export const fetchNewsQuizByNewsNo = async (news_no) => {
//   try {
//     const res = await api.get('/passage/get-news-quiz-object', {
//       params: { news_no },
//     });
//     return res.data;
//   } catch (err) {
//     console.error('뉴스 퀴즈 불러오기 실패:', err);
//     return null;
//   }
// };
// export const saveNewsQuizResult = async ({ news_quiz_no, selected_option_index }) => {
//   try {
//     const res = await api.post('/passage/save-member-solved-news-quiz', {
//       news_quiz_no,
//       selected_option_index,
//     });
//     return res.data;
//   } catch (err) {
//     console.error('뉴스 퀴즈 정답 저장 실패:', err);
//     return null;
//   }
// };

// export const debouncedFetchNewsListByLanguageAndLevelAndCategory = debounce(fetchNewsListByLanguageAndLevelAndCategory, 300);
