package org.dhruvk.rectangle;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.util.HashSet;
import java.util.Optional;
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
    private String className;
    private String callableMethod;
    private boolean hasConstructor = false;
    private boolean isCallableMethodStatic = false;

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
        if (!feedbacks.contains(NO_CONSTRUCTOR_FOUND) && numberOfConstructorParameters == 0)
            feedbacks.add(NO_CONSTRUCTOR_PARAMETER);
        if (numberOfConstructorParameters == 1) feedbacks.add(ONLY_ONE_CONSTRUCTOR_PARAMETER);
        if (numberOfConstructorParameters > 2) feedbacks.add(TOO_MANY_CONSTRUCTOR_PARAMETER);
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
        hasConstructor = true;
    }

    public void incrementNumberOfFields() {
        feedbacks.remove(NO_FIELDS_FOUND);
    }

    public void markSomeFieldIsNotFinal() {
        feedbacks.add(FIELDS_CAN_BE_FINAL);
    }

    private Optional<String> getClassName() {
        return className == null ? Optional.empty() : Optional.of(className);
    }

    private Optional<String> getCallableMethod() {
        return callableMethod == null ? Optional.empty() : Optional.of(callableMethod);
    }

    public void setClassName(String nameAsString) {
        className = nameAsString;
    }

    public void setCallableMethod(String callableMethod) {
        this.callableMethod = callableMethod;
    }

    public void markCallableMethodIsStatic() {
        this.isCallableMethodStatic = true;
    }

    // TODO - maybe we can use java-parser for generating the call expressions too, but couldn't find a way to do it as of now...
    public Optional<String> invokeExpression(ReferenceRectangle rectangle) {
        // TODO - should likely extract these conditions out
        // TODO - add appropriate feedbacks in these cases
        if (numberOfConstructorParameters == 2 && getClassName().isPresent() && getCallableMethod().isPresent()) {
            return Optional.of("new %s(%d,%d).%s()".formatted(
                    getClassName().get(),
                    rectangle.getLength(),
                    rectangle.getBreath(),
                    getCallableMethod().get()
            ));
        }

        // Assuming we use default constructor and some public callable method is present with 2 args
        if (!hasConstructor && getCallableMethod().isPresent() && !isCallableMethodStatic) {
            return Optional.of("new %s().%s(%d,%d)".formatted(
                    getClassName().get(),
                    getCallableMethod().get(),
                    rectangle.getLength(),
                    rectangle.getBreath()
                    ));
        }

        // Assuming no constuctor, there is a procedural callable method and its static
        if (!hasConstructor && getCallableMethod().isPresent() && isCallableMethodStatic) {
            return Optional.of("%s.%s(%d,%d)".formatted(
                    getClassName().get(),
                    getCallableMethod().get(),
                    rectangle.getLength(),
                    rectangle.getBreath()
                    ));
        }

        // There is likely an unnecessary constructor
        if (hasConstructor && numberOfConstructorParameters == 0 && getCallableMethod().isPresent() && isCallableMethodStatic) {
            return Optional.of("%s.%s(%d,%d)".formatted(
                    getClassName().get(),
                    getCallableMethod().get(),
                    rectangle.getLength(),
                    rectangle.getBreath()
            ));
        }

        feedbacks.add("NON_UNDERSTANDABLE_API"); // TODO - test this.
        return Optional.empty();
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

        new PopulateRectangleCodeMetrics().visit(compilationUnit, rectangleCodeMetrics);

        return rectangleCodeMetrics.getFeedbacks().isEmpty() ? Set.of("UNKNOWN_SCENARIO") : rectangleCodeMetrics.getFeedbacks();
    }

}
