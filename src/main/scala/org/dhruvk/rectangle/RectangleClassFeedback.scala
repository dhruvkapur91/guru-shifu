package org.dhruvk.rectangle

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit

// TODO - think about interface again, simple enough to start with though

class RectangleClassFeedback(val sourceCode: String) {
  def suggestionKey: Set[String] = {
    val rectangleCodeMetrics: RectangleCodeMetrics = new RectangleCodeMetrics
    val compilationUnit: CompilationUnit = StaticJavaParser.parse(sourceCode)
    new PopulateRectangleCodeMetrics().visit(compilationUnit, rectangleCodeMetrics)
    if (rectangleCodeMetrics.getFeedbacks.isEmpty) Set("UNKNOWN_SCENARIO")
    else rectangleCodeMetrics.getFeedbacks.toSet
  }
}

