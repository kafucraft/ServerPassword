package com.patta.serverpassword.auth;

import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class AuthSession {

    private final UUID playerUUID;
    private final StringBuilder currentInput;
    private int failedAttempts;
    private final long startTime;
    private BukkitTask timeoutTask;
    private BukkitTask promptTask;
    private boolean alphabetMode;
    private boolean processingSubmission;

    public AuthSession(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.currentInput = new StringBuilder();
        this.failedAttempts = 0;
        this.startTime = System.currentTimeMillis();
        this.alphabetMode = false;
        this.processingSubmission = false;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public synchronized boolean appendChar(char c) {
        if (currentInput.length() < 8) {
            currentInput.append(c);
            return true;
        }
        return false;
    }

    public synchronized void backspace() {
        if (currentInput.length() > 0) {
            currentInput.deleteCharAt(currentInput.length() - 1);
        }
    }

    public synchronized void clear() {
        currentInput.setLength(0);
    }

    public synchronized String getInput() {
        return currentInput.toString();
    }

    public synchronized int getInputLength() {
        return currentInput.length();
    }

    public synchronized String getMaskedInput(String maskChar) {
        if (maskChar == null || maskChar.isEmpty()) {
            maskChar = "*";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < currentInput.length(); i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(maskChar);
        }
        return sb.toString();
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void incrementFailedAttempts() {
        this.failedAttempts++;
    }

    public long getStartTime() {
        return startTime;
    }

    public BukkitTask getTimeoutTask() {
        return timeoutTask;
    }

    public void setTimeoutTask(BukkitTask timeoutTask) {
        this.timeoutTask = timeoutTask;
    }

    public BukkitTask getPromptTask() {
        return promptTask;
    }

    public void setPromptTask(BukkitTask promptTask) {
        this.promptTask = promptTask;
    }

    public boolean isAlphabetMode() {
        return alphabetMode;
    }

    public void toggleAlphabetMode() {
        this.alphabetMode = !this.alphabetMode;
    }

    public boolean isProcessingSubmission() {
        return processingSubmission;
    }

    public void setProcessingSubmission(boolean processingSubmission) {
        this.processingSubmission = processingSubmission;
    }
}
