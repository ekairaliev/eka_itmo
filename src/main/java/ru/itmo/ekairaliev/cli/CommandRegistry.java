package ru.itmo.ekairaliev.cli;

import ru.itmo.ekairaliev.cli.command.AbstractCommand;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class CommandRegistry {
    private final Map<String, AbstractCommand> commands = new LinkedHashMap<>();

    public CommandRegistry(List<AbstractCommand> commands) {
        for (AbstractCommand command : commands) {
            register(command);
        }
    }

    public Optional<AbstractCommand> find(String name) {
        return Optional.ofNullable(commands.get(name));
    }

    public Collection<AbstractCommand> getCommands() {
        return commands.values();
    }

    private void register(AbstractCommand command) {
        AbstractCommand previous = commands.put(command.getName(), command);
        if (previous != null) {
            throw new IllegalArgumentException("Duplicate command: " + command.getName());
        }
    }
}
