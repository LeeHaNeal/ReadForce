import React, { useEffect, useState } from 'react';
import './MyPage.css';
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import axiosInstance from '../../api/axiosInstance';
import { useNavigate } from 'react-router-dom';

const MyPage = () => {
  const [nickname, setNickname] = useState('');
  const [profileImageUrl, setProfileImageUrl] = useState(null);
  const [attendanceDates, setAttendanceDates] = useState([]);
  const [summary, setSummary] = useState({ total: 0, monthlyRate: 0, streak: 0 });
  const [correctRate, setCorrectRate] = useState(0);
  const [todaySolvedCount, setTodaySolvedCount] = useState(0);
  const [totalLearning, setTotalLearning] = useState([]);
  const [todayLearning, setTodayLearning] = useState([]);
  const [todayIncorrect, setTodayIncorrect] = useState([]);
  const [favoritLearning, setFavoritLearning] = useState([]);

  const isLoggedIn = !!localStorage.getItem('token');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProfileImage = async () => {
      try {
        const res = await axiosInstance.get('/file/get-profile-image', {
          responseType: 'blob',
        });
        const blob = res.data;
        setProfileImageUrl(URL.createObjectURL(blob));
      } catch (e) {
        console.error('프로필 이미지 불러오기 실패:', e);
      }
    };
    if (isLoggedIn) fetchProfileImage();
  }, [isLoggedIn]);

  useEffect(() => {
    if (isLoggedIn) {
      setNickname(localStorage.getItem('nickname') || '사용자');
    }
  }, [isLoggedIn]);

  useEffect(() => {
    axiosInstance
      .get('/attendance/get-attendance-date-list')
      .then((res) => {
        const data = res.data;
        const dates = Array.isArray(data) ? data.map((d) => new Date(d)) : [];
        setAttendanceDates(dates);

        const today = new Date();
        const thisMonthDates = dates.filter(
          (d) => d.getFullYear() === today.getFullYear() && d.getMonth() === today.getMonth()
        );
        const monthlyRate = Math.round((thisMonthDates.length / today.getDate()) * 100);

        const getStreak = (dates) => {
          const sorted = [...dates]
            .map((d) => new Date(d.getFullYear(), d.getMonth(), d.getDate()))
            .sort((a, b) => b - a);
          let streak = 0;
          let current = new Date();
          for (const date of sorted) {
            if (date.toDateString() === current.toDateString()) {
              streak++;
              current.setDate(current.getDate() - 1);
            } else if (
              date.toDateString() ===
              new Date(current.getFullYear(), current.getMonth(), current.getDate() - 1).toDateString()
            ) {
              streak++;
              current.setDate(current.getDate() - 1);
            } else break;
          }
          return streak;
        };

        setSummary({
          total: dates.length,
          monthlyRate,
          streak: getStreak(dates),
        });
      })
      .catch((e) => console.error('출석 로딩 실패:', e));
  }, []);

  useEffect(() => {
    axiosInstance
      .get('/result/get-overall-correct-answer-rate')
      .then((res) => {
        const rate = res.data?.OVERALL_CORRECT_ANSWER_RATE;
        if (typeof rate === 'number') setCorrectRate(rate);
      })
      .catch((e) => console.error('정답률 로딩 실패:', e));
  }, []);

  useEffect(() => {
    axiosInstance
      .get('/result/get-today-solved-question-count')
      .then((res) => {
        const count = res.data?.TODAY_SOLVED_QUESTION_COUNT;
        if (typeof count === 'number') setTodaySolvedCount(count);
      })
      .catch((e) => console.error('오늘 푼 문제 로딩 실패:', e));
  }, []);

  useEffect(() => {
    const fetchLearningData = async () => {
      try {
        const [total, today, todayWrong, fav] = await Promise.all([
          axiosInstance.get('/learning/get-total-learning').then((res) => res.data),
          axiosInstance.get('/learning/get-today-learning').then((res) => res.data),
          axiosInstance.get('/learning/get-today-incorrect-learning').then((res) => res.data),
          axiosInstance.get('/learning/get-favorit-learning').then((res) => res.data),
        ]);

        setTotalLearning(total);
        setTodayLearning(today);
        setTodayIncorrect(todayWrong);
        setFavoritLearning(fav);
      } catch (e) {
        console.error('학습노트 데이터 로딩 실패:', e);
      }
    };

    fetchLearningData();
  }, []);

  const getBadgeLabel = (rate) => {
    if (rate >= 100) return '초고수';
    if (rate >= 75) return '고급';
    if (rate >= 50) return '중급';
    if (rate >= 25) return '초심자';
    return '입문자';
  };

  return (
    <div className="mypage-container">
      <div className="top-section">
        <div className="left-top">
          <img src={profileImageUrl} alt="프로필" className="profile-img" />
          <h3 className="nickname">{nickname} 님</h3>
          <span className="badge">{getBadgeLabel(correctRate)}</span>
        </div>

        <div className="calendar-section">
          <h4>출석 현황</h4>
          <div className="calendar-summary">
            <div className="summary-row">
              <div className="summary-title">총 출석일</div>
              <div className="summary-title">이번 달 출석률</div>
              <div className="summary-title">연속 출석</div>
            </div>
            <div className="summary-row">
              <div className="summary-value">{summary.total}일</div>
              <div className="summary-value">{summary.monthlyRate}%</div>
              <div className="summary-value">{summary.streak}일</div>
            </div>
          </div>
          <div className="calendar-wrapper">
            <Calendar
              calendarType="gregory"
              next2Label={null}
              prev2Label={null}
              minDetail="month"
              maxDetail="month"
              tileClassName={({ date, view }) => {
                if (view === 'month') {
                  const isAttendance = attendanceDates.some(
                    (att) => att.toDateString() === date.toDateString()
                  );
                  if (isAttendance) return 'attended-day';
                  if (date.getDay() === 0) return 'sunday';
                  if (date.getDay() === 6) return 'saturday';
                }
                return null;
              }}
            />
          </div>
        </div>
      </div>

      <div className="bottom-section">
        <div className="learning-status-box">
          <h4>학습 현황</h4>
          <div className="summary-cards">
            <div className="summary-card">
              <div className="summary-title">전체 정답률</div>
              <div className="summary-value">{correctRate}%</div>
            </div>
            <div className="summary-card">
              <div className="summary-title">오늘 푼 문제</div>
              <div className="summary-value">{todaySolvedCount}문제</div>
            </div>
            <div className="summary-card">
              <div className="summary-title">연속 학습일</div>
              <div className="summary-value">{summary.streak}일</div>
            </div>
          </div>
        </div>

        <div className="learning-note-box">
          <h4>학습 노트</h4>
          <ul>
            <li>전체 푼 문제: {totalLearning.length}문제</li>
            <li>오늘의 푼 문제: {todayLearning.length}문제</li>
            <li>오늘의 틀린 문제: {todayIncorrect.length}문제</li>
            <li>즐겨찾기 문제: {favoritLearning.length}문제</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default MyPage;
