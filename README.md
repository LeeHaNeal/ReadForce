<div align="center">

# 📌 ReadForce
> "뉴스·문학 기반 문해력 진단 및 향상 웹 서비스"  

[![Backend](https://img.shields.io/badge/Backend-SpringBoot-blue)](https://spring.io/projects/spring-boot)
[![Frontend](https://img.shields.io/badge/Frontend-React-lightblue)](https://react.dev/)
[![Database](https://img.shields.io/badge/Database-PostgreSQL-blue)](https://www.postgresql.org/)
[![AI](https://img.shields.io/badge/AI-Gemini-orange)](https://www.openai.com/)
[![Infra](https://img.shields.io/badge/Infra-AWS-orange)](https://aws.amazon.com/)

</div>

---

## ✨ 프로젝트 소개
AI 기반 뉴스·문학 콘텐츠를 분석해 초/중/고급 문해력 문제를 자동 생성하고,  
사용자 맞춤형 학습과 점수 기반 랭킹 시스템을 제공하여 문해력을 진단·향상시키는 웹 서비스입니다.

---

## 🛠 사용 기술 (Tech Stack)
| 구분 | 기술 |
|:---:|:---|
| Backend | Spring Boot, Java 17, Lombok, JWT, Spring Security |
| Frontend | React, Node.js, Redux |
| Database | PostgreSQL, Redis |
| Infra | AWS (EC2, S3, RDS, CloudFront) |
| External API | Gemini API (문제·뉴스 자동 생성) |

---

## 👥 팀원 및 역할
| 이름 | 역할 |
|:---|:---|
| 이하늘 | 팀장 / 백엔드 (API 설계, 문제 생성 로직, 점수/랭킹 시스템) |
| 김제현 | 백엔드 (DB 구조 설계, 서비스 로직) |
| 최한솔 | 프론트엔드 (UI/UX, 학습 화면 구현) |
| 김기찬 | 프론트엔드 (문제 풀이/추천 UI, 통계 화면) |
| 정웅태 | 프론트엔드 (랭킹/관리자 페이지) |

---

## 🔥 주요 기능
### ✅ 회원 & 인증
- 이메일/소셜 로그인, 회원가입, 비밀번호 재설정
- JWT 기반 인증 및 권한 관리

### ✅ 마이페이지
- 프로필 관리, 출석 체크
- 학습 이력 및 실력 분석
- 도전 결과 및 랭킹 확인

### ✅ 문해력 학습
- 기사·소설·동화 기반 학습
- 언어/카테고리/난이도 선택 가능
- 오늘의 도전 문제 제공 (하루 1회 제한)

### ✅ 문제 풀이 & 추천
- 적응형 학습: 취약 유형 자동 추천
- 정답률 기반 피드백 제공
- 자동 문제 생성 (Gemini API 연동)

### ✅ 랭킹 시스템
- 주간/일간 랭킹 제공
- 카테고리별 점수 합산 → 점수 → 포인트 → 랭킹 반영

### ✅ 관리자 기능
- 회원, 콘텐츠, 문제 관리
- AI 기반 뉴스/문제 자동 생성

---

## 💡 Problem Solving / 성과
- Gemini API 불규칙 JSON 파싱 오류 해결 → 안정적 문제 생성 시스템 구현  
- 난이도별 점수 자동 배점 및 포인트 환산 설계 → 랭킹 시스템 안정화  
- DB 정규화 및 트랜잭션 설계 → 데이터 무결성 확보  
- 오늘의 도전, 대용량 요청 처리 테스트 → 서버 안정성 검증  

---

## 📂 프로젝트 구조
```text
ReadForce/
├─ backend/
│  ├─ src/main/java/com/readforce
│  ├─ src/main/resources
│  └─ Dockerfile
├─ frontend/
│  ├─ src/
│  └─ package.json
├─ infra/
│  ├─ aws-ec2/
│  └─ s3/
└─ README.md


<div align="center">

## 📊 GitHub Stats
![HaNeal's GitHub stats](https://github-readme-stats.vercel.app/api?username=LeeHaNeal&show_icons=true&theme=tokyonight)
![Top Langs](https://github-readme-stats.vercel.app/api/top-langs/?username=LeeHaNeal&layout=compact&theme=tokyonight)

</div>
