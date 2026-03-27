package ru.itmo.ekairaliev.cli;

import ru.itmo.ekairaliev.cli.command.CustodyAddCommand;
import ru.itmo.ekairaliev.cli.command.CustodyCheckCommand;
import ru.itmo.ekairaliev.cli.command.CustodyExportCommand;
import ru.itmo.ekairaliev.cli.command.CustodyListCommand;
import ru.itmo.ekairaliev.cli.command.CustodyShowCommand;
import ru.itmo.ekairaliev.cli.command.ExitCommand;
import ru.itmo.ekairaliev.cli.command.HelpCommand;
import ru.itmo.ekairaliev.cli.command.SampleAddCommand;
import ru.itmo.ekairaliev.cli.command.SampleHoldCommand;
import ru.itmo.ekairaliev.cli.command.SampleListCommand;
import ru.itmo.ekairaliev.cli.command.SampleReleaseCommand;
import ru.itmo.ekairaliev.cli.command.SealAddCommand;
import ru.itmo.ekairaliev.cli.command.SealBreakCommand;
import ru.itmo.ekairaliev.cli.command.SealListCommand;
import ru.itmo.ekairaliev.cli.command.SealShowCommand;
import ru.itmo.ekairaliev.service.CustodyService;
import ru.itmo.ekairaliev.service.SampleService;
import ru.itmo.ekairaliev.service.SealService;

import java.util.List;
import java.util.Scanner;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SampleService sampleService = new SampleService();
        SealService sealService = new SealService(sampleService);
        CustodyService custodyService = new CustodyService(sampleService);

        CommandRegistry commandRegistry = new CommandRegistry(List.of(
                new HelpCommand(),
                new ExitCommand(),
                new SampleAddCommand(),
                new SampleListCommand(),
                new SealAddCommand(),
                new SealListCommand(),
                new SealShowCommand(),
                new SealBreakCommand(),
                new CustodyAddCommand(),
                new CustodyListCommand(),
                new CustodyShowCommand(),
                new SampleHoldCommand(),
                new SampleReleaseCommand(),
                new CustodyCheckCommand(),
                new CustodyExportCommand()
        ));

        CliContext cliContext = new CliContext(
                sampleService,
                sealService,
                custodyService,
                commandRegistry,
                new Scanner(System.in)
        );

        new CliApplication(cliContext).run();
    }
}
