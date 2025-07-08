// import api from './axiosInstance';
// import debounce from 'lodash/debounce';

// const levelMap = {
//   '초급': 'BEGINNER',
//   '중급': 'INTERMEDIATE',
//   '고급': 'ADVANCED',
// };

// const categoryMap = {
//   '추리': 'MYSTERY',
//   '역사': 'HISTORY',
//   '고전': 'CLASSIC',
//   '근대': 'MODERN',
//   '동화': 'CHILDREN',
//   '기타': 'ETC',
// };

// const orderByMap = {
//   'latest': 'DESC',
//   'oldest': 'ASC',
//   'DESC': 'DESC',
//   'ASC': 'ASC',
// };

// export const fetchLiteratureParagraphList = async ({
//   type,          
//   level = '',
//   category = '',
//   orderBy = 'DESC',
// }) => {
//   try {
//     let url = '';
//     let params = {
//       type,
//       orderBy: orderByMap[orderBy],
//     };

//     if (!level && !category) {
//       url = '/passage/get-literature-paragraph-list-by-type';
//     } else if (level && !category) {
//       url = '/passage/get-literature-paragraph-list-by-type-and-level';
//       params.level = levelMap[level] || level;
//     } else if (!level && category) {
//       url = '/passage/get-literature-paragraph-list-by-type-and-category';
//       params.category = categoryMap[category] || category;
//     } else if (level && category) {
//       url = '/passage/get-literature-paragraph-list-by-type-and-level-and-category';
//       params.level = levelMap[level] || level;
//       params.category = categoryMap[category] || category;
//     }

//     const res = await api.get(url, { params });
//     return res.data;
//   } catch (err) {
//     console.error('문학 목록 불러오기 실패:', err);
//     return [];
//   }
// };


// export const debouncedFetchLiteratureParagraphList = debounce(fetchLiteratureParagraphList, 300);
