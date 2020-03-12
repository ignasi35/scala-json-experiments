package example

import java.io.ByteArrayOutputStream

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.Json

import scala.reflect.ClassTag

/**
  *
  */
class InteropSpec extends FlatSpec with Matchers {
  import example.playjson.PlayJsonSerializers._

  behavior of "Interop Play to Jackson"
  it should "interop Person" in { interop(Examples.jamesHolden) }
  it should "interop Crew" in { interop(Examples.captainJamesHolden) }
  it should "interop Starship" in { interop(Examples.Rocinante) }

  //  {"name":"Rocinante","crew":[...],
  //  "route":
  //  [
  //  {"enumClass":"example.InnerPlanet","value":"Earth"},
  //  {"enumClass":"example.InnerPlanet","value":"Mars"},
  //  {"enumClass":"example.InnerPlanet","value":"Earth"},
  //  {"enumClass":"example.InnerPlanet","value":"Venus"}
  //  ]
  //  }

  private def interop[T: ClassTag](t: T)(implicit f: Format[T]) = {
    // To String via Play
    val jsonString: String = Json.prettyPrint(Json.toJson(t))

    // String via Jackson to Obj
    val clazz = implicitly[ClassTag[T]].runtimeClass

    val mapper = new ObjectMapper()
    mapper.registerModules(
      new ParameterNamesModule,
      new Jdk8Module,
      new DefaultScalaModule
    )

    val reader = mapper.readerFor(clazz)
    val actual: T = reader.readValue(jsonString)

    actual should be(t)

  }

}
