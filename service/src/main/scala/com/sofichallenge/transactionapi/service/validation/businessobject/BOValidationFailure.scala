package com.sofichallenge.transactionapi.service.validation.businessobject

/**
 *
 */
case class BOValidationFailure(fieldName: BOFieldName,
                               failureCode: BOValidationFailureCode,
                               displayString: String)
