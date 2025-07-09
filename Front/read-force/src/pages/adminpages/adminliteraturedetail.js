import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import fetchWithAuth from "../../utils/fetchWithAuth";

const AdminLiteratureDetail = () => {
    const { passageNo } = useParams();
    const [passage, setPassage] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchPassageDetail = async () => {
            try {
                const res = await fetchWithAuth(`/administrator/passage/get-passage-detail?passage_no=${passageNo}`);
                if (!res.ok) throw new Error("Passage 정보 불러오기 실패");
                const data = await res.json();
                setPassage(data);
            } catch (err) {
                console.error(err);
                alert("Passage 정보를 불러오지 못했습니다.");
            }
        };

        fetchPassageDetail();
    }, [passageNo]);

    if (!passage) return <div>불러오는 중...</div>;

    return (
        <div style={{ padding: "24px" }}>
            <button onClick={() => navigate("/adminpage/adminliterature")} style={backbtn}>뒤로가기</button>
            <div style={TITLE_STYLE}>
                <div>
                    <h2>{passage.title}</h2>
                </div>
                <div style={BUTTON_LIST}>
                    <button
                        style={BUTTON_STYLE}
                        onClick={() => navigate(`/adminpage/adminliterature/add-passage`)}
                    >
                        새 Passage 추가
                    </button>
                </div>
            </div>
            <p><strong>저자:</strong> {passage.author}</p>
            <p><strong>유형:</strong> {passage.type}</p>
            <p><strong>카테고리:</strong> {passage.category}</p>
            <p><strong>언어:</strong> {passage.language}</p>
            <p><strong>분류:</strong> {passage.classification}</p>
            <p><strong>레벨:</strong> {passage.level}</p>
            <p><strong>생성일:</strong> {new Date(passage.created_date).toLocaleDateString()}</p>

            <hr style={{ margin: "24px 0" }} />
            <h3>내용</h3>
            <pre style={{ backgroundColor: "#f8f9fa", padding: "12px", whiteSpace: "pre-wrap" }}>
                {passage.content}
            </pre>
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
};

const TITLE_STYLE = {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "16px"
};

const BUTTON_LIST = {
    display: "flex",
    gap: "8px"
};

const BUTTON_STYLE = {
    marginBottom: "16px",
    padding: "8px 16px",
    backgroundColor: "#007BFF",
    color: "white",
    border: "none",
    borderRadius: "4px",
    cursor: "pointer"
};

export default AdminLiteratureDetail;
