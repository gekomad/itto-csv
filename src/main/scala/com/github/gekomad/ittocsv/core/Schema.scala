package com.github.gekomad.ittocsv.core

import cats._
import implicits._
import data.ValidatedNel
import shapeless._
import labelled._

sealed trait Schema[A] {
  def readFrom(input: Map[String, String]): ValidatedNel[ParseFailure, A]
}

object Schema {
  def of[A](implicit s: Schema[A]): Schema[A] = s

  private def instance[A](body: Map[String, String] => ValidatedNel[ParseFailure, A]): Schema[A] = new Schema[A] {
    def readFrom(input: Map[String, String]): ValidatedNel[ParseFailure, A] = body(input)
  }

  implicit val noOp: Schema[HNil] = new Schema[HNil] {
    def readFrom(input: Map[String, String]): ValidatedNel[Nothing, HNil.type] = HNil.validNel
  }

  implicit def parsing[K <: Symbol, V: Convert, T <: HList](implicit key: Witness.Aux[K], next: Schema[T]): Schema[FieldType[K, V] :: T] = Schema.instance { input =>
    (
      input
        .get(key.value.name)
        .fold(ParseFailure(s"${key.value.name} is missing").invalidNel: ValidatedNel[ParseFailure, V])(entry => Convert.to[V](entry))
        .map(field[K](_)),
      next.readFrom(input)
    ).mapN(_ :: _)
  }

  implicit def classes[A, R <: HList](implicit repr: LabelledGeneric.Aux[A, R], schema: Schema[R]): Schema[A] =
    Schema.instance { input =>
      schema.readFrom(input).map(x => repr.from(x))
    }
}
