package org.dhruvk.rectangle;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

class ShouldHaveConstructor extends VoidVisitorAdapter<AtomicBoolean> {
    @Override
    public void visit(ConstructorDeclaration constructorDeclaration, AtomicBoolean arg) {
        super.visit(constructorDeclaration, arg);
        arg.set(true);
    }
}

class NumberOfConstructorParameters extends VoidVisitorAdapter<AtomicInteger> {
    @Override
    public void visit(ConstructorDeclaration constructorDeclaration, AtomicInteger arg) {
        super.visit(constructorDeclaration, arg);
        NodeList<Parameter> parameters = constructorDeclaration.getParameters();
        parameters.forEach(p -> arg.incrementAndGet());
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
        CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);

        if(!hasConstructor(compilationUnit)) feedbacks.add("NO_CONSTRUCTOR_FOUND");
        if(numberOfConstructorParameters(compilationUnit) == 0) feedbacks.add("NO_CONSTRUCTOR_PARAMETER");

        return feedbacks.isEmpty() ? Set.of("UNKNOWN_SCENARIO") : feedbacks;
    }

    private int numberOfConstructorParameters(CompilationUnit compilationUnit) {
        AtomicInteger numberOfConstructorParameters = new AtomicInteger(0);
        new NumberOfConstructorParameters().visit(compilationUnit,numberOfConstructorParameters);
        return numberOfConstructorParameters.get();
    }

    private Boolean hasConstructor(CompilationUnit compilationUnit) {
        AtomicBoolean hasConstructor = new AtomicBoolean(false);
        new ShouldHaveConstructor().visit(compilationUnit,hasConstructor);
        return hasConstructor.get();
    }
}
