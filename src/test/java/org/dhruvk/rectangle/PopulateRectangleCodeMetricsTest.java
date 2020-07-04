package org.dhruvk.rectangle;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import jdk.jshell.JShell;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
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


class PopulateRectangleCodeMetricsTest {

    Map<RectangleDimensions, Integer> dimensionsAndCorrespondingArea = Map.of(
            new RectangleDimensions(0, 0), 0,
            new RectangleDimensions(0, 1), 0,
            new RectangleDimensions(1, 0), 0,
            new RectangleDimensions(1, 1), 1,
            new RectangleDimensions(2, 1), 2,
            new RectangleDimensions(1, 2), 2,
            new RectangleDimensions(10, 20), 200
    );


    @Test
    void shouldPopulateInvokeExpressionCorrectly() {
        String sourceCode = """
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
        CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);

        RectangleCodeMetrics rectangleCodeMetrics = new RectangleCodeMetrics();
        new PopulateRectangleCodeMetrics().visit(compilationUnit, rectangleCodeMetrics);

        JShell jShell = JShell.create();
        jShell.eval(sourceCode);

        Map<Map.Entry<RectangleDimensions, Integer>, Boolean> collect = dimensionsAndCorrespondingArea
                .entrySet()
                .stream()
                .map(x -> {
                    String testExpression = rectangleCodeMetrics.invokeExpression(x.getKey()).get();
                    String value = jShell.eval(testExpression).get(0).value();
                    return Map.entry(x, Integer.parseInt(value) == x.getValue());
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertTrue(collect.values().stream().allMatch(p -> p.equals(TRUE)));

        jShell.close(); // TODO - should use closable syntax

    }

}