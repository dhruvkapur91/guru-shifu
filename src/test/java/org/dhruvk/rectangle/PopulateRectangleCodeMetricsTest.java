package org.dhruvk.rectangle;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import jdk.jshell.JShell;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
I think the biggest difference b/w Exercism and us is that Exercism enforces an API, while we want people to come up with it... that means its harder to run tests to check functionality... Does it work? Hard to answer if API is unknown
To make this really work, we need to figure out the API and run our tests to ensure the participant came up with the right implementation before we give feedback on design
This is of course after it compiles

We need to be careful that this code runs either locally (which is preferable) but if we are running it in server, it needs to be sandboxed
 */

// TODO - the setup fo these tests is somewhat complicated.. should think more...
class PopulateRectangleCodeMetricsTest {

    List<ReferenceRectangle> referenceRectangles = List.of(
            new ReferenceRectangle(0, 0),
            new ReferenceRectangle(0, 1),
            new ReferenceRectangle(1, 0),
            new ReferenceRectangle(1, 1),
            new ReferenceRectangle(2, 1),
            new ReferenceRectangle(1, 2),
            new ReferenceRectangle(10, 20)
    );


    @Test
    void shouldPopulateInvokeExpressionCorrectly() {
        String someImplementation = """
                class Rectangle {
                    private final int length;
                    private final int breath;
                    
                    public Rectangle(int length, int breath) {
                        this.length = length;
                        this.breath = breath;
                    }
                    
                    public int area() {
                        return length * breath;
                    }
                }
                """;
        JShell jShell = getjShell(someImplementation);
        RectangleCodeMetrics rectangleCodeMetrics = populateRectangleCodeMetrics(someImplementation);

        verifyForAllInputs(jShell, rectangleCodeMetrics);

        jShell.close(); // TODO - should use closable syntax

    }

    private JShell getjShell(String someImplementation) {
        JShell jShell = JShell.create();
        jShell.eval(someImplementation);
        return jShell;
    }

    private RectangleCodeMetrics populateRectangleCodeMetrics(String someImplementation) {
        CompilationUnit compilationUnit = StaticJavaParser.parse(someImplementation);
        RectangleCodeMetrics rectangleCodeMetrics = new RectangleCodeMetrics();
        new PopulateRectangleCodeMetrics().visit(compilationUnit, rectangleCodeMetrics);
        return rectangleCodeMetrics;
    }

    private void verifyForAllInputs(JShell jShell, RectangleCodeMetrics rectangleCodeMetrics) {
        Map<ReferenceRectangle, Boolean> collect = referenceRectangles
                .stream()
                .map(referenceRectangle -> verifyOne(jShell, rectangleCodeMetrics, referenceRectangle))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertTrue(collect.values().stream().allMatch(p -> p.equals(TRUE)));
    }

    private Map.Entry<ReferenceRectangle, Boolean> verifyOne(JShell jShell, RectangleCodeMetrics rectangleCodeMetrics, ReferenceRectangle referenceRectangle) {
        String testExpression = rectangleCodeMetrics.invokeExpression(referenceRectangle).get();
        String value = jShell.eval(testExpression).get(0).value();
        return Map.entry(referenceRectangle, Integer.parseInt(value) == referenceRectangle.area());
    }

}