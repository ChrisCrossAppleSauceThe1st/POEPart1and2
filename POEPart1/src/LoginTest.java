import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.Scanner;

// Tests for the Login class
public class LoginTest {

    private Login login;

    // This runs before every test to give us a fresh Login object with known values
    @Before
    public void setUp() {
        login = new Login(new Scanner(System.in));
        login.username    = "kyl_1";
        login.password    = "Ch&&sec@ke99!";
        login.PhoneNumber = "+27838968976";
        login.firstName   = "Kyle";
        login.lastName    = "Smith";
    }

    // --- USERNAME TESTS ---

    // kyl_1 is valid so this should be true
    @Test
    public void testCheckUserName_correctly_formatted_assertTrue() {
        assertTrue(login.checkUserName("kyl_1"));
    }

    // kyle!!!!!!! has no underscore and is too long so this should be false
    @Test
    public void testCheckUserName_incorrectly_formatted_assertFalse() {
        assertFalse(login.checkUserName("kyle!!!!!!!"));
    }

    // valid username should give us the success message
    @Test
    public void testCheckUserName_correctly_formatted_assertEquals() {
        String result = login.checkUserName("kyl_1")
                ? "Username successfully captured."
                : "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        assertEquals("Username successfully captured.", result);
    }

    // invalid username should give us the error message
    @Test
    public void testCheckUserName_incorrectly_formatted_assertEquals() {
        String result = login.checkUserName("kyle!!!!!!!")
                ? "Username successfully captured."
                : "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        assertEquals(
                "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.",
                result);
    }

    // --- PASSWORD TESTS ---

    // Ch&&sec@ke99! has a capital, number and special character so should be true
    @Test
    public void testCheckPasswordComplexity_meets_requirements_assertTrue() {
        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    // "password" has none of the required complexity so should be false
    @Test
    public void testCheckPasswordComplexity_does_not_meet_requirements_assertFalse() {
        assertFalse(login.checkPasswordComplexity("password"));
    }

    // good password should give us the success message
    @Test
    public void testCheckPasswordComplexity_meets_requirements_assertEquals() {
        String result = login.checkPasswordComplexity("Ch&&sec@ke99!")
                ? "Password successfully captured."
                : "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        assertEquals("Password successfully captured.", result);
    }

    // bad password should give us the error message
    @Test
    public void testCheckPasswordComplexity_does_not_meet_requirements_assertEquals() {
        String result = login.checkPasswordComplexity("password")
                ? "Password successfully captured."
                : "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        assertEquals(
                "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.",
                result);
    }

    // --- CELL PHONE TESTS ---

    // +27838968976 is a valid SA number so should be true
    @Test
    public void testCheckCellPhoneNumber_correctly_formatted_assertTrue() {
        login.PhoneNumber = "+27838968976";
        assertTrue(login.checkCellPhoneNumber());
    }

    // 08966553 has no +27 and is too short so should be false
    @Test
    public void testCheckCellPhoneNumber_incorrectly_formatted_assertFalse() {
        login.PhoneNumber = "08966553";
        assertFalse(login.checkCellPhoneNumber());
    }

    // valid number should give us the success message
    @Test
    public void testCheckCellPhoneNumber_correctly_formatted_assertEquals() {
        login.PhoneNumber = "+27838968976";
        String result = login.checkCellPhoneNumber()
                ? "Cell number successfully captured."
                : "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
        assertEquals("Cell number successfully captured.", result);
    }

    // invalid number should give us the error message
    @Test
    public void testCheckCellPhoneNumber_incorrectly_formatted_assertEquals() {
        login.PhoneNumber = "08966553";
        String result = login.checkCellPhoneNumber()
                ? "Cell number successfully captured."
                : "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
        assertEquals(
                "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.",
                result);
    }

    // --- LOGIN TESTS ---

    // correct username and password should return true
    @Test
    public void testLoginUser_successful_assertTrue() {
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    // wrong username and password should return false
    @Test
    public void testLoginUser_failed_assertFalse() {
        assertFalse(login.loginUser("wronguser", "wrongpass"));
    }

    // successful login should give us the welcome message
    @Test
    public void testReturnLoginStatus_success_assertEquals() {
        assertEquals(
                "Welcome Kyle Smith it is great to see you again.",
                login.returnLoginStatus("kyl_1", "Ch&&sec@ke99!"));
    }

    // failed login should give us the error message
    @Test
    public void testReturnLoginStatus_failure_assertEquals() {
        assertEquals(
                "Username or password incorrect, please try again.",
                login.returnLoginStatus("wronguser", "wrongpass"));
    }
}
