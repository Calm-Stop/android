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

    @Test
    public void testEmptyPassword() {
        String testPassword = "";
        Assert.assertFalse(SignupVerification.validPassword(testPassword));
    }

    @Test
    public void testEmptyLicense() {
        String testLicense = "";
        Assert.assertFalse(SignupVerification.validLicense(testLicense));
    }

    @Test
    public void testEmptyAddress() {
        String testAddress = "";
        Assert.assertFalse(SignupVerification.validAddress(testAddress));
    }

    @Test
    public void testEmptyPhone() {
        String testPhone = "";
        Assert.assertFalse(SignupVerification.validPhone(testPhone));
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

    @Test
    public void testEmptyDateOfBirth() {
        String testDateOfBirth = "";
        Assert.assertFalse(SignupVerification.validDateOfBirth(testDateOfBirth));
    }



} // end SignupVerificationTest
