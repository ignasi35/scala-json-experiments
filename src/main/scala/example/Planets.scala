package example

import example.Planet.Planet
import play.api.libs.json.Format
import play.api.libs.json.Json

object Planet extends Enumeration {
  type Planet = Value
  val Mercury, Venus, Earth, Mars, Jupiter, Saturn, Uranus,
  Neptune = Value // Not you, Pluto
}

case class Person(name: String, surname: String)
case class Crew(role: String, person: Person)
case class Starship(name: String, crew: Seq[Crew], route: Seq[Planet])

object Examples {
  import example.Planet._
  val Rocinante = Starship(
    "Rocinante",
    Seq(
      Crew("Captain", Person("James", "Holden")),
      Crew("Executive Officer", Person("Naomi", "Nagata")),
      Crew("Pilot", Person("Alex", "Kamal")),
      Crew("Chief Engineer", Person("Amos", "Burton")),
    ),
    Seq(Earth, Mars, Earth, Venus)
  )
}
