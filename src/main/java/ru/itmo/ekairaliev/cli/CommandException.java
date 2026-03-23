package ru.itmo.ekairaliev.cli;

public final class CommandException extends RuntimeException {
    public CommandException(String message) {
        super(message);
    }
}
