package com.patta.serverpassword;

import com.patta.serverpassword.auth.AuthSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AuthSessionTest {

    private AuthSession session;
    private final UUID testUUID = UUID.randomUUID();

    @BeforeEach
    public void setUp() {
        session = new AuthSession(testUUID);
    }

    @Test
    @DisplayName("Test initial session state")
    public void testInitialState() {
        assertEquals(testUUID, session.getPlayerUUID());
        assertEquals(0, session.getInputLength());
        assertEquals("", session.getInput());
        assertEquals("", session.getMaskedInput("*"));
        assertEquals(0, session.getFailedAttempts());
        assertFalse(session.isAlphabetMode());
    }

    @Test
    @DisplayName("Test character appending and 8 character limit")
    public void testAppendCharAndLimit() {
        // Append 8 characters
        for (int i = 1; i <= 8; i++) {
            boolean added = session.appendChar((char) ('0' + i));
            assertTrue(added);
            assertEquals(i, session.getInputLength());
        }

        assertEquals("12345678", session.getInput());
        assertEquals("* * * * * * * *", session.getMaskedInput("*"));

        // 9th character must be rejected
        boolean overflow = session.appendChar('9');
        assertFalse(overflow);
        assertEquals(8, session.getInputLength());
        assertEquals("12345678", session.getInput());
    }

    @Test
    @DisplayName("Test backspace functionality")
    public void testBackspace() {
        session.appendChar('1');
        session.appendChar('A');
        assertEquals(2, session.getInputLength());
        assertEquals("1A", session.getInput());

        session.backspace();
        assertEquals(1, session.getInputLength());
        assertEquals("1", session.getInput());
        assertEquals("*", session.getMaskedInput("*"));

        session.backspace();
        assertEquals(0, session.getInputLength());
        assertEquals("", session.getInput());

        // Backspace on empty shouldn't throw exception
        assertDoesNotThrow(() -> session.backspace());
        assertEquals(0, session.getInputLength());
    }

    @Test
    @DisplayName("Test clear functionality")
    public void testClear() {
        session.appendChar('5');
        session.appendChar('6');
        session.appendChar('7');
        assertEquals(3, session.getInputLength());

        session.clear();
        assertEquals(0, session.getInputLength());
        assertEquals("", session.getInput());
        assertEquals("", session.getMaskedInput("*"));
    }

    @Test
    @DisplayName("Test alphabet mode toggle")
    public void testAlphabetModeToggle() {
        assertFalse(session.isAlphabetMode());
        session.toggleAlphabetMode();
        assertTrue(session.isAlphabetMode());
        session.toggleAlphabetMode();
        assertFalse(session.isAlphabetMode());
    }

    @Test
    @DisplayName("Test failed attempts tracking")
    public void testFailedAttempts() {
        assertEquals(0, session.getFailedAttempts());
        session.incrementFailedAttempts();
        assertEquals(1, session.getFailedAttempts());
        session.incrementFailedAttempts();
        assertEquals(2, session.getFailedAttempts());
    }
}
