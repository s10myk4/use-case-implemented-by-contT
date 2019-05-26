package com.s10myk4.application.support

import cats.Applicative
import cats.data.ContT
import cats.effect.IO
import org.scalatest.FlatSpec

class UseCaseContSpec extends FlatSpec {

  behavior of "ActionCont"

  it should "上から実行される" in {
    val composed = for {
      a <- ContT[IO, String, String](f => f("a"))
      b <- ContT[IO, String, String](f => f("b"))
      c <- ContT[IO, String, String](f => f("c"))
    } yield a + b + c
    val res = composed.run(Applicative[IO].pure).unsafeRunSync()
    assert(res == "abc")
  }

  it should "処理途中で異常系が発生した場合に処理を打ち切る" in {
    val res = for {
      a <- ContT[IO, String, String](f => f("a"))
      b <- ContT[IO, String, String](f => f("b"))
      c <- ContT[IO, String, String](_ => IO("break"))
      d <- ContT[IO, String, String](f => f("d"))
    } yield a + b + c + d
    res.run(Applicative[IO].pure).map(r => assert(r == "break")).unsafeRunSync()
  }

}
