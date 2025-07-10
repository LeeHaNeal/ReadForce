import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import axiosInstance from '../../api/axiosInstance';

const AdminLiteratureDetail = () => {
    const { passageNo } = useParams();
    const [passage, setPassage] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchPassageDetail = async () => {
            try {
                const res = await axiosInstance.post(`/admin/get-all-literature-list`);
                const data = res.data;
                const target = data.find(lit => lit.literature_no === Number(literatureNo));
                setLiterature(target);
            } catch (err) {
                console.error(err);
                alert("Passage 정보를 불러오지 못했습니다.");
            }
        };

        // 문단 불러오기
        const fetchParagraphs = async () => {
            try {
                const res = await axiosInstance.get(`/admin/get-all-literature-paragraph-list`);
                const data = res.data;
                const filtered = data.filter(p => String(p.literature_no) === literatureNo);
                setParagraphs(filtered);
            } catch (err) {
                console.error(err);
                alert("문단 정보를 불러오지 못했습니다.");
            }
        };

        // 퀴즈 불러오기
        const fetchQuizzes = async () => {
            try {
                const res = await axiosInstance.get(`/admin/get-all-literature-quiz-list`);
                const data = res.data;
                const filtered = data.filter(q => q.literature_no === Number(literatureNo));
                setQuizzes(filtered);
            } catch (err) {
                console.error(err);
                alert("퀴즈 정보를 불러오지 못했습니다.");
            }
        };

        fetchLiteratureDetail();
        fetchParagraphs();
        fetchQuizzes();
    }, [literatureNo]);

    if (!literature) return <div>불러오는 중...</div>;

    // 문단 삭제 ( 문제도 함께 )
    const handleDeleteParagraph = async (paragraphNo) => {
        if (!window.confirm("정말 이 문단과 관련된 문제도 함께 삭제하시겠습니까?")) return;

        try {
            await axiosInstance.delete(
                `/admin/delete-literature-paragraph-and-literature-by-literature-paragraph-no`, {
                params: {
                    literature_paragraph_no: paragraphNo,
                    literature_no: literatureNo
                }
            }
            );

            alert("문단이 삭제되었습니다.");
            setParagraphs(prev => prev.filter(p => p.literature_paragraph_no !== paragraphNo));
            setQuizzes(prev => prev.filter(q => q.literature_paragraph_no !== paragraphNo));
        } catch (err) {
            console.error(err);
            alert("문단 삭제 중 오류 발생");
        }
    };

    // 퀴즈 삭제
    const handleDeleteQuiz = async (quizNo) => {
        if (!window.confirm("정말 이 퀴즈를 삭제하시겠습니까?")) return;

        try {
            await axiosInstance.delete(`/admin/delete-literature-quiz`, {
                params: {
                    literature_quiz_no: quizNo
                }
            });

            alert("퀴즈가 삭제되었습니다.");
            setQuizzes(prev => prev.filter(q => q.literature_quiz_no !== quizNo));
        } catch (err) {
            console.error(err);
            alert("퀴즈 삭제 중 오류 발생");
        }
    };

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
