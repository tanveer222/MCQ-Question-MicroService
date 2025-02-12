package com.dark.quiz_service.service;


import com.dark.quiz_service.dao.QuizDao;
import com.dark.quiz_service.feign.QuizInterface;
import com.dark.quiz_service.model.QuestionWrapper;
import com.dark.quiz_service.model.Quiz;
import com.dark.quiz_service.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
public class QuizService {
    @Autowired
    QuizDao quizDao;
    @Autowired
    QuizInterface quizInterface;

    public ResponseEntity<String> createQuiz(String category, int numQ, String title) {

        try {
            List<Integer> questions = quizInterface.getQuestionForQuiz(category, numQ).getBody();
            Quiz quiz = new Quiz();
            quiz.setTitle(title);
            quiz.setQuestionIds(questions);

            quizDao.save(quiz);

            return new ResponseEntity<>("Success", HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
        try {
            Quiz quiz = quizDao.findById(id).get();
            List<Integer> questionIds = quiz.getQuestionIds();
            ResponseEntity<List<QuestionWrapper>> questions = quizInterface.getQuestionFromId(questionIds);
            return questions;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
        ResponseEntity<Integer> result = quizInterface.getScore(responses);
        return result;
    }
}
