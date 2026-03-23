package ru.itmo.ekairaliev.cli;

import ru.itmo.ekairaliev.model.CustodyEvent;
import ru.itmo.ekairaliev.model.Sample;
import ru.itmo.ekairaliev.model.SampleHoldStatus;
import ru.itmo.ekairaliev.model.Seal;
import ru.itmo.ekairaliev.service.CustodyService;
import ru.itmo.ekairaliev.service.SampleService;
import ru.itmo.ekairaliev.service.SealService;
import ru.itmo.ekairaliev.validation.ValidationException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;

public final class CliApplication {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    .withLocale(Locale.ROOT)
                    .withZone(ZoneId.systemDefault());

    private final SampleService sampleService;
    private final SealService sealService;
    private final CustodyService custodyService;
    private final Scanner scanner = new Scanner(System.in);

    public CliApplication(
            SampleService sampleService,
            SealService sealService,
            CustodyService custodyService
    ) {
        this.sampleService = sampleService;
        this.sealService = sealService;
        this.custodyService = custodyService;
    }

    public void run() {
        System.out.println("Chain of Custody CLI");
        System.out.println("Введите help, чтобы увидеть список команд.");

        while (true) {
            System.out.print("> ");

            if (!scanner.hasNextLine()) {
                System.out.println("Ввод завершен.");
                return;
            }

            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            try {
                boolean shouldExit = execute(line);
                if (shouldExit) {
                    System.out.println("Работа завершена.");
                    return;
                }
            } catch (ValidationException | CommandException e) {
                System.out.println(e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Непредвиденная ошибка: " + e.getMessage());
            }
        }
    }

    private boolean execute(String line) {
        List<String> tokens = CommandTokenizer.tokenize(line);
        if (tokens.isEmpty()) {
            return false;
        }

        String command = tokens.get(0);
        List<String> args = tokens.subList(1, tokens.size());

        switch (command) {
            case "help" -> {
                ensureArgCount(command, args, 0);
                printHelp();
                return false;
            }
            case "exit" -> {
                ensureArgCount(command, args, 0);
                return true;
            }
            case "sample_add" -> {
                ensureArgCount(command, args, 0);
                handleSampleAdd();
                return false;
            }
            case "sample_list" -> {
                ensureArgCount(command, args, 0);
                handleSampleList();
                return false;
            }
            case "seal_add" -> {
                ensureArgCount(command, args, 1);
                handleSealAdd(parseId(args.get(0), "sample_id"));
                return false;
            }
            case "seal_show" -> {
                ensureArgCount(command, args, 1);
                handleSealShow(parseId(args.get(0), "seal_id"));
                return false;
            }
            case "seal_break" -> {
                ensureArgCount(command, args, 1);
                handleSealBreak(parseId(args.get(0), "seal_id"));
                return false;
            }
            case "cust_add" -> {
                ensureArgCount(command, args, 1);
                handleCustodyAdd(parseId(args.get(0), "sample_id"));
                return false;
            }
            case "cust_list" -> {
                handleCustodyList(args);
                return false;
            }
            case "cust_show" -> {
                ensureArgCount(command, args, 1);
                handleCustodyShow(parseId(args.get(0), "event_id"));
                return false;
            }
            case "sample_hold" -> {
                ensureArgCount(command, args, 1);
                handleSampleHold(parseId(args.get(0), "sample_id"));
                return false;
            }
            case "sample_release" -> {
                ensureArgCount(command, args, 1);
                handleSampleRelease(parseId(args.get(0), "sample_id"));
                return false;
            }
            case "cust_check" -> {
                ensureArgCount(command, args, 1);
                handleCustodyCheck(parseId(args.get(0), "sample_id"));
                return false;
            }
            case "cust_export" -> {
                ensureArgCount(command, args, 1);
                handleCustodyExport(parseId(args.get(0), "sample_id"));
                return false;
            }
            default -> throw new CommandException("Ошибка: неизвестная команда '" + command + "'");
        }
    }

    private void handleSampleAdd() {
        String name = prompt("Название sample");
        Sample sample = sampleService.add(name);
        System.out.println("OK sample_id=" + sample.getId());
    }

    private void handleSampleList() {
        List<Sample> samples = sampleService.getAll();
        if (samples.isEmpty()) {
            System.out.println("Список sample пуст.");
            return;
        }

        System.out.printf("%-6s %-30s %-12s %-17s%n", "ID", "Name", "Status", "UpdatedAt");
        for (Sample sample : samples) {
            System.out.printf(
                    "%-6d %-30s %-12s %-17s%n",
                    sample.getId(),
                    trimForTable(sample.getName(), 30),
                    sample.getHoldStatus(),
                    formatInstant(sample.getUpdatedAt())
            );
        }
    }

    private void handleSealAdd(long sampleId) {
        sampleService.getById(sampleId);
        String sealNumber = prompt("Номер пломбы");
        Seal seal = sealService.add(sampleId, sealNumber, "SYSTEM");
        System.out.println("OK seal_id=" + seal.getId());
    }

    private void handleSealShow(long sealId) {
        Seal seal = sealService.getById(sealId);
        System.out.println("Seal #" + seal.getId());
        System.out.println("sample_id: " + seal.getSampleId());
        System.out.println("sealNumber: " + seal.getSealNumber());
        System.out.println("status: " + seal.getStatus());
    }

    private void handleSealBreak(long sealId) {
        sealService.breakSeal(sealId);
        System.out.println("OK seal " + sealId + " is BROKEN");
    }

    private void handleCustodyAdd(long sampleId) {
        Sample sample = sampleService.getById(sampleId);
        if (sample.getHoldStatus() == SampleHoldStatus.ON_HOLD) {
            throw new ValidationException("Ошибка: sample с id=" + sampleId + " находится ON_HOLD, сначала выполните sample_release");
        }

        String fromUser = prompt("От кого (только имя)");
        String toUser = prompt("Кому (только имя)");
        String location = prompt("Место");
        String comment = prompt("Комментарий (можно пусто)");

        CustodyEvent event = custodyService.add(
                sampleId,
                fromUser,
                toUser,
                location,
                blankToNull(comment),
                "SYSTEM"
        );
        System.out.println("OK event_id=" + event.getId());
    }

    private void handleCustodyList(List<String> args) {
        if (args.size() != 1 && args.size() != 3) {
            throw new CommandException("Ошибка: неверное число аргументов для cust_list");
        }

        long sampleId = parseId(args.get(0), "sample_id");
        List<CustodyEvent> events;

        if (args.size() == 1) {
            events = custodyService.listBySample(sampleId);
        } else {
            if (!"--last".equals(args.get(1))) {
                throw new CommandException("Ошибка: неизвестная опция '" + args.get(1) + "'");
            }
            int last = parsePositiveInt(args.get(2), "N");
            events = custodyService.listBySampleLastN(sampleId, last);
        }

        if (events.isEmpty()) {
            System.out.println("Для sample " + sampleId + " пока нет custody events.");
            return;
        }

        System.out.printf("%-6s %-14s %-14s %-24s %-17s%n", "ID", "From", "To", "Location", "Time");
        for (CustodyEvent event : events) {
            System.out.printf(
                    "%-6d %-14s %-14s %-24s %-17s%n",
                    event.getId(),
                    trimForTable(event.getFromUser(), 14),
                    trimForTable(event.getToUser(), 14),
                    trimForTable(event.getLocation(), 24),
                    formatInstant(event.getTransferredAt())
            );
        }
    }

    private void handleCustodyShow(long eventId) {
        CustodyEvent event = custodyService.getById(eventId);
        System.out.println("CustodyEvent #" + event.getId());
        System.out.println("sample_id: " + event.getSampleId());
        System.out.println("from: " + event.getFromUser());
        System.out.println("to: " + event.getToUser());
        System.out.println("location: " + event.getLocation());
        System.out.println("comment: " + (event.getComment() == null ? "-" : event.getComment()));
    }

    private void handleSampleHold(long sampleId) {
        sampleService.hold(sampleId);
        System.out.println("OK sample " + sampleId + " is ON_HOLD");
    }

    private void handleSampleRelease(long sampleId) {
        sampleService.release(sampleId);
        System.out.println("OK sample " + sampleId + " is ACTIVE");
    }

    private void handleCustodyCheck(long sampleId) {
        sampleService.getById(sampleId);
        Optional<String> currentOwner = custodyService.currentOwner(sampleId);
        if (currentOwner.isEmpty()) {
            System.out.println("Warning: no custody events");
            return;
        }
        System.out.println("OK current owner: " + currentOwner.get());
    }

    private void handleCustodyExport(long sampleId) {
        Sample sample = sampleService.getById(sampleId);
        List<CustodyEvent> events = custodyService.listBySampleChronological(sampleId);

        System.out.println("Sample " + sample.getId());
        System.out.println("name: " + sample.getName());
        System.out.println("holdStatus: " + sample.getHoldStatus());

        if (events.isEmpty()) {
            System.out.println("custody chain: no custody events");
        } else {
            System.out.println("custody chain:");
            for (CustodyEvent event : events) {
                System.out.println(
                        formatInstant(event.getTransferredAt())
                                + " | " + event.getFromUser()
                                + " -> " + event.getToUser()
                                + " | " + event.getLocation()
                                + " | " + (event.getComment() == null ? "-" : event.getComment())
                );
            }
        }

        System.out.println("Sample " + sampleId + " custody chain exported (text)");
    }

    private void printHelp() {
        System.out.println("help");
        System.out.println("exit");
        System.out.println("sample_add");
        System.out.println("sample_list");
        System.out.println("seal_add <sample_id>");
        System.out.println("seal_show <seal_id>");
        System.out.println("seal_break <seal_id>");
        System.out.println("cust_add <sample_id>");
        System.out.println("cust_list <sample_id> [--last N]");
        System.out.println("cust_show <event_id>");
        System.out.println("sample_hold <sample_id>");
        System.out.println("sample_release <sample_id>");
        System.out.println("cust_check <sample_id>");
        System.out.println("cust_export <sample_id>");
    }

    private void ensureArgCount(String command, List<String> args, int expected) {
        if (args.size() != expected) {
            throw new CommandException("Ошибка: неверное число аргументов для " + command);
        }
    }

    private long parseId(String rawValue, String fieldName) {
        try {
            return Long.parseLong(rawValue);
        } catch (NumberFormatException e) {
            throw new CommandException("Ошибка: " + fieldName + " должен быть числом");
        }
    }

    private int parsePositiveInt(String rawValue, String fieldName) {
        final int value;
        try {
            value = Integer.parseInt(rawValue);
        } catch (NumberFormatException e) {
            throw new CommandException("Ошибка: " + fieldName + " должно быть числом");
        }
        if (value <= 0) {
            throw new CommandException("Ошибка: " + fieldName + " должно быть > 0");
        }
        return value;
    }

    private String prompt(String label) {
        System.out.print(label + ": ");
        if (!scanner.hasNextLine()) {
            throw new CommandException("Ошибка: ввод неожиданно завершился");
        }
        return scanner.nextLine();
    }

    private String formatInstant(Instant instant) {
        return DATE_TIME_FORMATTER.format(instant);
    }

    private String trimForTable(String value, int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, Math.max(0, maxLength - 3)) + "...";
    }

    private String blankToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
