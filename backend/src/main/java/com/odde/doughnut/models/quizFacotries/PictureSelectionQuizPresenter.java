package com.odde.doughnut.models.quizFacotries;

import com.odde.doughnut.entities.Answer;
import com.odde.doughnut.entities.QuizQuestionEntity;
import com.odde.doughnut.entities.ReviewPoint;
import com.odde.doughnut.entities.Thing;
import com.odde.doughnut.entities.json.QuizQuestion;
import java.util.List;
import java.util.stream.Stream;

public class PictureSelectionQuizPresenter extends QuizQuestionWithOptionsPresenter {

  private ReviewPoint reviewPoint;

  @Override
  public boolean isAnswerCorrect(Answer answer) {
    return reviewPoint.getNote().getId().equals(answer.getAnswerNoteId());
  }

  public PictureSelectionQuizPresenter(QuizQuestionEntity quizQuestion) {
    this.reviewPoint = quizQuestion.getReviewPoint();
  }

  @Override
  public String mainTopic() {
    return reviewPoint.getNote().getTitle();
  }

  @Override
  public String instruction() {
    return "";
  }

  @Override
  protected List<QuizQuestion.Option> getOptionsFromThings(Stream<Thing> noteStream) {
    return noteStream
        .map(
            thing -> {
              QuizQuestion.Option option = new QuizQuestion.Option();
              option.setNoteId(thing.getNote().getId());
              option.setDisplay(thing.getNote().getTitle());
              option.setPictureWithMask(thing.getNote().getPictureWithMask().orElse(null));
              option.setPicture(true);
              return option;
            })
        .toList();
  }
}
