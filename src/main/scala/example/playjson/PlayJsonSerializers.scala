package example.playjson

import example.Crew
import example.Examples
import example.InnerPlanet
import example.Person
import example.Starship
import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.Json

object PlayJsonSerializers {

  implicit val formatPlanet = Json.formatEnum(InnerPlanet)
  implicit val formatPerson: Format[Person] = Json.format
  implicit val formatCrew: Format[Crew] = Json.format
  implicit val formatStarship: Format[Starship] = Json.format

}

object PlayJsonExamples {
  import example.playjson.PlayJsonSerializers._

  private val RocinanteJsValue: JsValue = Json.toJson(Examples.Rocinante)
  val RocinanteString: String = Json.prettyPrint(RocinanteJsValue)
}
