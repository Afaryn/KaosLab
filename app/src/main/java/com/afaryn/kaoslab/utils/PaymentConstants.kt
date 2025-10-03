package com.afaryn.kaoslab.utils

object PaymentConstants {
    //Intent Key
    const val KEY_TRANSACTION_RESULT = "UiKitConstants.key_transaction_result"
    const val KEY_ERROR_NAME = "UiKitConstant.key_error_name"
    const val KEY_ERROR_MESSAGE ="UiKitConstant.key_error_message"
    const val KEY_SNAP_TOKEN = "UiKitConstant.key_snap_token"

    //Transaction Status
    const val STATUS_PENDING = "pending"
    const val STATUS_SUCCESS = "success"
    const val STATUS_FAILED = "failed"
    const val STATUS_SETTLEMENT = "settlement"
    const val STATUS_CANCELED = "canceled"
    const val STATUS_DENY = "deny"
    const val STATUS_CAPTURE = "capture"
    const val STATUS_INVALID = "invalid"

    //Status Code
    const val STATUS_CODE_200 = "200"
    const val STATUS_CODE_201 = "201"

    //Symbol
    const val BULLET_LIST = "\u2022"

    //Activity Result
    const val ACTIVITY_CC_CHANGES = 2
}