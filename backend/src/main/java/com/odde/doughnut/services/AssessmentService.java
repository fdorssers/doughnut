package com.odde.doughnut.services;

import static com.odde.doughnut.controllers.dto.ApiError.ErrorType.ASSESSMENT_SERVICE_ERROR;

import com.odde.doughnut.algorithms.TimestampUtil;
import com.odde.doughnut.controllers.dto.AnswerSubmission;
import com.odde.doughnut.controllers.dto.AssessmentHistory;
import com.odde.doughnut.controllers.dto.AssessmentResult;
import com.odde.doughnut.entities.*;
import com.odde.doughnut.exceptions.ApiException;
import com.odde.doughnut.factoryServices.ModelFactoryService;
import com.odde.doughnut.models.UserModel;
import com.odde.doughnut.testability.TestabilitySettings;
import com.theokanning.openai.client.OpenAiApi;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AssessmentService {
  private final ModelFactoryService modelFactoryService;
  private final QuizQuestionService quizQuestionService;
  private final TestabilitySettings testabilitySettings;

  public AssessmentService(
      OpenAiApi openAiApi,
      ModelFactoryService modelFactoryService,
      TestabilitySettings testabilitySettings) {
    this.modelFactoryService = modelFactoryService;
    this.testabilitySettings = testabilitySettings;
    this.quizQuestionService = new QuizQuestionService(openAiApi, modelFactoryService);
  }

  public List<QuizQuestion> generateAssessment(Notebook notebook) {
    List<Note> notes = testabilitySettings.getRandomizer().shuffle(notebook.getNotes());

    List<QuizQuestionAndAnswer> questions =
        notes.stream()
            .map(quizQuestionService::selectRandomQuestionForANote)
            .filter(Objects::nonNull)
            .filter(QuizQuestionAndAnswer::isApproved)
            .toList();

    Integer numberOfQuestion = notebook.getNotebookSettings().getNumberOfQuestionsInAssessment();
    if (numberOfQuestion == null || numberOfQuestion == 0) {
      throw new ApiException(
          "The assessment is not available",
          ASSESSMENT_SERVICE_ERROR,
          "The assessment is not available");
    }

    if (questions.size() < numberOfQuestion) {
      throw new ApiException(
          "Not enough questions", ASSESSMENT_SERVICE_ERROR, "Not enough questions");
    }

    return questions.stream()
        .limit(numberOfQuestion)
        .map(QuizQuestionAndAnswer::getQuizQuestion)
        .toList();
  }

  public AssessmentResult submitAssessmentResult(
      User user,
      Notebook notebook,
      List<AnswerSubmission> answerSubmission,
      Timestamp currentUTCTimestamp) {
    AssessmentAttempt assessmentAttempt = new AssessmentAttempt();
    assessmentAttempt.setUser(user);
    assessmentAttempt.setNotebook(notebook);
    assessmentAttempt.setAnswersTotal(answerSubmission.size());
    assessmentAttempt.setSubmittedAt(currentUTCTimestamp);

    int totalCorrectAnswer =
        (int) answerSubmission.stream().filter(AnswerSubmission::isCorrectAnswers).count();
    assessmentAttempt.setAnswersCorrect(totalCorrectAnswer);

    modelFactoryService.save(assessmentAttempt);

    AssessmentResult assessmentResult = new AssessmentResult();
    assessmentResult.setTotalCount(answerSubmission.size());
    assessmentResult.setCorrectCount(totalCorrectAnswer);
    return assessmentResult;
  }

  public List<AssessmentHistory> getAssessmentHistory(User user) {
    List<AssessmentHistory> assessmentHistories = new ArrayList<>();
    modelFactoryService
        .assessmentAttemptRepository
        .findAll()
        .forEach(
            aa -> {
              if (Objects.equals(aa.getUser().getId(), user.getId())) {
                String result =
                    ((double) aa.getAnswersCorrect() / aa.getAnswersTotal()) >= 0.8
                        ? "Pass"
                        : "Fail";
                AssessmentHistory ah =
                    new AssessmentHistory(
                        aa.getId(),
                        aa.getNotebook().getHeadNote().getTopicConstructor(),
                        aa.getSubmittedAt(),
                        result);
                assessmentHistories.add(ah);
              }
            });
    return assessmentHistories;
  }

  public Certificate getCertificate(AssessmentAttempt assessmentAttempt, UserModel currentUser) {
    Optional<Certificate> optionalCertificate =
        modelFactoryService.certificateRepository.findFirstByNotebookAndUserOrderByExpiryDateDesc(
            assessmentAttempt.getNotebook(), currentUser.getEntity());
    return optionalCertificate.orElse(generateCertificate(assessmentAttempt, currentUser));
  }

  private Certificate generateCertificate(
      AssessmentAttempt assessmentAttempt, UserModel currentUser) {
    Certificate certificate = new Certificate();
    certificate.setNotebook(assessmentAttempt.getNotebook());
    certificate.setUser(currentUser.getEntity());
    certificate.setExpiryDate(
        TimestampUtil.addYearsToTimestamp(assessmentAttempt.getSubmittedAt()));
    return modelFactoryService.save(certificate);
  }
}
