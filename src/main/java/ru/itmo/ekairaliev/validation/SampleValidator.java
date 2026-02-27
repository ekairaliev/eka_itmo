package ru.itmo.ekairaliev.validation;

import ru.itmo.ekairaliev.model.Sample;
import ru.itmo.ekairaliev.model.SampleHoldStatus;

import static ru.itmo.ekairaliev.validation.TextRules.*;

public final class SampleValidator {
    private SampleValidator() {}

    public static void validateForCreate(String name) {
        name = notBlank(name, "name");
        maxLen(name, 64, "name");
    }

    public static void validateEntity(Sample s) {
        if (s == null) throw new ValidationException("Ошибка: sample=null");
        if (s.getId() <= 0) throw new ValidationException("Ошибка: id должен быть > 0");

        String n = notBlank(s.getName(), "name");
        maxLen(n, 64, "name");

        if (s.getHoldStatus() == null) throw new ValidationException("Ошибка: holdStatus обязателен");
        if (s.getHoldStatus() != SampleHoldStatus.ACTIVE && s.getHoldStatus() != SampleHoldStatus.ON_HOLD) {
            throw new ValidationException("Ошибка: неверный holdStatus");
        }

        if (s.getCreatedAt() == null) throw new ValidationException("Ошибка: createdAt обязателен");
        if (s.getUpdatedAt() == null) throw new ValidationException("Ошибка: updatedAt обязателен");
    }
}