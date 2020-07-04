package org.dhruvk.rectangle;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

class CodeMetrics {

    private boolean hasDefinedClass = false;
    private int numberOfConstructorParameters = 0;
    private boolean hasSomeNonPrivateFields = false;
    private boolean hasConstructor = false;
    private int numberOfFields = 0;
    private boolean someFieldBreaksJavaConventions = false;
    private boolean someMethodBreaksJavaConventions = false;
    private boolean someMethodNameBreaksEncapsulation = false;

    public void setHasDefinedClass() {
        this.hasDefinedClass = true;
    }

    public void incrementConstructorParameter() {
        numberOfConstructorParameters++;
    }

    public boolean isClassDefined() {
        return hasDefinedClass;
    }

    public int getNumberOfConstructorParameters() {
        return numberOfConstructorParameters;
    }

    public void markHasSomeNonPrivateFields() {
        hasSomeNonPrivateFields = true;
    }

    public void markSomeFieldBreaksJavaConventions() {
        someFieldBreaksJavaConventions = true;
    }

    public void markSomeMethodNameBreaksEncapsulation() {
        someMethodNameBreaksEncapsulation = true;
    }

    public void markSomeMethodBreaksJavaConventions() {
        someMethodBreaksJavaConventions = true;
    }

    public boolean hasSomeNonPrivateFields() {
        return hasSomeNonPrivateFields;
    }

    public void markHasConstructor() {
        hasConstructor = true;
    }

    public boolean hasConstructor() {
        return hasConstructor;
    }

    public boolean doesAFieldBreaksJavaConventions() {
        return someFieldBreaksJavaConventions;
    }

    public boolean doesAMethodBreaksJavaConventions() {
        return someMethodBreaksJavaConventions;
    }

    public boolean doesAMethodNameBreaksJavaEncapsulation() {
        return someMethodNameBreaksEncapsulation;
    }

    public int getNumberOfFields() {
        return numberOfFields;
    }

    public void incrementNumberOfFields() {
        numberOfFields++;
    }
}

class GetFeedback extends VoidVisitorAdapter<CodeMetrics> {
    @Override
    public void visit(ClassOrInterfaceDeclaration n, CodeMetrics metrics) {
        super.visit(n, metrics);
        metrics.setHasDefinedClass();
    }

    @Override
    public void visit(ConstructorDeclaration constructorDeclaration, CodeMetrics arg) {
        super.visit(constructorDeclaration, arg);
        arg.markHasConstructor();
        NodeList<Parameter> parameters = constructorDeclaration.getParameters();
        parameters.forEach(p -> arg.incrementConstructorParameter());
    }

    @Override
    public void visit(FieldDeclaration someField, CodeMetrics arg) {
        super.visit(someField, arg);
        boolean containsUnderscore = someField.toString().toLowerCase().contains("_");
        if (!someField.isPrivate()) arg.markHasSomeNonPrivateFields();
        if (containsUnderscore) arg.markSomeFieldBreaksJavaConventions();
        arg.incrementNumberOfFields();
    }

    @Override
    public void visit(MethodDeclaration someMethod, CodeMetrics arg) {
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
        Set<String> feedbacks = new HashSet<>();
        CodeMetrics codeMetrics = new CodeMetrics();
        CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);

        new GetFeedback().visit(compilationUnit, codeMetrics);

        if (!codeMetrics.isClassDefined()) feedbacks.add("NO_CLASS_FOUND");

        int numberOfConstructorParameters = codeMetrics.getNumberOfConstructorParameters();

        if (!codeMetrics.hasConstructor()) feedbacks.add("NO_CONSTRUCTOR_FOUND");
        if (codeMetrics.hasConstructor() && numberOfConstructorParameters == 0)
            feedbacks.add("NO_CONSTRUCTOR_PARAMETER");
        if (numberOfConstructorParameters == 1) feedbacks.add("ONLY_ONE_CONSTRUCTOR_PARAMETER");
        if (numberOfConstructorParameters > 2) feedbacks.add("TOO_MANY_CONSTRUCTOR_PARAMETER");

        int numberOfFields = codeMetrics.getNumberOfFields();
        if (numberOfFields == 0) feedbacks.add("NO_FIELDS_FOUND");

        if (codeMetrics.hasSomeNonPrivateFields()) feedbacks.add("FIELDS_SHOULD_BE_PRIVATE");

        if (codeMetrics.doesAMethodNameBreaksJavaEncapsulation()) feedbacks.add("METHOD_NAME_BREAKS_ENCAPSULATION");
        if (codeMetrics.doesAMethodBreaksJavaConventions())
            feedbacks.add("JAVA_METHOD_NAMING_CONVENTIONS_NOT_FOLLOWED");
        if (codeMetrics.doesAFieldBreaksJavaConventions())
            feedbacks.add("JAVA_FIELD_NAMING_CONVENTIONS_NOT_FOLLOWED");

        return feedbacks.isEmpty() ? Set.of("UNKNOWN_SCENARIO") : feedbacks;
    }

}
