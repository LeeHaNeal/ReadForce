import React, { useEffect, useState } from "react";
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance';

const AdminNews = () => {
    const navigate = useNavigate();
    const [newsList, setNewsList] = useState([]);
    const [error, setError] = useState(null);

    // 뉴스 불러오기
    useEffect(() => {
        const fetchNews = async () => {
            try {
                const res = await axiosInstance.post("/admin/get-all-news-list");
                setNewsList(res.data);
            } catch (err) {
                console.error(err);
                alert("뉴스 목록을 불러오는 데 실패했습니다.");
            }
        };

        fetchNews();
    }, []);

    // 뉴스 생성
    const handleGenerateNews = async () => {
        if (!window.confirm("뉴스를 생성하시겠습니까? 총 54문제가 생성됩니다")) return;

        try {
            const res = await axiosInstance.post("/ai/generate-creative-news");
            alert("뉴스 생성 완료: " + res.data.messageCode);
        } catch (err) {
            console.error(err);
            alert("뉴스 생성에 실패했습니다.");
        }
    };

    // 뉴스 삭제
    const handleDeleteNews = async (newsNo) => {
        const confirmDelete = window.confirm("정말로 이 뉴스를 삭제하시겠습니까? 관련 뉴스 퀴즈도 함께 삭제됩니다.");
        if (!confirmDelete) return;

        try {
            await axiosInstance.delete(`/admin/delete-news-and-news-quiz-by-news-no?news_no=${newsNo}`);
            setNewsList((prev) => prev.filter((news) => news.news_no !== newsNo));
        } catch (err) {
            console.error(err);
            alert("뉴스 삭제 실패");
        }
    };

    // 뉴스 퀴즈 생성
    const handleGenerateNewsQuiz = async () => {
        if (!window.confirm("뉴스 퀴즈를 생성하시겠습니까?\n(뉴스에 해당 문제 없는 경우만 생성됩니다)")) return;

        try {
            const res = await axiosInstance.post("/ai/generate-creative-news-quiz");
            alert("뉴스 퀴즈 생성 완료: " + res.data.messageCode);
        } catch (err) {
            console.error(err);
            alert("뉴스 퀴즈 생성에 실패했습니다.");
        }
    };

    return (
        <div style={{ padding: "24px" }}>
            <button onClick={() => navigate("/adminpage")} style={backbtn}>뒤로가기</button>
            <div style={ADMIN_NEWS_TITLE}>
                <div>
                    <h2>전체 뉴스 목록</h2>
                </div>
                <div style={ADMIN_BUTTONS_LIST}>
                    <button style={ADMIN_BUTTONS} onClick={handleGenerateNews}>뉴스 생성</button>
                    <button style={ADMIN_BUTTONS} onClick={handleGenerateNewsQuiz}>뉴스 퀴즈 생성</button>
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
                        <th style={thStyle}>삭제</th>
                    </tr>
                </thead>
                <tbody>
                    {newsList.map((news) => (
                        <tr key={news.news_no}>
                            <td style={tdStyle}>{news.news_no}</td>
                            <td
                                style={{
                                    cursor: "pointer", color: "blue", border: "1px solid #ddd",
                                    padding: "8px",
                                }}
                                onClick={() => navigate(`/adminpage/adminnews/${news.news_no}`, { state: { news } })}
                            >
                                {news.title}
                            </td>
                            <td style={tdStyle}>{LANGUAGE_LABELS[news.language] || news.language}</td>
                            <td style={tdStyle}>{CATEGORY_LABELS[news.category] || news.category}</td>
                            <td style={tdStyle}>{LEVEL_LABELS[news.level] || news.level}</td>
                            <td style={tdStyle}>
                                {new Date(news.created_date).toLocaleDateString()}
                            </td>
                            <td style={tdStyle}>
                                <button
                                    onClick={() => handleDeleteNews(news.news_no)}
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
    );
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

const backbtn = {
    marginBottom: "16px",
    padding: "8px 16px",
    backgroundColor: "#6c757d",
    color: "white",
    border: "none",
    borderRadius: "4px",
    cursor: "pointer"
};

const LEVEL_LABELS = {
    BEGINNER: "초급",
    INTERMEDIATE: "중급",
    ADVANCED: "고급"
};

const LANGUAGE_LABELS = {
    KOREAN: "한국어",
    JAPANESE: "일본어",
    ENGLISH: "영어"
};

const CATEGORY_LABELS = {
    POLITICS: "정치",
    ECONOMY: "경제",
    SOCIETY: "사회",
    CULTURE: "문화",
    SCIENCE: "과학",
    ETC: "기타"
};

const ADMIN_NEWS_TITLE = {
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

export default AdminNews;
