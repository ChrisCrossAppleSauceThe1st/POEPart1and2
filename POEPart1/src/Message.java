import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

// This class handles everything to do with messages - creating, sending, storing them
// JSON storage reference: https://www.geeksforgeeks.org/how-to-write-json-array-to-a-file-in-java/
public class Message {

    // Every message gets its own copy of these
    private String messageId;
    private int messageNumber;
    private String sender;
    private String recipient;
    private String messageText;
    private String messageHash;
    private String status;

    // These are shared across all messages - static means one copy for the whole class
    private static int messageCount = 0;  // goes up every time a new message is made
    private static int totalSent = 0;     // only goes up when a message is actually sent
    private static ArrayList<Message> allMessages = new ArrayList<>(); // list of all sent/stored messages

    // This runs when we do new Message(...) - sets everything up
    public Message(String sender, String recipient, String messageText) {
        this.sender = sender;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageNumber = messageCount; // grab the current count first (so first message is 0)
        messageCount++;                    // then bump it up for the next message
        this.messageId = generateMessageId();   // make a random 10 digit ID
        this.messageHash = createMessageHash(); // build the hash from the ID and message
    }

    // Makes a random 10 digit number as a string for the message ID
    // YOUR CODE FROM TEXT FILE
    private static String generateMessageId() {
        Random random = new Random();
        String id = "";
        for (int i = 0; i < 10; i++) {
            id += random.nextInt(10); // picks a random number 0-9 and adds it each time
        }
        return id;
    }

    // Builds the message hash like this: 67:0:HITONIGHT
    // First 2 chars of ID : message number : first word + last word in caps
    // YOUR CODE FROM TEXT FILE - just renamed from generateMessageHash
    public String createMessageHash() {
        // split the message into words
        String[] words = messageText.trim().split("\\s+");

        // grab first and last word, remove any punctuation like ? or ,
        String first = words.length > 0 ? words[0].replaceAll("[^a-zA-Z0-9]", "") : "";
        String last  = words.length > 1 ? words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "") : first;

        // put it all together in caps
        return messageId.substring(0, 2) + ":" + messageNumber + ":" + first.toUpperCase() + last.toUpperCase();
    }

    // Returns true if the message ID is 10 characters or less
    public boolean checkMessageID() {
        return messageId != null && messageId.length() <= 10;
    }

    // Checks if the recipient number is a valid +27 number
    public String checkRecipientCell() {
        if (recipient != null && recipient.matches("\\+27[0-9]{9}")) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    // Handles what happens when the user picks send, discard or store
    // YOUR CODE FROM TEXT FILE - the sub menu switch block
    public String sentMessage(int subChoice) {
        switch (subChoice) {
            case 1:
                // send it - mark as sent and add to our list
                status = "Sent";
                totalSent++;
                allMessages.add(this);
                return "Message successfully sent.";
            case 2:
                // throw it away - don't add to the list
                status = "Disregarded";
                return "Press 0 to delete the message.";
            case 3:
                // save it for later - write to file and add to list
                status = "Stored";
                storeMessage();
                allMessages.add(this);
                return "Message successfully stored.";
            default:
                return "Invalid option";
        }
    }

    // Prints out all the messages that were sent or stored
    public static String printMessages() {
        if (allMessages.isEmpty()) {
            return "No messages sent yet.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : allMessages) {
            sb.append("\nMessage ID   : ").append(m.messageId)
                    .append("\nMessage Hash : ").append(m.messageHash)
                    .append("\nRecipient    : ").append(m.recipient)
                    .append("\nMessage      : ").append(m.messageText)
                    .append("\nStatus       : ").append(m.status)
                    .append("\n-------------------------");
        }
        return sb.toString();
    }

    // Returns how many messages were actually sent
    // Note: the POE spec has a typo "returnTotalMessagess" so we include both spellings
    public static int returnTotalMessages() {
        return totalSent;
    }

    // Same method but with the typo spelling from the POE spec
    public static int returnTotalMessagess() {
        return totalSent;
    }

    // This is what shows on screen when the user sends a message - matches POE wording
    public static String checkMessageLengthForDisplay(String text) {
        if (text.length() <= 250) {
            return "Message sent";
        } else {
            return "Please enter a message of less than 250 characters.";
        }
    }

    // This is what the unit tests check against - different wording as per POE rubric
    public static String checkMessageLength(String text) {
        if (text.length() <= 250) {
            return "Message ready to send.";
        } else {
            int excess = text.length() - 250;
            return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
        }
    }

    // Saves the message to a JSON file and a text file
    // JSON storage reference: https://www.geeksforgeeks.org/how-to-write-json-array-to-a-file-in-java/
    public void storeMessage() {
        String filename = "stored_messages.json";

        // build the json block for this message
        String newEntry = "  {\n"
                + "    \"messageID\": \"" + messageId + "\",\n"
                + "    \"messageNumber\": " + messageNumber + ",\n"
                + "    \"sender\": \"" + sender + "\",\n"
                + "    \"recipient\": \"" + recipient + "\",\n"
                + "    \"message\": \"" + messageText + "\",\n"
                + "    \"messageHash\": \"" + messageHash + "\"\n"
                + "  }";

        try {
            File file = new File(filename);

            if (file.exists() && file.length() > 2) {
                // file already has messages in it - read it, cut off the last ], add new message, close it again
                String existing = new String(Files.readAllBytes(Paths.get(filename))).trim();
                existing = existing.substring(0, existing.lastIndexOf("]")).trim();
                try (FileWriter fw = new FileWriter(filename, false)) {
                    fw.write(existing + ",\n" + newEntry + "\n]");
                }
            } else {
                // first message - start a fresh json array
                try (FileWriter fw = new FileWriter(filename, false)) {
                    fw.write("[\n" + newEntry + "\n]");
                }
            }

            System.out.println("Message successfully stored in JSON file.");

        } catch (IOException e) {
            e.printStackTrace();
        }

        // also save to a plain text file - YOUR original storeMessageToTextFile code
        try (FileWriter file = new FileWriter("stored_message.txt", true)) {
            file.write("Message ID: " + messageId + "\n");
            file.write("Recipient: " + recipient + "\n");
            file.write("Message: " + messageText + "\n");
            file.write("Hash: " + messageHash + "\n");
            file.write("-----\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Resets everything back to zero - only used by the unit tests
    public static void resetCounters() {
        messageCount = 0;
        totalSent = 0;
        allMessages.clear();
    }

    // Getters - lets other classes read the private fields
    public String getMessageId()     { return messageId; }
    public String getMessageHash()   { return messageHash; }
    public String getRecipient()     { return recipient; }
    public String getMessageText()   { return messageText; }
    public String getSender()        { return sender; }
    public String getStatus()        { return status; }
    public int    getMessageNumber() { return messageNumber; }
}
