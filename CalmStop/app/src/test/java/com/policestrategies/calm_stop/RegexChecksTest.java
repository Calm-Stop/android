package com.policestrategies.calm_stop;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Unit test for {@link RegexChecks}
 * @author Talal Abou Haiba
 */
public class RegexChecksTest {

    @Test
    public void testEmptyEmail() {
        String testEmail = "";
        Assert.assertFalse(RegexChecks.validEmail(testEmail));
    }
//Password Tests
    @Test
    public void testEmptyPassword() {
        String testPassword = "";
        Assert.assertFalse(RegexChecks.validPassword(testPassword));
    }

    @Test
    public void testNumericPassword() {
        String testPassword = "123456";
        Assert.assertFalse(RegexChecks.validPassword(testPassword));
    }

    @Test
    public void testSmallPassword() {
        String testPassword = "12345";
        Assert.assertFalse(RegexChecks.validPassword(testPassword));
    }

    @Test
    public void testAlphaPassword() {
        String testPassword = "ABCDEFG";
        Assert.assertFalse(RegexChecks.validPassword(testPassword));
    }

    @Test
    public void testValidPassword() {
        String testPassword = "A12345";
        Assert.assertTrue(RegexChecks.validPassword(testPassword));
    }
//License Tests
    @Test
    public void testValidLicense() {
        String testLicense = "A12345678";
        Assert.assertTrue(RegexChecks.validLicense(testLicense));
    }

    @Test
    public void testFiveCharPassword() {
        String testPassword = "12345";
        Assert.assertFalse(RegexChecks.validPassword(testPassword));
    }

    @Test
    public void testEmptyLicense() {
        String testLicense = "";
        Assert.assertFalse(RegexChecks.validLicense(testLicense));
    }

    @Test
    public void testNumericLicense() {
        String testLicense = "123456789";
        Assert.assertFalse(RegexChecks.validLicense(testLicense));
    }

    @Test
    public void testAlphaLicense() {
        String testLicense = "ABCDEFGHI";
        Assert.assertFalse(RegexChecks.validLicense(testLicense));
    }

    @Test
    public void testMultiAlphaLicense() {
        String testLicense = "AB1234567";
        Assert.assertFalse(RegexChecks.validLicense(testLicense));
    }

    @Test
    public void testSmallLicense() {
        String testLicense = "A1234567";
        Assert.assertFalse(RegexChecks.validLicense(testLicense));
    }
//Address Tests
    @Test
    public void testEmptyAddress() {
        String testAddress = "";
        Assert.assertFalse(RegexChecks.validAddress(testAddress));
    }

//Phone Tests
    @Test
    public void testEmptyPhone() {
        String testPhone = "";
        Assert.assertFalse(RegexChecks.validPhone(testPhone));
    }

    @Test
    public void testParensPhone() {
        String testPhone = "(800)-666-6666";
        Assert.assertTrue(RegexChecks.validPhone(testPhone));
    }

    @Test
    public void testNoParensPhone() {
        String testPhone = "800-666-6666";
        Assert.assertTrue(RegexChecks.validPhone(testPhone));
    }

    @Test
    public void testNoAreaCodePhone() {
        String testPhone = "6666666";
        Assert.assertFalse(RegexChecks.validPhone(testPhone));
    }

    @Test
    public void testFullInputPhone() {
        String testPhone = "1-(408)-666-6666";
        Assert.assertTrue(RegexChecks.validPhone(testPhone));
    }

    @Test
    public void testEmptyFirstName() {
        String testFirstName = "";
        Assert.assertFalse(RegexChecks.validFirstName(testFirstName));
    }

    @Test
    public void testEmptyLastName() {
        String testLastName = "";
        Assert.assertFalse(RegexChecks.validLastName(testLastName));
    }

//DOB Test
    //MONTHS
    @Test
    public void testEmptyDateOfBirth() {
        String testDateOfBirth = "";
        Assert.assertFalse(RegexChecks.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void testOneMonthDateOfBirth() {
        String testDateOfBirth = "1-20-1999";
        Assert.assertTrue(RegexChecks.validDateOfBirth(testDateOfBirth));
    }
//RECHECK
    @Test
    public void testTwoMonthDateOfBirth() {
        String testDateOfBirth = "10-20-1999";
        Assert.assertTrue(RegexChecks.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void testThreeMonthDateOfBirth() {
        String testDateOfBirth = "100-20-1999";
        Assert.assertFalse(RegexChecks.validDateOfBirth(testDateOfBirth));
    }

    //DAYS
    @Test
    public void testOneDayDateOfBirth() {
        String testDateOfBirth = "1-1-1999";
        Assert.assertTrue(RegexChecks.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void testTwoDayDateOfBirth() {
        String testDateOfBirth = "1-20-1999";
        Assert.assertTrue(RegexChecks.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void testThreeDayDateOfBirth() {
        String testDateOfBirth = "1-200-1999";
        Assert.assertFalse(RegexChecks.validDateOfBirth(testDateOfBirth));
    }

    //YEARS
//RECHECK
    @Test
    public void testTwoThousandOneDateOfBirth() {
        String testDateOfBirth = "1-20-2001";
        Assert.assertTrue(RegexChecks.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void testMoreThanTwoThousandOneDateOfBirth() {
        String testDateOfBirth = "1-20-2002";
        Assert.assertFalse(RegexChecks.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void testLessThanNineteenTenDateOfBirth() {
        String testDateOfBirth = "1-20-1909";
        Assert.assertFalse(SignupVerification.validDateOfBirth(testDateOfBirth));

    public void testLessThanNineteenThirtyDateOfBirth() {
        String testDateOfBirth = "1-20-1929";
        Assert.assertFalse(RegexChecks.validDateOfBirth(testDateOfBirth));
    }
//RECHECK
    @Test
    public void testNineteenThirtyDateOfBirth() {
        String testDateOfBirth = "10-10-1930";
        Assert.assertTrue(RegexChecks.validDateOfBirth(testDateOfBirth));
    }

    //LEAP YEAR
//RECHECK
    @Test
    public void testLeapYearDateOfBirth() {
        String testDateOfBirth = "2-29-1996";
        Assert.assertTrue(RegexChecks.validDateOfBirth(testDateOfBirth));
    }

    @Test
    public void testNotLeapYearDateOfBirth() {
        String testDateOfBirth = "2-29-1995";
        Assert.assertFalse(RegexChecks.validDateOfBirth(testDateOfBirth));
    }



} // end RegexChecksTest
