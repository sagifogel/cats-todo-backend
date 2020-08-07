package com.github.sagifogel.todo.http

import cats.Applicative
import io.circe.refined._
import org.http4s.circe.jsonEncoderOf
import com.github.sagifogel.todo.domain.todo.{Todo, TodoItemDescription, TodoItemId, TodoStatus}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import org.http4s.EntityEncoder

object Json extends JsonCodecs {
  implicit def deriveEntityEncoder[F[_] : Applicative, A: Encoder]: EntityEncoder[F, A] = jsonEncoderOf[F, A]
}

private[http] trait JsonCodecs {
  implicit val itemIdDecoder: Decoder[TodoItemId] =
    Decoder.forProduct1("id")(TodoItemId.apply)

  implicit val itemIdEncoder: Encoder[TodoItemId] =
    Encoder.forProduct1("id")(_.value)

  implicit val itemDescriptionDecoder: Decoder[TodoItemDescription] =
    Decoder.forProduct1("description")(TodoItemDescription.apply)

  implicit val itemDescriptionEncoder: Encoder[TodoItemDescription] =
    Encoder.forProduct1("description")(_.value)

  implicit val statusDecoder: Decoder[TodoStatus] =
    Decoder.forProduct1("status")(TodoStatus.apply)

  implicit val statusEncoder: Encoder[TodoStatus] =
    Encoder.forProduct1("status")(_.done)

  implicit val todoDecoder: Decoder[Todo] = deriveDecoder[Todo]
  implicit val todoEncoder: Encoder[Todo] = deriveEncoder[Todo]
}

