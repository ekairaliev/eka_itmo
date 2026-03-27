package ru.itmo.ekairaliev.cli.command;

import ru.itmo.ekairaliev.cli.CliContext;
import ru.itmo.ekairaliev.cli.CommandException;
import ru.itmo.ekairaliev.cli.CommandExecutionResult;

import java.util.List;

public abstract class AbstractCommand {
    private final String name;
    private final String help;

    protected AbstractCommand(String name, String help) {
        this.name = name;
        this.help = help;
    }

    public final String getName() {
        return name;
    }

    public final String getHelp() {
        return help;
    }

    public void validateArgs(List<String> args) {
    }

    public abstract CommandExecutionResult execute(CliContext context, List<String> args);

    protected final void ensureArgCount(List<String> args, int expected) {
        if (args.size() != expected) {
            throw new CommandException("Ошибка: неверное число аргументов для " + name);
        }
    }

    protected final long parseId(String rawValue, String fieldName) {
        try {
            return Long.parseLong(rawValue);
        } catch (NumberFormatException e) {
            throw new CommandException("Ошибка: " + fieldName + " должен быть числом");
        }
    }

    protected final int parsePositiveInt(String rawValue) {
        final int value;
        try {
            value = Integer.parseInt(rawValue);
        } catch (NumberFormatException e) {
            throw new CommandException("Ошибка: N должно быть числом");
        }

        if (value <= 0) {
            throw new CommandException("Ошибка: N должно быть > 0");
        }
        return value;
    }
}
