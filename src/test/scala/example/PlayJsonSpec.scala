package example

import org.scalatest._
import play.api.libs.json.JsValue
import play.api.libs.json.Json

class PlayJsonSpec extends FlatSpec with Matchers {
  "Round-trip Play" should "roundtrip" in {

    val expected = Examples.Rocinante

    import example.playjson.PlayJsonSerializers._

    val jsonString: String = Json.prettyPrint(Json.toJson(Examples.Rocinante))
    println(jsonString)
    val jsValue: JsValue = Json.parse(jsonString)
    val actual = jsValue.as[Starship]

    actual should be(expected)

  }
}
