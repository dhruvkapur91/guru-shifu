package org.dhruvk.rectangle

import org.dhruvk.rectangle.RectangleCodeMetrics._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

case class GeneralCodeFeatures(
                                hasDefinedClass: Boolean,
                                hasCallableMethod: Boolean,
                                numberOfConstructorParameters: Int,
                                numberOfCallableMethodParameters: Int,
                                hasConstructor: Boolean,
                                isCallableMethodStatic: Boolean,
                                hasSetterMethods: Boolean
                              )

case class RectangleCodeMetrics() {


  private var hasSetterMethods: Boolean = false
  private val feedbacks: mutable.Set[String] = new mutable.HashSet[String] // TODO - think list or set, currently we maybe loosing information by keeping it a set.... I think we should have a list internally and a set externally... but this will do for now...

  private var hasCallableMethod = false
  private var hasDefinedClass = false
  private var numberOfConstructorParameters: Int = 0
  private var className: String = _
  private var callableMethod: String = _
  private var hasConstructor: Boolean = false
  private var isCallableMethodStatic: Boolean = false
  private var numberOfCallableMethodParameters: Int = 0
  private val setterMethodNames: mutable.Buffer[String] = mutable.Buffer.empty

  def addASetterMethod(name : String): Unit = {
    markHasSetterMethods()
    setterMethodNames += name
  }

  feedbacks += NO_CLASS_FOUND
  feedbacks += NO_CONSTRUCTOR_FOUND
  feedbacks += NO_FIELDS_FOUND

  def getFeedbacks: mutable.Set[String] = {
    if (!feedbacks.contains(NO_CONSTRUCTOR_FOUND) && numberOfConstructorParameters == 0) feedbacks.add(NO_CONSTRUCTOR_PARAMETER)
    if (numberOfConstructorParameters == 1) feedbacks.add(ONLY_ONE_CONSTRUCTOR_PARAMETER)
    if (numberOfConstructorParameters > 2) feedbacks.add(TOO_MANY_CONSTRUCTOR_PARAMETER)
    feedbacks
  }

  def setHasDefinedClass(): Unit = {
    feedbacks.remove(NO_CLASS_FOUND)
    this.hasDefinedClass = true;
  }

  def setNumberOfConstructorParameters(count : Int) : Unit = {
    numberOfConstructorParameters = count
  }

  def markHasSomeNonPrivateFields(): Unit = {
    feedbacks.add(FIELDS_SHOULD_BE_PRIVATE)
  }

  def markSomeFieldBreaksJavaConventions(): Unit = {
    feedbacks.add(JAVA_FIELD_NAMING_CONVENTIONS_NOT_FOLLOWED)
  }

  def markSomeMethodNameBreaksEncapsulation(): Unit = {
    feedbacks.add(METHOD_NAME_BREAKS_ENCAPSULATION)
  }

  def markSomeMethodBreaksJavaConventions(): Unit = {
    feedbacks.add(JAVA_METHOD_NAMING_CONVENTIONS_NOT_FOLLOWED)
  }

  def markHasConstructor(): Unit = {
    feedbacks.remove(NO_CONSTRUCTOR_FOUND)
    hasConstructor = true
  }

  def incrementNumberOfFields(): Unit = {
    feedbacks.remove(NO_FIELDS_FOUND)
  }

  def markSomeFieldIsNotFinal(): Unit = {
    feedbacks.add(FIELDS_CAN_BE_FINAL)
  }

  private def getClassName = Option(className)

  private def getCallableMethod = Option(callableMethod)

  def setClassName(nameAsString: String): Unit = {
    className = nameAsString
  }

  def setCallableMethod(callableMethod: String): Unit = {
    this.callableMethod = callableMethod
    this.hasCallableMethod = true
  }

  def markCallableMethodIsStatic(): Unit = {
    this.isCallableMethodStatic = true
  }

  def markHasSetterMethods(): Unit = {
    this.hasSetterMethods = true
  }

  def getTestStatements(rectangle: ReferenceRectangleImplementation): Seq[String] = { // TODO - should likely extract these conditions out
    // TODO - add appropriate feedbacks in these cases

    val configuration = GeneralCodeFeatures(hasDefinedClass, hasCallableMethod, numberOfConstructorParameters, numberOfCallableMethodParameters, hasConstructor, isCallableMethodStatic, hasSetterMethods)

    // TODO - there should be a way  of naming these case statements, but currently I'm struggling to do that...

    configuration match {
      case GeneralCodeFeatures(false, _, _, _, _, _, _) => throw new RuntimeException("Did not find a class")
      case GeneralCodeFeatures(_, false, _, _, _, _, _) => throw new RuntimeException("Did not find a method that can be called")
      case GeneralCodeFeatures(_, _, 2, 0, _, _, _) => return Seq(s"new ${getClassName.get}(${rectangle.length},${rectangle.breath}).${getCallableMethod.get}()")
      case GeneralCodeFeatures(_, _, 0, 2, false, false, _) => return Seq(s"new ${getClassName.get}().${getCallableMethod.get}(${rectangle.length},${rectangle.breath})")
      case GeneralCodeFeatures(_, _, 0, 2, false, true, _) => return Seq(s"${getClassName.get}.${getCallableMethod.get}(${rectangle.length},${rectangle.breath})")
      case GeneralCodeFeatures(_, _, 0, 2, true, true, _) => return Seq(s"${getClassName.get}.${getCallableMethod.get}(${rectangle.length},${rectangle.breath})")
      case GeneralCodeFeatures(_, _, 0, 0, _, _, true) => return Seq(
        s"$className rectangle = new $className();",
        s"rectangle.${setterMethodNames(0)}(${rectangle.length});",
        s"rectangle.${setterMethodNames(1)}(${rectangle.breath});",
        "rectangle.calculate_area();"
      )
      case _ => throw new RuntimeException("This path is not coded!")

    }

    feedbacks.add("NON_UNDERSTANDABLE_API") // TODO - test this.

    Seq.empty[String]
  }

  def setNumberOfCallableMethodParameters(number: Int): Unit = {
    this.numberOfCallableMethodParameters = number
  }


}

object RectangleCodeMetrics {
  private val NO_CLASS_FOUND = "NO_CLASS_FOUND"
  private val NO_CONSTRUCTOR_FOUND = "NO_CONSTRUCTOR_FOUND"
  private val FIELDS_CAN_BE_FINAL = "FIELDS_CAN_BE_FINAL"
  private val NO_CONSTRUCTOR_PARAMETER = "NO_CONSTRUCTOR_PARAMETER"
  private val ONLY_ONE_CONSTRUCTOR_PARAMETER = "ONLY_ONE_CONSTRUCTOR_PARAMETER"
  private val TOO_MANY_CONSTRUCTOR_PARAMETER = "TOO_MANY_CONSTRUCTOR_PARAMETER"
  private val NO_FIELDS_FOUND = "NO_FIELDS_FOUND"
  private val FIELDS_SHOULD_BE_PRIVATE = "FIELDS_SHOULD_BE_PRIVATE"
  private val METHOD_NAME_BREAKS_ENCAPSULATION = "METHOD_NAME_BREAKS_ENCAPSULATION"
  private val JAVA_METHOD_NAMING_CONVENTIONS_NOT_FOLLOWED = "JAVA_METHOD_NAMING_CONVENTIONS_NOT_FOLLOWED"
  private val JAVA_FIELD_NAMING_CONVENTIONS_NOT_FOLLOWED = "JAVA_FIELD_NAMING_CONVENTIONS_NOT_FOLLOWED"

}