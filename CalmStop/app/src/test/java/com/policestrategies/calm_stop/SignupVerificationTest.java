package com.policestrategies.calm_stop;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Unit test for {@link SignupVerification}
 * @author Talal Abou Haiba
 */
public class SignupVerificationTest {

    @Test
    public void testEmptyEmail() {
        String testEmail = "";
        Assert.assertFalse(SignupVerification.validEmail(testEmail));
    }
//Password Tests
    @Test
    public void testEmptyPassword() {
        String testPassword = "";
        Assert.assertFalse(SignupVerification.validPassword(testPassword));
    }

    @Test
    public void testNumericPassword() {
        String testPassword = "123456";
        Assert.assertFalse(SignupVerification.validPassword(testPassword));
    }

    @Test
    public void testSmallPassword() {
        String testPassword = "12345";
        Assert.assertFalse(SignupVerification.validPassword(testPassword));
    }

    @Test
    public void testAlphaPassword() {
        String testPassword = "ABCDEFG";
        Assert.assertFalse(SignupVerification.validPassword(testPassword));
    }

    @Test
    public void testValidPassword() {
        String testPassword = "A12345";
        Assert.assertTrue(SignupVerification.validPassword(testPassword));
    }
//License Tests
    @Test
    public void testValidLicense() {
        String testLicense = "A12345678";
        Assert.assertTrue(SignupVerification.validLicense(testLicense));
    }

    @Test
    public void testFiveCharPassword() {
        String testPassword = "12345";
        Assert.assertFalse(SignupVerification.validPassword(testPassword));
    }

    @Test
    public void testEmptyLicense() {
        String testLicense = "";
        Assert.assertFalse(SignupVerification.validLicense(testLicense));
    }

    @Test
    public void testNumericLicense() {
        String testLicense = "123456789";
        Assert.assertFalse(SignupVerification.validLicense(testLicense));
    }

    @Test
    public void testAlphaLicense() {
        String testLicense = "ABCDEFGHI";
        Assert.assertFalse(SignupVerification.validLicense(testLicense));
    }

    @Test
    public void testMultiAlphaLicense() {
        String testLicense = "AB1234567";
        Assert.assertFalse(SignupVerification.validLicense(testLicense));
    }

    @Test
    public void testSmallLicense() {
        String testLicense = "A1234567";
        Assert.assertFalse(SignupVerification.validLicense(testLicense));
    }
//Address Tests
    @Test
    public void testEmptyAddress() {
        String testAddress = "";
        Assert.assertFalse(SignupVerification.validAddress(testAddress));
    }

//Phone Tests
    @Test
    public void testEmptyPhone() {
        String testPhone = "";
        Assert.assertFalse(SignupVerification.validPhone(testPhone));
    }

    @Test
    public void testParensPhone() {
        String testPhone = "(800)-666-6666";
        Assert.assertTrue(SignupVerification.validPhone(testPhone));
    }

    @Test
    public void testNoParensPhone() {
        String testPhone = "800-666-6666";
        Assert.assertTrue(SignupVerification.validPhone(testPhone));
    }

    @Test
    public void testNoAreaCodePhone() {
        String testPhone = "6666666";
        Assert.assertFalse(SignupVerification.validPhone(testPhone));
    }

    @Test
    public void testFullInputPhone() {
        String testPhone = "1-(408)-666-6666";
        Assert.assertTrue(SignupVerification.validPhone(testPhone));
    }

    @Test
    public void testEmptyFirstName() {
        String testFirstName = "";
        Assert.assertFalse(SignupVerification.validFirstName(testFirstName));
    }

    @Test
    public void testEmptyLastName() {
        String testLastName = "";
        Assert.assertFalse(SignupVerification.validLastName(testLastName));
    }

//DOB Test
    @Test
    public void testEmptyDateOfBirth() {
        String testDateOfBirth = "";
        Assert.assertFalse(SignupVerification.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void zeroDateOfBirth() {
        String testDateOfBirth = "04-02-1995";
        Assert.assertFalse(SignupVerification.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void testNotLeapYearDateOfBirth() {
        String testDateOfBirth = "29-02-1995";
        Assert.assertFalse(SignupVerification.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void testLeapYearDateOfBirth() {
        String testDateOfBirth = "29-02-1996";
        Assert.assertTrue(SignupVerification.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void testMinimalDateOfBirth() {
        String testDateOfBirth = "1-2-1999";
        Assert.assertTrue(SignupVerification.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void testLongDateOfBirth() {
        String testDateOfBirth = "100-200-19999";
        Assert.assertFalse(SignupVerification.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void testShortDateOfBirth() {
        String testDateOfBirth = "1-2-199";
        Assert.assertFalse(SignupVerification.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void testZeroDateOfBirth() {
        String testDateOfBirth = "00-00-0000";
        Assert.assertFalse(SignupVerification.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void testJanDateOfBirth() {
        String testDateOfBirth = "31/1/1999";
        Assert.assertFalse(SignupVerification.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void testNineDateOfBirth() {
        String testDateOfBirth = "99/99/9999";
        Assert.assertFalse(SignupVerification.validDateOfBirth(testDateOfBirth));
    }

} // end SignupVerificationTest
