import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axiosInstance from '../../api/axiosInstance';

const AdminAddParagraph = () => {
    const navigate = useNavigate();

    const LANGUAGE_OPTIONS = ["KOREAN", "JAPANESE", "ENGLISH"];
    const CLASSIFICATION_OPTIONS = ["NORMAL", "CHALLANGE", "TEST"];

    const CATEGORY_OPTIONS = ["NEWS", "NOVEL", "FAIRY_TALE", "VOCABULARY", "FACTUAL", "INFERENTIAL"];
    const TYPE_OPTIONS = [
        "POLITICS", "ECONOMY", "SOCIETY", "LIFE_AND_CULTURE", "IT_AND_SCIENCE",
        "WORLD", "SPORTS", "ENTERTAINMENT", "MYSTERY", "SCIENCE_FICTION",
        "FANTASY", "ROMANCE", "HISTORICAL", "ADVENTURE", "THRILLER",
        "SLICE_OF_LIFE", "TRADITIONAL", "INFORMATIONAL"
    ];

    const LEVEL_OPTIONS = [
        { value: 1, question: "단순 사실 확인", sentence: "극히 짧은 단문 위주" },
        { value: 2, question: "구체적 정보 찾기", sentence: "주어/목적어 구분 등" },
        { value: 3, question: "내용 일치/불일치", sentence: "기본적인 접속사 연결" },
        { value: 4, question: "육하원칙 파악", sentence: "주어+서술어 관계 명확" },
        { value: 5, question: "요점/생각 파악", sentence: "단문과 복문 혼합" },
        { value: 6, question: "전체 주제 찾기", sentence: "복문 비중이 높음" },
        { value: 7, question: "문맥을 통한 추론", sentence: "구절들이 혼합된 복문" },
        { value: 8, question: "비판적 의도/감정 추론", sentence: "다양한 문장 구조 활용" },
        { value: 9, question: "주장/의도 파악", sentence: "논리적 전개, 비교/대조" },
        { value: 10, question: "새로운 상황 적용", sentence: "전문 서적의 복잡한 문어체" }
    ];

    const [title, setTitle] = useState("");
    const [author, setAuthor] = useState("");
    const [language, setLanguage] = useState(LANGUAGE_OPTIONS[0]);
    const [classification, setClassification] = useState(CLASSIFICATION_OPTIONS[0]);
    const [type, setType] = useState(TYPE_OPTIONS[0]);
    const [category, setCategory] = useState(CATEGORY_OPTIONS[0]);
    const [level, setLevel] = useState(1);
    const [content, setContent] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();

        const body = {
            literature_no: Number(literatureNo),
            category,
            level,
            content,
        };
        console.log("literatureNo:", literatureNo);
        console.log("body:", body);

        try {
            await axiosInstance.post("/admin/add-literature-paragraph", body);

            alert("문단이 성공적으로 추가되었습니다!");
            navigate(`/adminpage/adminliterature/${literatureNo}`);
        } catch (err) {
            console.error(err);
            alert("문단 추가 중 오류 발생: " + err.message);
        }
    };

    const selectedLevel = LEVEL_OPTIONS.find(l => l.value === level);

    return (
        <div style={pageStyle}>
            <h2 style={titleStyle}>Passage 추가</h2>
            <form onSubmit={handleSubmit} style={formStyle}>
                <FormItem label="제목" value={title} onChange={setTitle} />
                <FormItem label="저자" value={author} onChange={setAuthor} />
                <SelectItem label="언어" value={language} onChange={setLanguage} options={LANGUAGE_OPTIONS} />
                <SelectItem label="분류" value={classification} onChange={setClassification} options={CLASSIFICATION_OPTIONS} />
                <SelectItem label="카테고리" value={category} onChange={setCategory} options={CATEGORY_OPTIONS} />
                <SelectItem label="타입" value={type} onChange={setType} options={TYPE_OPTIONS} />
                <div style={formItemStyle}>
                    <label style={labelStyle}>레벨</label>
                    <select value={level} onChange={(e) => setLevel(Number(e.target.value))} style={inputStyle}>
                        {LEVEL_OPTIONS.map((lvl) => (
                            <option key={lvl.value} value={lvl.value}>
                                {lvl.value} - {lvl.question}
                            </option>
                        ))}
                    </select>
                    <p style={{ fontSize: "12px", marginTop: "4px", color: "#555" }}>{selectedLevel?.sentence}</p>
                </div>
                <div style={formItemStyle}>
                    <label style={labelStyle}>내용</label>
                    <textarea
                        value={content}
                        onChange={(e) => setContent(e.target.value)}
                        rows={8}
                        style={{ width: "100%", padding: "8px" }}
                        required
                    />
                </div>
                <button type="submit" style={submitBtn}>문단 추가하기</button>
            </form>
        </div>
    );
};

// 공통 폼 요소
const FormItem = ({ label, value, onChange }) => (
    <div style={formItemStyle}>
        <label style={labelStyle}>{label}</label>
        <input
            type="text"
            value={value}
            onChange={(e) => onChange(e.target.value)}
            style={inputStyle}
            required
        />
    </div>
);

const SelectItem = ({ label, value, onChange, options }) => (
    <div style={formItemStyle}>
        <label style={labelStyle}>{label}</label>
        <select value={value} onChange={(e) => onChange(e.target.value)} style={inputStyle}>
            {options.map(opt => (
                <option key={opt} value={opt}>
                    {opt}
                </option>
            ))}
        </select>
    </div>
);

// 스타일
const pageStyle = { padding: "24px", maxWidth: "600px", margin: "0 auto" };
const titleStyle = { marginBottom: "20px" };
const formStyle = { display: "flex", flexDirection: "column", gap: "12px" };
const formItemStyle = { display: "flex", flexDirection: "column" };
const labelStyle = { marginBottom: "4px", fontWeight: "bold" };
const inputStyle = { padding: "8px", borderRadius: "4px", border: "1px solid #ccc" };
const submitBtn = {
    padding: "10px 16px",
    backgroundColor: "#007bff",
    color: "white",
    border: "none",
    borderRadius: "4px",
    cursor: "pointer",
    marginTop: "12px"
};

export default AdminAddParagraph;
