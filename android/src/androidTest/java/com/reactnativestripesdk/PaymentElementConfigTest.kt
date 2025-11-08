package com.reactnativestripesdk

import androidx.test.core.app.ApplicationProvider
import com.facebook.react.soloader.OpenSourceMergedSoMapping
import com.facebook.soloader.SoLoader
import com.reactnativestripesdk.utils.PaymentSheetException
import com.reactnativestripesdk.utils.readableArrayOf
import com.reactnativestripesdk.utils.readableMapOf
import com.stripe.android.paymentsheet.PaymentSheet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class PaymentElementConfigTest {
  @Before
  fun setup() {
    SoLoader.init(ApplicationProvider.getApplicationContext(), OpenSourceMergedSoMapping)
  }

  // ============================================
  // buildIntentConfiguration Tests
  // ============================================

  @Test
  fun buildIntentConfiguration_NullParams_ReturnsNull() {
    val result = buildIntentConfiguration(null)
    assertNull(result)
  }

  @Test(expected = PaymentSheetException::class)
  fun buildIntentConfiguration_MissingMode_ThrowsException() {
    val params = readableMapOf()
    buildIntentConfiguration(params)
  }

  @Test
  fun buildIntentConfiguration_WithPaymentMode_Success() {
    val params = readableMapOf(
      "mode" to readableMapOf(
        "amount" to 1000,
        "currencyCode" to "usd"
      )
    )

    val result = buildIntentConfiguration(params)

    assertNotNull(result)
    assertEquals(1000L, (result?.mode as? PaymentSheet.IntentConfiguration.Mode.Payment)?.amount)
    assertEquals("usd", (result?.mode as? PaymentSheet.IntentConfiguration.Mode.Payment)?.currency)
  }

  @Test
  fun buildIntentConfiguration_WithPaymentMethodTypes_Success() {
    val params = readableMapOf(
      "mode" to readableMapOf(
        "amount" to 1000,
        "currencyCode" to "usd"
      ),
      "paymentMethodTypes" to readableArrayOf("card", "klarna")
    )

    val result = buildIntentConfiguration(params)

    assertNotNull(result)
    assertEquals(2, result?.paymentMethodTypes?.size)
    assertEquals("card", result?.paymentMethodTypes?.get(0))
    assertEquals("klarna", result?.paymentMethodTypes?.get(1))
  }

  @Test
  fun buildIntentConfiguration_EmptyPaymentMethodTypes_ReturnsEmptyList() {
    val params = readableMapOf(
      "mode" to readableMapOf(
        "amount" to 1000,
        "currencyCode" to "usd"
      )
    )

    val result = buildIntentConfiguration(params)

    assertNotNull(result)
    assertEquals(0, result?.paymentMethodTypes?.size)
  }

  // ============================================
  // buildIntentConfigurationMode Tests
  // ============================================

  @Test
  fun buildIntentConfigurationMode_PaymentMode_MinimalParams() {
    val params = readableMapOf(
      "mode" to readableMapOf(
        "amount" to 5000,
        "currencyCode" to "eur"
      )
    )

    val result = buildIntentConfiguration(params)

    val mode = result?.mode as? PaymentSheet.IntentConfiguration.Mode.Payment
    assertNotNull(mode)
    assertEquals(5000L, mode?.amount)
    assertEquals("eur", mode?.currency)
  }

  @Test(expected = PaymentSheetException::class)
  fun buildIntentConfigurationMode_PaymentMode_MissingCurrency_ThrowsException() {
    val params = readableMapOf(
      "mode" to readableMapOf(
        "amount" to 5000
      )
    )

    buildIntentConfiguration(params)
  }

  @Test
  fun buildIntentConfigurationMode_PaymentMode_WithSetupFutureUsage() {
    val params = readableMapOf(
      "mode" to readableMapOf(
        "amount" to 5000,
        "currencyCode" to "usd",
        "setupFutureUsage" to "OffSession"
      )
    )

    val result = buildIntentConfiguration(params)

    assertNotNull(result?.mode as? PaymentSheet.IntentConfiguration.Mode.Payment)
  }

  @Test
  fun buildIntentConfigurationMode_PaymentMode_WithCaptureMethod() {
    val params = readableMapOf(
      "mode" to readableMapOf(
        "amount" to 5000,
        "currencyCode" to "usd",
        "captureMethod" to "Manual"
      )
    )

    val result = buildIntentConfiguration(params)

    assertNotNull(result?.mode)
  }

  @Test
  fun buildIntentConfigurationMode_PaymentMode_WithPaymentMethodOptions() {
    val params = readableMapOf(
      "mode" to readableMapOf(
        "amount" to 5000,
        "currencyCode" to "usd",
        "paymentMethodOptions" to readableMapOf(
          "setupFutureUsageValues" to readableMapOf(
            "card" to "OffSession",
            "us_bank_account" to "OnSession"
          )
        )
      )
    )

    val result = buildIntentConfiguration(params)

    assertNotNull(result?.mode)
  }

  @Test
  fun buildIntentConfigurationMode_SetupMode_Success() {
    val params = readableMapOf(
      "mode" to readableMapOf(
        "setupFutureUsage" to "OffSession"
      )
    )

    val result = buildIntentConfiguration(params)

    assertNotNull(result?.mode)
  }

  @Test
  fun buildIntentConfigurationMode_SetupMode_WithCurrency() {
    val params = readableMapOf(
      "mode" to readableMapOf(
        "setupFutureUsage" to "OnSession",
        "currencyCode" to "gbp"
      )
    )

    val result = buildIntentConfiguration(params)

    assertNotNull(result?.mode)
  }

  @Test(expected = PaymentSheetException::class)
  fun buildIntentConfigurationMode_SetupMode_MissingSetupFutureUsage_ThrowsException() {
    val params = readableMapOf(
      "mode" to readableMapOf()
    )

    buildIntentConfiguration(params)
  }

  // ============================================
  // buildLinkConfig Tests
  // ============================================

  @Test
  fun buildLinkConfig_NullParams_ReturnsDefaultConfig() {
    val result = buildLinkConfig(null)

    assertNotNull(result)
  }

  @Test
  fun buildLinkConfig_EmptyParams_ReturnsDefaultConfig() {
    val params = readableMapOf()
    val result = buildLinkConfig(params)

    assertNotNull(result)
  }

  @Test
  fun buildLinkConfig_DisplayAutomatic() {
    val params = readableMapOf(
      "display" to "automatic"
    )
    val result = buildLinkConfig(params)

    assertNotNull(result)
  }

  @Test
  fun buildLinkConfig_DisplayNever() {
    val params = readableMapOf(
      "display" to "never"
    )
    val result = buildLinkConfig(params)

    assertNotNull(result)
  }

  @Test
  fun buildLinkConfig_InvalidDisplay_DefaultsToAutomatic() {
    val params = readableMapOf(
      "display" to "invalid_value"
    )
    val result = buildLinkConfig(params)

    assertNotNull(result)
  }

  @Test
  fun buildLinkConfig_NullDisplay_DefaultsToAutomatic() {
    val params = readableMapOf()
    val result = buildLinkConfig(params)

    assertNotNull(result)
  }

  // ============================================
  // buildGooglePayConfig Tests
  // ============================================

  @Test
  fun buildGooglePayConfig_NullParams_ReturnsNull() {
    val result = buildGooglePayConfig(null)
    assertNull(result)
  }

  @Test
  fun buildGooglePayConfig_EmptyParams_ReturnsNull() {
    val params = readableMapOf()
    val result = buildGooglePayConfig(params)
    assertNull(result)
  }

  @Test
  fun buildGooglePayConfig_MinimalParams_TestEnvironment() {
    val params = readableMapOf(
      "merchantCountryCode" to "US",
      "currencyCode" to "usd",
      "testEnv" to true
    )

    val result = buildGooglePayConfig(params)

    assertNotNull(result)
    assertEquals("US", result?.countryCode)
    assertEquals("usd", result?.currencyCode)
    assertEquals(PaymentSheet.GooglePayConfiguration.Environment.Test, result?.environment)
    assertNull(result?.amount)
    assertNull(result?.label)
    assertEquals(PaymentSheet.GooglePayConfiguration.ButtonType.Pay, result?.buttonType)
  }

  @Test
  fun buildGooglePayConfig_ProductionEnvironment() {
    val params = readableMapOf(
      "merchantCountryCode" to "CA",
      "currencyCode" to "cad",
      "testEnv" to false
    )

    val result = buildGooglePayConfig(params)

    assertNotNull(result)
    assertEquals(PaymentSheet.GooglePayConfiguration.Environment.Production, result?.environment)
  }

  @Test
  fun buildGooglePayConfig_WithAmount() {
    val params = readableMapOf(
      "merchantCountryCode" to "US",
      "currencyCode" to "usd",
      "testEnv" to true,
      "amount" to "2500"
    )

    val result = buildGooglePayConfig(params)

    assertNotNull(result)
    assertEquals(2500L, result?.amount)
  }

  @Test
  fun buildGooglePayConfig_WithInvalidAmount() {
    val params = readableMapOf(
      "merchantCountryCode" to "US",
      "currencyCode" to "usd",
      "testEnv" to true,
      "amount" to "not_a_number"
    )

    val result = buildGooglePayConfig(params)

    assertNotNull(result)
    assertNull(result?.amount)
  }

  @Test
  fun buildGooglePayConfig_WithLabel() {
    val params = readableMapOf(
      "merchantCountryCode" to "US",
      "currencyCode" to "usd",
      "testEnv" to true,
      "label" to "Total"
    )

    val result = buildGooglePayConfig(params)

    assertNotNull(result)
    assertEquals("Total", result?.label)
  }

  @Test
  fun buildGooglePayConfig_ButtonTypes() {
    val testCases = listOf(
      1 to PaymentSheet.GooglePayConfiguration.ButtonType.Buy,
      6 to PaymentSheet.GooglePayConfiguration.ButtonType.Book,
      5 to PaymentSheet.GooglePayConfiguration.ButtonType.Checkout,
      4 to PaymentSheet.GooglePayConfiguration.ButtonType.Donate,
      11 to PaymentSheet.GooglePayConfiguration.ButtonType.Order,
      1000 to PaymentSheet.GooglePayConfiguration.ButtonType.Pay,
      7 to PaymentSheet.GooglePayConfiguration.ButtonType.Subscribe,
      1001 to PaymentSheet.GooglePayConfiguration.ButtonType.Plain,
      9999 to PaymentSheet.GooglePayConfiguration.ButtonType.Pay // Invalid defaults to Pay
    )

    for ((buttonTypeValue, expectedButtonType) in testCases) {
      val params = readableMapOf(
        "merchantCountryCode" to "US",
        "currencyCode" to "usd",
        "testEnv" to true,
        "buttonType" to buttonTypeValue
      )

      val result = buildGooglePayConfig(params)

      assertEquals(expectedButtonType, result?.buttonType)
    }
  }

  @Test
  fun buildGooglePayConfig_CompleteConfig() {
    val params = readableMapOf(
      "merchantCountryCode" to "GB",
      "currencyCode" to "gbp",
      "testEnv" to false,
      "amount" to "10000",
      "label" to "Order Total",
      "buttonType" to 5
    )

    val result = buildGooglePayConfig(params)

    assertNotNull(result)
    assertEquals("GB", result?.countryCode)
    assertEquals("gbp", result?.currencyCode)
    assertEquals(PaymentSheet.GooglePayConfiguration.Environment.Production, result?.environment)
    assertEquals(10000L, result?.amount)
    assertEquals("Order Total", result?.label)
    assertEquals(PaymentSheet.GooglePayConfiguration.ButtonType.Checkout, result?.buttonType)
  }

  // ============================================
  // buildCustomerConfiguration Tests
  // ============================================

  @Test
  fun buildCustomerConfiguration_NullParams_ReturnsNull() {
    val result = buildCustomerConfiguration(null)
    assertNull(result)
  }

  @Test
  fun buildCustomerConfiguration_EmptyParams_ReturnsNull() {
    val params = readableMapOf()
    val result = buildCustomerConfiguration(params)
    assertNull(result)
  }

  @Test
  fun buildCustomerConfiguration_OnlyCustomerId_ReturnsNull() {
    val params = readableMapOf(
      "customerId" to "cus_123"
    )

    val result = buildCustomerConfiguration(params)
    assertNull(result)
  }

  @Test
  fun buildCustomerConfiguration_WithEphemeralKey_Success() {
    val params = readableMapOf(
      "customerId" to "cus_123",
      "customerEphemeralKeySecret" to "ek_test_123"
    )

    val result = buildCustomerConfiguration(params)

    assertNotNull(result)
    assertEquals("cus_123", result?.id)
    assertEquals("ek_test_123", result?.ephemeralKeySecret)
  }

  @Test
  fun buildCustomerConfiguration_WithCustomerSession_Success() {
    val params = readableMapOf(
      "customerId" to "cus_456",
      "customerSessionClientSecret" to "cuss_test_456"
    )

    val result = buildCustomerConfiguration(params)

    assertNotNull(result)
    assertEquals("cus_456", result?.id)
  }

  @Test(expected = PaymentSheetException::class)
  fun buildCustomerConfiguration_BothSecretsProvided_ThrowsException() {
    val params = readableMapOf(
      "customerId" to "cus_789",
      "customerEphemeralKeySecret" to "ek_test_789",
      "customerSessionClientSecret" to "cuss_test_789"
    )

    buildCustomerConfiguration(params)
  }

  @Test
  fun buildCustomerConfiguration_OnlyEphemeralKey_NoCustomerId_ReturnsNull() {
    val params = readableMapOf(
      "customerEphemeralKeySecret" to "ek_test_123"
    )

    val result = buildCustomerConfiguration(params)
    assertNull(result)
  }

  @Test
  fun buildCustomerConfiguration_OnlyCustomerSession_NoCustomerId_ReturnsNull() {
    val params = readableMapOf(
      "customerSessionClientSecret" to "cuss_test_456"
    )

    val result = buildCustomerConfiguration(params)
    assertNull(result)
  }
}
