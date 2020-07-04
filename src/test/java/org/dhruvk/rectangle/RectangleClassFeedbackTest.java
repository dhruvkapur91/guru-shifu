package org.dhruvk.rectangle;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

// TODO - need to find a way to detect someone playing the system.. that'll be when they just click submit to get all the feedback... or something similar.. that's why its really important to capture things on timeline...

public class RectangleClassFeedbackTest {

    @Test
    void shouldGiveFeedbackIfThereIsNoConstructor() {
        String sourceCode = """
                class Rectangle {
                   public double getArea() {}
                }
                """;

        Set<String> feedbacks = new RectangleClassFeedback(sourceCode).suggestionKey();
        assertThat(feedbacks, is(Set.of("NO_CONSTRUCTOR_FOUND", "NO_CONSTRUCTOR_PARAMETER")));
    }

    @Test
    void shouldGiveFeedbackIfThereAreNoParametersInConstructor() {
        String sourceCode = """
                class Rectangle {
                   public Rectangle() {}
                                
                   public double getArea() {}
                }
                """;

        Set<String> feedbacks = new RectangleClassFeedback(sourceCode).suggestionKey();
        assertThat(feedbacks, is(Set.of("NO_CONSTRUCTOR_PARAMETER")));
    }

    @Test
    void shouldGiveFeedbackIfThereIsOnlyOneParametersInConstructor() {
        String sourceCode = """
                class Rectangle {
                   public Rectangle(int x) {}
                                
                   public double getArea() {}
                }
                """;

        Set<String> feedbacks = new RectangleClassFeedback(sourceCode).suggestionKey();
        assertThat(feedbacks, is(Set.of("ONLY_ONE_CONSTRUCTOR_PARAMETER")));
    }

    @Test
    @Tag("ToRemove")
    void shouldBeUnknownScenario() {
        String sourceCode = """
                class Rectangle {
                   
                   public Rectangle(int length, int breath) {}
                                
                   public double getArea() {}
                }
                """;

        Set<String> feedbacks = new RectangleClassFeedback(sourceCode).suggestionKey();
        assertThat(feedbacks, is(Set.of("UNKNOWN_SCENARIO")));
    }
}
