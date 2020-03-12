package example

import java.io.ByteArrayOutputStream

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.scalatest._

import scala.reflect.ClassTag

class JacksonSpec extends FlatSpec with Matchers {
  behavior of "Round-trip Jackson"
  it should "roundtrip Person" in { roundTrip(Examples.jamesHolden) }
  it should "roundtrip Crew" in { roundTrip(Examples.captainJamesHolden) }
  it should "roundtrip Starship" in { roundTrip(Examples.Rocinante) }

  private def roundTrip[T: ClassTag](t: T) = {
    val clazz = implicitly[ClassTag[T]].runtimeClass

    val mapper = new ObjectMapper()
    mapper.registerModules(
      new ParameterNamesModule,
      new Jdk8Module,
      new DefaultScalaModule,
    )
    val baos: ByteArrayOutputStream = new ByteArrayOutputStream(50)
    mapper.writerFor(clazz).writeValue(baos, t)

    val bytes = {
      baos.close()
      baos.toByteArray
    }
    println(new String(bytes))
    val actual: T = mapper.readerFor(clazz).readValue(bytes)

    actual should be(t)
  }
}
