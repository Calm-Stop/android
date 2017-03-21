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
    //MONTHS
    @Test
    public void testEmptyDateOfBirth() {
        String testDateOfBirth = "";
        Assert.assertFalse(SignupVerification.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void testOneMonthDateOfBirth() {
        String testDateOfBirth = "1-20-1999";
        Assert.assertTrue(SignupVerification.validDateOfBirth(testDateOfBirth));
    }
//RECHECK
    @Test
    public void testTwoMonthDateOfBirth() {
        String testDateOfBirth = "10-20-1999";
        Assert.assertTrue(SignupVerification.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void testThreeMonthDateOfBirth() {
        String testDateOfBirth = "100-20-1999";
        Assert.assertFalse(SignupVerification.validDateOfBirth(testDateOfBirth));
    }

    //DAYS
    @Test
    public void testOneDayDateOfBirth() {
        String testDateOfBirth = "1-1-1999";
        Assert.assertTrue(SignupVerification.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void testTwoDayDateOfBirth() {
        String testDateOfBirth = "1-20-1999";
        Assert.assertTrue(SignupVerification.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void testThreeDayDateOfBirth() {
        String testDateOfBirth = "1-200-1999";
        Assert.assertFalse(SignupVerification.validDateOfBirth(testDateOfBirth));
    }

    //YEARS
//RECHECK
    @Test
    public void testTwoThousandOneDateOfBirth() {
        String testDateOfBirth = "1-20-2001";
        Assert.assertTrue(SignupVerification.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void testMoreThanTwoThousandOneDateOfBirth() {
        String testDateOfBirth = "1-20-2002";
        Assert.assertFalse(SignupVerification.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void testLessThanNineteenTenDateOfBirth() {
        String testDateOfBirth = "1-20-1909";
        Assert.assertFalse(SignupVerification.validDateOfBirth(testDateOfBirth));
    }
//RECHECK
    @Test
    public void testNineteenThirtyDateOfBirth() {
        String testDateOfBirth = "10-10-1930";
        Assert.assertTrue(SignupVerification.validDateOfBirth(testDateOfBirth));
    }

    //LEAP YEAR
//RECHECK
    @Test
    public void testLeapYearDateOfBirth() {
        String testDateOfBirth = "2-29-1996";
        Assert.assertTrue(SignupVerification.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void testNotLeapYearDateOfBirth() {
        String testDateOfBirth = "2-29-1995";
        Assert.assertFalse(SignupVerification.validDateOfBirth(testDateOfBirth));
    }



} // end SignupVerificationTest
