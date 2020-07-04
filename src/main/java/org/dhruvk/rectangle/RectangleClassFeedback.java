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

class ThereShouldBeAClass extends VoidVisitorAdapter<AtomicBoolean> {
    @Override
    public void visit(ClassOrInterfaceDeclaration n, AtomicBoolean arg) {
        super.visit(n, arg);
        arg.set(true);
    }
}

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

class NumberOfFields extends VoidVisitorAdapter<AtomicInteger> {
    @Override
    public void visit(FieldDeclaration n, AtomicInteger arg) {
        super.visit(n, arg);
        arg.incrementAndGet();
    }
}

class IsAnyFieldPublic extends VoidVisitorAdapter<AtomicBoolean> {
    @Override
    public void visit(FieldDeclaration someField, AtomicBoolean arg) {
        super.visit(someField, arg);
        if (!someField.isPrivate()) arg.set(true);
    }
}

class MethodNamesShouldNotBreakEncapsulation extends VoidVisitorAdapter<AtomicBoolean> {
    @Override
    public void visit(MethodDeclaration someMethod, AtomicBoolean arg) {
        super.visit(someMethod, arg);
        boolean containsGet = someMethod.getNameAsString().toLowerCase().contains("get");
        boolean containsCalculate = someMethod.getNameAsString().toLowerCase().contains("calculate");
        if(containsGet || containsCalculate) arg.set(true);
    }
}

class MethodNamesShouldFollowJavaConventions extends VoidVisitorAdapter<AtomicBoolean> {
    @Override
    public void visit(MethodDeclaration someMethod, AtomicBoolean arg) {
        super.visit(someMethod, arg);
        boolean containsUnderscore = someMethod.getNameAsString().toLowerCase().contains("_");
        if(containsUnderscore) arg.set(true);
    }
}

class FieldNamesShouldFollowJavaConventions extends VoidVisitorAdapter<AtomicBoolean> {

    @Override
    public void visit(FieldDeclaration n, AtomicBoolean arg) {
        super.visit(n, arg);
        boolean containsUnderscore = n.toString().toLowerCase().contains("_");
        if(containsUnderscore) arg.set(true);
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

        if(!hasOneClass(compilationUnit)) feedbacks.add("NO_CLASS_FOUND");

        int numberOfConstructorParameters = numberOfConstructorParameters(compilationUnit);

        if (!hasConstructor(compilationUnit)) feedbacks.add("NO_CONSTRUCTOR_FOUND");
        if (hasConstructor(compilationUnit) && numberOfConstructorParameters == 0) feedbacks.add("NO_CONSTRUCTOR_PARAMETER");
        if (numberOfConstructorParameters == 1) feedbacks.add("ONLY_ONE_CONSTRUCTOR_PARAMETER");
        if (numberOfConstructorParameters > 2) feedbacks.add("TOO_MANY_CONSTRUCTOR_PARAMETER");

        int numberOfFields = numberOfFields(compilationUnit);
        if (numberOfFields == 0) feedbacks.add("NO_FIELDS_FOUND");

        if (hasPublicFields(compilationUnit)) feedbacks.add("FIELDS_SHOULD_BE_PRIVATE");
        if (methodsBreakEncapsulation(compilationUnit)) feedbacks.add("METHOD_NAME_BREAKS_ENCAPSULATION");
        if (methodNamesBreakJavaConventions(compilationUnit)) feedbacks.add("JAVA_METHOD_NAMING_CONVENTIONS_NOT_FOLLOWED");
        if (fieldNamesBreakJavaConventions(compilationUnit)) feedbacks.add("JAVA_FIELD_NAMING_CONVENTIONS_NOT_FOLLOWED");

        return feedbacks.isEmpty() ? Set.of("UNKNOWN_SCENARIO") : feedbacks;
    }

    private boolean methodNamesBreakJavaConventions(CompilationUnit compilationUnit) {
        AtomicBoolean hasAClass = new AtomicBoolean(false);
        new MethodNamesShouldFollowJavaConventions().visit(compilationUnit, hasAClass);
        return hasAClass.get();
    }

    private boolean fieldNamesBreakJavaConventions(CompilationUnit compilationUnit) {
        AtomicBoolean hasAClass = new AtomicBoolean(false);
        new FieldNamesShouldFollowJavaConventions().visit(compilationUnit, hasAClass);
        return hasAClass.get();
    }

    private boolean hasOneClass(CompilationUnit compilationUnit) {
        AtomicBoolean hasAClass = new AtomicBoolean(false);
        new ThereShouldBeAClass().visit(compilationUnit, hasAClass);
        return hasAClass.get();
    }

    private int numberOfFields(CompilationUnit compilationUnit) {
        AtomicInteger numberOfFields = new AtomicInteger(0);
        new NumberOfFields().visit(compilationUnit, numberOfFields);
        return numberOfFields.get();
    }

    private int numberOfConstructorParameters(CompilationUnit compilationUnit) {
        AtomicInteger numberOfConstructorParameters = new AtomicInteger(0);
        new NumberOfConstructorParameters().visit(compilationUnit, numberOfConstructorParameters);
        return numberOfConstructorParameters.get();
    }

    private Boolean hasConstructor(CompilationUnit compilationUnit) {
        AtomicBoolean hasConstructor = new AtomicBoolean(false);
        new ShouldHaveConstructor().visit(compilationUnit, hasConstructor);
        return hasConstructor.get();
    }

    private Boolean hasPublicFields(CompilationUnit compilationUnit) {
        int numberOfFields = numberOfFields(compilationUnit);
        AtomicBoolean hasPublicFields = new AtomicBoolean(false);
        if (numberOfFields > 0) {
            new IsAnyFieldPublic().visit(compilationUnit, hasPublicFields);
        }
        return hasPublicFields.get();
    }

    private Boolean methodsBreakEncapsulation(CompilationUnit compilationUnit) {
        AtomicBoolean doesMethodsBreakEncapsulation = new AtomicBoolean(false);
        new MethodNamesShouldNotBreakEncapsulation().visit(compilationUnit,doesMethodsBreakEncapsulation);
        return doesMethodsBreakEncapsulation.get();
    }
}
