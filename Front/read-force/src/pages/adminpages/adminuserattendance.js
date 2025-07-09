import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useNavigate } from 'react-router-dom';
import axiosInstance from "../../api/axiosInstance";

const AdminUserAttendance = () => {
    const navigate = useNavigate();
    const { email } = useParams();
    const [attendanceList, setAttendanceList] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [selectedDate, setSelectedDate] = useState(new Date().toISOString().substring(0, 10)); // yyyy-MM-dd

    // 출석 정보 가져오기
    const fetchAttendanceList = async () => {
        try {
            const res = await axiosInstance.get(`/admin/get-member-attendance-list`, {
                params: { email }
            });
            setAttendanceList(res.data);
        } catch (err) {
            console.error(err);
            alert("출석 정보를 불러오는 데 실패했습니다.");
        }
    };

    useEffect(() => {
        fetchAttendanceList();
    }, [email]);

    // 출석 추가
    const handleAddAttendance = async () => {
        try {
            await axiosInstance.post(`/admin/add-attendance`, null, {
                params: { email, date: selectedDate }
            });
            alert("출석이 추가되었습니다.");
            setShowModal(false);
            fetchAttendanceList();
        } catch (err) {
            console.error(err);
            alert("출석 추가 중 오류 발생");
        }
    };


    // 출석 삭제
    const handleDelete = async (attendanceNo) => {
        if (!window.confirm("정말 이 출석 기록을 삭제하시겠습니까?")) return;
        try {
            await axiosInstance.delete(`/admin/delete-attendance`, {
                params: { attendance_no: attendanceNo }
            });
            alert("삭제 완료되었습니다.");
            fetchAttendanceList();
        } catch (err) {
            console.error(err);
            alert("출석 삭제 중 오류가 발생했습니다.");
        }
    };


    return (
        <div>
            <button onClick={() => navigate(`/adminpage/adminuserinfo/${email}`)} style={backbtn}>뒤로가기</button>
            <h3>출석 내역</h3>
            <button onClick={() => setShowModal(true)} style={addBtn}>출석 추가</button>
            {attendanceList.length === 0 ? (
                <p>출석 기록이 없습니다.</p>
            ) : (
                <ul style={{ listStyle: "none", paddingLeft: 0 }}>
                    {attendanceList.map((a) => (
                        <li key={a.attendance_no} style={{ marginBottom: "8px" }}>
                            출석 날짜: {new Date(a.created_date).toLocaleDateString()}
                            <button
                                onClick={() => handleDelete(a.attendance_no)}
                                style={deleteBtn}
                            >
                                삭제
                            </button>
                        </li>
                    ))}
                </ul>
            )}
            {showModal && (
                <div style={modalOverlay}>
                    <div style={modalContent}>
                        <h4>출석 날짜 선택</h4>
                        <input
                            type="date"
                            value={selectedDate}
                            onChange={(e) => setSelectedDate(e.target.value)}
                            style={{ marginBottom: "12px" }}
                        />
                        <div>
                            <button onClick={handleAddAttendance} style={confirmBtn}>추가</button>
                            <button onClick={() => setShowModal(false)} style={cancelBtn}>취소</button>
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

const deleteBtn = {
    marginLeft: "12px",
    padding: "4px 8px",
    backgroundColor: "#dc3545",
    color: "white",
    border: "none",
    borderRadius: "4px",
    cursor: "pointer"
};

const addBtn = {
  marginBottom: "12px",
  padding: "8px 16px",
  backgroundColor: "#28a745",
  color: "white",
  border: "none",
  borderRadius: "4px",
  cursor: "pointer"
};

const modalOverlay = {
  position: "fixed",
  top: 0, left: 0, right: 0, bottom: 0,
  backgroundColor: "rgba(0,0,0,0.5)",
  display: "flex",
  justifyContent: "center",
  alignItems: "center"
};

const modalContent = {
  backgroundColor: "white",
  padding: "24px",
  borderRadius: "8px",
  width: "300px",
  textAlign: "center"
};

const confirmBtn = {
  padding: "8px 16px",
  marginRight: "10px",
  backgroundColor: "#007bff",
  color: "white",
  border: "none",
  borderRadius: "4px"
};

const cancelBtn = {
  padding: "8px 16px",
  backgroundColor: "#6c757d",
  color: "white",
  border: "none",
  borderRadius: "4px"
};

export default AdminUserAttendance;