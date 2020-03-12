package example

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import example.InnerPlanet.InnerPlanet

object InnerPlanet extends Enumeration {
  type InnerPlanet = Value
  val Mercury, Venus, Earth, Mars = Value
}

class InnerPlanetType extends TypeReference[InnerPlanet.type] {}

case class Person(name: String, surname: String)
case class Crew(role: String, person: Person)
case class Starship(
  name: String,
  crew: Seq[Crew],
  @JsonScalaEnumeration(classOf[InnerPlanetType]) route: Seq[InnerPlanet]
)

object Examples {
  import example.InnerPlanet._

  val jamesHolden: Person = Person("James", "Holden")
  val captainJamesHolden: Crew = Crew("Captain", jamesHolden)

  val Rocinante = Starship(
    "Rocinante",
    Seq(
      captainJamesHolden,
      Crew("Executive Officer", Person("Naomi", "Nagata")),
      Crew("Pilot", Person("Alex", "Kamal")),
      Crew("Chief Engineer", Person("Amos", "Burton")),
    ),
    Seq(Earth, Mars, Earth, Venus)
  )
}
