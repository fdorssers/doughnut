package com.odde.doughnut.models.quizFacotries;

import com.odde.doughnut.entities.Thingy;
import java.util.List;

public interface QuestionOptionsFactory {
  Thingy generateAnswer();

  List<? extends Thingy> generateFillingOptions();

  default int minimumOptionCount() {
    return 2;
  }
  ;
}
