import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance';

const AdminPage = () => {
    const navigate = useNavigate();
    const [users, setUsers] = useState([]);

    useEffect(() => {
        const nickname = localStorage.getItem("nickname");
        if (nickname !== "관리자") {
            alert("접근 권한이 없습니다.");
            navigate("/");
        }
    }, []);

    // 모든 회원정보 불러오기
    const fetchUsers = async () => {
        try {
            const res = await axiosInstance.get("/administrator/member/get-all-member-list");
            setUsers(res.data);
        } catch (error) {
            console.error("회원 목록 불러오기 실패", error);
            if (error.response?.status === 403) {
                alert("관리자 권한이 없습니다.");
            } else if (error.response?.status === 401) {
                alert("로그인 정보가 만료되었습니다. 다시 로그인 해주세요.");
            }
        }
    };

    useEffect(() => {
        fetchUsers();
    }, []);

    // 계정 상태 변경
    const handleChangeStatus = async (email, newStatus) => {
        try {
            const res = await axiosInstance.patch("/administrator/member/modify", { email, status: newStatus });

            updateUserStatus(email, newStatus);
        } catch (err) {
            console.error(err);
            alert("계정 상태 변경 실패");
        }
    };

    // 회원 삭제
    const handleDelete = async (email) => {
        if (!window.confirm("정말로 이 회원을 삭제하시겠습니까?")) return;
        try {
            const res = await axiosInstance.delete(`/administrator/member/delete`, {
                params: { email },
            });

            alert("회원이 삭제되었습니다.");
            setUsers((prev) => prev.filter((user) => user.email !== email));
        } catch (err) {
            console.error(err);
            alert("계정 삭제 실패");
        }
    };

    const updateUserStatus = (email, newStatus) => {
        setUsers((prev) =>
            prev.map((user) =>
                user.email === email ? { ...user, status: newStatus } : user
            )
        );
    };

    return (
        <div style={{ padding: "24px" }}>
            <span style={ADMIN_BUTTONS_LIST}>
                <button style={ADMIN_BUTTONS} onClick={() => navigate('/adminpage/adminpassage')}>문제 관리</button>
                <button style={ADMIN_BUTTONS} onClick={() => navigate("/adminpage/average-stat")}>평균 통계</button>
                <button style={ADMIN_BUTTONS} onClick={() => navigate("/adminpage/category-edit")}>카테고리 편집</button>
                <button style={ADMIN_BUTTONS} onClick={() => navigate("/adminpage/classification-edit")}>분류 편집</button>
                <button style={ADMIN_BUTTONS} onClick={() => navigate("/adminpage/language")}>언어 편집</button>
                <button style={ADMIN_BUTTONS} onClick={() => navigate("/adminpage/level")}>난이도 편집</button>
            </span>
            <div style={ADMIN_TITLE}>
                <div>
                    <h2>회원 관리</h2>
                </div>
                <div>
                    <button style={ADMIN_AI_BUTTONS}>맴버 추가</button>
                </div>
            </div>
            <table style={{ width: "100%", borderCollapse: "collapse", marginTop: "16px" }}>
                <thead>
                    <tr>
                        <th style={thStyle}>닉네임</th>
                        <th style={thStyle}>이메일</th>
                        <th style={thStyle}>생일</th>
                        <th style={thStyle}>가입일</th>
                        <th style={thStyle}>수정일</th>
                        <th style={thStyle}>탈퇴일</th>
                        <th style={thStyle}>소셜</th>
                        <th style={thStyle}>권한</th>
                        <th style={thStyle}>상태</th>
                        <th style={thStyle}>관리</th>
                    </tr>
                </thead>
                <tbody>
                    {users.map((user) => (
                        <tr key={user.email}>
                            <td style={tdStyle}>{user.nickname}</td>
                            <td
                                style={{
                                    cursor: "pointer",
                                    color: "blue",
                                    border: "1px solid #ddd",
                                    padding: "8px",
                                }}
                                onClick={() => navigate(`/adminpage/adminuserinfo/${user.email}`)}
                            >
                                {user.email}
                            </td>
                            <td style={tdStyle}>{user.birthday || "-"}</td>
                            <td style={tdStyle}>{new Date(user.createdAt).toLocaleDateString()}</td>
                            <td style={tdStyle}>{user.lastModifiedAt ? new Date(user.lastModifiedAt).toLocaleDateString() : "-"}</td>
                            <td style={tdStyle}>{user.withdrawAt ? new Date(user.withdrawAt).toLocaleDateString() : "-"}</td>
                            <td style={tdStyle}>{user.socialProvider || "-"}</td>
                            <td style={tdStyle}>{user.role}</td>
                            <td style={{
                                color: user.status === "ACTIVE" ? "green" : "red", border: "1px solid #ddd",
                                padding: "8px",
                            }}>
                                {user.status === "ACTIVE" ? "활성화" : "비활성화"}
                            </td>
                            <td style={tdStyle}>
                                {user.nickname !== "관리자" && (
                                    <>
                                        {user.status === "PENDING_DELETION" ? (
                                            <>
                                                <button onClick={() => handleChangeStatus(user.email, "ACTIVE")} style={{ color: "green" }}>
                                                    계정 활성화
                                                </button>
                                                <button onClick={() => handleDelete(user.email)} style={{ color: "gray", marginLeft: "8px" }}>
                                                    계정 삭제
                                                </button>
                                            </>
                                        ) : (
                                            <button onClick={() => handleChangeStatus(user.email, "PENDING_DELETION")} style={{ color: "red" }}>
                                                계정 비활성화
                                            </button>
                                        )}
                                    </>
                                )}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

const ADMIN_BUTTONS_LIST = {
    display: "flex",
    gap: "8px"
}

const ADMIN_BUTTONS = {
    marginBottom: "16px",
    padding: "8px 16px",
    backgroundColor: "#007BFF",
    color: "white",
    border: "none",
    borderRadius: "4px",
    cursor: "pointer"
};

const thStyle = {
    border: "1px solid #ccc",
    padding: "8px",
    backgroundColor: "#f2f2f2",
    textAlign: "left",
};

const tdStyle = {
    border: "1px solid #ddd",
    padding: "8px",
};

const ADMIN_TITLE = {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "16px"
};

const ADMIN_AI_BUTTONS = {
    marginBottom: "16px",
    padding: "8px 16px",
    backgroundColor: "#007BFF",
    color: "white",
    border: "none",
    borderRadius: "4px",
    cursor: "pointer"
};

export default AdminPage;