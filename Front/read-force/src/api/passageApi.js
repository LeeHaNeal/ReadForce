import api from './axiosInstance';
import debounce from 'lodash/debounce';

const orderByMap = {
  latest: 'DESC',
  oldest: 'ASC',
  DESC: 'DESC',
  ASC: 'ASC',
};

export const fetchPassageList = async ({ language, classification, category, type, level, orderBy }) => {
  try {
    let endpoint = '/passage/get-passage-list-by-language-and-category';
    const params = {
      language,
      classification,
      category,
      orderBy: orderByMap[orderBy],
    };

    if (type && level) {
      endpoint = '/passage/get-passage-list-by-language-and-category-and-type-and-level';
      params.type = type;
      params.level = level;
    } else if (type && !level) {
      endpoint = '/passage/get-passage-list-by-language-and-category-and-type';
      params.type = type;
    } else if (!type && level) {
      console.warn('type 없이 level만 있는 경우는 지원되지 않습니다. level은 무시됩니다.');
    }

    const res = await api.get(endpoint, { params });
    return res.data;
  } catch (err) {
    console.error('지문 목록 불러오기 실패:', err);
    return [];
  }
};

export const debouncedFetchPassageList = debounce(async (params, callback) => {
  const data = await fetchPassageList(params);
  callback(data);
}, 300);

export const fetchPassageQuizByNo = async (passage_no) => {
  try {
    const res = await api.get('/passage/get-passage-quiz-object', {
      params: { passage_no },
    });
    return res.data;
  } catch (err) {
    console.error('지문 퀴즈 불러오기 실패:', err);
    return null;
  }
};

export const savePassageQuizResult = async ({ passage_quiz_no, selected_option_index }) => {
  try {
    const res = await api.post('/passage/save-member-solved-passage-quiz', {
      passage_quiz_no,
      selected_option_index,
    });
    return res.data;
  } catch (err) {
    console.error('지문 퀴즈 정답 저장 실패:', err);
    return null;
  }
};
