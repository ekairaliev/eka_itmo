package ru.itmo.ekairaliev.cli.command;

import ru.itmo.ekairaliev.cli.CliContext;
import ru.itmo.ekairaliev.cli.CommandExecutionResult;

import java.util.List;

public final class HelpCommand extends AbstractCommand {
    public HelpCommand() {
        super("help", "help");
    }

    @Override
    public void validateArgs(List<String> args) {
        ensureArgCount(args, 0);
    }

    @Override
    public CommandExecutionResult execute(CliContext context, List<String> args) {
        for (AbstractCommand command : context.getCommandRegistry().getCommands()) {
            System.out.println(command.getHelp());
        }
        return CommandExecutionResult.CONTINUE;
    }
}
