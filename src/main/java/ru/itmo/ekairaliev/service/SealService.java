package ru.itmo.ekairaliev.service;

import ru.itmo.ekairaliev.model.Seal;
import ru.itmo.ekairaliev.model.SealStatus;
import ru.itmo.ekairaliev.validation.SealValidator;
import ru.itmo.ekairaliev.validation.ValidationException;

import java.time.Instant;
import java.util.*;

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
        Seal s = seals.get(id);
        if (s == null) throw new ValidationException("Ошибка: seal с id=" + id + " не найден");
        return s;
    }

    public List<Seal> getAll() {
        return new ArrayList<>(seals.values());
    }

    public void remove(long id) {
        if (seals.remove(id) == null) {
            throw new ValidationException("Ошибка: seal с id=" + id + " не найден");
        }
    }

    public Seal breakSeal(long id) {
        Seal s = getById(id);
        if (s.getStatus() == SealStatus.BROKEN) {
            throw new ValidationException("Ошибка: пломба уже BROKEN");
        }
        s.setStatus(SealStatus.BROKEN);
        s.touch();
        SealValidator.validateEntity(s);
        return s;
    }

    public Seal updateSealNumber(long id, String newSealNumber) {
        Seal s = getById(id);

        // update тоже должен валидироваться
        SealValidator.validateForCreate(s.getSampleId(), newSealNumber, s.getOwnerUsername());

        s.setSealNumber(newSealNumber.trim());
        s.touch();
        SealValidator.validateEntity(s);
        return s;
    }
}