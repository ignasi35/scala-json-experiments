package example

import org.scalatest._
import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.Json

import scala.reflect.ClassTag

class PlayJsonSpec extends FlatSpec with Matchers {
  import example.playjson.PlayJsonSerializers._
  behavior of "Round-trip Play"

  it should "roundtrip Person" in roundTrip(Examples.jamesHolden)
  it should "roundtrip Crew" in roundTrip(Examples.captainJamesHolden)
  it should "roundtrip Starship" in roundTrip(Examples.Rocinante)

  //  {"name":"Rocinante",
  //  "crew":[...],
  //  "route":[
  //    "Earth",
  //    "Mars",
  //    "Earth",
  //    "Venus"
  //  ]
  //  }

  private def roundTrip[T: ClassTag](t: T)(implicit f: Format[T]): Unit = {
    val jsonString: String = Json.stringify(Json.toJson(t))
//    val jsonString: String = Json.prettyPrint(Json.toJson(t))

    println(jsonString)

    val jsValue: JsValue = Json.parse(jsonString)

    val actual = jsValue.as[T]

    actual should be(t)
  }
}
