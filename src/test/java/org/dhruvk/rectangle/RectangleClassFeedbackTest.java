package org.dhruvk.rectangle;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;

// TODO - need to find a way to detect someone playing the system.. that'll be when they just click submit to get all the feedback... or something similar.. that's why its really important to capture things on timeline...

public class RectangleClassFeedbackTest {

    @Test
    void shouldGiveFeedbackIfThereIsNoClass() {
        Set<String> feedbacks = new RectangleClassFeedback("").suggestionKey();
        assertThat(feedbacks, containsInAnyOrder(
                "NO_CONSTRUCTOR_FOUND",
                "NO_FIELDS_FOUND",
                "NO_CLASS_FOUND"
        ));
    }

    @Test
    void shouldGiveFeedbackIfThereIsNoConstructor() {
        String sourceCode = """
                class Rectangle {
                   public double area() {}
                }
                """;

        Set<String> feedbacks = new RectangleClassFeedback(sourceCode).suggestionKey();
        assertThat(feedbacks, containsInAnyOrder(
                "NO_CONSTRUCTOR_FOUND",
                "NO_FIELDS_FOUND"
        ));
    }

    @Test
    void shouldGiveFeedbackIfThereAreNoParametersInConstructor() {
        String sourceCode = """
                class Rectangle {
                   public Rectangle() {}
                                
                   public double area() {}
                }
                """;

        Set<String> feedbacks = new RectangleClassFeedback(sourceCode).suggestionKey();
        assertThat(feedbacks, containsInAnyOrder(
                "NO_CONSTRUCTOR_PARAMETER",
                "NO_FIELDS_FOUND"
        ));
    }

    @Test
    void shouldGiveFeedbackIfThereIsOnlyOneParametersInConstructor() {
        String sourceCode = """
                class Rectangle {
                   public Rectangle(int x) {}
                                
                   public double area() {}
                }
                """;

        Set<String> feedbacks = new RectangleClassFeedback(sourceCode).suggestionKey();
        assertThat(feedbacks, containsInAnyOrder(
                "ONLY_ONE_CONSTRUCTOR_PARAMETER",
                "NO_FIELDS_FOUND"
        ));
    }

    @Test
    void shouldGiveFeedbackIfThereIsMoreThanTwoParametersInConstructor() {
        String sourceCode = """
                class Rectangle {
                   
                   public Rectangle(int length, int breath, int height) {}
                                
                   public double area() {}
                }
                """;

        Set<String> feedbacks = new RectangleClassFeedback(sourceCode).suggestionKey();
        assertThat(feedbacks, containsInAnyOrder(
                "TOO_MANY_CONSTRUCTOR_PARAMETER",
                "NO_FIELDS_FOUND"
        ));
    }

    @Test
    void shouldGiveFeedbackIfThereAreNoFields() {
        String sourceCode = """
                class Rectangle {
                   
                   public Rectangle(int length, int breath) {}
                                
                   public double area() {}
                }
                """;

        Set<String> feedbacks = new RectangleClassFeedback(sourceCode).suggestionKey();
        assertThat(feedbacks, is(Set.of("NO_FIELDS_FOUND")));
    }

    @Test
    void shouldGiveFeedbackIfFieldsAreNotPrivate() {
        String sourceCode = """
                class Rectangle {
                                
                   int length;
                   int breath;
                   
                   public Rectangle(int length, int breath) {}
                                
                   public double area() {}
                }
                """;

        Set<String> feedbacks = new RectangleClassFeedback(sourceCode).suggestionKey();
        assertThat(feedbacks, containsInAnyOrder(
                "FIELDS_SHOULD_BE_PRIVATE"
        ));
    }

    @Test
    void shouldGiveFeedbackIfMethodsNamesHaveUnderscores() {
        String sourceCode = """
                class Rectangle {
                                
                   private int area;
                   
                   public Rectangle(int length, int breath) {
                        this.area = length * breath;
                   }
                                
                   public double get_area() {
                    return area;
                   }
                }
                """;

        Set<String> feedbacks = new RectangleClassFeedback(sourceCode).suggestionKey();
        assertThat(feedbacks, containsInAnyOrder(
                "METHOD_NAME_BREAKS_ENCAPSULATION",
                "JAVA_METHOD_NAMING_CONVENTIONS_NOT_FOLLOWED"
        ));
    }

    @Test
    void shouldGiveFeedbackIfMFieldNamesHaveUnderscores() {
        String sourceCode = """
                class Rectangle {
                                
                   private int rectangle_length;
                   private int rectangle_breath;
                   
                   public Rectangle(int length, int breath) {
                        this.area = length * breath;
                   }
                                
                   public double get_area() {
                    return area;
                   }
                }
                """;

        Set<String> feedbacks = new RectangleClassFeedback(sourceCode).suggestionKey();
        assertThat(feedbacks, containsInAnyOrder(
                "METHOD_NAME_BREAKS_ENCAPSULATION",
                "JAVA_METHOD_NAMING_CONVENTIONS_NOT_FOLLOWED",
                "JAVA_FIELD_NAMING_CONVENTIONS_NOT_FOLLOWED"
        ));
    }

    @Test
    void shouldGiveFeedbackIfMethodsNameBreakEncapsulation() {
        String sourceCodeWithGet = """
                class Rectangle {
                                
                   int length;
                   int breath;
                   
                   public Rectangle(int length, int breath) {}
                                
                   public double getArea() {}
                }
                """;

        String sourceCodeWithCalculate = """
                class Rectangle {
                                
                   int length;
                   int breath;
                   
                   public Rectangle(int length, int breath) {}
                                
                   public double calculateArea() {}
                }
                """;

        Set<String> feedbacksForGet = new RectangleClassFeedback(sourceCodeWithGet).suggestionKey();
        Set<String> feedbacksForCalculate = new RectangleClassFeedback(sourceCodeWithCalculate).suggestionKey();


        assertAll(
                "Verify different ways of breaking encapsulation",
                () -> assertThat(feedbacksForGet, containsInAnyOrder(
                        "FIELDS_SHOULD_BE_PRIVATE",
                        "METHOD_NAME_BREAKS_ENCAPSULATION"
                )),
                () -> assertThat(feedbacksForCalculate, containsInAnyOrder(
                        "FIELDS_SHOULD_BE_PRIVATE",
                        "METHOD_NAME_BREAKS_ENCAPSULATION"
                ))
        );
    }

    @Test
    @Tag("ToRemove")
    void shouldBeUnknownScenarioIfThereIsAnAreaField() {
        String sourceCode = """
                class Rectangle {
                                
                   private int area;
                   
                   public Rectangle(int length, int breath) {}
                                
                   public double area() {}
                }
                """;

        Set<String> feedbacks = new RectangleClassFeedback(sourceCode).suggestionKey();
        assertThat(feedbacks, is(Set.of("UNKNOWN_SCENARIO")));
    }

    @Test
    @Tag("ToRemove")
    void shouldBeUnknownScenarioIfThereIsAnAreaFieldInAdditionToLengthAndBreath() {
        String sourceCode = """
                class Rectangle {
                                
                   private int area;
                   private int length;
                   private int breath;
                   
                   public Rectangle(int length, int breath) {}
                                
                   public double area() {}
                }
                """;

        Set<String> feedbacks = new RectangleClassFeedback(sourceCode).suggestionKey();
        assertThat(feedbacks, is(Set.of("UNKNOWN_SCENARIO")));
    }
}
