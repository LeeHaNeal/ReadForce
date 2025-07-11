import { useEffect, useState } from "react";
import axiosInstance from "../../api/axiosInstance";
import { useNavigate } from "react-router-dom";

const AdminAverageStat = () => {
  const [averageStats, setAverageStats] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const res = await axiosInstance.get("/administrator/average-question-solving-time/get-all-list");
        setAverageStats(res.data);
      } catch (err) {
        console.error("불러오기 실패:", err);
        alert("통계를 불러오는 데 실패했습니다.");
      }
    };

    fetchStats();
  }, []);

  return (
    <div style={{ padding: "24px" }}>
      <button onClick={() => navigate("/adminpage")} style={backbtn}>뒤로가기</button>
      <h2>연령별 평균 문제 풀이 시간 통계</h2>

      {averageStats.length > 0 ? (
        <table style={{ width: "100%", borderCollapse: "collapse", marginTop: "16px" }}>
          <thead>
            <tr>
              <th style={thStyle}>번호</th>
              <th style={thStyle}>언어</th>
              <th style={thStyle}>카테고리</th>
              <th style={thStyle}>유형</th>
              <th style={thStyle}>난이도</th>
              <th style={thStyle}>연령대</th>
              <th style={thStyle}>평균 시간(ms)</th>
              <th style={thStyle}>등록일</th>
              <th style={thStyle}>수정일</th>
            </tr>
          </thead>
          <tbody>
            {averageStats.map((item) => (
              <tr key={item.averageQuestionSolvingTimeNo}>
                <td style={tdStyle}>{item.averageQuestionSolvingTimeNo}</td>
                <td style={tdStyle}>{item.language}</td>
                <td style={tdStyle}>{item.category}</td>
                <td style={tdStyle}>{item.type}</td>
                <td style={tdStyle}>{item.level}</td>
                <td style={tdStyle}>{item.ageGroup}대</td>
                <td style={tdStyle}>{item.averageQuestionSolvingTime.toLocaleString()}</td>
                <td style={tdStyle}>{new Date(item.createdAt).toLocaleDateString()}</td>
                <td style={tdStyle}>{new Date(item.lastModifiedAt).toLocaleDateString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p>등록된 통계가 없습니다.</p>
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
};

const thStyle = {
  border: "1px solid #ccc",
  padding: "8px",
  backgroundColor: "#f2f2f2",
  textAlign: "left"
};

const tdStyle = {
  border: "1px solid #ddd",
  padding: "8px"
};

export default AdminAverageStat;