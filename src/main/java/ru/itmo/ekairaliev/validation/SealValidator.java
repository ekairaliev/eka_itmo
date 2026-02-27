package ru.itmo.ekairaliev.validation;

import ru.itmo.ekairaliev.model.Seal;

import static ru.itmo.ekairaliev.validation.TextRules.*;

public final class SealValidator {
    private SealValidator() {}

    public static void validateForCreate(long sampleId, String sealNumber, String ownerUsername) {
        if (sampleId <= 0) throw new ValidationException("Ошибка: sampleId должен быть > 0");

        sealNumber = notBlank(sealNumber, "seal_number");
        maxLen(sealNumber, 64, "seal_number");

        ownerUsername = notBlank(ownerUsername, "owner_username");
        maxLen(ownerUsername, 64, "owner_username");
    }

    public static void validateEntity(Seal s) {
        if (s == null) throw new ValidationException("Ошибка: seal=null");
        if (s.getId() <= 0) throw new ValidationException("Ошибка: id должен быть > 0");
        if (s.getSampleId() <= 0) throw new ValidationException("Ошибка: sampleId должен быть > 0");

        String num = notBlank(s.getSealNumber(), "seal_number");
        maxLen(num, 64, "seal_number");

        String owner = notBlank(s.getOwnerUsername(), "owner_username");
        maxLen(owner, 64, "owner_username");

        if (s.getStatus() == null) throw new ValidationException("Ошибка: status обязателен");
        if (s.getCreatedAt() == null) throw new ValidationException("Ошибка: createdAt обязателен");
        if (s.getUpdatedAt() == null) throw new ValidationException("Ошибка: updatedAt обязателен");
    }
}