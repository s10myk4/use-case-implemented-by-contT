package com.s10myk4.adapter.http.presenter.json

import com.s10myk4.application.usecase._
import io.circe.syntax._
import play.api.mvc.Result
import play.api.mvc.Results.{BadRequest, InternalServerError, Ok}

final class DefaultPresenter extends Presenter[UseCaseResult, Result] with JsonEncoder with WritableJsonConverter {

  def present(arg: UseCaseResult): Result = {
    arg match {
      case NormalCase => Ok
      case res: InvalidInputParameters =>
        BadRequest(res.asJson(formErrorEncoder))
      case res: AbnormalCase => InternalServerError(res.asJson)
    }
  }

}
