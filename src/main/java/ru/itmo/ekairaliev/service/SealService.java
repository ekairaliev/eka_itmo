package ru.itmo.ekairaliev.service;

import ru.itmo.ekairaliev.model.Seal;
import ru.itmo.ekairaliev.model.SealStatus;
import ru.itmo.ekairaliev.validation.SealValidator;
import ru.itmo.ekairaliev.validation.ValidationException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SealService {
    private final Map<Long, Seal> seals = new LinkedHashMap<>();
    private long nextId = 1;

    private final SampleService sampleService;

    public SealService(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    public Seal add(long sampleId, String sealNumber, String ownerUsername) {
        SealValidator.validateForCreate(sampleId, sealNumber, ownerUsername);

        if (!sampleService.exists(sampleId)) {
            throw new ValidationException("Ошибка: sample с id=" + sampleId + " не найден");
        }

        long id = nextId++;
        Instant now = Instant.now();

        Seal seal = new Seal(
                id,
                sampleId,
                SealStatus.ACTIVE,
                sealNumber.trim(),
                ownerUsername.trim(),
                now,
                now
        );

        SealValidator.validateEntity(seal);
        seals.put(id, seal);
        return seal;
    }

    public Seal getById(long id) {
        Seal seal = seals.get(id);
        if (seal == null) {
            throw new ValidationException("Ошибка: seal с id=" + id + " не найден");
        }
        return seal;
    }

    public List<Seal> getAll() {
        return new ArrayList<>(seals.values());
    }

    public Seal breakSeal(long id) {
        Seal seal = getById(id);
        if (seal.getStatus() == SealStatus.BROKEN) {
            throw new ValidationException("Ошибка: пломба уже BROKEN");
        }
        seal.setStatus(SealStatus.BROKEN);
        seal.touch();
        SealValidator.validateEntity(seal);
        return seal;
    }

    public List<Seal> listBySample(long sampleId) {
        List<Seal> result = new ArrayList<>();
        for (Seal seal : seals.values()) {
            if (seal.getSampleId() == sampleId) {
                result.add(seal);
            }
        }
        return result;
    }
}
