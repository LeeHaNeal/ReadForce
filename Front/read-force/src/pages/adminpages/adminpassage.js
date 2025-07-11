import React, { useEffect, useState } from "react";
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance';

const AdminPassage = () => {
    const navigate = useNavigate();

    const [loadingTestPassage, setLoadingTestPassage] = useState(false);
    const [loadingTestQuestion, setLoadingTestQuestion] = useState(false);

    const [loadingPassage, setLoadingPassage] = useState(false);
    const [loadingQuestion, setLoadingQuestion] = useState(false);

    const [showPassageModal, setShowPassageModal] = useState(false);
    const [language, setLanguage] = useState("KOREAN");
    const [level, setLevel] = useState(1);
    const [category, setCategory] = useState("NEWS");
    const [type, setType] = useState("ECONOMY");
    const [classification, setClassification] = useState("NORMAL");

    const [passageList, setPassageList] = useState([]);
    const [count, setCount] = useState(1);
    // 지문 불러오기
    useEffect(() => {
        const fetchPassages = async () => {
            try {
                const res = await axiosInstance.get("/passage/get-all-passages");
                setPassageList(res.data);
            } catch (err) {
                console.error("전체 지문 불러오기 실패", err);
            }
        };

        fetchPassages();
    }, []);

    // 테스트 지문 생성
    const handleGenerateTestPassage = async () => {
        setLoadingTestPassage(true);
        try {
            const res = await axiosInstance.post("/ai/generate-test-passage?language=KOREAN");
            alert("성공 : " + res.data.message);
        } catch (err) {
            console.error(err);
            alert("실패 : 지문 생성 중 오류가 발생했습니다.");
        } finally {
            setLoadingTestPassage(false);
        }
    };

    // 테스트 문제 생성
    const handleGenerateTestQuestion = async () => {
        setLoadingTestQuestion(true);
        try {
            const res = await axiosInstance.post("/ai/generate-test-question?language=KOREAN");
            alert("성공 : " + res.data.message);
        } catch (err) {
            console.error(err);
            alert("실패 : 문제 생성 중 오류가 발생했습니다.");
        } finally {
            setLoadingTestQuestion(false);
        }
    };

    // 일반 지문 생성
    const handleGeneratePassageWithParams = async () => {
        setLoadingPassage(true);
        try {
            const res = await axiosInstance.post("/ai/generate-passage", {
                language,
                level,
                category,
                type,
                classification,
                 count
            });
            const data = await res.data;
            alert("성공 : " + data.message);
            setShowPassageModal(false);
        } catch (err) {
            console.error(err);
            alert("실패 : 지문 생성 중 오류가 발생했습니다.");
        } finally {
            setLoadingPassage(false);
        }
    };

    // 일반 문제 생성
    const handleGenerateQuestion = async () => {
        setLoadingQuestion(true);
        try {
            const res = await axiosInstance.post("/ai/generate-question");
            const data = await res.data;
            alert("성공 : " + data.message);
        } catch (err) {
            console.error(err);
            alert("실패 : 문제 생성 중 오류가 발생했습니다.");
        } finally {
            setLoadingQuestion(false);
        }
    };

    // 지문 삭제
    const handleDeletePassage = async (passageNo) => {
    const confirmDelete = window.confirm("정말 이 지문을 삭제하시겠습니까?");
    if (!confirmDelete) return;

    try {
        const res = await axiosInstance.delete(`/administrator/passage/delete`, {
            params: { passageNo },
        });

        alert("성공 : 지문이 삭제되었습니다.");
        // 삭제 후 리스트 갱신
        setPassageList(prev => prev.filter(p => p.passageNo !== passageNo));
    } catch (err) {
        console.error("실패 : 지문 삭제 중 오류 발생:", err);
        alert("실패 : 지문 삭제 실패");
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
                            <option value="KOREAN">한국어</option>
                            <option value="ENGLISH">영어</option>
                            <option value="JAPANESE">일본어</option>

                        </select>

                      <br /><label>난이도:</label>
                        <select value={level} onChange={(e) => setLevel(parseInt(e.target.value))}>
                            {[...Array(10)].map((_, idx) => (
                                <option key={idx + 1} value={idx + 1}>{idx + 1}</option>
                            ))}
                        </select>

                        <br /><label>카테고리:</label>
                        <select value={category} onChange={(e) => setCategory(e.target.value)}>
                            <option value="NEWS">뉴스</option>
                            <option value="NOVEL">소설</option>
                            <option value="FAIRY_TALE">동화</option>
                            <option value="VOCABULARY">어휘</option>
                            <option value="FACTUAL">사실적</option>
                            <option value="INFERENTIAL">추론적</option>
                        </select>

                        <br /><label>유형:</label>
                        <select value={type} onChange={(e) => setType(e.target.value)}>
                            <option value="POLITICS">정치</option>
                            <option value="ECONOMY">경제</option>
                            <option value="SOCIETY">사회</option>
                            <option value="LIFE_AND_CULTURE">생활/문화</option>
                            <option value="IT_AND_SCIENCE">IT/과학</option>
                            <option value="WORLD">세계</option>
                            <option value="SPORTS">스포츠</option>
                            <option value="ENTERTAINMENT">연예</option>
                            <option value="MYSTERY">미스터리</option>
                            <option value="SCIENCE_FICTION">과학 소설</option>
                            <option value="FANTASY">판타지</option>
                            <option value="ROMANCE">로맨스</option>
                            <option value="HISTORICAL">역사</option>
                            <option value="ADVENTURE">모험</option>
                            <option value="THRILLER">스릴러</option>
                            <option value="SLICE_OF_LIFE">일상</option>
                            <option value="TRADITIONAL">전통</option>
                            <option value="INFORMATIONAL">정보성</option>
                        </select>

                        <br /><label>분류:</label>
                        <select value={classification} onChange={(e) => setClassification(e.target.value)}>
                            <option value="NORMAL">일반</option>
                            <option value="CHALLANGE">챌린지</option>
                            <option value="TEST">테스트</option>
                        </select>

                        <br /><label>생성 개수:</label> {/* ✅ 추가됨 */}
                        <input type="number" min="1" max="20" value={count} onChange={(e) => setCount(parseInt(e.target.value) || 1)} />

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
                <button onClick={() => navigate("/adminpage")} style={backbtn}>뒤로가기</button>
                <div style={ADMIN_PASSAGE_TITLE}>
                    <div>
                        <h2>전체 지문 목록</h2>
                    </div>
                    <div style={ADMIN_BUTTONS_LIST}>
                        <button style={ADMIN_BUTTONS} onClick={handleGenerateTestPassage} disabled={loadingTestPassage}>
                            {loadingTestPassage ? '생성 중...' : '테스트 지문 생성'}
                        </button>
                        <button style={ADMIN_BUTTONS} onClick={handleGenerateTestQuestion} disabled={loadingTestQuestion}>
                            {loadingTestQuestion ? '생성 중...' : '테스트 문제 생성'}
                        </button>
                        <button style={ADMIN_BUTTONS} onClick={() => setShowPassageModal(true)} disabled={loadingPassage}>
                            {loadingPassage ? '생성 중...' : '일반 지문 생성'}
                        </button>
                        <button style={ADMIN_BUTTONS} onClick={handleGenerateQuestion} disabled={loadingQuestion}>
                            {loadingQuestion ? '생성 중...' : '일반 문제 생성'}
                        </button>
                    </div>
                </div>
                <table style={{ width: "100%", borderCollapse: "collapse", marginTop: "16px" }}>
                    <thead>
                        <tr>
                            <th style={thStyle}>번호</th>
                            <th style={thStyle}>제목</th>
                            <th style={thStyle}>언어</th>
                            <th style={thStyle}>카테고리</th>
                            <th style={thStyle}>난이도</th>
                            <th style={thStyle}>생성일</th>
                            <th style={thStyle}>문제유형</th>
                            <th style={thStyle}>작성자</th>
                            <th style={thStyle}>삭제</th>
                        </tr>
                    </thead>
                    <tbody>
                        {passageList.map((passage) => (
                            <tr key={passage.passageNo}>
                                <td style={tdStyle}>{passage.passageNo}</td>
                                <td
                                    style={{ cursor: "pointer", color: "blue", ...tdStyle }}
                                    onClick={() => navigate(`/adminpage/passage/${passage.passageNo}`, { state: { passage } })}
                                >
                                    {passage.title}
                                </td>
                                <td style={tdStyle}>{LANGUAGE_LABELS[passage.language] || passage.language}</td>
                                <td style={tdStyle}>{CATEGORY_LABELS[passage.category] || passage.category}</td>
                                <td style={tdStyle}>{passage.level}</td>
                                <td style={tdStyle}>
                                    {passage.createdAt ? new Date(passage.createdAt).toLocaleDateString() : "-"}
                                </td>
                                <td style={tdStyle}>{CLASSIFICATION_LABELS[passage.classification]}</td>
                                <td style={tdStyle}>{AUTHOR_LABELS[passage.author]}</td>
                                <td style={tdStyle}>
                                    <button
                                        onClick={() => handleDeletePassage(passage.passageNo)}
                                        style={{ color: "red", border: "none", background: "none", cursor: "pointer" }}
                                    >
                                        삭제
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </>
    );
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

const backbtn = {
    marginBottom: "16px",
    padding: "8px 16px",
    backgroundColor: "#6c757d",
    color: "white",
    border: "none",
    borderRadius: "4px",
    cursor: "pointer"
};

const LANGUAGE_LABELS = {
    KOREAN: "ko",
    JAPANESE: "jp",
    ENGLISH: "en"
};

const AUTHOR_LABELS = {
    GEMINI: "ai"
}

const CATEGORY_LABELS = {
    NEWS: "뉴스",
    NOVEL: "소설",
    FAIRY_TALE: "동화",
    VOCABULARY: "사전",
    FACTUAL: "사실",
    INFERENTIAL: "인퍼뭐노"
};

const CLASSIFICATION_LABELS = {
    NORMAL: "일반문제",
    CHALLENGE: "도전문제",
    TEST: "테스트문제"
}

const ADMIN_PASSAGE_TITLE = {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "16px"
};

const ADMIN_BUTTONS_LIST = {
    display: "flex",
    gap: "8px"
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

export default AdminPassage;