import java.util.Scanner;

// This is where the app starts - it runs registration, login, then the chat menu
public class Main {

    public static void main(String[] args) {

        // One scanner for the whole app - if you make two they fight over the input and it crashes
        Scanner scanner = new Scanner(System.in);
        Login login = new Login(scanner);

        // PART 1 - register the user first
        System.out.println(login.registerUser());

        // PART 1 - now ask them to log in, keep trying until they get it right
        boolean loggedIn = false;
        while (!loggedIn) {
            System.out.print("Enter username to login: ");
            String enteredUsername = scanner.nextLine();
            System.out.print("Enter password to login: ");
            String enteredPassword = scanner.nextLine();

            // print welcome or error message
            System.out.println(login.returnLoginStatus(enteredUsername, enteredPassword));

            // update loggedIn - if true the while loop stops
            loggedIn = login.loginUser(enteredUsername, enteredPassword);

            if (!loggedIn) {
                System.out.println("Try again.\n");
            }
        }

        // PART 2 - ask how many messages they want to send
        System.out.print("\nHow many messages do you want to send? ");
        int numMessages = Integer.parseInt(scanner.nextLine().trim());

        // open the chat menu - only gets here if they logged in successfully
        startChat(login.username, numMessages, scanner);

        scanner.close();
    }

    // Shows the main menu and handles whatever the user picks
    // YOUR startChat() FROM TEXT FILE - completed and connected up
    public static void startChat(String sender, int numMessages, Scanner scanner) {

        // keeps showing the menu until the user picks quit
        while (true) {
            System.out.println("\nWelcome to Quick Chat");
            System.out.println("Select transaction:");
            System.out.println("Option 1 - Select Quickchat");
            System.out.println("Option 2 - Send Quickchat");
            System.out.println("Option 3 - Quit");

            System.out.print("Enter your choice (1, 2, or 3): ");
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {

                case 1:
                    // not built yet
                    System.out.println("You selected: Select Quickchat");
                    System.out.println("This feature is coming soon, please stay tuned");
                    break;

                case 2:
                    System.out.println("You selected: Send Quickchat");

                    // runs once for each message the user asked to send
                    for (int i = 0; i < numMessages; i++) {
                        System.out.println("\n--- Message " + (i + 1) + " of " + numMessages + " ---");

                        // YOUR do-while from text file - keeps asking until the number is valid
                        String recipient;
                        do {
                            System.out.print("Enter your number (must start with +27 and be exactly 12 characters): ");
                            recipient = scanner.nextLine();
                        } while (!(recipient.startsWith("+27") && recipient.length() == 12));

                        // get the message text
                        System.out.print("Enter your Quickchat (must be 250 characters or less): ");
                        String message = scanner.nextLine();

                        // tell the user if the message is ok or too long
                        System.out.println(Message.checkMessageLengthForDisplay(message));

                        // if too long, don't count this attempt and go back to the top of the loop
                        if (message.length() > 250) {
                            i--; // undo the loop count so they still get their full number of messages
                            continue;
                        }

                        // create the message - this auto generates the ID and hash
                        Message msg = new Message(sender, recipient, message);
                        System.out.println("Message Hash: " + msg.getMessageHash());

                        // YOUR sub-menu from text file - ask what to do with the message
                        System.out.println("Choose an option:");
                        System.out.println("Option 1 - Send Quickchat");
                        System.out.println("Option 2 - Disregard Quickchat");
                        System.out.println("Option 3 - Store Quickchat to send later");

                        int subChoice = Integer.parseInt(scanner.nextLine().trim());

                        // handle the choice and print the result
                        System.out.println(msg.sentMessage(subChoice));

                        // show the full message details in the order the POE asks for
                        System.out.println("\nMessage ID   : " + msg.getMessageId());
                        System.out.println("Message Hash : " + msg.getMessageHash());
                        System.out.println("Recipient    : " + msg.getRecipient());
                        System.out.println("Message      : " + msg.getMessageText());
                    }

                    // show the total after all messages are done
                    System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
                    break;

                case 3:
                    // user wants to quit
                    System.out.println("Goodbye!");
                    return;

                default:
                    System.out.println("Invalid option, please enter 1, 2 or 3.");
            }
        }
    }
}