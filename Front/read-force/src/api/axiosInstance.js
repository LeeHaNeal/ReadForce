// import axios from 'axios';

// const api = axios.create({
//   baseURL: '/',
//   headers: {
//     'Content-Type': 'application/json',
//   },
// });


// api.interceptors.request.use(
//   config => {
//     const token = localStorage.getItem('token');
//     if (token && token !== 'null' && token !== 'undefined') {
//       config.headers.Authorization = `Bearer ${token}`;
//     }
//     return config;
//   },
//   error => Promise.reject(error)
// );

// api.interceptors.response.use(
//   response => response,
//   error => {
//     if (error.response?.status === 401) {
//       const ignoredPaths = [
//         '/ranking', 
//       ];
//       const requestUrl = error.config?.url ?? '';
//       const shouldRedirect = !ignoredPaths.some(path => requestUrl.startsWith(path));

//       if (shouldRedirect) {
//         localStorage.clear();
//         window.location.href = '/login';
//       }
//     }
//     return Promise.reject(error);
//   }
// );

// export default api;

// refresh-token 적용 ㅜㅜ개힘드노
import axios from 'axios';

const api = axios.create({
  baseURL: '/',
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 시 Access Token 추가
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token && token !== 'null' && token !== 'undefined') {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => Promise.reject(error)
);

// 401 에러 시 재시도 로직 (리프레시 토큰 기반)
let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, newToken = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(newToken);
    }
  });
  failedQueue = [];
};

api.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && error.response?.status === 400 && !originalRequest._retry) {
      originalRequest._retry = true;
      const refreshToken = localStorage.getItem('refresh_token');

      if (!refreshToken) {
        localStorage.clear();
        window.location.href = '/login';
        return Promise.reject(error);
      }

      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({
            resolve: (token) => {
              originalRequest.headers.Authorization = `Bearer ${token}`;
              resolve(api(originalRequest));
            },
            reject: (err) => reject(err),
          });
        });
      }

      isRefreshing = true;

      try {
        const res = await axios.post(
          '/authentication/reissue-refresh-token',
          `refreshToken=${encodeURIComponent(refreshToken)}`,
          {
            headers: {
              'Content-Type': 'application/x-www-form-urlencoded',
            },
          }
        );

        const { ACCESS_TOKEN, REFRESH_TOKEN } = res.data;

        console.log('✅ 새로 발급된 ACCESS_TOKEN:', ACCESS_TOKEN);
        console.log('✅ 새로 발급된 REFRESH_TOKEN:', REFRESH_TOKEN);
        localStorage.setItem('token', ACCESS_TOKEN);
        localStorage.setItem('refresh_token', REFRESH_TOKEN);

        api.defaults.headers.Authorization = `Bearer ${ACCESS_TOKEN}`;
        originalRequest.headers.Authorization = `Bearer ${ACCESS_TOKEN}`;

        processQueue(null, ACCESS_TOKEN);
        return api(originalRequest);
      } catch (err) {
        processQueue(err, null);
        localStorage.clear();
        window.location.href = '/login';
        return Promise.reject(err);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

export default api;