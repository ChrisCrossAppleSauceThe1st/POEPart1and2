import java.util.Scanner;

// This is where the app starts - it runs registration, login, then the chat menu
public class Main {

    public static void main(String[] args) {

        // One scanner for the whole app - two scanners fight over System.in and crash
        Scanner scanner = new Scanner(System.in);
        Login login = new Login(scanner);

        // PART 3 - load any previously stored messages from the JSON file on startup
        // JSON reading reference: https://www.geeksforgeeks.org/parse-json-java/
        Message.loadStoredMessagesFromJson();

        // PART 1 - register the user first
        System.out.println(login.registerUser());

        // PART 1 - keep asking them to log in until they get it right
        boolean loggedIn = false;
        while (!loggedIn) {
            System.out.print("Enter username to login: ");
            String enteredUsername = scanner.nextLine();
            System.out.print("Enter password to login: ");
            String enteredPassword = scanner.nextLine();

            System.out.println(login.returnLoginStatus(enteredUsername, enteredPassword));

            loggedIn = login.loginUser(enteredUsername, enteredPassword);

            if (!loggedIn) {
                System.out.println("Try again.\n");
            }
        }

        // PART 2 - ask how many messages they want to send
        System.out.print("\nHow many messages do you want to send? ");
        int numMessages = Integer.parseInt(scanner.nextLine().trim());

        // Open the chat menu - only gets here after a successful login
        startChat(login.username, numMessages, scanner);

        scanner.close();
    }

    // -------------------------------------------------------------------------
    // Shows the main menu and handles whatever the user picks
    // PART 3 adds Option 4 - Stored Messages menu
    // -------------------------------------------------------------------------
    public static void startChat(String sender, int numMessages, Scanner scanner) {

        while (true) {
            System.out.println("\nWelcome to Quick Chat");
            System.out.println("Select transaction:");
            System.out.println("Option 1 - Select Quickchat");
            System.out.println("Option 2 - Send Quickchat");
            System.out.println("Option 3 - Quit");
            System.out.println("Option 4 - Stored Messages");  // PART 3

            System.out.print("Enter your choice (1, 2, 3 or 4): ");
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {

                case 1:
                    // placeholder - coming in a future release
                    System.out.println("You selected: Select Quickchat");
                    System.out.println("This feature is coming soon, please stay tuned");
                    break;

                case 2:
                    System.out.println("You selected: Send Quickchat");

                    for (int i = 0; i < numMessages; i++) {
                        System.out.println("\n--- Message " + (i + 1) + " of " + numMessages + " ---");

                        // keep asking until the recipient number is a valid +27 SA number
                        String recipient;
                        do {
                            System.out.print("Enter your number (must start with +27 and be exactly 12 characters): ");
                            recipient = scanner.nextLine();
                        } while (!(recipient.startsWith("+27") && recipient.length() == 12));

                        System.out.print("Enter your Quickchat (must be 250 characters or less): ");
                        String message = scanner.nextLine();

                        System.out.println(Message.checkMessageLengthForDisplay(message));

                        if (message.length() > 250) {
                            i--; // don't count this attempt
                            continue;
                        }

                        Message msg = new Message(sender, recipient, message);
                        System.out.println("Message Hash: " + msg.getMessageHash());

                        System.out.println("Choose an option:");
                        System.out.println("Option 1 - Send Quickchat");
                        System.out.println("Option 2 - Disregard Quickchat");
                        System.out.println("Option 3 - Store Quickchat to send later");

                        int subChoice = Integer.parseInt(scanner.nextLine().trim());

                        System.out.println(msg.sentMessage(subChoice));

                        // display full message details in the order the POE requires
                        System.out.println("\nMessage ID   : " + msg.getMessageId());
                        System.out.println("Message Hash : " + msg.getMessageHash());
                        System.out.println("Recipient    : " + msg.getRecipient());
                        System.out.println("Message      : " + msg.getMessageText());
                    }

                    System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
                    break;

                case 3:
                    System.out.println("Goodbye!");
                    return;

                // PART 3 - new stored-messages menu
                case 4:
                    showStoredMessagesMenu(scanner);
                    break;

                default:
                    System.out.println("Invalid option, please enter 1, 2, 3 or 4.");
            }
        }
    }

    // -------------------------------------------------------------------------
    // PART 3 - Stored Messages sub-menu (a-f from the POE)
    // -------------------------------------------------------------------------
    private static void showStoredMessagesMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Stored Messages Menu ---");
            System.out.println("a) Show sender and recipient of all stored messages");
            System.out.println("b) Display the longest message");
            System.out.println("c) Search for a message by ID");
            System.out.println("d) Search all messages for a recipient");
            System.out.println("e) Delete a message by hash");
            System.out.println("f) Display full message report");
            System.out.println("0) Back to main menu");
            System.out.print("Enter option: ");

            String option = scanner.nextLine().trim().toLowerCase();

            switch (option) {
                case "a":
                    System.out.println(Message.displayStoredSenderRecipient());
                    break;

                case "b":
                    System.out.println("Longest message:\n" + Message.getLongestMessage());
                    break;

                case "c":
                    System.out.print("Enter message ID to search: ");
                    String searchId = scanner.nextLine().trim();
                    System.out.println(Message.searchByMessageId(searchId));
                    break;

                case "d":
                    System.out.print("Enter recipient number to search: ");
                    String recipientSearch = scanner.nextLine().trim();
                    System.out.println(Message.searchByRecipient(recipientSearch));
                    break;

                case "e":
                    System.out.print("Enter message hash to delete: ");
                    String hash = scanner.nextLine().trim();
                    System.out.println(Message.deleteMessageByHash(hash));
                    break;

                case "f":
                    System.out.println(Message.displayReport());
                    break;

                case "0":
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Please enter a, b, c, d, e, f, or 0.");
            }
        }
    }
}
