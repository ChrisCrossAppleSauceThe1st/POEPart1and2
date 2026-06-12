import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

// This class handles everything to do with messages - creating, sending, storing them
// JSON storage reference: https://www.geeksforgeeks.org/how-to-write-json-array-to-a-file-in-java/
// JSON reading reference: https://www.geeksforgeeks.org/parse-json-java/
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
    private static int messageCount = 0;   // goes up every time a new message is made
    private static int totalSent    = 0;   // only goes up when a message is actually sent

    // PART 3 - parallel arrays to hold each category of message
    // Using ArrayList so the size is flexible and we can remove items (delete by hash)
    private static ArrayList<Message> sentMessages       = new ArrayList<>();   // option 1 - sent
    private static ArrayList<Message> disregardedMessages = new ArrayList<>();  // option 2 - discarded
    private static ArrayList<Message> storedMessages     = new ArrayList<>();   // option 3 - stored to JSON

    // Combined view used by printMessages() and the old allMessages references
    private static ArrayList<Message> allMessages = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Constructor - runs when we do new Message(...)
    // -------------------------------------------------------------------------
    public Message(String sender, String recipient, String messageText) {
        this.sender      = sender;
        this.recipient   = recipient;
        this.messageText = messageText;
        this.messageNumber = messageCount; // grab current count (first message = 0)
        messageCount++;                    // bump for the next message
        this.messageId   = generateMessageId();   // random 10-digit ID
        this.messageHash = createMessageHash();   // hash from ID and message text
    }

    // -------------------------------------------------------------------------
    // Makes a random 10-digit number as a string for the message ID
    // -------------------------------------------------------------------------
    private static String generateMessageId() {
        Random random = new Random();
        String id = "";
        for (int i = 0; i < 10; i++) {
            id += random.nextInt(10); // adds one digit at a time
        }
        return id;
    }

    // -------------------------------------------------------------------------
    // Builds the message hash: first 2 chars of ID : message number : FIRSTLAST
    // e.g. 67:0:HITONIGHT
    // -------------------------------------------------------------------------
    public String createMessageHash() {
        String[] words = messageText.trim().split("\\s+");

        // strip punctuation from first and last words
        String first = words.length > 0 ? words[0].replaceAll("[^a-zA-Z0-9]", "") : "";
        String last  = words.length > 1 ? words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "") : first;

        return messageId.substring(0, 2) + ":" + messageNumber + ":" + first.toUpperCase() + last.toUpperCase();
    }

    // -------------------------------------------------------------------------
    // Returns true if the message ID is 10 characters or less
    // -------------------------------------------------------------------------
    public boolean checkMessageID() {
        return messageId != null && messageId.length() <= 10;
    }

    // -------------------------------------------------------------------------
    // Checks if the recipient number is a valid +27 SA number
    // -------------------------------------------------------------------------
    public String checkRecipientCell() {
        if (recipient != null && recipient.matches("\\+27[0-9]{9}")) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    // -------------------------------------------------------------------------
    // Handles the send / discard / store sub-menu choice
    // -------------------------------------------------------------------------
    public String sentMessage(int subChoice) {
        switch (subChoice) {
            case 1:
                status = "Sent";
                totalSent++;
                sentMessages.add(this);
                allMessages.add(this);
                return "Message successfully sent.";
            case 2:
                status = "Disregarded";
                disregardedMessages.add(this);
                return "Press 0 to delete the message.";
            case 3:
                status = "Stored";
                storeMessage();          // write to JSON file
                storedMessages.add(this);
                allMessages.add(this);
                return "Message successfully stored.";
            default:
                return "Invalid option";
        }
    }

    // -------------------------------------------------------------------------
    // Returns all messages that were sent or stored (for the menu option 1 display)
    // -------------------------------------------------------------------------
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

    // -------------------------------------------------------------------------
    // Returns how many messages were actually sent (not stored, not discarded)
    // -------------------------------------------------------------------------
    public static int returnTotalMessages() {
        return totalSent;
    }

    public static int returnTotalMessagess() {
        return totalSent;
    }

    // -------------------------------------------------------------------------
    // Message length checks - two versions: one for display, one for unit tests
    // -------------------------------------------------------------------------

    // Used on screen during chat
    public static String checkMessageLengthForDisplay(String text) {
        if (text.length() <= 250) {
            return "Message sent";
        } else {
            return "Please enter a message of less than 250 characters.";
        }
    }

    public static String checkMessageLength(String text) {
        if (text.length() <= 250) {
            return "Message ready to send.";
        } else {
            int excess = text.length() - 250;
            return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
        }
    }

    // -------------------------------------------------------------------------
    // PART 3 - Array methods
    // -------------------------------------------------------------------------

    /**
     * Returns all messages that were flagged as Sent.
     * Used to populate the sentMessages array for the Part 3 report and tests.
     */
    public static ArrayList<Message> getSentMessages() {
        return sentMessages;
    }

    /**
     * Returns all messages that were flagged as Stored.
     */
    public static ArrayList<Message> getStoredMessages() {
        return storedMessages;
    }

    /**
     * Returns all messages that were flagged as Disregarded.
     */
    public static ArrayList<Message> getDisregardedMessages() {
        return disregardedMessages;
    }

    /**
     * Returns all message hashes across every message ever created (sent, stored, disregarded).
     * Stored in insertion order.
     */
    public static ArrayList<String> getAllMessageHashes() {
        ArrayList<String> hashes = new ArrayList<>();
        for (Message m : allMessages) {
            hashes.add(m.messageHash);
        }
        // also include discarded so the list is truly complete
        for (Message m : disregardedMessages) {
            if (!hashes.contains(m.messageHash)) {
                hashes.add(m.messageHash);
            }
        }
        return hashes;
    }

    /**
     * Returns all message IDs across sent and stored messages.
     */
    public static ArrayList<String> getAllMessageIds() {
        ArrayList<String> ids = new ArrayList<>();
        for (Message m : allMessages) {
            ids.add(m.messageId);
        }
        return ids;
    }

    /**
     * Displays the sender and recipient of every stored message.
     */
    public static String displayStoredSenderRecipient() {
        if (storedMessages.isEmpty()) {
            return "No stored messages found.";
        }
        StringBuilder sb = new StringBuilder("--- Stored Messages: Sender and Recipient ---\n");
        for (Message m : storedMessages) {
            sb.append("Sender    : ").append(m.sender)
                    .append("\nRecipient : ").append(m.recipient)
                    .append("\n---\n");
        }
        return sb.toString().trim();
    }

    /**
     * Finds and returns the longest message text across ALL messages (sent, stored, disregarded).
     */
    public static String getLongestMessage() {
        // combine all three arrays to check every message
        ArrayList<Message> all = new ArrayList<>();
        all.addAll(sentMessages);
        all.addAll(storedMessages);
        all.addAll(disregardedMessages);

        if (all.isEmpty()) {
            return "No messages found.";
        }

        Message longest = all.get(0);
        for (Message m : all) {
            if (m.messageText.length() > longest.messageText.length()) {
                longest = m;
            }
        }
        return longest.messageText;
    }

    /**
     * Searches by message ID and returns the recipient and message text.
     */
    public static String searchByMessageId(String searchId) {
        ArrayList<Message> all = new ArrayList<>();
        all.addAll(sentMessages);
        all.addAll(storedMessages);
        all.addAll(disregardedMessages);

        for (Message m : all) {
            if (m.messageId.equals(searchId)) {
                return "Recipient : " + m.recipient + "\nMessage   : " + m.messageText;
            }
        }
        return "Message ID not found.";
    }

    /**
     * Returns the message text (only) when searched by ID - used by unit tests.
     * The unit test in the POE checks "It is dinner time!" given recipient 0838884567 as ID context.
     */
    public static String getMessageByRecipient(String recipientNumber) {
        ArrayList<Message> all = new ArrayList<>();
        all.addAll(sentMessages);
        all.addAll(storedMessages);
        all.addAll(disregardedMessages);

        StringBuilder sb = new StringBuilder();
        for (Message m : all) {
            if (m.recipient.equals(recipientNumber)) {
                if (sb.length() > 0) sb.append("\n");
                sb.append(m.messageText);
            }
        }
        return sb.length() > 0 ? sb.toString() : "No messages found for that recipient.";
    }

    /**
     * Searches ALL messages sent or stored for a particular recipient and returns
     */
    public static String searchByRecipient(String recipientNumber) {
        // check sent and stored (not discarded - they were thrown away)
        ArrayList<Message> all = new ArrayList<>();
        all.addAll(sentMessages);
        all.addAll(storedMessages);

        StringBuilder sb = new StringBuilder();
        for (Message m : all) {
            if (m.recipient.equals(recipientNumber)) {
                sb.append("\nMessage ID   : ").append(m.messageId)
                        .append("\nMessage Hash : ").append(m.messageHash)
                        .append("\nRecipient    : ").append(m.recipient)
                        .append("\nMessage      : ").append(m.messageText)
                        .append("\nStatus       : ").append(m.status)
                        .append("\n---\n");
            }
        }
        return sb.length() > 0 ? sb.toString().trim() : "No messages found for recipient: " + recipientNumber;
    }

    /**
     * Deletes a message using its hash from ALL arrays (sent, stored, disregarded).
     */
    public static String deleteMessageByHash(String hash) {
        // check each list in turn
        for (ArrayList<Message> list : new ArrayList<ArrayList<Message>>() {{
            add(sentMessages); add(storedMessages); add(disregardedMessages); add(allMessages);
        }}) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).messageHash.equals(hash)) {
                    String text = list.get(i).messageText;
                    list.remove(i);
                    return "Message: \"" + text + "\" successfully deleted.";
                }
            }
        }
        return "Message hash not found.";
    }

    /**
     * Displays a full report of all sent messages.
     */
    public static String displayReport() {
        ArrayList<Message> reportList = new ArrayList<>();
        reportList.addAll(sentMessages);
        reportList.addAll(storedMessages);

        if (reportList.isEmpty()) {
            return "No messages to report.";
        }

        StringBuilder sb = new StringBuilder("========== MESSAGE REPORT ==========\n");
        for (Message m : reportList) {
            sb.append("\nMessage Hash : ").append(m.messageHash)
                    .append("\nRecipient    : ").append(m.recipient)
                    .append("\nMessage      : ").append(m.messageText)
                    .append("\nStatus       : ").append(m.status)
                    .append("\n------------------------------------");
        }
        sb.append("\n====================================");
        return sb.toString();
    }

    /**
     * Reads the stored_messages.json file back into the storedMessages array.
     * Called at app startup so previously stored messages are available.
     * JSON reading reference: https://www.geeksforgeeks.org/parse-json-java/
     * Uses a simple manual parser - no external library needed.
     */
    public static void loadStoredMessagesFromJson() {
        String filename = "stored_messages.json";
        File file = new File(filename);
        if (!file.exists()) return; // nothing to load yet

        try {
            String content = new String(Files.readAllBytes(Paths.get(filename))).trim();
            if (content.isEmpty() || content.equals("[]")) return;

            // split on each { to get individual message blocks
            String[] blocks = content.split("\\{");
            for (String block : blocks) {
                if (!block.contains("messageID")) continue; // skip the opening [ bracket

                // parse each field with simple substring extraction
                String id       = extractJsonValue(block, "messageID");
                String sender   = extractJsonValue(block, "sender");
                String recipient = extractJsonValue(block, "recipient");
                String text     = extractJsonValue(block, "message");
                String hashVal  = extractJsonValue(block, "messageHash");

                if (id == null || text == null) continue;

                // rebuild the Message object and mark it as Stored
                // We use the raw constructor then fix the fields via the stored list
                Message m = new Message(sender != null ? sender : "unknown",
                        recipient != null ? recipient : "unknown",
                        text);
                m.messageId   = id;
                m.messageHash = hashVal != null ? hashVal : m.messageHash;
                m.status      = "Stored";

                // avoid loading duplicates if the app has already run this session
                boolean duplicate = false;
                for (Message existing : storedMessages) {
                    if (existing.messageId.equals(m.messageId)) { duplicate = true; break; }
                }
                if (!duplicate) {
                    storedMessages.add(m);
                    allMessages.add(m);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read stored messages file: " + e.getMessage());
        }
    }

    /**
     * Helper: pulls the value of a JSON string field out of a raw block of JSON text.
     * e.g. given block containing "messageID": "1234567890", returns "1234567890"
     */
    private static String extractJsonValue(String block, String key) {
        String search = "\"" + key + "\": \"";
        int start = block.indexOf(search);
        if (start == -1) return null;
        start += search.length();
        int end = block.indexOf("\"", start);
        if (end == -1) return null;
        return block.substring(start, end);
    }

    // -------------------------------------------------------------------------
    // Saves the message to a JSON file and a plain text file
    // JSON storage reference: https://www.geeksforgeeks.org/how-to-write-json-array-to-a-file-in-java/
    // -------------------------------------------------------------------------
    public void storeMessage() {
        String filename = "stored_messages.json";

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
                // already has messages - append before the closing ]
                String existing = new String(Files.readAllBytes(Paths.get(filename))).trim();
                existing = existing.substring(0, existing.lastIndexOf("]")).trim();
                try (FileWriter fw = new FileWriter(filename, false)) {
                    fw.write(existing + ",\n" + newEntry + "\n]");
                }
            } else {
                // first message - start a fresh JSON array
                try (FileWriter fw = new FileWriter(filename, false)) {
                    fw.write("[\n" + newEntry + "\n]");
                }
            }

            System.out.println("Message successfully stored in JSON file.");

        } catch (IOException e) {
            e.printStackTrace();
        }

        // also write to a human-readable text file
        try (FileWriter fw = new FileWriter("stored_message.txt", true)) {
            fw.write("Message ID: " + messageId + "\n");
            fw.write("Recipient: " + recipient + "\n");
            fw.write("Message: " + messageText + "\n");
            fw.write("Hash: " + messageHash + "\n");
            fw.write("-----\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // Resets everything - only used by unit tests so one test doesn't affect the next
    // -------------------------------------------------------------------------
    public static void resetCounters() {
        messageCount = 0;
        totalSent    = 0;
        allMessages.clear();
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
    }

    // -------------------------------------------------------------------------
    // Getters - lets other classes read the private fields without breaking encapsulation
    // -------------------------------------------------------------------------
    public String getMessageId()      { return messageId;    }
    public String getMessageHash()    { return messageHash;  }
    public String getRecipient()      { return recipient;    }
    public String getMessageText()    { return messageText;  }
    public String getSender()         { return sender;       }
    public String getStatus()         { return status;       }
    public int    getMessageNumber()  { return messageNumber;}

    // Setter needed by loadStoredMessagesFromJson to restore the ID and hash from file
    public void setMessageId(String id)     { this.messageId   = id;   }
    public void setMessageHash(String hash) { this.messageHash = hash; }
    public void setStatus(String s)         { this.status      = s;    }
}
