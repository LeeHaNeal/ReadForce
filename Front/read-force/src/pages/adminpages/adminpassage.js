import React, { useEffect, useState } from "react";
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance';

const AdminPassage = () => {
    const navigate = useNavigate();

    const [loadingTestPassage, setLoadingTestPassage] = useState(false);
    const [loadingTestQuestion, setLoadingTestQuestion] = useState(false);
    const [passageList, setPassageList] = useState([]);

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

    return (
        <div style={{ padding: "24px" }}>
            <button onClick={() => navigate("/adminpage")} style={backbtn}>뒤로가기</button>
            <div style={ADMIN_PASSAGE_TITLE}>
                <div>
                    <h2>전체 지문 목록</h2>
                </div>
                <div style={ADMIN_BUTTONS_LIST}>
                    <button style={ADMIN_BUTTONS} onClick={handleGenerateTestPassage}>테스트 지문 생성</button>
                    <button style={ADMIN_BUTTONS} onClick={handleGenerateTestQuestion}>테스트 문제 생성</button>
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
                            <td style={tdStyle}>
                                <button
                                    // onClick={() => handleDeletePassage(passage.passageNo)}
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

const CATEGORY_LABELS = {
    NEWS: "뉴스",
    NOVEL: "소설",
    FAIRY_TALE: "동화",
    VOCABULARY: "사전",
    FACTUAL: "사실",
    INFERENTIAL: "인퍼뭐노"
};

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