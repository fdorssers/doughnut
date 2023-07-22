package com.odde.doughnut.models.quizFacotries;

import com.odde.doughnut.entities.Answer;
import com.odde.doughnut.entities.Link;
import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.QuizQuestionEntity;

public class LinkTargetQuizPresenter extends QuizQuestionWithOptionsPresenter {
  protected final Link link;
  protected final Note answerNote;

  @Override
  public boolean isAnswerCorrect(Answer answer) {
    return answerNote.getId().equals(answer.getAnswerNoteId());
  }

  public LinkTargetQuizPresenter(QuizQuestionEntity quizQuestion) {
    this.link = quizQuestion.getReviewPoint().getLink();
    this.answerNote = link.getTargetNote();
  }

  @Override
  public String mainTopic() {
    return "";
  }

  @Override
  public String instruction() {
    return "<mark>"
        + link.getSourceNote().getTitle()
        + "</mark> is "
        + link.getLinkTypeLabel()
        + ":";
  }
}
