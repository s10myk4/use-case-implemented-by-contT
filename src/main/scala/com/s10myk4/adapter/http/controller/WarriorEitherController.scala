package com.s10myk4.adapter.http.controller

import cats.data.EitherT
import cats.effect.IO
import com.s10myk4.adapter.http.controller.support.{ActionSupport, FormHelper}
import com.s10myk4.adapter.http.form.EquipWeaponForm
import com.s10myk4.application.usecase._
import com.s10myk4.domain.model.character.warrior.WarriorId
import play.api.mvc._

import scala.concurrent.ExecutionContext

final class WarriorEitherController(
    cc: ControllerComponents,
    findWarrior: FindWarriorEither[IO],
    warriorEquippedNewWeapon: EquipWeaponToWarriorEither[IO],
    presenter: Presenter[UseCaseResult, Result],
    val ec: ExecutionContext
) extends AbstractController(cc)
    with FormHelper
    with ActionSupport {

  def equipWeapon(id: Long): EssentialAction = Action.async { implicit r =>
    (for {
      form <- EitherT(EquipWeaponForm.apply.bindEither[IO])
      (warriorId, weapon) = (WarriorId(id), form.weapon)
      warrior <- EitherT(findWarrior.exec(warriorId))
      result  <- EitherT(warriorEquippedNewWeapon.exec(warrior, weapon))
    } yield result)
      .fold(abnormal => presenter.present(abnormal), normal => presenter.present(normal))
      .toFuture
  }

}
