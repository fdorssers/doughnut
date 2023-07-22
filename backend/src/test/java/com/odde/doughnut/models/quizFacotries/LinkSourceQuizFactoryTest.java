package com.odde.doughnut.models.quizFacotries;

import static com.odde.doughnut.entities.QuizQuestionEntity.QuestionType.LINK_SOURCE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.odde.doughnut.entities.AnswerViewedByUser;
import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.ReviewPoint;
import com.odde.doughnut.entities.json.QuizQuestion;
import com.odde.doughnut.models.UserModel;
import com.odde.doughnut.testability.MakeMe;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:repository.xml"})
@Transactional
class LinkSourceQuizFactoryTest {
  @Autowired MakeMe makeMe;
  UserModel userModel;
  Note top;
  Note target;
  Note source;
  Note anotherSource;
  ReviewPoint reviewPoint;

  @BeforeEach
  void setup() {
    userModel = makeMe.aUser().toModelPlease();
    top = makeMe.aNote().creatorAndOwner(userModel).please();
    target = makeMe.aNote("sauce").under(top).please();
    source = makeMe.aNote("tomato sauce").under(top).linkTo(target).please();
    anotherSource = makeMe.aNote("blue cheese").under(top).please();
    reviewPoint = makeMe.aReviewPointFor(source.getLinks().get(0)).inMemoryPlease();
    makeMe.refresh(top);
  }

  @Test
  void shouldReturnNullIfCannotFindEnoughOptions() {
    makeMe.aLink().between(anotherSource, target).please();
    makeMe.refresh(top);
    assertThat(buildLinkTargetQuizQuestion(), is(nullValue()));
  }

  @Nested
  class WhenThereAreMoreThanOneOptions {
    @BeforeEach
    void setup() {
      makeMe.refresh(top);
    }

    @Test
    void shouldIncludeRightAnswers() {
      QuizQuestion quizQuestion = buildLinkTargetQuizQuestion();
      assertThat(
          quizQuestion.getDescription(),
          equalTo("Which one <em>is immediately a specialization of</em>:"));
      assertThat(quizQuestion.getMainTopic(), equalTo(target.getTitle()));
      List<String> options = toOptionStrings(quizQuestion);
      assertThat(anotherSource.getTitle(), in(options));
      assertThat("tomato sauce", in(options));
    }

    @Nested
    class Answer {
      @Test
      void correct() {
        AnswerViewedByUser answerResult =
            makeMe
                .anAnswerViewedByUser()
                .validQuestionOfType(LINK_SOURCE, reviewPoint)
                .answerWith(source)
                .inMemoryPlease();
        assertTrue(answerResult.correct);
      }
    }
  }

  private QuizQuestion buildLinkTargetQuizQuestion() {
    return makeMe.buildAQuestion(LINK_SOURCE, reviewPoint);
  }

  private List<String> toOptionStrings(QuizQuestion quizQuestion) {
    return quizQuestion.getOptions().stream().map(QuizQuestion.Option::getDisplay).toList();
  }
}
