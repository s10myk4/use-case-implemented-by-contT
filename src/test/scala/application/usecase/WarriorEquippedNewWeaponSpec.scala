package application.usecase

import domain.lifcycle.DefaultIOContext
import domain.model.character.warrior.Warrior
import domain.model.character.warrior.WarriorArbitrary._
import domain.model.weapon.Weapon
import domain.model.weapon.WeaponArbitrary._
import org.scalatest.FlatSpec
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import scalaz.std.scalaFuture

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class WarriorEquippedNewWeaponSpec
  extends FlatSpec
    with GeneratorDrivenPropertyChecks {

  behavior of "戦士に新しい武器を装備する"

  it should "成功する" in {
    import domain.lifcycle.WarriorRepository._

    implicit val ctx = DefaultIOContext
    implicit val fm = scalaFuture.futureInstance(ctx.ec)

    val uc = new WarriorEquippedNewWeaponImpl
    forAll { (warrior: Warrior, weapon: Weapon) =>
      whenever(
        warrior.attribute == weapon.attribute &&
          weapon.levelConditionOfEquipment <= warrior.level.value
      ) {
        // HACK: whenever は Result を返却する必要があるため、Awaitしている
        val result = Await.result(uc(warrior, weapon).run_, Duration.Inf)
        assert(result === NormalCase)
      }
    }
  }
}