import java.util.Scanner;

// This class deals with registering a new user and logging them in
public class Login {

    // We use one scanner the whole time so we don't break the input
    private Scanner scanner;

    // These store the user's details after they register
    public String username;
    public String password;
    public String PhoneNumber;
    public String firstName;
    public String lastName;

    // This runs when we create a new Login object, we pass the scanner in from Main
    public Login(Scanner scanner) {
        this.scanner = scanner;
    }

    // Checks the username has an underscore and is 5 chars or less
    public boolean checkUserName(String Username) {
        return Username.contains("_") && Username.length() <= 5;
    }

    // Checks the password has at least 8 chars, a capital, a number and a special character
    public boolean checkPasswordComplexity(String Password) {
        return Password.length() >= 8 &&
                Password.matches(".*[A-Z].*") &&
                Password.matches(".*[0-9].*") &&
                Password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
    }

    // Checks the phone number starts with +27 and is exactly 12 characters long
    // Regex reference: South African international number format (+27 followed by 9 digits)
    // Source: https://www.regexlib.com/Search.aspx?k=south+africa+phone
    public boolean checkCellPhoneNumber() {
        return PhoneNumber != null && PhoneNumber.matches("\\+27[0-9]{9}");
    }

    // Checks if what the user typed matches what they registered with
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        return username != null && password != null
                && username.equals(enteredUsername)
                && password.equals(enteredPassword);
    }

    // Walks the user through the full registration process
    public String registerUser() {

        // Get their name
        System.out.println("please enter first name: ");
        firstName = scanner.nextLine();
        System.out.println("please enter last name: ");
        lastName = scanner.nextLine();

        // Keep asking for a username until they give a valid one
        do {
            System.out.println("Please enter your username: ");
            username = scanner.nextLine();
            if (!checkUserName(username)) {
                System.out.println("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.");
            }
        } while (!checkUserName(username));
        System.out.println("Username successfully captured.");

        // Keep asking for a password until they give a valid one
        do {
            System.out.println("please enter your password: ");
            password = scanner.nextLine();
            if (!checkPasswordComplexity(password)) {
                System.out.println("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.");
            }
        } while (!checkPasswordComplexity(password));
        System.out.println("Password successfully captured.");

        // Keep asking for a phone number until they give a valid one
        do {
            System.out.println("Please enter your Phone Number (+27838968967): ");
            PhoneNumber = scanner.nextLine();
            if (!checkCellPhoneNumber()) {
                System.out.println("Cell phone number incorrectly formatted or does not contain international code.");
            }
        } while (!checkCellPhoneNumber());
        System.out.println("Cell phone number successfully added.");

        return "User has been successfully registered";
    }

    // Returns a welcome message if login worked, or an error if it didn't
    public String returnLoginStatus(String enteredUsername, String enteredPassword) {
        if (loginUser(enteredUsername, enteredPassword)) {
            return "Welcome " + firstName + " " + lastName + " it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
}