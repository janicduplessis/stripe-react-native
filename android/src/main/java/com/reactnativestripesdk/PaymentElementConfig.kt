package com.reactnativestripesdk

import com.facebook.react.bridge.ReadableMap
import com.reactnativestripesdk.utils.PaymentSheetException
import com.reactnativestripesdk.utils.getIntOr
import com.reactnativestripesdk.utils.isEmpty
import com.stripe.android.paymentelement.PaymentMethodOptionsSetupFutureUsagePreview
import com.stripe.android.paymentsheet.ExperimentalCustomerSessionApi
import com.stripe.android.paymentsheet.PaymentSheet

@Throws(PaymentSheetException::class)
internal fun buildIntentConfiguration(intentConfigurationParams: ReadableMap?): PaymentSheet.IntentConfiguration? {
  if (intentConfigurationParams == null) {
    return null
  }
  val modeParams =
    intentConfigurationParams.getMap("mode")
      ?: throw PaymentSheetException(
        "If `intentConfiguration` is provided, `intentConfiguration.mode` is required",
      )

  return PaymentSheet.IntentConfiguration(
    mode = buildIntentConfigurationMode(modeParams),
    paymentMethodTypes =
      intentConfigurationParams.getStringArrayList("paymentMethodTypes")?.toList()
        ?: emptyList(),
  )
}

@OptIn(PaymentMethodOptionsSetupFutureUsagePreview::class)
private fun buildIntentConfigurationMode(modeParams: ReadableMap): PaymentSheet.IntentConfiguration.Mode =
  if (modeParams.hasKey("amount")) {
    val currencyCode =
      modeParams.getString("currencyCode")
        ?: throw PaymentSheetException(
          "You must provide a value to intentConfiguration.mode.currencyCode",
        )
    PaymentSheet.IntentConfiguration.Mode.Payment(
      amount = modeParams.getInt("amount").toLong(),
      currency = currencyCode,
      setupFutureUse = mapToSetupFutureUse(modeParams.getString("setupFutureUsage")),
      captureMethod = mapToCaptureMethod(modeParams.getString("captureMethod")),
      paymentMethodOptions = mapToPaymentMethodOptions(modeParams.getMap("paymentMethodOptions")),
    )
  } else {
    val setupFutureUsage =
      mapToSetupFutureUse(modeParams.getString("setupFutureUsage"))
        ?: throw PaymentSheetException(
          "You must provide a value to intentConfiguration.mode.setupFutureUsage",
        )
    PaymentSheet.IntentConfiguration.Mode.Setup(
      currency = modeParams.getString("currencyCode"),
      setupFutureUse = setupFutureUsage,
    )
  }

internal fun buildLinkConfig(params: ReadableMap?): PaymentSheet.LinkConfiguration {
  if (params == null) {
    return PaymentSheet.LinkConfiguration()
  }

  val display = mapStringToLinkDisplay(params.getString("display"))

  return PaymentSheet.LinkConfiguration(
    display = display,
  )
}

private fun mapStringToLinkDisplay(value: String?): PaymentSheet.LinkConfiguration.Display =
  when (value) {
    "automatic" -> PaymentSheet.LinkConfiguration.Display.Automatic
    "never" -> PaymentSheet.LinkConfiguration.Display.Never
    else -> PaymentSheet.LinkConfiguration.Display.Automatic
  }

private val mapIntToButtonType =
  mapOf(
    1 to PaymentSheet.GooglePayConfiguration.ButtonType.Buy,
    6 to PaymentSheet.GooglePayConfiguration.ButtonType.Book,
    5 to PaymentSheet.GooglePayConfiguration.ButtonType.Checkout,
    4 to PaymentSheet.GooglePayConfiguration.ButtonType.Donate,
    11 to PaymentSheet.GooglePayConfiguration.ButtonType.Order,
    1000 to PaymentSheet.GooglePayConfiguration.ButtonType.Pay,
    7 to PaymentSheet.GooglePayConfiguration.ButtonType.Subscribe,
    1001 to PaymentSheet.GooglePayConfiguration.ButtonType.Plain,
  )

internal fun buildGooglePayConfig(params: ReadableMap?): PaymentSheet.GooglePayConfiguration? {
  if (params == null || params.isEmpty()) {
    return null
  }

  val countryCode = params.getString("merchantCountryCode").orEmpty()
  val currencyCode = params.getString("currencyCode").orEmpty()
  val testEnv = params.getBoolean("testEnv")
  val amount = params.getString("amount")?.toLongOrNull()
  val label = params.getString("label")
  val buttonType =
    mapIntToButtonType[params.getIntOr("buttonType", 0)]
      ?: PaymentSheet.GooglePayConfiguration.ButtonType.Pay

  return PaymentSheet.GooglePayConfiguration(
    environment =
      if (testEnv) {
        PaymentSheet.GooglePayConfiguration.Environment.Test
      } else {
        PaymentSheet.GooglePayConfiguration.Environment.Production
      },
    countryCode = countryCode,
    currencyCode = currencyCode,
    amount = amount,
    label = label,
    buttonType = buttonType,
  )
}

@OptIn(ExperimentalCustomerSessionApi::class)
@Throws(PaymentSheetException::class)
internal fun buildCustomerConfiguration(map: ReadableMap?): PaymentSheet.CustomerConfiguration? {
  val customerId = map?.getString("customerId").orEmpty()
  val customerEphemeralKeySecret = map?.getString("customerEphemeralKeySecret").orEmpty()
  val customerSessionClientSecret = map?.getString("customerSessionClientSecret").orEmpty()
  return if (customerSessionClientSecret.isNotEmpty() &&
    customerEphemeralKeySecret.isNotEmpty()
  ) {
    throw PaymentSheetException(
      "`customerEphemeralKeySecret` and `customerSessionClientSecret` cannot both be set",
    )
  } else if (customerId.isNotEmpty() && customerSessionClientSecret.isNotEmpty()) {
    PaymentSheet.CustomerConfiguration.createWithCustomerSession(
      id = customerId,
      clientSecret = customerSessionClientSecret,
    )
  } else if (customerId.isNotEmpty() && customerEphemeralKeySecret.isNotEmpty()) {
    PaymentSheet.CustomerConfiguration(
      id = customerId,
      ephemeralKeySecret = customerEphemeralKeySecret,
    )
  } else {
    null
  }
}
