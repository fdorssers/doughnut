package com.odde.doughnut.models;

import static java.util.Objects.isNull;

import com.odde.doughnut.controllers.json.QuestionSuggestionCreationParams;
import com.odde.doughnut.controllers.json.QuestionSuggestionParams;
import com.odde.doughnut.entities.QuizQuestionEntity;
import com.odde.doughnut.entities.SuggestedQuestionForFineTuning;
import com.odde.doughnut.factoryServices.ModelFactoryService;

public class SuggestedQuestionForFineTuningService {
  private final SuggestedQuestionForFineTuning entity;
  private final ModelFactoryService modelFactoryService;

  public SuggestedQuestionForFineTuningService(
      SuggestedQuestionForFineTuning suggestion, ModelFactoryService modelFactoryService) {
    this.entity = suggestion;
    this.modelFactoryService = modelFactoryService;
  }

  public SuggestedQuestionForFineTuning create(
      QuizQuestionEntity quizQuestionEntity, QuestionSuggestionCreationParams params)
      throws Exception {
    entity.setQuizQuestion(quizQuestionEntity);
    entity.setPreservedQuestion(quizQuestionEntity.getMcqWithAnswer());
    entity.setComment(params.comment);
    if (isNull(params.isPositive)) throw new Exception("Feedback cannot be null.");
    entity.setPositiveFeedback(params.isPositive);
    return save();
  }

  public SuggestedQuestionForFineTuning update(QuestionSuggestionParams params) {
    entity.setPreservedQuestion(params.preservedQuestion);
    entity.setComment(params.comment);
    return save();
  }

  private SuggestedQuestionForFineTuning save() {
    return modelFactoryService.questionSuggestionForFineTuningRepository.save(entity);
  }
}
