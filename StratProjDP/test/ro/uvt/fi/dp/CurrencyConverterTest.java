package ro.uvt.fi.dp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ro.uvt.fi.dp.account.Currency;
import ro.uvt.fi.dp.account.CurrencyConverter;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CurrencyConverter Tests")
class CurrencyConverterTest {

    // Tests for same currency conversions (should return the same amount)

    @Test
    @DisplayName("convert RON to RON returns the same amount")
    void convert_ronToRon_unchanged() {
        assertEquals(100.0, CurrencyConverter.convert(100.0, Currency.RON, Currency.RON), 1e-9);
    }

    @Test
    @DisplayName("convert EUR to EUR returns the same amount")
    void convert_eurToEur_unchanged() {
        assertEquals(50.0, CurrencyConverter.convert(50.0, Currency.EUR, Currency.EUR), 1e-9);
    }

    // Tests for EUR to RON conversions

    @Test
    @DisplayName("convert 1 EUR to RON equals EUR_TO_RON rate (5.0)")
    void convert_eurToRon_oneUnit() {
        assertEquals(CurrencyConverter.EUR_TO_RON,
                CurrencyConverter.convert(1.0, Currency.EUR, Currency.RON), 1e-9);
    }

    @Test
    @DisplayName("convert 100 EUR to RON = 500 RON")
    void convert_eurToRon_100() {
        assertEquals(500.0, CurrencyConverter.convert(100.0, Currency.EUR, Currency.RON), 1e-9);
    }

    @Test
    @DisplayName("convert 0.5 EUR to RON = 2.5 RON")
    void convert_eurToRon_fraction() {
        assertEquals(2.5, CurrencyConverter.convert(0.5, Currency.EUR, Currency.RON), 1e-9);
    }

    // Tests for RON to EUR conversions
    @Test
    @DisplayName("convert 1 RON to EUR equals RON_TO_EUR rate (0.2)")
    void convert_ronToEur_oneUnit() {
        assertEquals(CurrencyConverter.RON_TO_EUR,
                CurrencyConverter.convert(1.0, Currency.RON, Currency.EUR), 1e-9);
    }

    @Test
    @DisplayName("convert 500 RON to EUR = 100 EUR")
    void convert_ronToEur_500() {
        assertEquals(100.0, CurrencyConverter.convert(500.0, Currency.RON, Currency.EUR), 1e-9);
    }

    @Test
    @DisplayName("convert 10 RON to EUR = 2 EUR")
    void convert_ronToEur_10() {
        assertEquals(2.0, CurrencyConverter.convert(10.0, Currency.RON, Currency.EUR), 1e-9);
    }

}
