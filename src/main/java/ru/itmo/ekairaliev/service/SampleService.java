package ru.itmo.ekairaliev.service;

import ru.itmo.ekairaliev.model.Sample;
import ru.itmo.ekairaliev.model.SampleHoldStatus;
import ru.itmo.ekairaliev.validation.SampleValidator;
import ru.itmo.ekairaliev.validation.ValidationException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SampleService {

    private final Map<Long, Sample> samples = new LinkedHashMap<>();
    private long nextId = 1;

    public Sample add(String name) {
        SampleValidator.validateForCreate(name);

        long id = nextId++;
        Instant now = Instant.now();

        Sample sample = new Sample(
                id,
                name.trim(),
                SampleHoldStatus.ACTIVE,
                now,
                now
        );

        SampleValidator.validateEntity(sample);
        samples.put(id, sample);
        return sample;
    }

    public Sample getById(long id) {
        Sample sample = samples.get(id);
        if (sample == null) {
            throw new ValidationException("Ошибка: sample с id=" + id + " не найден");
        }
        return sample;
    }

    public List<Sample> getAll() {
        return new ArrayList<>(samples.values());
    }

    public Sample hold(long id) {
        Sample sample = getById(id);
        sample.setHoldStatus(SampleHoldStatus.ON_HOLD);
        sample.touch();

        SampleValidator.validateEntity(sample);
        return sample;
    }

    public Sample release(long id) {
        Sample sample = getById(id);
        sample.setHoldStatus(SampleHoldStatus.ACTIVE);
        sample.touch();

        SampleValidator.validateEntity(sample);
        return sample;
    }

    public boolean exists(long id) {
        return samples.containsKey(id);
    }
}
