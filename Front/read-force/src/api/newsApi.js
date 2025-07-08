import api from './axiosInstance';
import debounce from 'lodash/debounce';

// ENUM 변환 매핑
const categoryMap = {
  '정치': 'POLITICS',
  '경제': 'ECONOMY',
  '사회': 'SOCIETY',
  '생활/문화': 'CULTURE',
  'IT/과학': 'SCIENCE',
  '기타': 'ETC',
};

const orderByMap = {
  'latest': 'DESC',
  'oldest': 'ASC',
  'DESC': 'DESC',
  'ASC': 'ASC',
};

// passage API 단일화
export const fetchPassageList = async ({ language, classification, category, type, level, orderBy }) => {
  try {
    let url = '/passage/get-passage-list-by-language-and-category';

    if (type && !level) {
      url = '/passage/get-passage-list-by-language-and-category-and-type';
    } else if (type && level) {
      url = '/passage/get-passage-list-by-language-and-category-and-type-and-level';
    }

    const res = await api.get(url, {
      params: {
        language,
        classification,
        category: categoryMap[category] || category,
        type,
        level,
        orderBy: orderByMap[orderBy],
      },
    });
    return res.data;
  } catch (err) {
    console.error('지문 목록 불러오기 실패:', err);
    return [];
  }
};

export const debouncedFetchPassageList = debounce(fetchPassageList, 300);
