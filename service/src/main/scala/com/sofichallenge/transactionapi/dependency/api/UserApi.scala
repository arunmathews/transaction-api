package com.sofichallenge.transactionapi.dependency.api

import com.sofichallenge.transactionapi.businessobject.UserBO

import scala.concurrent.Future

/**
  *
  */
trait UserApi {
  def createUser(user: UserBO): Future[UserBO]

  def getUser(userId: String): Future[Option[UserBO]]
}
