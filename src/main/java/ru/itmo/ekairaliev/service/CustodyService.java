package ru.itmo.ekairaliev.service;

import ru.itmo.ekairaliev.model.CustodyEvent;
import ru.itmo.ekairaliev.model.Sample;
import ru.itmo.ekairaliev.model.SampleHoldStatus;
import ru.itmo.ekairaliev.validation.CustodyEventValidator;
import ru.itmo.ekairaliev.validation.ValidationException;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class CustodyService {

    private final Map<Long, CustodyEvent> events = new LinkedHashMap<>();
    private long nextId = 1;

    private final SampleService sampleService;

    public CustodyService(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    public CustodyEvent add(long sampleId, String fromUser, String toUser, String location, String comment, String ownerUsername) {
        CustodyEventValidator.validateForCreate(sampleId, fromUser, toUser, location, comment);

        Sample sample = sampleService.getById(sampleId);
        if (sample.getHoldStatus() == SampleHoldStatus.ON_HOLD) {
            throw new ValidationException("Ошибка: sample с id=" + sampleId + " находится ON_HOLD, сначала выполните sample_release");
        }

        long id = nextId++;
        Instant now = Instant.now();

        CustodyEvent event = new CustodyEvent(
                id,
                sampleId,
                fromUser.trim(),
                toUser.trim(),
                location.trim(),
                comment == null ? null : comment.trim(),
                now,
                ownerUsername == null || ownerUsername.trim().isEmpty() ? "SYSTEM" : ownerUsername.trim(),
                now
        );

        CustodyEventValidator.validateEntity(event);
        events.put(id, event);
        return event;
    }

    public CustodyEvent getById(long id) {
        CustodyEvent event = events.get(id);
        if (event == null) {
            throw new ValidationException("Ошибка: custody_event с id=" + id + " не найден");
        }
        return event;
    }

    public List<CustodyEvent> listBySample(long sampleId) {
        if (!sampleService.exists(sampleId)) {
            throw new ValidationException("Ошибка: sample с id=" + sampleId + " не найден");
        }

        return events.values().stream()
                .filter(event -> event.getSampleId() == sampleId)
                .sorted(Comparator.comparing(CustodyEvent::getTransferredAt).reversed())
                .collect(Collectors.toList());
    }

    public List<CustodyEvent> listBySampleLastN(long sampleId, int n) {
        if (n <= 0) {
            throw new ValidationException("Ошибка: N должно быть > 0");
        }

        return listBySample(sampleId).stream()
                .limit(n)
                .collect(Collectors.toList());
    }

    public Optional<String> currentOwner(long sampleId) {
        List<CustodyEvent> list = listBySample(sampleId);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(list.get(0).getToUser());
    }

    public List<CustodyEvent> listBySampleChronological(long sampleId) {
        return listBySample(sampleId).stream()
                .sorted(Comparator.comparing(CustodyEvent::getTransferredAt))
                .collect(Collectors.toList());
    }
}
