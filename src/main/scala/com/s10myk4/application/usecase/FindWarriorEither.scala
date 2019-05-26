package com.s10myk4.application.usecase

import cats.Monad
import cats.syntax.functor._
import com.s10myk4.application.usecase.FindWarrior.WarriorNotFound
import com.s10myk4.domain.lifcycle.WarriorRepository
import com.s10myk4.domain.model.character.warrior.{Warrior, WarriorId}

import scala.language.higherKinds

final class FindWarriorEither[F[_]: Monad](
    repository: WarriorRepository[F]
) {

  def exec(id: WarriorId): F[Either[AbnormalCase, Warrior]] = {
    repository.resolveBy(id).map {
      case Some(w) => Right(w)
      case None    => Left(WarriorNotFound)
    }
  }

}
