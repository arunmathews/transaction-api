package com.sofichallenge.transactionapi.handler

import com.sofichallenge.transactionapi.bootstrap.handler.TransactionRequestHandlerTestSupport
import com.sofichallenge.transactionapi.businessobject.{GetMerchantsSpecifiers, GetTransactionsSpecifiers, TransactionBO}
import com.sofichallenge.transactionapi.datetime.DateTimeFactory
import com.sofichallenge.transactionapi.service.validation.businessobject.BOValidationFailureCode.{Duplicate, Invalid}
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.{AsyncFlatSpec, Matchers}

import scala.concurrent.Future

/**
  *
  */
class TransactionRequestHandlerSpec
  extends AsyncFlatSpec
    with TransactionRequestHandlerTestSupport
    with Matchers {

  it should "create transaction successfully" in {
    val userId = 1001
    val merchantId = 1002
    val txId = 1003
    val txBO = createTransaction(userId, merchantId, txId)
    val testSeq = for {
      createdTx <- txHandler.createTransaction(txBO)
      failedDupeTx <- txHandler.createTransaction(txBO)
    } yield (txBO, createdTx, failedDupeTx)

    testSeq.map {
      case (inputTx, createdTx, notCreatedTx) =>
        createdTx.fold(
          failure => fail(s"Unexpected failure - $failure"),
          succ => {
            assert(inputTx.txId === succ.txId)
            assert(inputTx.merchant === succ.merchant)
            assert(!inputTx.void)
          })
        notCreatedTx.fold(failure => assert(failure.head.failureCode === Duplicate),
          succ => fail(s"Unexpected success - $succ"))
    }
  }

  it should "void transaction successfully" in {
    val userId = 1001
    val merchantId = 1002
    val txId = 1004
    val txBO = createTransaction(userId, merchantId, txId)
    val testSeq = for {
      createdTx <- txHandler.createTransaction(txBO)
      voidedTx <- txHandler.voidTransaction(txBO.txId)
    } yield (txBO, createdTx, voidedTx)

    testSeq.map {
      case (inputTx, createdTx, voidedTx) =>
        createdTx.fold(
          failure => fail(s"Unexpected failure - $failure"),
          succ => {
            assert(!succ.void)
          })
        voidedTx.fold(failure => fail(s"Unexpected failure - $failure"),
          succ => {
            assert(succ.void)
          })
    }
  }

  it should "get merchants successfully" in {
    val userIdVal = 1010
    val userIdInval = 1020
    val merchantId = 2010
    val txIdStart = 3005
    val txFuts = (1 to 6).map(counter => {
      val txBO = createTransaction(userIdVal, merchantId, txIdStart + counter)
      txHandler.createTransaction(txBO)
    })

    val txBO = createTransaction(userIdInval, merchantId, 3020)
    val testSeq = for {
      _ <- txHandler.createTransaction(txBO)
      _ <- Future.sequence(txFuts)
      getMerchsVal <- txHandler.getMerchants(GetMerchantsSpecifiers(Seq(userIdVal), 3, true))
      getMerchsInval <- txHandler.getMerchants(GetMerchantsSpecifiers(Seq(userIdInval), 3, true))
    } yield (getMerchsVal, getMerchsInval)

    testSeq.map {
      case (getMerchsVal, getMerchsInval) =>
        getMerchsVal.fold(
          failure => fail(s"Unexpected failure - $failure"),
          succ => {
            assert(succ.merchants.headOption.fold(fail("No merchants returned"))(
              merchant => merchant === "Test Merchant"))
          })
        getMerchsInval.fold(failure => assert(failure.head.failureCode === Invalid),
          succ => {
            fail(s"Unexpected success - $succ")
          })
    }
  }

  it should "get transactions successfully" in {
    val userIdVal = 5010
    val merchantId = 7010
    val txIdStart = 9005
    val txFuts = (1 to 6).map(counter => {
      val txBO = createTransaction(userIdVal, merchantId, txIdStart + counter)
      txHandler.createTransaction(txBO)
    })

    val testSeq = for {
      _ <- Future.sequence(txFuts)
      getTxs <- txHandler.getTransactions(GetTransactionsSpecifiers(userIdVal, 3))
    } yield getTxs

    testSeq.map {
      case (transs) =>
        assert(transs.count === 6)
        assert(transs.nextOffset === None)
        assert(transs.prevOffset === None)
    }
  }

  private def createTransaction(userId: Int, merchantId: Int,  txId: Int): TransactionBO = {
    val merchantName = "Test Merchant"
    val price = BigDecimal(23.23)
    val purchaseDate = new DateTime(DateTimeZone.UTC).minusDays(5)

    TransactionBO(txId, userId, merchantId, merchantName, price, purchaseDate, void = false)
  }
}
