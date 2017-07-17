import com.fasterxml.jackson.databind.ObjectMapper

/**
  * Created by nicolalasagni on 16/07/2017.
  */
object TravisTest {

  /**
    * @deprecated
    */
  def hello() = {
    println("Hello World!")
    val mapper = new ObjectMapper()
  }

}

object Test extends App {

  TravisTest.hello()

}
