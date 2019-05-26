package com.s10myk4.application.usecase

import cats.Monad
import cats.data.EitherT
import cats.data.Validated.{Invalid, Valid}
import cats.syntax.functor._
import com.s10myk4.domain.lifcycle.WarriorRepository
import com.s10myk4.domain.model.character.warrior.Warrior
import com.s10myk4.domain.model.weapon.Weapon

import scala.language.higherKinds

final class EquipWeaponToWarriorEither[F[_]: Monad](
    repository: WarriorRepository[F]
) {

  import EquipWeaponToWarrior._

  def exec(warrior: Warrior, newWeapon: Weapon): F[Either[AbnormalCase, NormalCase.type]] = {
    warrior.equip(newWeapon) match {
      case Valid(w)     => repository.update(w).map(_ => Right(NormalCase))
      case Invalid(err) => Monad[F].point(Left(err))
    }
  }

  def hoge(warrior: Warrior, newWeapon: Weapon): EitherT[F, AbnormalCase, NormalCase.type] = {
    warrior.equip(newWeapon) match {
      case Valid(w) =>
        EitherT.right[AbnormalCase](repository.update(w).map(_ => NormalCase))
      case Invalid(err) =>
        EitherT.left[NormalCase.type](Monad[F].pure(toUseCaseResult(err)))
    }
  }

}
