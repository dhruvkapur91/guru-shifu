package org.dhruvk.rectangle;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class PopulateRectangleCodeMetricsTest {
    @Test
    void shouldPopulateNameOfTheClass() {
        String sourceCode = """
                class Rectangle {}
                """;

        CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);

        RectangleCodeMetrics rectangleCodeMetrics = new RectangleCodeMetrics();
        new PopulateRectangleCodeMetrics().visit(compilationUnit, rectangleCodeMetrics);

        assertThat(rectangleCodeMetrics.getClassName(), is(Optional.of("Rectangle")));
    }

    @Test
    void shouldPopulateNameOfTheClassEvenWhenJavaConventionsAreNotFollowed() {
        String sourceCode = """
                class rectangle {}
                """;

        CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);

        RectangleCodeMetrics rectangleCodeMetrics = new RectangleCodeMetrics();
        new PopulateRectangleCodeMetrics().visit(compilationUnit, rectangleCodeMetrics);

        assertThat(rectangleCodeMetrics.getClassName(), is(Optional.of("rectangle")));
    }

    @Test
    void shouldNotPopulateNameOfTheClassWhenNotFound() {
        CompilationUnit compilationUnit = StaticJavaParser.parse("");

        RectangleCodeMetrics rectangleCodeMetrics = new RectangleCodeMetrics();
        new PopulateRectangleCodeMetrics().visit(compilationUnit, rectangleCodeMetrics);

        assertThat(rectangleCodeMetrics.getClassName(), is(Optional.empty()));
    }


}