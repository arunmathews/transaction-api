package com.sofichallenge.transactionapi.dependency.impl

import com.sofichallenge.transactionapi.database.tablemapping.Tables._
import com.sofichallenge.transactionapi.database.tablemapping.{Tables, TablesWithDb}
import com.sofichallenge.transactionapi.businessobject.UserBO
import com.sofichallenge.transactionapi.datetime.DateTimeFactory
import com.sofichallenge.transactionapi.dependency.api.UserApi

import scala.concurrent.{ExecutionContext, Future}

/**
  *
  */
class UserApiDBImpl(val tablesWithDb: TablesWithDb)(implicit val ec: ExecutionContext) extends UserApi {
  import tablesWithDb.profile.api._

  private val db = tablesWithDb.db

  private val users = TableQuery[User]

  override def createUser(user: UserBO): Future[UserBO] = {
    val dbAction = for {
      _ <- users += UserRow(0, user.userId, active = true, DateTimeFactory.currentTimeStamp)
    } yield user

    val namedAction = dbAction.named(s"${getClass.getSimpleName} - storeUser")

    db.run(namedAction)
  }

  override def getUser(userId: String): Future[Option[UserBO]] = {
    val query = for {
      user <- users if user.userId === userId
    } yield user

    val action = query.result.map(_.headOption).map(toUserBO)

    db.run(action)
  }

  private def toUserBO(maybeRow: Option[UserRow]): Option[UserBO] = {
    maybeRow.map(userRow => UserBO(userRow.userId))
  }
}
