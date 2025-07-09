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

    // 퀴즈 풀이 기록 가져오기
    const [newsQuizAttempts, setNewsQuizAttempts] = useState([]);
    const [literatureQuizAttempts, setLiteratureQuizAttempts] = useState([]);

    // 뉴스 퀴즈 풀이 기록 추가 모달
    const [showAddModal, setShowAddModal] = useState(false);
    const [newQuizNo, setNewQuizNo] = useState("");
    const [selectedOptionIndex, setSelectedOptionIndex] = useState(0);

    // 문학 퀴즈 풀이 기록 추가 모달
    const [showAddLiteratureModal, setShowAddLiteratureModal] = useState(false);
    const [newLiteratureQuizNo, setNewLiteratureQuizNo] = useState("");
    const [selectedLiteratureOptionIndex, setSelectedLiteratureOptionIndex] = useState(0);
    const [isLiteratureCorrect, setIsLiteratureCorrect] = useState(true);

    // 회원 정보 가져오기
    const fetchUserInfo = async () => {
        try {
            const res = await axiosInstance.get(`/admin/get-member-info-object`, {
                params: { email }
            });
            setUser(res.data);
        } catch (err) {
            console.error(err);
            alert("회원 정보를 불러오는 데 실패했습니다.");
        }
    };

    // 포인트 정보 가져오기
    const fetchPointInfo = async () => {
        try {
            const res = await axiosInstance.get(`/admin/get-member-point-object`, {
                params: { email }
            });
            console.log("✅ 백엔드 응답 확인:", res.data);
            setPointInfo(res.data);
        } catch (err) {
            console.error(err);
            alert("포인트 정보를 불러오는 데 실패했습니다.");
        }
    };

    // 뉴스 퀴즈 풀이 기록 가져오기
    const fetchNewsQuizAttempts = async () => {
        try {
            const res = await axiosInstance.get(`/admin/get-member-news-quiz-attempt-list`, {
                params: { email }
            });
            setNewsQuizAttempts(res.data);
        } catch (err) {
            console.error("뉴스 퀴즈 응시 기록 오류:", err);
        }
    };

    // 문학 퀴즈 풀이 기록 가져오기
    const fetchLiteratureQuizAttempts = async () => {
        try {
            const res = await axiosInstance.get(`/admin/get-member-literature-quiz-attempt-list`, {
                params: { email }
            });
            setLiteratureQuizAttempts(res.data);
        } catch (err) {
            console.error("문학 퀴즈 응시 기록 오류:", err);
        }
    };

    useEffect(() => {
        fetchUserInfo();
        fetchPointInfo();
        fetchNewsQuizAttempts();
        fetchLiteratureQuizAttempts();
    }, [email]);

    if (!user) return <div>불러오는 중...</div>;

    // 포인트 정보 수정
    const handlePointUpdate = async () => {
        try {
            await axiosInstance.patch(`/admin/increment-point`, {
                email,
                category: selectedCategory,
                delta: pointDelta
            });
            alert("수정 완료!");
            setShowModal(false);
            fetchPointInfo();
        } catch (err) {
            console.error(err);
            alert("점수 수정 중 오류 발생");
        }
    };

    // 뉴스 퀴즈 풀이 기록 삭제
    const handleNewQuizDeleteAttempt = async (email, newsQuizNo) => {
        const confirmed = window.confirm("정말 삭제하시겠습니까?");
        if (!confirmed) return;

        try {
            const res = await axiosInstance.delete(`/admin/delete-news-quiz-attempt`, {
                params: { email, news_quiz_no: newsQuizNo }
            });
            alert(res.data.message || "삭제 성공!");
        } catch (err) {
            console.error("삭제 실패", err);
            alert("삭제 중 오류 발생");
        }
        await fetchNewsQuizAttempts();
    };

    // 문학 퀴즈 풀이 기록 삭제
    const handleLiteratureQuizDeleteAttempt = async (email, literatureQuizNo) => {
        const confirmed = window.confirm("정말 삭제하시겠습니까?");
        if (!confirmed) return;

        try {
            const res = await axiosInstance.delete(`/admin/delete-literature-quiz-attempt`, {
                params: { email, literature_quiz_no: literatureQuizNo }
            });
            alert(res.data.message || "삭제 성공!");
        } catch (err) {
            console.error("삭제 실패", err);
            alert("삭제 중 오류 발생");
        }
        await fetchLiteratureQuizAttempts();
    };

    // 뉴스 퀴즈 풀이 기록 추가
    const handleAddNewsQuizAttempt = async () => {
        try {
            const payload = {
                email,
                news_quiz_no: Number(newQuizNo),
                selected_option_index: selectedOptionIndex,
            };
            const res = await axiosInstance.post("/admin/add-news-quiz-attempt", payload);
            alert(res.data.message || "추가 완료!");
            setNewQuizNo("");
            setSelectedOptionIndex(0);
            setShowAddModal(false);
            await fetchNewsQuizAttempts();
        } catch (err) {
            console.error("추가 실패", err);
            alert("추가 중 오류 발생");
        }
    };

    // 문학 퀴즈 풀이 기록 추가
    const handleAddLiteratureQuizAttempt = async () => {
        try {
            const payload = {
                email,
                literature_quiz_no: Number(newLiteratureQuizNo),
                selected_option_index: selectedLiteratureOptionIndex,
            };
            await axiosInstance.post("/admin/add-literature-quiz-attempt", payload);
            alert("문학 퀴즈 응시 기록이 추가되었습니다.");
            setShowAddLiteratureModal(false);
            await fetchLiteratureQuizAttempts();
        } catch (error) {
            alert("오류 발생: " + error.message);
        }
    };

    return (
        <div style={{ padding: "24px" }}>
            <button onClick={() => navigate("/adminpage")} style={backbtn}>뒤로가기</button>
            <h2>회원 상세 정보</h2>
            <p><strong>이메일:</strong> {user.email}</p>
            <p><strong>닉네임:</strong> {user.nickname}</p>
            <p><strong>생일:</strong> {user.birthday}</p>
            <p><strong>상태:</strong> {user.status}</p>
            <p><strong>가입일:</strong> {new Date(user.create_date).toLocaleDateString()}</p>
            <button style={ADMIN_BUTTONS} onClick={() => navigate(`/adminpage/adminuserinfo/${email}/attendance`)}>출석 기록 관리</button>

            {/* <h3>출석 내역</h3>
            {attendanceList.length === 0 ? (
                <p>출석 기록이 없습니다.</p>
            ) : (
                <ul style={{ listStyle: "none", paddingLeft: 0 }}>
                    {attendanceList.map((a) => (
                        <li key={a.attendance_no} style={{ marginBottom: "8px" }}>
                            출석 날짜: {new Date(a.created_date).toLocaleDateString()}
                        </li>
                    ))}
                </ul>
            )} */}

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
                <div>
                    <h3>뉴스 퀴즈 응시 기록</h3>
                </div>
                <div style={BUTTON_LIST}>
                    <button
                        style={BUTTON_STYLE}
                        onClick={() => setShowAddModal(true)}
                    >
                        뉴스 퀴즈 풀이 기록 추가
                    </button>
                </div>
            </div>
            {Array.isArray(newsQuizAttempts) && newsQuizAttempts.length > 0 ? (
                <table style={{ width: "100%", borderCollapse: "collapse" }}>
                    <thead>
                        <tr>
                            <th style={thStyle}>퀴즈 번호</th>
                            <th style={thStyle}>선택지</th>
                            <th style={thStyle}>정답 여부</th>
                            <th style={thStyle}>응시일</th>
                            <th style={thStyle}>관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        {newsQuizAttempts.map((attempt, idx) => (
                            <tr key={idx}>
                                <td style={tdStyle}>{attempt.news_quiz_no}</td>
                                <td style={tdStyle}>{(attempt.selected_option_index ?? 0) + 1}번</td>
                                <td style={tdStyle}>{attempt.is_correct ? "정답" : "오답"}</td>
                                <td style={tdStyle}>{new Date(attempt.created_date).toLocaleString()}</td>
                                <td style={tdStyle}>
                                    <button
                                        onClick={() => handleNewQuizDeleteAttempt(attempt.email, attempt.news_quiz_no)}
                                        style={{ color: "red", border: "none", background: "none", cursor: "pointer" }}
                                    >
                                        삭제
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            ) : (
                <p>기록이 없습니다.</p>
            )}

            <hr style={{ margin: "24px 0" }} />

            <div style={TITLE_STYLE}>
                <div>
                    <h3>문학 퀴즈 응시 기록</h3>
                </div>
                <div style={BUTTON_LIST}>
                    <button
                        style={BUTTON_STYLE}
                        onClick={() => setShowAddLiteratureModal(true)}
                    >
                        문학 퀴즈 풀이 기록 추가
                    </button>
                </div>
            </div>
            {Array.isArray(literatureQuizAttempts) && literatureQuizAttempts.length > 0 ? (
                <table style={{ width: "100%", borderCollapse: "collapse" }}>
                    <thead>
                        <tr>
                            <th style={thStyle}>퀴즈 번호</th>
                            <th style={thStyle}>선택지</th>
                            <th style={thStyle}>정답 여부</th>
                            <th style={thStyle}>응시일</th>
                            <th style={thStyle}>관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        {literatureQuizAttempts.map((attempt, idx) => (
                            <tr key={idx}>
                                <td style={tdStyle}>{attempt.literature_quiz_no}</td>
                                <td style={tdStyle}>{(attempt.selected_option_index ?? 0) + 1}번</td>
                                <td style={tdStyle}>{attempt.is_correct ? "정답" : "오답"}</td>
                                <td style={tdStyle}>{new Date(attempt.created_date).toLocaleString()}</td>
                                <td style={tdStyle}>
                                    <button
                                        onClick={() => handleLiteratureQuizDeleteAttempt(attempt.email, attempt.literature_quiz_no)}
                                        style={{ color: "red", border: "none", background: "none", cursor: "pointer" }}
                                    >
                                        삭제
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            ) : (
                <p>기록이 없습니다.</p>
            )}
            {showAddModal && (
                <div style={modalOverlayStyle}>
                    <div style={modalContentStyle}>
                        <h3>뉴스 퀴즈 응시 기록 추가</h3>

                        {/* 퀴즈 번호 입력 (줄바꿈) */}
                        <div style={{ marginBottom: "12px" }}>
                            <label>
                                퀴즈 번호:
                                <br />
                                <input
                                    type="number"
                                    value={newQuizNo}
                                    onChange={(e) => setNewQuizNo(e.target.value)}
                                />
                            </label>
                        </div>

                        {/* 선택 보기 번호 드롭다운 (줄바꿈) */}
                        <div style={{ marginBottom: "12px" }}>
                            <label>
                                선택한 보기 번호:
                                <br />
                                <select
                                    value={selectedOptionIndex}
                                    onChange={(e) => setSelectedOptionIndex(Number(e.target.value))}
                                >
                                    <option value={0}>1번</option>
                                    <option value={1}>2번</option>
                                    <option value={2}>3번</option>
                                    <option value={3}>4번</option>
                                </select>
                            </label>
                        </div>

                        <div style={{ marginTop: "16px" }}>
                            <button onClick={handleAddNewsQuizAttempt}>저장</button>
                            <button onClick={() => setShowAddModal(false)} style={{ marginLeft: "8px" }}>취소</button>
                        </div>
                    </div>
                </div>
            )}
            {showAddLiteratureModal && (
                <div style={modalOverlayStyle}>
                    <div style={modalContentStyle}>
                        <h3>문학 퀴즈 응시 기록 추가</h3>

                        {/* 문학 퀴즈 번호 입력 */}
                        <div style={{ marginBottom: "12px" }}>
                            <label>
                                문학 퀴즈 번호:
                                <br />
                                <input
                                    type="number"
                                    value={newLiteratureQuizNo ?? ""}
                                    onChange={(e) => {
                                        const value = e.target.value;
                                        setNewLiteratureQuizNo(value === "" ? "" : Number(value));
                                    }}
                                />
                            </label>
                        </div>

                        {/* 선택한 보기 번호 */}
                        <div style={{ marginBottom: "12px" }}>
                            <label>
                                선택한 보기 번호:
                                <br />
                                <select
                                    value={selectedLiteratureOptionIndex}
                                    onChange={(e) => setSelectedLiteratureOptionIndex(Number(e.target.value))}
                                >
                                    <option value={0}>1번</option>
                                    <option value={1}>2번</option>
                                    <option value={2}>3번</option>
                                    <option value={3}>4번</option>
                                </select>
                            </label>
                        </div>

                        {/* 버튼 영역 */}
                        <div style={{ marginTop: "16px" }}>
                            <button onClick={handleAddLiteratureQuizAttempt}>저장</button>
                            <button onClick={() => setShowAddLiteratureModal(false)} style={{ marginLeft: "8px" }}>취소</button>
                        </div>
                    </div>
                </div>
            )}
            {showModal && (
                <div style={modalOverlayStyle}>
                    <div style={modalContentStyle}>
                        <h4>점수 수정</h4>

                        <label>카테고리 선택</label>
                        <select value={selectedCategory} onChange={(e) => setSelectedCategory(e.target.value)}>
                            <option value="english_news">영어 뉴스</option>
                            <option value="japanese_news">일본어 뉴스</option>
                            <option value="korean_news">국어 뉴스</option>
                            <option value="fairytale">동화</option>
                            <option value="novel">소설</option>
                        </select>

                        <label>변경할 점수 입력</label>
                        <input
                            type="number"
                            value={pointDelta}
                            onChange={(e) => setPointDelta(parseInt(e.target.value))}
                        />

                        <div style={{ marginTop: "16px" }}>
                            <button onClick={handlePointUpdate}>적용</button>
                            <button onClick={() => setShowModal(false)}>취소</button>
                        </div>
                    </div>
                </div>
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