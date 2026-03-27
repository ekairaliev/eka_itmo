package ru.itmo.ekairaliev.cli.command;

import ru.itmo.ekairaliev.cli.CliContext;
import ru.itmo.ekairaliev.cli.CommandExecutionResult;
import ru.itmo.ekairaliev.model.Sample;

import java.util.List;

public final class SampleListCommand extends AbstractCommand {
    public SampleListCommand() {
        super("sample_list", "sample_list");
    }

    @Override
    public void validateArgs(List<String> args) {
        ensureArgCount(args, 0);
    }

    @Override
    public CommandExecutionResult execute(CliContext context, List<String> args) {
        List<Sample> samples = context.getSampleService().getAll();
        if (samples.isEmpty()) {
            System.out.println("Список sample пуст.");
            return CommandExecutionResult.CONTINUE;
        }

        System.out.printf("%-6s %-30s %-12s %-17s%n", "ID", "Name", "Status", "UpdatedAt");
        for (Sample sample : samples) {
            System.out.printf(
                    "%-6d %-30s %-12s %-17s%n",
                    sample.getId(),
                    context.trimForTable(sample.getName(), 30),
                    sample.getHoldStatus(),
                    context.formatInstant(sample.getUpdatedAt())
            );
        }
        return CommandExecutionResult.CONTINUE;
    }
}
