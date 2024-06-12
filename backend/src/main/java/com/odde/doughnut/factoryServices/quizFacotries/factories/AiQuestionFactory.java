package com.odde.doughnut.factoryServices.quizFacotries.factories;

import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.QuizQuestion;
import com.odde.doughnut.factoryServices.quizFacotries.QuizQuestionFactory;
import com.odde.doughnut.factoryServices.quizFacotries.QuizQuestionNotPossibleException;
import com.odde.doughnut.factoryServices.quizFacotries.QuizQuestionServant;
import com.odde.doughnut.services.ai.AiQuestionGenerator;
import com.odde.doughnut.services.ai.MCQWithAnswer;

public class AiQuestionFactory extends QuizQuestionFactory {
  private Note note;
  AiQuestionGenerator aiQuestionGenerator;

  public AiQuestionFactory(Note note, AiQuestionGenerator questionGenerator) {
    this.note = note;
    this.aiQuestionGenerator = questionGenerator;
  }

  @Override
  public QuizQuestion buildValidQuizQuestion(QuizQuestionServant servant)
      throws QuizQuestionNotPossibleException {
    MCQWithAnswer MCQWithAnswer = aiQuestionGenerator.getAiGeneratedQuestion(note);
    if (MCQWithAnswer == null) {
      throw new QuizQuestionNotPossibleException();
    }
    return QuizQuestion.fromMCQWithAnswer(MCQWithAnswer, note);
  }

  @Override
  public QuizQuestion refineQuestion(MCQWithAnswer mcqWithAnswer)
      throws QuizQuestionNotPossibleException {
    MCQWithAnswer MCQWithAnswer =
        aiQuestionGenerator.getAiGeneratedRefineQuestion(note, mcqWithAnswer);
    if (MCQWithAnswer == null) {
      throw new QuizQuestionNotPossibleException();
    }
    return QuizQuestion.fromMCQWithAnswer(MCQWithAnswer, note);
  }
}
