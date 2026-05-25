import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

// Tests for the Message class
public class MessageTest {

    // runs before every test - resets the counters so one test doesn't affect the next
    @Before
    public void reset() {
        Message.resetCounters();
    }

    // --- MESSAGE LENGTH TESTS ---

    // a short message should come back as ready to send
    @Test
    public void testCheckMessageLength_success_assertEquals() {
        assertEquals("Message ready to send.",
                Message.checkMessageLength("Hi Mike, can you join us for dinner tonight?"));
    }

    // a 260 char message is 10 over the limit so it should say that
    @Test
    public void testCheckMessageLength_failure_assertEquals() {
        String longMsg = "A".repeat(260);
        assertEquals(
                "Message exceeds 250 characters by 10; please reduce the size.",
                Message.checkMessageLength(longMsg));
    }

    // --- RECIPIENT NUMBER TESTS ---

    // +27718693002 is a valid number so should say captured
    @Test
    public void testCheckRecipientCell_correct_assertEquals() {
        Message msg = new Message("kyl_1", "+27718693002", "Test");
        assertEquals("Cell phone number successfully captured.", msg.checkRecipientCell());
    }

    // 08575975889 has no +27 so should give the error message
    @Test
    public void testCheckRecipientCell_incorrect_assertEquals() {
        Message msg = new Message("kyl_1", "08575975889", "Test");
        assertEquals(
                "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                msg.checkRecipientCell());
    }

    // --- MESSAGE HASH TESTS ---

    // first message with this text should have :0:HITONIGHT in the hash
    @Test
    public void testCreateMessageHash_testCase1_assertEquals() {
        Message msg = new Message("kyl_1", "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        // the first 2 chars are random so we just check the part we know
        assertTrue("Hash must contain :0:HITONIGHT", msg.getMessageHash().contains(":0:HITONIGHT"));
    }

    // the words part of the hash should always be uppercase
    @Test
    public void testCreateMessageHash_allCaps_assertEquals() {
        Message msg = new Message("kyl_1", "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        String wordPart = msg.getMessageHash().split(":")[2];
        assertEquals(wordPart.toUpperCase(), wordPart);
    }

    // --- MESSAGE ID TESTS ---

    // a new message should always get an ID
    @Test
    public void testCheckMessageID_created_assertNotNull() {
        Message msg = new Message("kyl_1", "+27718693002", "Test");
        assertNotNull(msg.getMessageId());
        System.out.println("Message ID generated: " + msg.getMessageId());
    }

    // the ID should be exactly 10 characters
    @Test
    public void testCheckMessageID_isTenCharacters_assertTrue() {
        Message msg = new Message("kyl_1", "+27718693002", "Test");
        assertEquals(10, msg.getMessageId().length());
        assertTrue(msg.checkMessageID());
    }

    // --- SEND / DISCARD / STORE TESTS ---

    // picking option 1 should say message successfully sent
    @Test
    public void testSentMessage_send_assertEquals() {
        Message msg = new Message("kyl_1", "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message successfully sent.", msg.sentMessage(1));
    }

    // picking option 2 should say press 0 to delete
    @Test
    public void testSentMessage_discard_assertEquals() {
        Message msg = new Message("kyl_1", "08575975889", "Hi Keegan, did you receive the payment?");
        assertEquals("Press 0 to delete the message.", msg.sentMessage(2));
    }

    // picking option 3 should say message successfully stored
    @Test
    public void testSentMessage_store_assertEquals() {
        Message msg = new Message("kyl_1", "+27718693002", "Test store message");
        assertEquals("Message successfully stored.", msg.sentMessage(3));
    }

    // --- TOTAL MESSAGES TEST ---

    // we send one and discard one - total should only be 1
    @Test
    public void testReturnTotalMessages_assertEquals() {
        Message msg1 = new Message("kyl_1", "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        msg1.sentMessage(1); // sent

        Message msg2 = new Message("kyl_1", "08575975889", "Hi Keegan, did you receive the payment?");
        msg2.sentMessage(2); // discarded - should not count

        assertEquals(1, Message.returnTotalMessages());
        assertEquals(1, Message.returnTotalMessagess()); // also check the typo spelling from the POE spec
    }
}
