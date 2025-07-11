import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance';

const AdminUserInfo = () => {
    const navigate = useNavigate();
    const { email } = useParams();
    const [user, setUser] = useState(null);

    // 포인트 
    const [pointInfo, setPointInfo] = useState({});
    const [showModal, setShowModal] = useState(false);
    const [selectedCategory, setSelectedCategory] = useState("english_news");
    const [pointDelta, setPointDelta] = useState(0);

    // 문제 풀이 기록 가져오기
    const [totalLearning, setTotalLearning] = useState([]);

    // 퀴즈 풀이 기록 가져오기
    // const [newsQuizAttempts, setNewsQuizAttempts] = useState([]);
    // const [literatureQuizAttempts, setLiteratureQuizAttempts] = useState([]);

    // 뉴스 퀴즈 풀이 기록 추가 모달
    // const [showAddModal, setShowAddModal] = useState(false);
    // const [newQuizNo, setNewQuizNo] = useState("");
    // const [selectedOptionIndex, setSelectedOptionIndex] = useState(0);

    // 문학 퀴즈 풀이 기록 추가 모달
    // const [showAddLiteratureModal, setShowAddLiteratureModal] = useState(false);
    // const [newLiteratureQuizNo, setNewLiteratureQuizNo] = useState("");
    // const [selectedLiteratureOptionIndex, setSelectedLiteratureOptionIndex] = useState(0);
    // const [isLiteratureCorrect, setIsLiteratureCorrect] = useState(true);

    // 회원 정보 가져오기
    const fetchUserInfo = async () => {
        try {
            const res = await axiosInstance.get(`/administrator/member/get-member`, {
                params: { email }
            });
            setUser(res.data);
        } catch (err) {
            console.error(err);
            alert("회원 정보를 불러오는 데 실패했습니다.");
        }
    };

    // 문제 풀이 기록 가져오기
    const fetchTotalLearning = async () => {
        try {
            const res = await axiosInstance.get("/administrator/member/get-total-learning", {
                params: { email }
            });
            setTotalLearning(res.data);
        } catch (err) {
            console.error("문제 풀이 기록 불러오기 실패:", err);
        }
    };

    useEffect(() => {
        fetchUserInfo();
        fetchTotalLearning();
    }, [email]);

    return (
        <div style={{ padding: "24px" }}>
            <button onClick={() => navigate("/adminpage")} style={backbtn}>뒤로가기</button>
            {user ? (
                <div>
                    <h2>회원 상세 정보</h2>
                    <p><strong>이메일:</strong> {user.email}</p>
                    <p><strong>닉네임:</strong> {user.nickname}</p>
                    <p><strong>생일:</strong> {user.birthday}</p>
                    <p><strong>상태:</strong> {user.status}</p>
                    <p><strong>역할:</strong> {user.role}</p>
                    <p><strong>소셜 로그인:</strong> {user.socialProvider || "없음"}</p>
                    <p><strong>가입일:</strong> {new Date(user.createdAt).toLocaleDateString()}</p>
                    <p><strong>최근 수정일:</strong> {user.lastModifiedAt ? new Date(user.lastModifiedAt).toLocaleDateString() : "-"}</p>
                    <p><strong>탈퇴일:</strong> {user.withdrawAt ? new Date(user.withdrawAt).toLocaleDateString() : "-"}</p>
                    {user.profileImagePath && (
                        <div>
                            <strong>프로필 이미지:</strong><br />
                            <img src={user.profileImagePath} alt="프로필" style={{ width: "100px", borderRadius: "50%" }} />
                        </div>
                    )}
                </div>
            ) : (
                <p>회원 정보를 불러오는 중입니다...</p>
            )}
            <button style={ADMIN_BUTTONS} onClick={() => navigate(`/adminpage/adminuserinfo/${email}/attendance`)}>출석 기록 관리</button>

            <hr style={{ margin: "24px 0" }} />

            <div style={TITLE_STYLE}>
                <div>
                    <h3>포인트 정보 - <strong>총 포인트:</strong> {pointInfo.total}</h3>
                </div>
                <div style={BUTTON_LIST}>
                    <button
                        style={BUTTON_STYLE}
                        onClick={() => setShowModal(true)}
                    >
                        점수 수정
                    </button>
                </div>
            </div>
            <p><strong>영어 뉴스:</strong> {pointInfo.english_news}</p>
            <p><strong>일본어 뉴스:</strong> {pointInfo.japanese_news}</p>
            <p><strong>국어 뉴스:</strong> {pointInfo.korean_news}</p>
            <p><strong>동화:</strong> {pointInfo.fairytale}</p>
            <p><strong>소설:</strong> {pointInfo.novel}</p>
            <p><strong>최근 수정일:</strong> {new Date(pointInfo.last_modified_date).toLocaleDateString()}</p>

            <hr style={{ margin: "24px 0" }} />

            <div style={TITLE_STYLE}>
                <h3>문제 풀이 기록</h3>
            </div>

            {Array.isArray(totalLearning) && totalLearning.length > 0 ? (
                <table style={{ width: "100%", borderCollapse: "collapse" }}>
                    <thead>
                        <tr>
                            <th style={thStyle}>문제 번호</th>
                            <th style={thStyle}>지문 제목</th>
                            <th style={thStyle}>정답 여부</th>
                            <th style={thStyle}>응시일</th>
                        </tr>
                    </thead>
                    <tbody>
                        {totalLearning.map((item, idx) => (
                            <tr key={idx}>
                                <td style={tdStyle}>{item.questionNo}</td>
                                <td style={tdStyle}>{item.title}</td>
                                <td style={tdStyle}>{item.isCorrect ? "정답" : "오답"}</td>
                                <td style={tdStyle}>{new Date(item.createdAt).toLocaleString()}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            ) : (
                <p>기록이 없습니다.</p>
            )}
        </div>
    );
};

const backbtn = {
    marginBottom: "16px",
    padding: "8px 16px",
    backgroundColor: "#6c757d",
    color: "white",
    border: "none",
    borderRadius: "4px",
    cursor: "pointer"
}

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

const TITLE_STYLE = {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "16px"
}

const BUTTON_LIST = {
    display: "flex",
    gap: "8px"
}

const BUTTON_STYLE = {
    marginBottom: "16px",
    padding: "8px 16px",
    backgroundColor: "#007BFF",
    color: "white",
    border: "none",
    borderRadius: "4px",
    cursor: "pointer"
}

const modalOverlayStyle = {
    position: "fixed",
    top: 0, left: 0, right: 0, bottom: 0,
    backgroundColor: "rgba(0,0,0,0.5)",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    zIndex: 1000,
};

const modalContentStyle = {
    backgroundColor: "#fff",
    padding: "24px",
    borderRadius: "8px",
    minWidth: "300px",
    boxShadow: "0 4px 12px rgba(0,0,0,0.2)",
};

const ADMIN_BUTTONS = {
    marginBottom: "16px",
    padding: "8px 16px",
    backgroundColor: "#007BFF",
    color: "white",
    border: "none",
    borderRadius: "4px",
    cursor: "pointer"
};

export default AdminUserInfo;