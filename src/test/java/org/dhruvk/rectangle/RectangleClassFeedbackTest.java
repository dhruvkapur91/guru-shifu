package org.dhruvk.rectangle;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RectangleClassFeedbackTest {

    @Test
    void shouldGiveFeedbackIfThereIsNoConstructor() {
        String sourceCode = """
                class Rectangle {
                   public double getArea() {}
                }
                """;

        Set<String> feedbacks = new RectangleClassFeedback(sourceCode).suggestionKey();
        assertThat(feedbacks, is(Set.of("NO_CONSTRUCTOR_FOUND")));
    }
}
