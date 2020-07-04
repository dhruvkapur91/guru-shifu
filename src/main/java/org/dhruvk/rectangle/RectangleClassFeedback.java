package org.dhruvk.rectangle;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.HashSet;
import java.util.Set;

// TODO - this should probably be more general... but for now I'm going to stay with this concrete implementation
class RectangleCodeMetrics {

    private static final String NO_CLASS_FOUND = "NO_CLASS_FOUND";
    private static final String NO_CONSTRUCTOR_FOUND = "NO_CONSTRUCTOR_FOUND";
    private static final String FIELDS_CAN_BE_FINAL = "FIELDS_CAN_BE_FINAL";
    private static final String NO_CONSTRUCTOR_PARAMETER = "NO_CONSTRUCTOR_PARAMETER";
    private static final String ONLY_ONE_CONSTRUCTOR_PARAMETER = "ONLY_ONE_CONSTRUCTOR_PARAMETER";
    private static final String TOO_MANY_CONSTRUCTOR_PARAMETER = "TOO_MANY_CONSTRUCTOR_PARAMETER";
    private static final String NO_FIELDS_FOUND = "NO_FIELDS_FOUND";
    private static final String FIELDS_SHOULD_BE_PRIVATE = "FIELDS_SHOULD_BE_PRIVATE";
    private static final String METHOD_NAME_BREAKS_ENCAPSULATION = "METHOD_NAME_BREAKS_ENCAPSULATION";
    private static final String JAVA_METHOD_NAMING_CONVENTIONS_NOT_FOLLOWED = "JAVA_METHOD_NAMING_CONVENTIONS_NOT_FOLLOWED";
    private static final String JAVA_FIELD_NAMING_CONVENTIONS_NOT_FOLLOWED = "JAVA_FIELD_NAMING_CONVENTIONS_NOT_FOLLOWED";

    private final Set<String> feedbacks = new HashSet<>(); // TODO - think list or set, currently we maybe loosing information by keeping it a set.... I think we should have a list internally and a set externally... but this will do for now...
    private int numberOfConstructorParameters = 0;

    RectangleCodeMetrics() {
        feedbacks.addAll(
                Set.of(
                        NO_CLASS_FOUND,
                        NO_CONSTRUCTOR_FOUND,
                        NO_FIELDS_FOUND
                )
        );
    }

    public Set<String> getFeedbacks() {
        if(!feedbacks.contains(NO_CONSTRUCTOR_FOUND) && numberOfConstructorParameters == 0) feedbacks.add(NO_CONSTRUCTOR_PARAMETER);
        if(numberOfConstructorParameters == 1) feedbacks.add(ONLY_ONE_CONSTRUCTOR_PARAMETER);
        if(numberOfConstructorParameters > 2) feedbacks.add(TOO_MANY_CONSTRUCTOR_PARAMETER);
        return feedbacks;
    }

    public void setHasDefinedClass() {
        feedbacks.remove(NO_CLASS_FOUND);
    }

    public void incrementConstructorParameter() {
        numberOfConstructorParameters++;
    }

    public void markHasSomeNonPrivateFields() {
        feedbacks.add(FIELDS_SHOULD_BE_PRIVATE);
    }

    public void markSomeFieldBreaksJavaConventions() {
        feedbacks.add(JAVA_FIELD_NAMING_CONVENTIONS_NOT_FOLLOWED);
    }

    public void markSomeMethodNameBreaksEncapsulation() {
        feedbacks.add(METHOD_NAME_BREAKS_ENCAPSULATION);
    }

    public void markSomeMethodBreaksJavaConventions() {
        feedbacks.add(JAVA_METHOD_NAMING_CONVENTIONS_NOT_FOLLOWED);
    }

    public void markHasConstructor() {
        feedbacks.remove(NO_CONSTRUCTOR_FOUND);
    }

    public void incrementNumberOfFields() {
        feedbacks.remove(NO_FIELDS_FOUND);
    }

    public void markSomeFieldIsNotFinal() {
        feedbacks.add(FIELDS_CAN_BE_FINAL);
    }
}

class GetFeedback extends VoidVisitorAdapter<RectangleCodeMetrics> {
    @Override
    public void visit(ClassOrInterfaceDeclaration n, RectangleCodeMetrics metrics) {
        super.visit(n, metrics);
        metrics.setHasDefinedClass();
    }

    @Override
    public void visit(ConstructorDeclaration constructorDeclaration, RectangleCodeMetrics arg) {
        super.visit(constructorDeclaration, arg);
        arg.markHasConstructor();
        NodeList<Parameter> parameters = constructorDeclaration.getParameters();
        parameters.forEach(p -> arg.incrementConstructorParameter());
    }

    @Override
    public void visit(FieldDeclaration someField, RectangleCodeMetrics arg) {
        super.visit(someField, arg);
        boolean containsUnderscore = someField.toString().toLowerCase().contains("_");
        if (!someField.isFinal()) arg.markSomeFieldIsNotFinal();
        if (!someField.isPrivate()) arg.markHasSomeNonPrivateFields();
        if (containsUnderscore) arg.markSomeFieldBreaksJavaConventions();
        arg.incrementNumberOfFields();
    }

    @Override
    public void visit(MethodDeclaration someMethod, RectangleCodeMetrics arg) {
        super.visit(someMethod, arg);
        boolean containsUnderscore = someMethod.toString().toLowerCase().contains("_");
        if (containsUnderscore) arg.markSomeMethodBreaksJavaConventions();
        boolean containsGet = someMethod.getNameAsString().toLowerCase().contains("get");
        boolean containsCalculate = someMethod.getNameAsString().toLowerCase().contains("calculate");
        if (containsGet || containsCalculate) arg.markSomeMethodNameBreaksEncapsulation();
    }
}

public class RectangleClassFeedback implements Rule {

    private final String sourceCode;

    public RectangleClassFeedback(String sourceCode) { // TODO - think about interface again, simple enough to start with though
        this.sourceCode = sourceCode;
    }

    @Override
    public Set<String> suggestionKey() {
        RectangleCodeMetrics rectangleCodeMetrics = new RectangleCodeMetrics();
        CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);

        new GetFeedback().visit(compilationUnit, rectangleCodeMetrics);

        return rectangleCodeMetrics.getFeedbacks().isEmpty() ? Set.of("UNKNOWN_SCENARIO") : rectangleCodeMetrics.getFeedbacks();
    }

}
