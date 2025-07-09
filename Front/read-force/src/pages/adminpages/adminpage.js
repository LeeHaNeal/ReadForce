import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance';
// import Flex from 'react-calendar/dist/Flex.js';

const AdminPage = () => {
    const navigate = useNavigate();
    const [users, setUsers] = useState([]);
    const [loadingAI, setLoadingAI] = useState(false);
    const [aiMessage, setAiMessage] = useState('');
    const [loadingPassage, setLoadingPassage] = useState(false);
    const [loadingQuestion, setLoadingQuestion] = useState(false);

    const [showPassageModal, setShowPassageModal] = useState(false);
    const [language, setLanguage] = useState("KOREAN");
    const [level, setLevel] = useState(1);
    const [category, setCategory] = useState("NEWS");
    const [type, setType] = useState("ECONOMY");
    const [classification, setClassification] = useState("NORMAL");

    const [loadingTestPassage, setLoadingTestPassage] = useState(false);
    const [loadingTestQuestion, setLoadingTestQuestion] = useState(false);

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
            const res = await axiosInstance.get("/admin/get-all-member-list");
            if (!res.ok) throw new Error("권한 없음 또는 토큰 문제");

            const data = await res.data;
            setUsers(data);
        } catch (error) {
            console.error("회원 목록 불러오기 실패", error);
        }
    };

    useEffect(() => {
        fetchUsers();
    }, []);

    const handleChangeStatus = async (email, newStatus) => {
        try {
            const res = await axiosInstance.patch("/admin/modify-info", { email, status: newStatus });

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
            const res = await axiosInstance.delete(`/admin/delete-member-by-email?email=${email}`);

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

    const handleGenerateTestPassage = async () => {
        setLoadingTestPassage(true);
        try {
            const res = await axiosInstance.post("/ai/generate-test-passage?language=KOREAN");
            const data = res.data;
            alert("✅ " + data.message);
        } catch (err) {
            console.error(err);
            alert("❌ 지문 생성 중 오류가 발생했습니다.");
        } finally {
            setLoadingTestPassage(false);
        }
    };

    const handleGenerateTestQuestion = async () => {
        setLoadingTestQuestion(true);
        try {
            const res = await axiosInstance.post("/ai/generate-test-question?language=KOREAN");
            const data = await res.data;
            alert("✅ " + data.message);
        } catch (err) {
            console.error(err);
            alert("❌ 문제 생성 중 오류가 발생했습니다.");
        } finally {
            setLoadingTestQuestion(false);
        }
    };

    const handleGeneratePassageWithParams = async () => {
        setLoadingPassage(true);
        try {
            const res = await axiosInstance.post("/ai/generate-passage", {
                    language,
                    level,
                    category,
                    type,
                    classification,         
            });
            const data = await res.data;
            alert("✅ " + data.message);
            setShowPassageModal(false);
        } catch (err) {
            console.error(err);
            alert("❌ 지문 생성 중 오류가 발생했습니다.");
        } finally {
            setLoadingPassage(false);
        }
    };

    // AI 문제 생성
    const handleGenerateQuestion = async () => {
        setLoadingQuestion(true);
        try {
            const res = await axiosInstance.post("/ai/generate-question");
            const data = await res.data;
            alert("✅ " + data.message);
        } catch (err) {
            console.error(err);
            alert("❌ 문제 생성 중 오류가 발생했습니다.");
        } finally {
            setLoadingQuestion(false);
        }
    };

    return (
        <>
            {showPassageModal && (
                <div style={{
                    position: "fixed", top: 0, left: 0, width: "100%", height: "100%",
                    backgroundColor: "rgba(0,0,0,0.5)", display: "flex", justifyContent: "center", alignItems: "center"
                }}>
                    <div style={{
                        backgroundColor: "white", padding: "24px", borderRadius: "8px", width: "400px"
                    }}>
                        <h3>AI 지문 생성 옵션</h3>

                        <label>언어:</label>
                        <select value={language} onChange={(e) => setLanguage(e.target.value)}>
                            <option value="KOREAN">KOREAN</option>
                            <option value="ENGLISH">ENGLISH</option>
                            <option value="JAPANESE">JAPANESE</option>

                        </select>

                        <br /><label>난이도:</label>
                        <select value={level} onChange={(e) => setLevel(parseInt(e.target.value))}>
                            <option value={1}>1</option>
                            <option value={2}>2</option>
                            <option value={3}>3</option>
                        </select>

                        <br /><label>카테고리:</label>
                        <select value={category} onChange={(e) => setCategory(e.target.value)}>
                            <option value="NEWS">NEWS</option>
                            <option value="LITERATURE">LITERATURE</option>
                        </select>

                        <br /><label>유형:</label>
                        <select value={type} onChange={(e) => setType(e.target.value)}>
                            <option value="ECONOMY">ECONOMY</option>
                            <option value="SOCIETY">SOCIETY</option>
                            <option value="SCIENCE">SCIENCE</option>
                        </select>

                        <br /><label>분류:</label>
                        <select value={classification} onChange={(e) => setClassification(e.target.value)}>
                            <option value="NORMAL">NORMAL</option>
                            <option value="CHALLENGE">CHALLENGE</option>
                        </select>

                        <div style={{ marginTop: "16px", display: "flex", justifyContent: "space-between" }}>
                            <button onClick={handleGeneratePassageWithParams} disabled={loadingPassage}>
                                {loadingPassage ? "생성 중..." : "생성"}
                            </button>
                            <button onClick={() => setShowPassageModal(false)}>닫기</button>
                        </div>
                    </div>
                </div>
            )}
            <div style={{ padding: "24px" }}>
                <span style={ADMIN_BUTTONS_LIST}>
                    <button style={ADMIN_BUTTONS} onClick={() => navigate("/adminpage/adminnews")}>뉴스 관리</button>
                    <button style={ADMIN_BUTTONS} onClick={() => navigate('/adminpage/adminliterature')}>문학 관리</button>
                </span>
                <div style={ADMIN_TITLE}>
                    <div>
                        <h2>회원 관리</h2>
                    </div>
                    <div style={{ display: 'flex', gap: '10px' }}>
                        <button style={ADMIN_AI_BUTTONS} onClick={handleGenerateTestPassage} disabled={loadingTestPassage}>
                            {loadingTestPassage ? '지문 생성 중...' : '테스트 - 지문 생성'}
                        </button>
                        <button style={ADMIN_AI_BUTTONS} onClick={handleGenerateTestQuestion} disabled={loadingTestQuestion}>
                            {loadingTestQuestion ? '문제 생성 중...' : '테스트 - 문제 생성'}
                        </button>
                        {/* <button style={ADMIN_AI_BUTTONS} onClick={handleGeneratePassage} disabled={loadingPassage}>
                        {loadingPassage ? '생성 중...' : '지문 생성'}
                    </button> */}
                        <button style={ADMIN_AI_BUTTONS} onClick={() => setShowPassageModal(true)} disabled={loadingPassage}>
                            {loadingPassage ? '생성 중...' : '지문 생성'}
                        </button>
                        <button style={ADMIN_AI_BUTTONS} onClick={handleGenerateQuestion} disabled={loadingQuestion}>
                            {loadingQuestion ? '생성 중...' : '문제 생성'}
                        </button>
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
                                <td style={tdStyle}>{new Date(user.create_date).toLocaleDateString()}</td>
                                <td style={tdStyle}>{user.last_modified_date ? new Date(user.last_modified_date).toLocaleDateString() : "-"}</td>
                                <td style={tdStyle}>{user.withdraw_date ? new Date(user.withdraw_date).toLocaleDateString() : "-"}</td>
                                <td style={tdStyle}>{user.social_provider || "-"}</td>
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
        </>
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
