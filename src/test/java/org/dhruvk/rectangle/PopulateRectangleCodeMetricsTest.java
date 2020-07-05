package org.dhruvk.rectangle;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import jdk.jshell.JShell;
import jdk.jshell.SnippetEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static jdk.jshell.Snippet.Status.REJECTED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;

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

    @Test
    void shouldPopulateInvokeExpressionCorrectlyEvenIfPublicMethodBreaksEncapsulation() {
        String someImplementation = """
                class Rectangle {
                    private final int length;
                    private final int breath;
                    
                    public Rectangle(int length, int breath) {
                        this.length = length;
                        this.breath = breath;
                    }
                    
                    public int getArea() {
                        return length * breath;
                    }
                }
                """;
        JShell jShell = getjShell(someImplementation);
        RectangleCodeMetrics rectangleCodeMetrics = populateRectangleCodeMetrics(someImplementation);

        verifyForAllInputs(jShell, rectangleCodeMetrics);

        jShell.close(); // TODO - should use closable syntax

    }

    @Test
    void shouldPopulateInvokeExpressionCorrectlyEvenIfPublicMethodBreaksEncapsulationAndSomeJavaConventions() {
        String someImplementation = """
                class Rectangle {
                    private final int length;
                    private final int breath;
                    
                    public Rectangle(int length, int breath) {
                        this.length = length;
                        this.breath = breath;
                    }
                    
                    public int calculate_area() {
                        return length * breath;
                    }
                }
                """;
        JShell jShell = getjShell(someImplementation);
        RectangleCodeMetrics rectangleCodeMetrics = populateRectangleCodeMetrics(someImplementation);

        verifyForAllInputs(jShell, rectangleCodeMetrics);

        jShell.close(); // TODO - should use closable syntax

    }

    @Test
    void shouldPopulateInvokeExpressionCorrectlyIfProceduralWayWasUsed() {
        String someImplementation = """
                class Rectangle {
                    public int calculate_area(int length, int breath) {
                        return length * breath;
                    }
                }
                """;
        JShell jShell = getjShell(someImplementation);
        RectangleCodeMetrics rectangleCodeMetrics = populateRectangleCodeMetrics(someImplementation);

        verifyForAllInputs(jShell, rectangleCodeMetrics);

        jShell.close(); // TODO - should use closable syntax

    }

    @Test
    void shouldPopulateInvokeExpressionCorrectlyIfProceduralWayWasUsedWithStaticMethod() {
        String someImplementation = """
                class Rectangle {
                    public static int calculate_area(int length, int breath) {
                        return length * breath;
                    }
                }
                """;
        JShell jShell = getjShell(someImplementation);
        RectangleCodeMetrics rectangleCodeMetrics = populateRectangleCodeMetrics(someImplementation);

        verifyForAllInputs(jShell, rectangleCodeMetrics);

        jShell.close(); // TODO - should use closable syntax

    }

    @Test
    void shouldPopulateInvokeExpressionCorrectlyIfProceduralWayWasUsedWithStaticMethodWithRedundantConstructor() {
        String someImplementation = """
                class Rectangle {
                    
                    public Rectangle() {
                    }
                
                    public static int calculate_area(int length, int breath) {
                        return length * breath;
                    }
                }
                """;
        JShell jShell = getjShell(someImplementation);
        RectangleCodeMetrics rectangleCodeMetrics = populateRectangleCodeMetrics(someImplementation);

        verifyForAllInputs(jShell, rectangleCodeMetrics);

        jShell.close(); // TODO - should use closable syntax

    }

    @Test
    void shouldPopulateInvokeExpressionCorrectlyIfSettersAreUsed() {
        String someImplementation = """
                class Rectangle {
                
                    int length;
                    int breath;
                    
                    public Rectangle() {
                    }
                    
                    public void setLength(int length) {
                        this.length = length;
                    }
                    
                    public void setBreath(int breath) {
                        this.breath = breath;
                    }
                
                    public int calculate_area() {
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
        List<SnippetEvent> eval = jShell.eval(someImplementation);
        if(eval.get(0).status().equals(REJECTED)) {
            fail("The code in the test snippet does not compile properly");
        }
        return jShell;
    }

    private RectangleCodeMetrics populateRectangleCodeMetrics(String someImplementation) {
        CompilationUnit compilationUnit = StaticJavaParser.parse(someImplementation);
        RectangleCodeMetrics rectangleCodeMetrics = new RectangleCodeMetrics();
        new PopulateRectangleCodeMetrics().visit(compilationUnit, rectangleCodeMetrics);
        return rectangleCodeMetrics;
    }

    private void verifyForAllInputs(JShell jShell, RectangleCodeMetrics rectangleCodeMetrics) {
        referenceRectangles
                .forEach(referenceRectangle -> verifyOne(jShell, rectangleCodeMetrics, referenceRectangle));

    }

    private void verifyOne(JShell jShell, RectangleCodeMetrics rectangleCodeMetrics, ReferenceRectangle referenceRectangle) {
        List<String> testStatements = rectangleCodeMetrics.getTestStatements(referenceRectangle);
        if(testStatements.isEmpty()) {
            fail("Unable to find the invoke expression");
        }

        String result = "";
        for(String statement : testStatements) {
            List<SnippetEvent> eval = jShell.eval(statement);
            SnippetEvent snippetEvent = eval.get(0);
            if(snippetEvent.status().equals(REJECTED)) {
                fail("The statement %s does not compile properly".formatted(statement));
            }
            result = snippetEvent.value();
        }
        assertThat("Expected value %s to be %d".formatted(result, referenceRectangle.area()),
                Integer.parseInt(result), is(referenceRectangle.area()));

    }

}