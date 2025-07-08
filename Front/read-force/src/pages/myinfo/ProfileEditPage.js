import React, { useEffect, useState } from 'react';
import axiosInstance from '../../api/axiosInstance';
import kakaoIcon from '../../assets/image/kakao.png';
import naverIcon from '../../assets/image/naver.png';
import googleIcon from '../../assets/image/google.png';
import './EditProfilePage.css';

const ProfileEditPage = () => {
  const [nickname, setNickname] = useState('');
  const [nicknameMessage, setNicknameMessage] = useState('');
  const [isNicknameValid, setIsNicknameValid] = useState(null);
  const [birthday, setBirthday] = useState('');
  const [birthdayMessage, setBirthdayMessage] = useState('');
  const [isBirthdayValid, setIsBirthdayValid] = useState(null);
  const [imageUrl, setImageUrl] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);
  const [showModal, setShowModal] = useState(false);

  useEffect(() => {
    fetchProfileImage();
  }, []);

  const fetchProfileImage = async () => {
    try {
      const res = await axiosInstance.get('/file/get-profile-image', {
        responseType: 'blob',
      });
      const url = URL.createObjectURL(res.data);
      setImageUrl(url);
    } catch (err) {
      console.error('기본 이미지 불러오기 실패:', err);
    }
  };

  const checkNicknameDuplicate = async (nickname) => {
    try {
      const res = await axiosInstance.get(`/member/nickname-check?nickname=${nickname}`);
      return res.status === 200;
    } catch {
      return false;
    }
  };

  const validateNickname = async (value) => {
    setNicknameMessage('');
    const nicknameRegex = /^[a-zA-Z가-힣0-9]{2,12}$/;
    if (!nicknameRegex.test(value)) {
      setNicknameMessage('한글/영문/숫자 조합 2~12자만 사용 가능합니다.');
      setIsNicknameValid(false);
      return;
    }
    const isAvailable = await checkNicknameDuplicate(value);
    setNicknameMessage(
      isAvailable ? '사용 가능한 닉네임입니다.' : '이미 존재하는 닉네임입니다.'
    );
    setIsNicknameValid(isAvailable);
  };

  const validateBirthday = (value) => {
    setBirthdayMessage('');
    const birthdayRegex = /^\d{4}-\d{2}-\d{2}$/;
    if (birthdayRegex.test(value)) {
      setBirthdayMessage('생년월일 입력 완료');
      setIsBirthdayValid(true);
    } else {
      setBirthdayMessage('생년월일 8자리를 입력해주세요 (예: 19971104)');
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

  const handleSubmit = async (e) => {
    e.preventDefault();
    const updates = [];
    const infoPayload = {};

    if (nickname && isNicknameValid) infoPayload.nickname = nickname;
    if (birthday && isBirthdayValid) infoPayload.birthday = birthday;

    if (Object.keys(infoPayload).length > 0) {
      updates.push(axiosInstance.patch('/member/modify-info', infoPayload));
    }

    if (selectedFile) {
      const formData = new FormData();
      formData.append('profileImageFile', selectedFile);
      updates.push(
        axiosInstance.post('/file/upload-profile-image', formData, {
          headers: { 'Content-Type': 'multipart/form-data' },
        })
      );
    }

    try {
      const responses = await Promise.all(updates);
      const modifyResponse = responses.find((res) => res?.data?.NICK_NAME);
      if (modifyResponse) {
        localStorage.setItem('nickname', modifyResponse.data.NICK_NAME);
      }
      alert('회원정보가 수정되었습니다.');
      window.location.href = '/';
    } catch {
      alert('회원정보 수정 실패');
    }
  };

  const handleWithdraw = async () => {
    try {
      await axiosInstance.delete('/member/withdraw-member');
      localStorage.clear();
      alert('탈퇴 완료되었습니다.');
      window.location.href = '/';
    } catch {
      alert('탈퇴 실패');
    }
  };

  const handleImageDelete = async () => {
    try {
      await axiosInstance.delete('/file/delete-profile-image');
      await fetchProfileImage();
      setSelectedFile(null);
    } catch {
      alert('이미지 삭제 실패');
    }
  };

  const openSocialRedirect = async (provider) => {
    try {
      const res = await axiosInstance.post('/auth/get-social-account-link-token');
      const state = res.data.STATE;
      const redirectUri = `http://localhost:8080/oauth2/authorization/${provider}?state=${state}`;
      console.log(state);
      window.location.href = redirectUri;
    } catch (err) {
      alert('SNS 연동 요청 실패');
    }
  };

  return (
    <div className="profile-edit-page">
      <h2>회원정보 수정</h2>
      <form className="edit-form" onSubmit={handleSubmit}>
        <div className="form-group">
          <label>회원 이미지</label>
          <div className="profile-image-box">
            {imageUrl && (
              <img src={imageUrl} alt="프로필 이미지" className="profile-image" />
            )}
            <input
              type="file"
              accept="image/jpeg,image/png,image/gif"
              onChange={(e) => {
                const file = e.target.files[0];
                if (file) {
                  if (file.size > 5 * 1024 * 1024) {
                    alert('이미지 용량은 5MB 이하여야 합니다.');
                    return;
                  }
                  setSelectedFile(file);
                  setImageUrl(URL.createObjectURL(file));
                }
              }}
            />
            <button
              type="button"
              className="remove-image-button"
              onClick={handleImageDelete}
            >
              이미지 삭제
            </button>
          </div>
        </div>

        <div className="form-group">
          <label>닉네임</label>
          <div className="input-with-message">
            <input
              type="text"
              value={nickname}
              onChange={async (e) => {
                const value = e.target.value;
                setNickname(value);
                setNicknameMessage('');
                if (value.length >= 2) {
                  await validateNickname(value);
                } else {
                  setNicknameMessage('2자 이상 입력해주세요');
                  setIsNicknameValid(false);
                }
              }}
            />
            {nicknameMessage && (
              <span className={`validation-message ${isNicknameValid ? 'valid' : 'invalid'}`}>
                {nicknameMessage}
              </span>
            )}
          </div>
        </div>

        <div className="form-group">
          <label>생년월일</label>
          <div className="input-with-message">
            <input
              type="text"
              placeholder="예: 1997-11-04"
              value={birthday}
              onChange={(e) => {
                const value = e.target.value;
                setBirthdayMessage('');
                handleBirthdayChange(value);
              }}
            />
            {birthdayMessage && (
              <span className={`validation-message ${isBirthdayValid ? 'valid' : 'invalid'}`}>
                {birthdayMessage}
              </span>
            )}
          </div>
        </div>

        <div className="form-group">
          <label>SNS 계정 연동</label>
          <div className="social-login">
            <button type="button" className="social-btn" onClick={() => openSocialRedirect('kakao')}>
              <img src={kakaoIcon} alt="카카오" />
            </button>
            <button type="button" className="social-btn" onClick={() => openSocialRedirect('naver')}>
              <img src={naverIcon} alt="네이버" />
            </button>
            <button type="button" className="social-btn" onClick={() => openSocialRedirect('google')}>
              <img src={googleIcon} alt="구글" />
            </button>
          </div>
        </div>

        <div className="button-group">
          <button type="submit">정보 수정</button>
        </div>

        <div className="withdraw-area">
          <button type="button" className="withdraw-button" onClick={() => setShowModal(true)}>
            회원 탈퇴
          </button>
        </div>
      </form>

      {showModal && (
        <div className="modal-overlay">
          <div className="modal-box">
            <p>정말로 탈퇴하시겠습니까?</p>
            <div className="modal-buttons">
              <button className="confirm" onClick={handleWithdraw}>예</button>
              <button className="cancel" onClick={() => setShowModal(false)}>아니요</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProfileEditPage;
