package org.dhruvk.rectangle

import com.github.javaparser.ast.body._
import com.github.javaparser.ast.visitor.VoidVisitorAdapter

// This will usually be created per requirement to populate its associated metrics
class PopulateRectangleCodeMetrics extends VoidVisitorAdapter[RectangleCodeMetrics] {
  override def visit(n: ClassOrInterfaceDeclaration, metrics: RectangleCodeMetrics): Unit = {
    super.visit(n, metrics)
    metrics.setClassName(n.getNameAsString)
    metrics.setHasDefinedAClass()
  }

  override def visit(constructorDeclaration: ConstructorDeclaration, arg: RectangleCodeMetrics): Unit = {
    super.visit(constructorDeclaration, arg)
    arg.markHasAConstructor()
    arg.setNumberOfConstructorParameters(constructorDeclaration.getParameters.size())
  }

  override def visit(someField: FieldDeclaration, arg: RectangleCodeMetrics): Unit = {
    super.visit(someField, arg)
    val containsUnderscore = someField.toString.toLowerCase.contains("_")
    if (!someField.isFinal) arg.markSomeFieldIsNotFinal()
    if (!someField.isPrivate) arg.markHasSomeNonPrivateFields()
    if (containsUnderscore) arg.markSomeFieldBreaksJavaConventions()
    arg.incrementNumberOfFields()
  }

  override def visit(someMethod: MethodDeclaration, arg: RectangleCodeMetrics): Unit = {
    super.visit(someMethod, arg)
    val containsUnderscore = someMethod.toString.toLowerCase.contains("_")
    if (containsUnderscore) arg.markSomeMethodBreaksJavaConventions()
    val containsGet = someMethod.getNameAsString.toLowerCase.contains("get")
    val containsCalculate = someMethod.getNameAsString.toLowerCase.contains("calculate")
    if (containsGet || containsCalculate) arg.markSomeMethodNameBreaksEncapsulation()
    if (someMethod.getNameAsString.toLowerCase.contains("set")) arg.markHasSetterMethods()
    if (someMethod.getParameters.size() == 1 && someMethod.getType.isVoidType) { // possibly a setter - heuristic
      arg.markHasSetterMethods()
      arg.addASetterMethod(someMethod.getNameAsString)
    }
    if (someMethod.isPublic) arg.setCallableMethod(someMethod.getNameAsString)
    if (someMethod.isPublic && someMethod.isStatic) {
      arg.setCallableMethod(someMethod.getNameAsString)
      arg.markCallableMethodIsStatic()
    }
    arg.setNumberOfCallableMethodParameters(someMethod.getParameters.size)
  }
}
