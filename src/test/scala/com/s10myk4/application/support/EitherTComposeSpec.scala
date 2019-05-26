package com.s10myk4.application.support

import cats.data.EitherT
import cats.effect.IO
import org.scalatest.FlatSpec

class EitherTComposeSpec extends FlatSpec {

  it should "EitherT" in {
    val res = for {
      a <- EitherT[IO, String, String](IO(Right("a")))
      b <- EitherT[IO, String, String](IO(Left("break")))
      c <- EitherT[IO, String, String](IO(Right("c")))
    } yield a + b + c
    res
      .fold(
        left => assert(left == "break"),
        _ => false
      )
      .unsafeRunSync()
  }

}
