package ru.itmo.ekairaliev.cli.command;

import ru.itmo.ekairaliev.cli.CliContext;
import ru.itmo.ekairaliev.cli.CommandExecutionResult;

import java.util.List;

public final class ExitCommand extends AbstractCommand {
    public ExitCommand() {
        super("exit", "exit");
    }

    @Override
    public void validateArgs(List<String> args) {
        ensureArgCount(args, 0);
    }

    @Override
    public CommandExecutionResult execute(CliContext context, List<String> args) {
        return CommandExecutionResult.EXIT;
    }
}
