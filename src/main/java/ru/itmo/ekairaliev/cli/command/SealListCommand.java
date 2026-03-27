package ru.itmo.ekairaliev.cli.command;

import ru.itmo.ekairaliev.cli.CliContext;
import ru.itmo.ekairaliev.cli.CommandExecutionResult;
import ru.itmo.ekairaliev.model.Seal;

import java.util.List;

public final class SealListCommand extends AbstractCommand {
    public SealListCommand() {
        super("seal_list", "seal_list");
    }

    @Override
    public void validateArgs(List<String> args) {
        ensureArgCount(args, 0);
    }

    @Override
    public CommandExecutionResult execute(CliContext context, List<String> args) {
        List<Seal> seals = context.getSealService().getAll();
        if (seals.isEmpty()) {
            System.out.println("Список seal пуст.");
            return CommandExecutionResult.CONTINUE;
        }

        System.out.printf("%-6s %-10s %-20s %-10s %-17s%n", "ID", "SampleID", "SealNumber", "Status", "UpdatedAt");
        for (Seal seal : seals) {
            System.out.printf(
                    "%-6d %-10d %-20s %-10s %-17s%n",
                    seal.getId(),
                    seal.getSampleId(),
                    context.trimForTable(seal.getSealNumber(), 20),
                    seal.getStatus(),
                    context.formatInstant(seal.getUpdatedAt())
            );
        }
        return CommandExecutionResult.CONTINUE;
    }
}
