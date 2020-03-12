package example.playjson

import example.Crew
import example.Examples
import example.Person
import example.Planet
import example.Starship
import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes

object PlayJsonSerializers {

  implicit val planetReads = Reads.enumNameReads(Planet)
  implicit val planeWrites = Writes.enumNameWrites
  implicit val formatPerson: Format[Person] = Json.format
  implicit val formatCrew: Format[Crew] = Json.format
  implicit val formatStarship: Format[Starship] = Json.format

}

object PlayJsonExamples {
  import example.playjson.PlayJsonSerializers._

  private val RocinanteJsValue: JsValue = Json.toJson(Examples.Rocinante)
  val RocinanteString: String = Json.prettyPrint(RocinanteJsValue)
}
