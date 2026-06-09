package org.example.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilsTest {

    @Test
    public void testValidateEmailNullIsInvalid() {
        assertNotNull(ValidationUtils.validateEmail(null), "Boş e-posta için hata dönmeli.");
    }

    @Test
    public void testValidateEmailBlankIsInvalid() {
        assertNotNull(ValidationUtils.validateEmail("   "), "Sadece boşluk geçersiz olmalı.");
    }

    @Test
    public void testValidateEmailInvalidFormat() {
        assertNotNull(ValidationUtils.validateEmail("gecersiz"), "@ ve alan adı olmadan geçersiz.");
        assertNotNull(ValidationUtils.validateEmail("a@b"), "Tld kısa olunca geçersiz.");
    }

    @Test
    public void testValidateEmailValid() {
        assertNull(ValidationUtils.validateEmail("ad@isikun.com"), "Geçerli e-posta null dönmeli.");
        assertNull(ValidationUtils.validateEmail("user.name+tag@example.co.uk"));
    }

    @Test
    public void testValidateEmailTrimsWhitespace() {
        assertNull(ValidationUtils.validateEmail("  ad@isikun.com  "));
    }

    @Test
    public void testValidatePasswordNullOrEmpty() {
        assertNotNull(ValidationUtils.validatePassword(null));
        assertNotNull(ValidationUtils.validatePassword(""));
    }

    @Test
    public void testValidatePasswordTooShort() {
        String msg = ValidationUtils.validatePassword("Aa1!xxx");
        assertNotNull(msg, "7 karakter şifre geçersiz olmalı.");
        assertTrue(msg.contains("8"), "Mesaj uzunluk kuralını belirtmeli.");
    }

    @Test
    public void testValidatePasswordMissingUppercase() {
        assertNotNull(ValidationUtils.validatePassword("abcdef1!"), "Büyük harf zorunlu.");
    }

    @Test
    public void testValidatePasswordMissingLowercase() {
        assertNotNull(ValidationUtils.validatePassword("ABCDEF1!"), "Küçük harf zorunlu.");
    }

    @Test
    public void testValidatePasswordMissingDigit() {
        assertNotNull(ValidationUtils.validatePassword("Abcdefgh!"), "Rakam zorunlu.");
    }

    @Test
    public void testValidatePasswordMissingSpecial() {
        assertNotNull(ValidationUtils.validatePassword("Abcdefgh1"), "Özel karakter zorunlu.");
    }

    @Test
    public void testValidatePasswordValid() {
        assertNull(ValidationUtils.validatePassword("Valid1!pass"), "Tüm kuralları sağlayan şifre geçerli.");
    }

    @Test
    public void testPasswordHintNotEmpty() {
        assertFalse(ValidationUtils.passwordHint().isBlank());
    }
}
