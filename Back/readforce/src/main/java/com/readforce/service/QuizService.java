package com.readforce.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readforce.dto.LiteratureDto.GetChallengeLiteratureQuiz;
import com.readforce.dto.MemberDto.MemberAttemptedQuiz;
import com.readforce.dto.MemberDto.MemberIncorrectQuiz;
import com.readforce.dto.NewsDto.GetChallengeNewsQuiz;
import com.readforce.dto.QuizDto.GetQuiz;
import com.readforce.dto.QuizDto.IncorrectQuiz;
import com.readforce.entity.LiteratureQuiz;
import com.readforce.entity.LiteratureQuizAttempt;
import com.readforce.entity.NewsQuiz;
import com.readforce.entity.NewsQuizAttempt;
import com.readforce.enums.Classification;
import com.readforce.enums.Level;
import com.readforce.enums.MessageCode;
import com.readforce.exception.ResourceNotFoundException;
import com.readforce.repository.LiteratureQuizAttemptRepository;
import com.readforce.repository.LiteratureQuizRepository;
import com.readforce.repository.NewsQuizAttemptRepository;
import com.readforce.repository.NewsQuizRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class QuizService {
	
	private final NewsQuizAttemptRepository news_quiz_attempt_repository;
	private final LiteratureQuizAttemptRepository literature_quiz_attempt_repository;
	private final NewsQuizRepository news_quiz_repository;
	private final LiteratureQuizRepository literature_quiz_repository;

	

	// 사용자가 푼 최근 문제 10개 가져오기
	@Transactional(readOnly = true)
	public List<MemberAttemptedQuiz> getMemberSolvedQuizList10(String email) {
		
		// 사용자가 풀은 뉴스 문제 가져오기
		List<NewsQuizAttempt> news_quiz_attempt_list = news_quiz_attempt_repository.findByEmailWithNewQuizList(email);
		
		// 사용자가 푼 문학 문제 가져오기
		List<LiteratureQuizAttempt> literature_quiz_attempt_list = literature_quiz_attempt_repository.getMemberSolvedLiteratureQuizList(email);
		
		if(news_quiz_attempt_list.isEmpty() && literature_quiz_attempt_list.isEmpty()) {
			
			if(news_quiz_attempt_list.isEmpty()) {
				
				throw new ResourceNotFoundException(MessageCode.NEWS_QUIZ_ATTEMPT_NOT_FOUND);
				
			}
			
			
			if(literature_quiz_attempt_list.isEmpty()) {
				
				throw new ResourceNotFoundException(MessageCode.LITERATURE_QUIZ_ATTEMPT_NOT_FOUND);
				
			}
			
		}
		
		// 합치기
		List<MemberAttemptedQuiz> member_attempted_quiz = new ArrayList<>();
		
		// 뉴스 문제
		for(NewsQuizAttempt news_quiz_attempt : news_quiz_attempt_list) {
			
			MemberAttemptedQuiz member_attempt_quiz = new MemberAttemptedQuiz();
			member_attempt_quiz.setQuestion_text(news_quiz_attempt.getNews_quiz().getQuestion_text());
			member_attempt_quiz.setClassification(Classification.NEWS.toString());
			member_attempt_quiz.setCreated_date(news_quiz_attempt.getCreated_date());
			
			member_attempted_quiz.add(member_attempt_quiz);
			
		}
		
		// 문학 문제
		for(LiteratureQuizAttempt literature_quiz_attempt : literature_quiz_attempt_list) {
			
			MemberAttemptedQuiz member_attempt_quiz = new MemberAttemptedQuiz();
			member_attempt_quiz.setQuestion_text(literature_quiz_attempt.getLiterature_quiz().getQuestion_text());
			member_attempt_quiz.setClassification(Classification.LITERATURE.toString());
			member_attempt_quiz.setCreated_date(literature_quiz_attempt.getCreated_date());
			
			member_attempted_quiz.add(member_attempt_quiz);
			
		}
		
		// 내림차순 정렬
		member_attempted_quiz.sort(Comparator.comparing(MemberAttemptedQuiz::getCreated_date).reversed());
		
		// 상위 10개만 반환
		int size = member_attempted_quiz.size();
		List<MemberAttemptedQuiz> final_member_attempted_quiz_list = member_attempted_quiz.subList(0, Math.min(size, 10));
		
		return final_member_attempted_quiz_list;

	}


	// 틀린 문제 가져오기(최신순)
	@Transactional(readOnly = true)
	public List<MemberIncorrectQuiz> getMemberIncorrectQuizList(String email) {

		// 사용자가 풀은 뉴스 문제중 틀린 문제 가져오기
		List<NewsQuizAttempt> incorrect_news_quiz_attempt = news_quiz_attempt_repository.getIncorrectNewQuizAttemptList(email);
		
		// 사용자가 풀은 문학 문제중 틀린 문제 가져오기
		List<LiteratureQuizAttempt> incorrect_literature_quiz_attempt = literature_quiz_attempt_repository.getIncorrectLiteratureQuizAttemptList(email);
		
		// 합치기
		List<MemberIncorrectQuiz> member_incorrect_quiz_list = new ArrayList<>();
		
		// 뉴스 문제
		for(NewsQuizAttempt news_quiz_attempt : incorrect_news_quiz_attempt) {
			
			MemberIncorrectQuiz member_incorrect_quiz = new MemberIncorrectQuiz();
			member_incorrect_quiz.setQuestion_text(news_quiz_attempt.getNews_quiz().getQuestion_text());
			member_incorrect_quiz.setQuiz_no(news_quiz_attempt.getNews_quiz().getNews_quiz_no());
			member_incorrect_quiz.setClassification(Classification.NEWS.toString());
			member_incorrect_quiz.setCreated_date(news_quiz_attempt.getCreated_date());
			
			member_incorrect_quiz_list.add(member_incorrect_quiz);
			
		}
		
		// 문학 문제
		for(LiteratureQuizAttempt literature_quiz_attempt : incorrect_literature_quiz_attempt) {
			
			MemberIncorrectQuiz member_incorrect_quiz = new MemberIncorrectQuiz();
			member_incorrect_quiz.setQuestion_text(literature_quiz_attempt.getLiterature_quiz().getQuestion_text());
			member_incorrect_quiz.setQuiz_no(literature_quiz_attempt.getLiterature_quiz().getLiterature_quiz_no());
			member_incorrect_quiz.setClassification(Classification.LITERATURE.toString());
			member_incorrect_quiz.setCreated_date(literature_quiz_attempt.getCreated_date());
			
			member_incorrect_quiz_list.add(member_incorrect_quiz);
			
		}
		
		// 내림차순 정렬
		member_incorrect_quiz_list.sort(Comparator.comparing(MemberIncorrectQuiz::getCreated_date).reversed());
		
		return member_incorrect_quiz_list;
		
	}
	
	// 퀴즈 ID 목록을 가져와 무작위로 섞은 뒤, 필요한 개수만큼 ID를 반환
	private List<Long> getRandomQuizIdList(List<Long> all_id_list, int required_number, String exception_message){
		
		if(all_id_list.size() < required_number) {
			
			throw new ResourceNotFoundException(exception_message);
			
		}
		
		Collections.shuffle(all_id_list);
		
		return all_id_list.subList(0, required_number);
		
	}

	// 뉴스 도전 문제 가져오기
	@Transactional(readOnly = true)
	public List<GetChallengeNewsQuiz> getChallengeQuizWithNewsQuiz(String language) {
		
		// 뉴스 초급과 언어에 해당하는 뉴스 퀴즈 번호 7개 랜덤으로 가져오기
		List<Long> beginner_news_quiz_no_list = getRandomQuizIdList(
				news_quiz_repository.findNewsQuizNoByLevelAndLanguage(Level.BEGINNER.toString(), language),
				7,
				MessageCode.LACK_OF_BEGINNER_NEWS_QUIZ
		);

		// 뉴스 중급과 언어에 해당하는 뉴스 퀴즈 번호 6개 랜덤으로 가져오기
		List<Long> intermediate_news_quiz_no_list = getRandomQuizIdList(
				news_quiz_repository.findNewsQuizNoByLevelAndLanguage(Level.INTERMEDIATE.toString(), language),
				6,
				MessageCode.LACK_OF_INTERMEDIATE_NEWS_QUIZ
		);
		
		// 뉴스 고급과 언어에 해당하는 뉴스 퀴즈 번호 7개 랜덤으로 가져오기
		List<Long> advanced_news_quiz_no_list = getRandomQuizIdList(
				news_quiz_repository.findNewsQuizNoByLevelAndLanguage(Level.ADVANCED.toString(), language),
				7,
				MessageCode.LACK_OF_ADVANCED_NEWS_QUIZ
		);
		
		// 뉴스 퀴즈 번호 합치기
		List<Long> combined_news_quiz_no_list = Stream.of(beginner_news_quiz_no_list, intermediate_news_quiz_no_list, advanced_news_quiz_no_list)
				.flatMap(List::stream)
				.collect(Collectors.toList());
		
		// 합친 뉴스 퀴즈 번호에 해당하는 뉴스 퀴즈 가져오기
		List<NewsQuiz> combined_news_quiz_list = news_quiz_repository.findAllById(combined_news_quiz_no_list);
		
		// DTO에 넣기
		List<GetChallengeNewsQuiz> get_challenge_news_quiz_list = new ArrayList<>();
		
		for(NewsQuiz news_quiz : combined_news_quiz_list) {

			GetChallengeNewsQuiz get_challenge_news_quiz = new GetChallengeNewsQuiz();
			get_challenge_news_quiz.setNews_quiz_no(news_quiz.getNews_quiz_no());
			get_challenge_news_quiz.setQuestion_text(news_quiz.getQuestion_text());
			get_challenge_news_quiz.setTitle(news_quiz.getNews().getTitle());
			get_challenge_news_quiz.setChoice1(news_quiz.getChoice1());
			get_challenge_news_quiz.setChoice2(news_quiz.getChoice2());
			get_challenge_news_quiz.setChoice3(news_quiz.getChoice3());
			get_challenge_news_quiz.setChoice4(news_quiz.getChoice4());
			get_challenge_news_quiz.setCorrect_answer_index(news_quiz.getCorrect_answer_index());
			get_challenge_news_quiz.setExplanation(news_quiz.getExplanation());
			get_challenge_news_quiz.setLevel(news_quiz.getNews().getLevel());
			get_challenge_news_quiz.setScore(news_quiz.getScore());
			get_challenge_news_quiz.setNews_no(news_quiz.getNews_no());
			get_challenge_news_quiz.setContent(news_quiz.getNews().getContent());
			
			get_challenge_news_quiz_list.add(get_challenge_news_quiz);

		}

		return get_challenge_news_quiz_list;

	}

	// 문학 도전 문제 가져오기
	@Transactional(readOnly = true)
	public List<GetChallengeLiteratureQuiz> getChallengeQuizWithLiteratureQuiz(String type) {
		
		// 문학 초급과 타입에 해당하는 문학 퀴즈 번호 7개 랜덤으로 가져오기
		List<Long> beginner_literature_quiz_no_list = getRandomQuizIdList(
				literature_quiz_repository.findLiteratureQuizNoByLevelAndType(Level.BEGINNER.toString(), type),
				7,
				MessageCode.LACK_OF_BEGINNER_LITERATURE_QUIZ
		);

		// 문학 중급에 해당하는 문학 퀴즈 번호 6개 랜덤으로 가져오기
		List<Long> intermediate_literature_quiz_no_list = getRandomQuizIdList(
				literature_quiz_repository.findLiteratureQuizNoByLevelAndType(Level.INTERMEDIATE.toString(), type),
				6,
				MessageCode.LACK_OF_INTERMEDIATE_LITERATURE_QUIZ
		);
		
		// 문학 고급에 해당하는 문학 퀴즈 번호 7개 랜덤으로 가져오기
		List<Long> advanced_literature_quiz_no_list = getRandomQuizIdList(
				literature_quiz_repository.findLiteratureQuizNoByLevelAndType(Level.ADVANCED.toString(), type),
				7,
				MessageCode.LACK_OF_ADVANCED_LITERATURE_QUIZ
		);
		
		// 문학 퀴즈 번호 합치기
		List<Long> combined_literature_quiz_no_list = Stream.of(beginner_literature_quiz_no_list, intermediate_literature_quiz_no_list, advanced_literature_quiz_no_list)
				.flatMap(List::stream)
				.collect(Collectors.toList());
		
		// 합친 문학 퀴즈 번호에 해당하는 문학 퀴즈 가져오기
		List<LiteratureQuiz> combined_literature_quiz_list = literature_quiz_repository.findAllById(combined_literature_quiz_no_list);
		
		// DTO에 넣기
		List<GetChallengeLiteratureQuiz> get_challenge_literature_quiz_list = new ArrayList<>();
		
		for(LiteratureQuiz literature_quiz : combined_literature_quiz_list) {
			
			GetChallengeLiteratureQuiz get_challenge_literature_quiz = new GetChallengeLiteratureQuiz();
			get_challenge_literature_quiz.setChoice1(literature_quiz.getChoice1());
			get_challenge_literature_quiz.setChoice2(literature_quiz.getChoice2());
			get_challenge_literature_quiz.setChoice3(literature_quiz.getChoice3());
			get_challenge_literature_quiz.setChoice4(literature_quiz.getChoice4());
			get_challenge_literature_quiz.setCorrect_answer_index(literature_quiz.getCorrect_answer_index());
			get_challenge_literature_quiz.setExplanation(literature_quiz.getExplanation());
			get_challenge_literature_quiz.setLevel(literature_quiz.getLiterature_paragraph().getLevel());
			get_challenge_literature_quiz.setLiterature_no(literature_quiz.getLiterature_no());
			get_challenge_literature_quiz.setLiterature_paragraph_no(literature_quiz.getLiterature_paragraph_no());
			get_challenge_literature_quiz.setLiterature_quiz_no(literature_quiz.getLiterature_quiz_no());
			get_challenge_literature_quiz.setQuestion_text(literature_quiz.getQuestion_text());
			get_challenge_literature_quiz.setScore(literature_quiz.getScore());
			get_challenge_literature_quiz.setTitle(literature_quiz.getLiterature_paragraph().getLiterature().getTitle());
			
			get_challenge_literature_quiz_list.add(get_challenge_literature_quiz);
			
		}
		
		return get_challenge_literature_quiz_list;

	}


	// 가장 많이 틀린 문제 가져오기(뉴스 + 문학)(5개)
	@Transactional(readOnly = true)
	public List<GetQuiz> getMostIncorrectedQuiz() {

		// 뉴스 오답 통계 조회
		List<Object[]> news_result_list = news_quiz_attempt_repository.findIncorrectNewsQuizStatus();
		
		// 문학 오답 통계 조회
		List<Object[]> literature_result_list = literature_quiz_attempt_repository.findIncorrectLiteratureQuizStatus();
		
		List<IncorrectQuiz> combined_list = new ArrayList<>();
		
		// 뉴스 통합
		news_result_list.forEach(result -> 
			combined_list.add(new IncorrectQuiz(
					((Number) result[0]).longValue(),
					(String) result[1],
					Classification.NEWS.toString(),
					((Number) result[2]).longValue(),
					((Number) result[3]).longValue()
			))
		);
		
		// 문학 통합
		literature_result_list.forEach(result -> 
			combined_list.add(new IncorrectQuiz(
				((Number) result[0]).longValue(),
				(String) result[1],
				Classification.LITERATURE.toString(),
				((Number) result[2]).longValue(),
				((Number) result[3]).longValue()
			))
		);
		
		if(combined_list.isEmpty()) {
			
			return Collections.emptyList();
			
		}
		
		// 통합된 리스트를 "틀린 횟수"를 기준으로 내림차순 정렬 후 5개 선택
		List<IncorrectQuiz> top5_incorrect_quiz_list = 
				combined_list.stream()
				.sorted(Comparator.comparingLong(IncorrectQuiz::getIncorrect_count).reversed())
				.limit(5)
				.collect(Collectors.toList());
		
		// 최종적으로 반환할 DTO 리스트로 변환		
		return top5_incorrect_quiz_list.stream()
				.map(data -> {
					
					GetQuiz get_quiz = new GetQuiz();
					get_quiz.setQuestion_text(data.getQuestion_text());
					
					// 오답률 계산
					int percentage = (int) ((double) data.getIncorrect_count() / data.getTotal_count() * 100);
					get_quiz.setPercentage(percentage);
					
					// 분류에 따라 적절한 퀴즈 번호 설정
					if(Classification.NEWS.toString().equals(data.getClassification())) {
						get_quiz.setNews_quiz_no(data.getQuiz_no());					
					} else {
						get_quiz.setLiterature_quiz_no(data.getQuiz_no());
					}
					
					return get_quiz;
					
				})
				.collect(Collectors.toList());
		
	}

	// 사용자 전체 문제 정답률 구하
	@Transactional(readOnly = true)
	public int calculateUserCorrectRate(String email) {

	    // 뉴스 응시 이력 조회
	    List<NewsQuizAttempt> newsList = news_quiz_attempt_repository.findByEmailWithNewQuizList(email);
	    // 문학 응시 이력 조회
	    List<LiteratureQuizAttempt> literatureList = literature_quiz_attempt_repository.getMemberSolvedLiteratureQuizList(email);

	    long totalAttemptCount = newsList.size() + literatureList.size();
	    long correctCount = 0;

	    for (NewsQuizAttempt n : newsList) {
	    	if (Boolean.TRUE.equals(n.getIs_correct())) correctCount++;
	    }

	    for (LiteratureQuizAttempt l : literatureList) {
	    	if (Boolean.TRUE.equals(l.getIs_correct())) correctCount++;
	    }

	    if (totalAttemptCount == 0) return 0;

	    // 정답률 (0 ~ 100 사이 정수)
	    return (int) ((double) correctCount / totalAttemptCount * 100);
	}

}
