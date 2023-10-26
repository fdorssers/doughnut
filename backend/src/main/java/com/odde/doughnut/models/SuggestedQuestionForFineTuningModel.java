package com.odde.doughnut.models;

import com.odde.doughnut.controllers.json.QuestionSuggestionCreationParams;
import com.odde.doughnut.controllers.json.QuestionSuggestionParams;
import com.odde.doughnut.entities.QuizQuestionEntity;
import com.odde.doughnut.entities.SuggestedQuestionForFineTuning;
import com.odde.doughnut.entities.User;
import com.odde.doughnut.factoryServices.ModelFactoryService;
import java.sql.Timestamp;

public class SuggestedQuestionForFineTuningModel {
  private final SuggestedQuestionForFineTuning entity;
  private final ModelFactoryService modelFactoryService;

  public SuggestedQuestionForFineTuningModel(
      SuggestedQuestionForFineTuning suggestion, ModelFactoryService modelFactoryService) {
    this.entity = suggestion;
    this.modelFactoryService = modelFactoryService;
  }

  public SuggestedQuestionForFineTuning update(QuestionSuggestionParams params) {
    entity.setPreservedQuestion(params.preservedQuestion);
    entity.setComment(params.comment);
    return save();
  }

  private SuggestedQuestionForFineTuning save() {
    return modelFactoryService.questionSuggestionForFineTuningRepository.save(entity);
  }

  public SuggestedQuestionForFineTuning suggestQuestionForFineTuning(
      QuizQuestionEntity quizQuestion,
      QuestionSuggestionCreationParams suggestionCreationParams,
      User user,
      Timestamp currentUTCTimestamp) {
    entity.setUser(user);
    entity.setCreatedAt(currentUTCTimestamp);
    entity.preserveNoteContent(quizQuestion.getThing().getNote());
    entity.setPreservedQuestion(quizQuestion.getMcqWithAnswer());
    entity.setComment(suggestionCreationParams.comment);
    entity.setPositiveFeedback(suggestionCreationParams.isPositiveFeedback);
    entity.setDuplicated(false);
    save();

    return entity;
  }

  public SuggestedQuestionForFineTuning duplicate() {
    SuggestedQuestionForFineTuning newObject = new SuggestedQuestionForFineTuning();
    newObject.setUser(entity.getUser());
    newObject.setPreservedQuestion(entity.getPreservedQuestion());
    newObject.setPreservedNoteContent(entity.getPreservedNoteContent());
    newObject.setComment(entity.getComment());
    newObject.setPositiveFeedback(entity.isPositiveFeedback());
    newObject.setDuplicated(true);
    modelFactoryService.questionSuggestionForFineTuningRepository.save(newObject);
    return newObject;
  }
}
