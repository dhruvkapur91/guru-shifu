package org.dhruvk.rectangle;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

class PopulateRectangleCodeMetrics extends VoidVisitorAdapter<RectangleCodeMetrics> {
    @Override
    public void visit(ClassOrInterfaceDeclaration n, RectangleCodeMetrics metrics) {
        super.visit(n, metrics);
        metrics.setClassName(n.getNameAsString());
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
