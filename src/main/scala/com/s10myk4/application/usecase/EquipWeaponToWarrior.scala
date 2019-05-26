package com.s10myk4.application.usecase

import cats.Monad
import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import cats.syntax.flatMap._
import com.s10myk4.application.cont.UseCaseCont
import com.s10myk4.application.support.UseCaseCont
import com.s10myk4.domain.lifcycle.WarriorRepository
import com.s10myk4.domain.model.character.warrior.Warrior.{DifferentAttributeError, NotOverLevelError}
import com.s10myk4.domain.model.character.warrior.{Warrior, WarriorError}
import com.s10myk4.domain.model.weapon.Weapon

import scala.language.higherKinds

final class EquipWeaponToWarrior[F[_]: Monad](
    repository: WarriorRepository[F]
) {

  import EquipWeaponToWarrior._

  def exec(warrior: Warrior, newWeapon: Weapon): UseCaseCont[F, UseCaseResult] =
    UseCaseCont { f =>
      warrior.equip(newWeapon) match {
        case Valid(w)     => repository.update(w).flatMap(_ => f(NormalCase))
        case Invalid(err) => Monad[F].point(err)
      }
    }
}

object EquipWeaponToWarrior {

  final case class EquipWeaponToWarriorInput(
      weapon: Weapon
  )

  implicit def toUseCaseResult(domainErrors: NonEmptyList[WarriorError]): AbnormalCase = {
    val errors = domainErrors.toList.toSet
    errors match {
      case _ if errors == Set(DifferentAttributeError, NotOverLevelError) => DifferentAttributeAndNotOverLevel
      case _ if errors == Set(DifferentAttributeError)                    => DifferentAttribute
      case _ if errors == Set(NotOverLevelError)                          => NotOverLevel
      case _                                                              => NotConsideredDomainError
    }
  }

  object DifferentAttributeAndNotOverLevel extends AbnormalCase {
    val cause: String = s"${DifferentAttribute.cause} and ${NotOverLevel.cause}"
  }

  object DifferentAttribute extends AbnormalCase {
    val cause: String = s"Weapon attribute is different warrior attribute"
  }

  object NotOverLevel extends AbnormalCase {
    val cause: String = s"Warrior level is not over weapon level"
  }

}
