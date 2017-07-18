import com.fasterxml.jackson.databind.ObjectMapper

/**
  * @author Nicola Lasagni
  */
object TravisTest {

  /**
    * Hello test method
    */
  def hello() = {
    println("Hello World!")
    val mapper = new ObjectMapper()
  }

}

object Test extends App {

  TravisTest.hello()

}
