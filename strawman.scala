//////////////////////////////////////////////////////////////////////////////
// This is an interactive strawman proposal for a way we might consider
// using Scalaz 7 validations with the new, less-haskell-oriented syntax. 
//
// Proposed benefits of this syntax:
//   * no starship operators <*> nor |@|
//   * normal function application f(x, y, z)
//   * one-liner to wrap any function to handle validated input and output
// 
// To load in an interactive sbt console:
//
//   cat load_file_in_sbt - | sbt-extras.sh console-quick
//
//////////////////////////////////////////////////////////////////////////////

// Imports the minimum necessary parts of Scalaz for the usage below
import scalaz.{Applicative, NonEmptyList, Validation}
import scalaz.syntax.validation._ 

// We want to return either a type X, or a non-empty list of strings for error messages
type ValidOrErrorStrings[X] = Validation[NonEmptyList[String], X]

//////////////////////////////////////////////////////////////////////////////
//
// Example 1: Adding two numbers
//
//////////////////////////////////////////////////////////////////////////////

// Let's say we care about the add function...
def add(x: Int, y: Int) = x + y

// And we want to validate that our inputs numbers are positive...
def validatePositive(n: Int) = if (n > 0) n.successNel else "invalid num".failNel

val validNum   = validatePositive(1)  // Validation[NonEmptyList[String],Int] = Success(1)
val invalidNum = validatePositive(-1) // Validation[NonEmptyList[String],Int] = Failure(NonEmptyList(invalid num))

// We can make our add function handle the validated inputs...
val addValidated = Applicative[ValidOrErrorStrings].lift2(add)

// And Bob's your uncle!
addValidated(validNum,   validNum)   // Validation[NonEmptyList[String],Int] = Success(2)
addValidated(invalidNum, validNum)   // Validation[NonEmptyList[String],Int] = Failure(NonEmptyList(invalid num))
addValidated(invalidNum, invalidNum) // Validation[NonEmptyList[String],Int] = Failure(NonEmptyList(invalid num, invalid num))


//////////////////////////////////////////////////////////////////////////////
//
// Example 2: Composing a case class from inputs
//
//////////////////////////////////////////////////////////////////////////////

// Let's say we care about constructing this case class from some inputs...
case class Person(name: String, age: Int, postCode: String)

// And we want to validate our inputs...
def validateName(n: String) = if (n == "Tom")    n.successNel else "invalid name".failNel
def validateAge(a: Int)     = if (a >= 21)       a.successNel else "invalid age".failNel
def validatePost(p: String) = if (p.length == 5) p.successNel else "invalid post code".failNel

val validName   = validateName("Tom")
val invalidName = validateName("Marc")
val validAge    = validateAge(32)
val invalidAge  = validateAge(19)
val validPost   = validatePost("02110")
val invalidPost = validatePost("0123456789")

// We can make our case class constructor handle the validated inputs...
val PersonValidated = Applicative[ValidOrErrorStrings].lift3(Person)

// And Bob's your uncle!
PersonValidated(validName, validAge, validPost)     // ValidOrErrorStrings[Person] = Success(Person(Tom,32,02110))
PersonValidated(validName, invalidAge, invalidPost) // ValidOrErrorStrings[Person] = Failure(NonEmptyList(invalid age, invalid post code))


//////////////////////////////////////////////////////////////////////////////
//
// Example 3: What if we have existing code returning Eithers?
//
//////////////////////////////////////////////////////////////////////////////

// Let's say we have existing validation code returning eithers:
def validateNegative(n: Int) = if (n < 0) Right(n) else Left("invalid num")

val validNeg   = validateNegative(-1) // Either[String, Int] = Right(-1)
val invalidNeg = validateNegative(1)  // Either[String, Int] = Left(invalid num)

// We can use an implicit conversion defined in a limited scope...
implicit def e2v[X](e: Either[String, X]) = Validation.fromEither(e).toValidationNel 

// We're good to go!
addValidated(validNeg,   validNeg)    // Validation[NonEmptyList[String],Int] = Success(-2)
addValidated(validNeg,   invalidNeg)  // Validation[NonEmptyList[String],Int] = Failure(NonEmptyList(invalid num))
addValidated(invalidNeg, invalidNeg)  // Validation[NonEmptyList[String],Int] = Failure(NonEmptyList(invalid num, invalid num))

// But wait, that's *too much magic* for me, can't we just be explicit? Sure...
addValidated(e2v(validNeg), e2v(invalidNeg))

//
// That's it! 
//
println("\n\nLoaded. Feel free to play with the code in the console! :)\n")


//////////////////////////////////////////////////////////////////////////////
// ADDITIONAL NOTES
// ----------------
//
// 1. There is an additional Scalaz syntax sugaring for Function2, but it's not available
//    for the other arities, so I didn't use it:
// 
//      // exactly equivalant to Applicative[ValidOrErrorStrings].lift2(add)
//      val addValidated = add.lift[ValidOrErrorStrings]    
// 
//    See https://github.com/scalaz/scalaz/blob/03ae2eb2d09fb918ade038ad1683d202bb952e75/core/src/main/scala/scalaz/syntax/std/Function2Ops.scala
//
//
// 2. The most *general* Scalaz 7 imports for the usage is as follows:
//
//      import scalaz._
//      import Scalaz._
//
//////////////////////////////////////////////////////////////////////////////


