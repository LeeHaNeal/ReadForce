import React, { useState } from 'react';
import { useLocation } from 'react-router-dom';
import './resetpassword.css';

export default function ResetPassword() {
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [birthday, setBirthday] = useState('');
  const [birthdayMessage, setBirthdayMessage] = useState('');
  const [isBirthdayValid, setIsBirthdayValid] = useState(null);
  const location = useLocation();

  const queryParams = new URLSearchParams(location.search);
  const token = queryParams.get('token');

  const validateBirthday = (value) => {
    const birthdayRegex = /^\d{4}-\d{2}-\d{2}$/;
    if (!birthdayRegex.test(value)) {
      setBirthdayMessage('생년월일 8자리를 입력해주세요 (예: 19971104)');
      setIsBirthdayValid(false);
      return;
    }

    const [year, month, day] = value.split('-').map(Number);
    const date = new Date(year, month - 1, day);

    if (
      date.getFullYear() === year &&
      date.getMonth() === month - 1 &&
      date.getDate() === day
    ) {
      setBirthdayMessage('생년월일 입력 완료');
      setIsBirthdayValid(true);
    } else {
      setBirthdayMessage('존재하지 않는 날짜입니다. 다시 확인해주세요.');
      setIsBirthdayValid(false);
    }
  };

  const handleBirthdayChange = (value) => {
    const numeric = value.replace(/\D/g, '').slice(0, 8);
    let formatted = numeric;
    if (numeric.length >= 5) {
      formatted = `${numeric.slice(0, 4)}-${numeric.slice(4, 6)}`;
      if (numeric.length >= 7) {
        formatted += `-${numeric.slice(6, 8)}`;
      }
    }
    setBirthday(formatted);
    validateBirthday(formatted);
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();

    if (!token) {
      alert('유효하지 않은 접근입니다.');
      return;
    }

    if (!isBirthdayValid) {
      alert('생년월일 형식을 확인해주세요 (예: 1997-11-04)');
      return;
    }

    if (newPassword !== confirmPassword) {
      alert('비밀번호가 서로 다릅니다.');
      return;
    }

    try {
      const response = await fetch('/member/password-reset-from-link', {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          temporalToken: token,
          newPassword: newPassword,
          birthday: birthday,
        }),
      });

      if (response.ok) {
        alert('비밀번호가 성공적으로 재설정되었습니다.');
        window.location.href = '/login';
      } else {
        const data = await response.json();
        alert(`재설정 실패: ${data.message || '오류 발생'}`);
      }
    } catch (error) {
      console.error('비밀번호 재설정 에러:', error);
      alert('서버 오류로 비밀번호를 재설정할 수 없습니다.');
    }
  };

  return (
    <div className="page-container">
      <div className="reset-password-wrapper">
        <h2 className="reset-password-title">비밀번호 재설정</h2>
        <form className="reset-password-form" onSubmit={handleResetPassword}>
          <div className="form-group">
            <label>생년월일</label>
            <div className="input-with-message">
              <input
                type="text"
                placeholder="예: 19971104"
                value={birthday}
                onChange={(e) => handleBirthdayChange(e.target.value)}
                required
              />
              <span
                className="validation-message"
                style={{
                  color:
                    isBirthdayValid === null
                      ? 'inherit'
                      : isBirthdayValid
                      ? 'green'
                      : 'red',
                  fontSize: '0.85rem',
                }}
              >
                {birthdayMessage}
              </span>
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="newPassword">새 비밀번호</label>
            <input
              id="newPassword"
              type="password"
              value={newPassword}
              placeholder="새 비밀번호"
              onChange={(e) => setNewPassword(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">비밀번호 확인</label>
            <input
              id="confirmPassword"
              type="password"
              value={confirmPassword}
              placeholder="비밀번호 확인"
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
            />
          </div>

          <button type="submit" className="reset-btn">
            비밀번호 재설정
          </button>
        </form>
      </div>
    </div>
  );
}
