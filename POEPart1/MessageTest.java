import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.ArrayList;

// Tests for the Message class - covers Part 2 tests AND Part 3 tests
public class MessageTest {

    // -------------------------------------------------------------------------
    // @Before - runs before EVERY test to keep the state clean
    // -------------------------------------------------------------------------
    @Before
    public void reset() {
        Message.resetCounters();
    }

    // =========================================================================
    // PART 2 TESTS (kept from previous submission)
    // =========================================================================

    // --- Message length tests ---

    // A short message should come back as ready to send
    @Test
    public void testCheckMessageLength_success_assertEquals() {
        assertEquals("Message ready to send.",
                Message.checkMessageLength("Hi Mike, can you join us for dinner tonight?"));
    }

    // A 260-char message is 10 over the limit - should say that
    @Test
    public void testCheckMessageLength_failure_assertEquals() {
        String longMsg = "A".repeat(260);
        assertEquals(
                "Message exceeds 250 characters by 10; please reduce the size.",
                Message.checkMessageLength(longMsg));
    }

    // --- Recipient number tests ---

    // +27718693002 is a valid SA number
    @Test
    public void testCheckRecipientCell_correct_assertEquals() {
        Message msg = new Message("kyl_1", "+27718693002", "Test");
        assertEquals("Cell phone number successfully captured.", msg.checkRecipientCell());
    }

    // 08575975889 has no +27 code
    @Test
    public void testCheckRecipientCell_incorrect_assertEquals() {
        Message msg = new Message("kyl_1", "08575975889", "Test");
        assertEquals(
                "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                msg.checkRecipientCell());
    }

    // --- Message hash tests ---

    // The first message with this text should have :0:HITONIGHT in the hash
    @Test
    public void testCreateMessageHash_testCase1_assertEquals() {
        Message msg = new Message("kyl_1", "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertTrue("Hash must contain :0:HITONIGHT", msg.getMessageHash().contains(":0:HITONIGHT"));
    }

    // The word part of the hash must always be uppercase
    @Test
    public void testCreateMessageHash_allCaps_assertEquals() {
        Message msg = new Message("kyl_1", "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        String wordPart = msg.getMessageHash().split(":")[2];
        assertEquals(wordPart.toUpperCase(), wordPart);
    }

    // --- Message ID tests ---

    // A new message should always get an ID
    @Test
    public void testCheckMessageID_created_assertNotNull() {
        Message msg = new Message("kyl_1", "+27718693002", "Test");
        assertNotNull(msg.getMessageId());
        System.out.println("Message ID generated: " + msg.getMessageId());
    }

    // The ID should be exactly 10 characters
    @Test
    public void testCheckMessageID_isTenCharacters_assertTrue() {
        Message msg = new Message("kyl_1", "+27718693002", "Test");
        assertEquals(10, msg.getMessageId().length());
        assertTrue(msg.checkMessageID());
    }

    // --- Send / discard / store tests ---

    @Test
    public void testSentMessage_send_assertEquals() {
        Message msg = new Message("kyl_1", "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message successfully sent.", msg.sentMessage(1));
    }

    @Test
    public void testSentMessage_discard_assertEquals() {
        Message msg = new Message("kyl_1", "08575975889", "Hi Keegan, did you receive the payment?");
        assertEquals("Press 0 to delete the message.", msg.sentMessage(2));
    }

    @Test
    public void testSentMessage_store_assertEquals() {
        Message msg = new Message("kyl_1", "+27718693002", "Test store message");
        assertEquals("Message successfully stored.", msg.sentMessage(3));
    }

    // --- Total messages test ---

    // Send one and discard one - total should only be 1
    @Test
    public void testReturnTotalMessages_assertEquals() {
        Message msg1 = new Message("kyl_1", "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        msg1.sentMessage(1); // sent

        Message msg2 = new Message("kyl_1", "08575975889", "Hi Keegan, did you receive the payment?");
        msg2.sentMessage(2); // discarded - should NOT count

        assertEquals(1, Message.returnTotalMessages());
        assertEquals(1, Message.returnTotalMessagess()); // also check the POE typo spelling
    }

    // =========================================================================
    // PART 3 TESTS
    // Using the 5 test messages
    //
    // Message 1: +27834557896  "Did you get the cake?"          -> SENT
    // Message 2: +27838884567  "Where are you? You are late!..."-> STORED
    // Message 3: +27834484567  "Yohoooo, I am at your gate."    -> DISREGARD
    // Message 4: 0838884567    "It is dinner time !"            -> SENT
    // Message 5: +27838884567  "Ok, I am leaving without you."  -> STORED
    // =========================================================================

    // Helper that loads the 5 POE test messages into the arrays
    // Called at the start of each Part 3 test
    private void loadPart3TestData() {
        Message.resetCounters(); // always start clean

        Message m1 = new Message("kyl_1", "+27834557896", "Did you get the cake?");
        m1.sentMessage(1);   // Sent

        Message m2 = new Message("kyl_1", "+27838884567",
                "Where are you? You are late! I have asked you to be on time.");
        m2.sentMessage(3);   // Stored

        Message m3 = new Message("kyl_1", "+27834484567", "Yohoooo, I am at your gate.");
        m3.sentMessage(2);   // Disregarded

        Message m4 = new Message("kyl_1", "0838884567", "It is dinner time !");
        m4.sentMessage(1);   // Sent

        Message m5 = new Message("kyl_1", "+27838884567", "Ok, I am leaving without you.");
        m5.sentMessage(3);   // Stored
    }

    // Test: Sent Messages array correctly populated
    @Test
    public void testSentMessagesArray_correctlyPopulated_assertEquals() {
        loadPart3TestData();

        ArrayList<Message> sent = Message.getSentMessages();

        // there should be exactly 2 sent messages
        assertEquals(2, sent.size());

        // check the text matches what the POE specifies
        assertEquals("Did you get the cake?",  sent.get(0).getMessageText());
        assertEquals("It is dinner time !",     sent.get(1).getMessageText());
    }

    // Test: Display the longest message
    @Test
    public void testGetLongestMessage_assertEquals() {
        loadPart3TestData();
        assertEquals(
                "Where are you? You are late! I have asked you to be on time.",
                Message.getLongestMessage());
    }

    // Test: Search for message by recipient (0838884567 -> "It is dinner time !")
    @Test
    public void testGetMessageByRecipient_messageId4_assertEquals() {
        loadPart3TestData();
        String result = Message.getMessageByRecipient("0838884567");
        assertEquals("It is dinner time !", result);
    }

    // Test: Search all messages for a particular recipient (+27838884567)
    @Test
    public void testSearchByRecipient_twoMessages_assertEquals() {
        loadPart3TestData();
        String result = Message.searchByRecipient("+27838884567");

        // both messages should appear in the result
        assertTrue("Should contain message 2",
                result.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue("Should contain message 5",
                result.contains("Ok, I am leaving without you."));
    }

    // Test: Delete a message using its hash (message 2)
    @Test
    public void testDeleteMessageByHash_assertEquals() {
        loadPart3TestData();

        // grab the hash of message 2 (index 1 in storedMessages)
        ArrayList<Message> stored = Message.getStoredMessages();
        String hashToDelete = stored.get(0).getMessageHash(); // message 2 is first stored
        String expectedText = stored.get(0).getMessageText();

        String result = Message.deleteMessageByHash(hashToDelete);
        assertEquals("Message: \"" + expectedText + "\" successfully deleted.", result);
    }

    // -------------------------------------------------------------------------
    // Test: Display report - should contain Message Hash, Recipient, Message
    // for all sent and stored messages
    // -------------------------------------------------------------------------
    @Test
    public void testDisplayReport_containsRequiredFields() {
        loadPart3TestData();
        String report = Message.displayReport();

        // report must mention the sent and stored messages
        assertTrue("Report should contain message 1",  report.contains("Did you get the cake?"));
        assertTrue("Report should contain message 2",  report.contains("Where are you? You are late!"));
        assertTrue("Report should contain message 4",  report.contains("It is dinner time !"));
        assertTrue("Report should contain message 5",  report.contains("Ok, I am leaving without you."));

        // report must include the required field labels
        assertTrue("Report should show Message Hash",  report.contains("Message Hash"));
        assertTrue("Report should show Recipient",     report.contains("Recipient"));
        assertTrue("Report should show Message",       report.contains("Message"));
    }

    // -------------------------------------------------------------------------
    // Test: Stored messages array correctly populated (messages 2 and 5)
    // -------------------------------------------------------------------------
    @Test
    public void testStoredMessagesArray_correctlyPopulated() {
        loadPart3TestData();

        ArrayList<Message> stored = Message.getStoredMessages();
        assertEquals(2, stored.size());

        boolean hasMsg2 = false;
        boolean hasMsg5 = false;
        for (Message m : stored) {
            if (m.getMessageText().equals("Where are you? You are late! I have asked you to be on time.")) hasMsg2 = true;
            if (m.getMessageText().equals("Ok, I am leaving without you.")) hasMsg5 = true;
        }
        assertTrue("Stored array should contain message 2", hasMsg2);
        assertTrue("Stored array should contain message 5", hasMsg5);
    }

    // -------------------------------------------------------------------------
    // Test: Disregarded messages array correctly populated (message 3 only)
    // -------------------------------------------------------------------------
    @Test
    public void testDisregardedMessagesArray_correctlyPopulated() {
        loadPart3TestData();

        ArrayList<Message> disregarded = Message.getDisregardedMessages();
        assertEquals(1, disregarded.size());
        assertEquals("Yohoooo, I am at your gate.", disregarded.get(0).getMessageText());
    }

    // -------------------------------------------------------------------------
    // Test: Display sender and recipient of stored messages
    // Both stored messages should appear with their recipients
    // -------------------------------------------------------------------------
    @Test
    public void testDisplayStoredSenderRecipient_containsBothRecipients() {
        loadPart3TestData();
        String result = Message.displayStoredSenderRecipient();

        // both stored messages point to +27838884567
        assertTrue("Should show recipient of message 2", result.contains("+27838884567"));
    }
}
