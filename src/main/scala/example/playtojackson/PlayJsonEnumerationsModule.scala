/**

THIS IS UNFINISHED WORK AND UNUSED. As it is it is only a bunch of 
copy/paste/rename from https://github.com/FasterXML/jackson-module-scala/blob/master/src/main/scala/com/fasterxml/jackson/module/scala/deser/EnumerationDeserializerModule.scala

*/ 


package example.playtojackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.deser.ContextualKeyDeserializer
import com.fasterxml.jackson.databind.deser.Deserializers
import com.fasterxml.jackson.databind.deser.KeyDeserializers
import com.fasterxml.jackson.module.scala.JacksonModule
import com.fasterxml.jackson.module.scala.util.EnumResolver

trait PlayJsonEnumerationsModule extends JacksonModule {
  this += { ctxt =>
    {
      ctxt.addDeserializers(PlayJsonEnumerationDeserializerResolver)
      ctxt.addKeyDeserializers(PlayEnumerationKeyDeserializers)
    }
  }
}

private object PlayJsonEnumerationDeserializerResolver
    extends Deserializers.Base {

  override def findBeanDeserializer(javaType: JavaType,
                                    config: DeserializationConfig,
                                    beanDesc: BeanDescription) = {

    val clazz = javaType.getRawClass
    var deserializer: JsonDeserializer[_] = null

    if (classOf[scala.Enumeration#Value].isAssignableFrom(clazz)) {
      deserializer = new PlayJsonEnumerationDeserializer(javaType)
    }

    deserializer
  }
}

private class PlayAnnotatedEnumerationDeserializer(r: EnumResolver)
    extends JsonDeserializer[Enumeration#Value]
    with ContextualEnumerationDeserializer {
  override def deserialize(jp: JsonParser,
                           ctxt: DeserializationContext): Enumeration#Value = {
    jp.getCurrentToken match {
      case JsonToken.VALUE_STRING => r.getEnum(jp.getValueAsString)
      case _ =>
        ctxt
          .handleUnexpectedToken(r.getEnumClass, jp)
          .asInstanceOf[Enumeration#Value]
    }
  }
}

private trait ContextualEnumerationDeserializer extends ContextualDeserializer {
  self: JsonDeserializer[Enumeration#Value] =>

  override def createContextual(
    ctxt: DeserializationContext,
    property: BeanProperty
  ): JsonDeserializer[Enumeration#Value]
    with ContextualEnumerationDeserializer = {
    EnumResolver(property)
      .map(r => new PlayAnnotatedEnumerationDeserializer(r))
      .getOrElse(this)
  }

}

/**
  * This class is mostly legacy logic to be deprecated/removed in 3.0
  */
private class PlayJsonEnumerationDeserializer(theType: JavaType)
    extends JsonDeserializer[Enumeration#Value]
    with ContextualEnumerationDeserializer {
  override def deserialize(jp: JsonParser,
                           ctxt: DeserializationContext): Enumeration#Value = {
    if (jp.getCurrentToken != JsonToken.START_OBJECT) {
      ctxt
        .handleUnexpectedToken(theType.getRawClass, jp)
        .asInstanceOf[Enumeration#Value]
    } else {
      val (eclass, eclassName) = parsePair(jp)
      if (eclass != "enumClass") {
        ctxt
          .handleUnexpectedToken(theType.getRawClass, jp)
          .asInstanceOf[Enumeration#Value]
      } else {
        val (value, valueValue) = parsePair(jp)
        if (value != "value") {
          ctxt
            .handleUnexpectedToken(theType.getRawClass, jp)
            .asInstanceOf[Enumeration#Value]
        } else {
          jp.nextToken()
          Class
            .forName(eclassName + "$")
            .getField("MODULE$")
            .get(null)
            .asInstanceOf[Enumeration]
            .withName(valueValue)
        }
      }
    }
  }

  private def parsePair(jp: JsonParser) =
    ({ jp.nextToken; jp.getText }, { jp.nextToken; jp.getText })
}

private object PlayEnumerationKeyDeserializers extends KeyDeserializers {
  def findKeyDeserializer(tp: JavaType,
                          cfg: DeserializationConfig,
                          desc: BeanDescription): KeyDeserializer = {
    if (classOf[scala.Enumeration#Value].isAssignableFrom(tp.getRawClass)) {
      new EnumerationKeyDeserializer(None)
    } else null
  }
}

private class EnumerationKeyDeserializer(r: Option[EnumResolver])
    extends KeyDeserializer
    with ContextualKeyDeserializer {

  override def createContextual(ctxt: DeserializationContext,
                                property: BeanProperty) = {
    val newResolver = EnumResolver(property)
    if (newResolver != r) new EnumerationKeyDeserializer(newResolver) else this
  }

  def deserializeKey(s: String,
                     ctxt: DeserializationContext): Enumeration#Value = {
    if (r.isDefined) {
      return r.get.getEnum(s)
    }

    throw ctxt.mappingException(
      "Need @JsonScalaEnumeration to determine key type"
    )
  }
}
