package ru.itmo.ekairaliev.cli.command;

import ru.itmo.ekairaliev.cli.CliContext;
import ru.itmo.ekairaliev.cli.CommandException;
import ru.itmo.ekairaliev.cli.CommandExecutionResult;

import java.util.List;

public final class HistoryCommand extends AbstractCommand {
    public HistoryCommand() {
        super("history", "history");
    }

    @Override
    public void validateArgs(List<String> args) {
        ensureArgCount(args, 0);
    }

    @Override
    public CommandExecutionResult execute(CliContext context, List<String> args) {
        List<String> commands = context.recentCommands();
        if (commands.isEmpty()) {
            throw new CommandException("Ошибка: ранее не было введено ни одной команды");
        }

        for (String command : commands) {
            System.out.println(command);
        }
        return CommandExecutionResult.CONTINUE;
    }
}
