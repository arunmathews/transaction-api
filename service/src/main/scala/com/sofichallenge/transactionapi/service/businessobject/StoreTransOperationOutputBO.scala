package com.sofichallenge.transactionapi.service.businessobject

import com.sofichallenge.transactionapi.businessobject.TransactionBO
import com.sofichallenge.transactionapi.service.validation.businessobject.BOValidationFailure

/**
  *
  */
case class StoreTransOperationOutputBO(storedTrans: Option[TransactionBO] = None,
                                       validationFailures: Option[List[BOValidationFailure]] = None)