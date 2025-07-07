import React, { useEffect, useState } from 'react';
import './MyPage.css';
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import EditProfileModal from './EditProfileModal';
import fetchWithAuth from '../../utils/fetchWithAuth';
import { useNavigate } from 'react-router-dom';

const MyPage = () => {
  const [nickname, setNickname] = useState('');
  const isLoggedIn = !!localStorage.getItem("token");
  // const [showModal, setShowModal] = useState(false);
  const [attendanceDates, setAttendanceDates] = useState([]);
  const [profileImageUrl, setProfileImageUrl] = useState(null);
  const [wrongQuestions, setWrongQuestions] = useState([]);
  const [summary, setSummary] = useState({ total: 0, monthlyRate: 0, streak: 0 });
  // const [recentSolved, setRecentSolved] = useState([]);
  const [correctRate, setCorrectRate] = useState(0);
  const [todaySolvedCount, setTodaySolvedCount] = useState(0);

  const navigate = useNavigate();

  useEffect(() => {
    const fetchProfileImage = async () => {
      try {
        const response = await fetch('/member/get-profile-image', {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
        });
        if (!response.ok) throw new Error('이미지 로딩 실패');
        const blob = await response.blob();
        const url = URL.createObjectURL(blob);
        setProfileImageUrl(url);
      } catch (error) {
        console.error('프로필 이미지 불러오기 실패:', error);
      }
    };
    if (isLoggedIn) fetchProfileImage();
  }, [isLoggedIn]);

  useEffect(() => {
    fetchWithAuth('/attendance/get-attendance-date-list')
      .then(res => res.json())
      .then(data => {
        const dates = Array.isArray(data) ? data.map(d => new Date(d)) : [];
        setAttendanceDates(dates);

        const today = new Date();
        const currentYear = today.getFullYear();
        const currentMonth = today.getMonth();
        const thisMonthDates = dates.filter(d => d.getFullYear() === currentYear && d.getMonth() === currentMonth);
        const monthlyRate = Math.round((thisMonthDates.length / today.getDate()) * 100);

        const getStreak = (dates) => {
          const sorted = [...dates].map(d => new Date(d.getFullYear(), d.getMonth(), d.getDate())).sort((a, b) => b - a);
          let streak = 0;
          let current = new Date();
          for (const date of sorted) {
            if (date.toDateString() === current.toDateString()) {
              streak++;
              current.setDate(current.getDate() - 1);
            } else if (date.toDateString() === new Date(current.getFullYear(), current.getMonth(), current.getDate() - 1).toDateString()) {
              streak++;
              current.setDate(current.getDate() - 1);
            } else {
              break;
            }
          }
          return streak;
        };

        setSummary({
          total: dates.length,
          monthlyRate,
          streak: getStreak(dates),
        });
      });
  }, []);

  useEffect(() => {
    if (isLoggedIn) {
      const storedNickname = localStorage.getItem("nickname");
      setNickname(storedNickname || "사용자");
    }
  }, [isLoggedIn]);

  useEffect(() => {
    fetchWithAuth('/result/get-overall-correct-answer-rate')
      .then(res => res.json())
      .then(data => {
        const rate = data?.OVERALL_CORRECT_ANSWER_RATE;
        if (typeof rate === 'number') {
          setCorrectRate(rate);
        }
      })
      .catch(err => {
        console.error('전체 정답률 불러오기 실패:', err);
      });
  }, []);

  useEffect(() => {
    fetchWithAuth('/result/get-today-solved-question-count')
      .then(res => res.json())
      .then(data => {
        const count = data?.TODAY_SOLVED_QUESTION_COUNT;
        if (typeof count === 'number') {
          setTodaySolvedCount(count);
        }
      })
      .catch(err => {
        console.error('오늘 푼 문제 수 불러오기 실패:', err);
      });
  }, []);
  useEffect(() => {
    fetchWithAuth('/member/get-member-incorrect-quiz-list')
      .then(res => res.json())
      .then(data => {
        if (Array.isArray(data)) {
          setWrongQuestions(data);
        }
      })
      .catch(err => {
        console.error("틀린 문제 불러오기 실패:", err);
      });
  }, []);

  // useEffect(() => {
  //   fetchWithAuth('/member/get-member-solved-quiz-list-10')
  //     .then(res => res.json())
  //     .then(data => {
  //       if (Array.isArray(data)) {
  //         setRecentSolved(data);
  //       }
  //     })
  //     .catch(err => {
  //       console.error("최근 푼 문제 불러오기 실패:", err);
  //     });
  // }, []);

  const handleRetry = (quiz) => {
    navigate(`/question/${quiz.quiz_no}`, { state: { article: { news_no: quiz.quiz_no } } });
  };

  useEffect(() => {
    fetchWithAuth('/quiz/get-correct-rate')
      .then(res => res.json())
      .then(data => {
        if (typeof data === 'number') {
          setCorrectRate(data);
        }
      })
      .catch(err => {
        console.error('정답률 불러오기 실패:', err);
      });
  }, []);

  const getBadgeLabel = (rate) => {
    if (rate >= 100) return '초고수';
    if (rate >= 75) return '고급';
    if (rate >= 50) return '중급';
    if (rate >= 25) return '초심자';
    return '입문자';
  };

  const badgeLabel = getBadgeLabel(correctRate);

  return (
    <div className="mypage-container">
      <div className="top-section">
        <div className="left-top">
          <img src={profileImageUrl} alt="프로필" className="profile-img" />
          <h3 className="nickname">{nickname} 님</h3>
          <span className="badge">{badgeLabel}</span>
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
                  const isAttendance = attendanceDates.some(att => att.toDateString() === date.toDateString());
                  const day = date.getDay();
                  if (isAttendance) return 'attended-day';
                  if (day === 0) return 'sunday';
                  if (day === 6) return 'saturday';
                }
                return null;
              }}
            />
          </div>
        </div>
      </div>

      {/* <div className="history-section">
        <h4>전체 정답률</h4>
        <div className="overall-correct-rate">
          <span className="rate-value">{correctRate}%</span>
          <span className="rate-label">{getBadgeLabel(correctRate)}</span>
        </div>
      </div> */}

      <div className="summary-section">
        <div className="summary-card">
          <div className="summary-title">전체 정답률</div>
          <div className="summary-value">{correctRate}%</div>
        </div>
        <div className="summary-card">
          <div className="summary-title">오늘 푼 문제</div>
          <div className="summary-value">{todaySolvedCount}문제</div>
        </div>
      </div>
    </div>
  );
};

export default MyPage;
