package ru.itmo.ekairaliev.cli.command;

import ru.itmo.ekairaliev.cli.CliContext;
import ru.itmo.ekairaliev.cli.CommandExecutionResult;
import ru.itmo.ekairaliev.model.Sample;

import java.util.List;

public final class SampleAddCommand extends AbstractCommand {
    public SampleAddCommand() {
        super("sample_add", "sample_add");
    }

    @Override
    public void validateArgs(List<String> args) {
        ensureArgCount(args, 0);
    }

    @Override
    public CommandExecutionResult execute(CliContext context, List<String> args) {
        String name = context.prompt("Название sample");
        Sample sample = context.getSampleService().add(name);
        System.out.println("OK sample_id=" + sample.getId());
        return CommandExecutionResult.CONTINUE;
    }
}
