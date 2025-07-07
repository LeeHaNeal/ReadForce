const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

const fetchWithAuth = async (url, options = {}) => {
  const accessToken = localStorage.getItem('token');
  const refreshToken = localStorage.getItem('refresh_token');

  console.log('📢 요청 시작:', url);

  let res = await fetch(url, {
    ...options,
    headers: {
      ...(options.headers || {}),
      Authorization: `Bearer ${accessToken}`,
    },
  });

  // ✅ AccessToken 만료 + RefreshToken 있음
  if (res.status === 401) {
    if (!refreshToken) {
      console.warn('❌ RefreshToken 없음. 로그인 페이지로 이동');
      localStorage.clear();
      await delay(4000); // 4초 대기
      window.location.href = '/login';
      return;
    }

    console.warn('⚠️ AccessToken 만료. RefreshToken으로 재발급 시도 중...');

    const refreshRes = await fetch(
      `/authentication/reissue-refresh-token?refreshToken=${encodeURIComponent(refreshToken)}`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
      }
    );

    if (refreshRes.ok) {
      const data = await refreshRes.json();
      console.log('✅ 새 AccessToken:', data.ACCESS_TOKEN);
      console.log('✅ 새 RefreshToken:', data.REFESH_TOKEN);

      localStorage.setItem('token', data.ACCESS_TOKEN);
      localStorage.setItem('refresh_token', data.REFESH_TOKEN);

      // 재요청
      res = await fetch(url, {
        ...options,
        headers: {
          ...(options.headers || {}),
          Authorization: `Bearer ${data.ACCESS_TOKEN}`,
        },
      });

      if (res.status === 401) {
        console.error('❌ 재발급 후에도 401. 로그아웃 처리');
        localStorage.clear();
        await delay(4000);
        window.location.href = '/login';
        return;
      }
    } else {
      console.error('❌ RefreshToken도 만료. 로그인 페이지로 이동');
      localStorage.clear();
      await delay(4000);
      window.location.href = '/login';
      return;
    }
  }

  return res;
};

export default fetchWithAuth;
