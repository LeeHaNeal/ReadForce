import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

import Main from "./pages/main";
import MyPage from './pages/myinfo/MyPage';
import LayOut from "./components/layout";
import ArticlePage from './pages/article/ArticlePage';
import SignupWithEmail from "./pages/signup/signupwithemail";
import SignupChoice from "./pages/signup/signupchoice";
import EmailVerifyPage from "./pages/signup/emailverifypage";
import SignupCompletePage from "./pages/signup/signupcompletepage";
import Socialsignup from './pages/signup/socialsignup';
import Login from "./pages/login/login";
import FindPassword from "./pages/login/findpassword";
import ResetPassword from "./pages/login/resetpassword";
import Oauth2redirect from './pages/login/oauth2redirect';
import Authcallback from './pages/login/authcallback';
import ProfileEditPage from './pages/myinfo/ProfileEditPage';
import ChangePasswordPage from './pages/myinfo/ChangePasswordPage';
import ReadTest from './pages/challenge/readtest';
import ChallengePage from "./pages/challenge/challengepage";
import AdminPage from './pages/adminpages/adminpage';
import ChallengeQuizPage from './pages/challenge/challengeQuizPage';
import ArticleQuestionPage from './pages/article/ArticleQuestionPage';
import ArticleResultPage from './pages/article/ArticleResultPage';
import AdminNews from './pages/adminpages/adminnews';
import AdminNewsDetail from './pages/adminpages/adminnewsdetail';
import AdminLiterature from './pages/adminpages/adminliterature';
import AdminLiteratureDetail from './pages/adminpages/adminliteraturedetail';
import AdminAddParagraph from './pages/adminpages/adminaddparagraph';
import AdminUserInfo from './pages/adminpages/adminuserinifo';
import AdminUserAttendance from './pages/adminpages/adminuserattendance';
import TestQuestionPage from './pages/challenge/testquestionpage';
import TestResultPage from './pages/challenge/testresultpage';
import TestReviewPage from './pages/challenge/testreviewpage';
import RankingPage from './pages/challenge/RankingPage';
import ChallengeResultPage from './pages/challenge/ChallengeResultPage';
import NovelPage from './pages/literature/NovelPage';
import FairyTalePage from './pages/literature/FairyTalePage';
import LiteratureQuizPage from './pages/literature/LiteratureQuestionPage';
import LiteratureResultPage from './pages/literature/LiteratureResultPage';
import AdaptiveLearningPage from './pages/adaptive/AdaptiveLearningPage';
import AdaptiveQuizPage from './pages/adaptive/AdaptiveQuizPage';
import AdaptiveResultPage from './pages/adaptive/AdaptiveResultPage';

function App() {
  return (
    <Router>
      <Routes>
        <Route element={<LayOut />}>
          <Route path="/" element={<Main />} />
          <Route path="/login" element={<Login />} />
          <Route path="/findpassword" element={<FindPassword />} />
          <Route path="/resetpassword" element={<ResetPassword />} />
          <Route path="/mypage" element={<MyPage />} />
          <Route path="/article" element={<ArticlePage />} />
          <Route path="/signup/signupchoice" element={<SignupChoice />} />
          <Route path="/signup" element={<SignupWithEmail />} />
          <Route path="/signup/emailverifypage" element={<EmailVerifyPage />} />
          <Route path="/signup/signupcompletepage" element={<SignupCompletePage />} />
          <Route path="/challenge" element={<ChallengePage />} />
          <Route path="/challenge/quiz" element={<ChallengeQuizPage />} />
          <Route path="/challenge/today" element={<ChallengeQuizPage />} />
          <Route path="/challenge/result" element={<ChallengeResultPage />} />
          <Route path="/oauth2/redirect" element={<Oauth2redirect />} />
          <Route path="/authcallback" element={<Authcallback />} />
          <Route path="/social-sign-up" element={<Socialsignup />} />
          <Route path="/profile-edit" element={<ProfileEditPage />} />
          <Route path="/change-password" element={<ChangePasswordPage />} />
          <Route path="/test-start" element={<ReadTest />} />
          <Route path="/adminpage" element={<AdminPage />} />
          <Route path="/question/:id" element={<ArticleQuestionPage />} />
          <Route path="/question-result" element={<ArticleResultPage />} />
          <Route path="/adminpage/adminnews" element={<AdminNews />} />
          <Route path="/adminpage/adminnews/:newsNo" element={<AdminNewsDetail />} />
          <Route path="/adminpage/adminliterature" element={<AdminLiterature />} />
          <Route path="/adminpage/adminliterature/:literatureNo" element={<AdminLiteratureDetail />} />
          <Route path="/adminpage/adminliterature/add-paragraph" element={<AdminAddParagraph />} />
          <Route path="/adminpage/adminliterature/:literatureNo/add-paragraph" element={<AdminAddParagraph />} />
          <Route path="/adminpage/adminuserinfo/:email" element={<AdminUserInfo />} />
          <Route path="/adminpage/adminuserinfo/:email/attendance" element={<AdminUserAttendance />} />
          <Route path="/test-question" element={<TestQuestionPage />} />
          <Route path="/test-result" element={<TestResultPage />} />
          <Route path="/test-review" element={<TestReviewPage />} />
          <Route path="/ranking" element={<RankingPage />} />
          <Route path="/literature/novel" element={<NovelPage />} />
          <Route path="/literature/fairytale" element={<FairyTalePage />} />
          <Route path="/literature-quiz/:quizId" element={<LiteratureQuizPage />} />
          <Route path="/literature-result" element={<LiteratureResultPage />} />
          <Route path="/adaptive-learning" element={<AdaptiveLearningPage />} />
          <Route path="/adaptive-learning/start" element={<AdaptiveQuizPage />} />
          <Route path="/adaptive-learning/result" element={<AdaptiveResultPage />} />
        </Route>
      </Routes>
    </Router>
  );
}

export default App;
