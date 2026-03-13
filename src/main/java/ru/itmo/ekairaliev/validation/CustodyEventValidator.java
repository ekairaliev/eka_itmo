package ru.itmo.ekairaliev.validation;

import static ru.itmo.ekairaliev.validation.TextRules.*;

public final class CustodyEventValidator {
    private CustodyEventValidator() {}

    public static void validateForCreate(long sampleId, String fromUser, String toUser, String location, String comment) {
        if (sampleId <= 0) throw new ValidationException("Ошибка: sampleId должен быть > 0");

        fromUser = notBlank(fromUser, "from");
        maxLen(fromUser, 64, "from");

        toUser = notBlank(toUser, "to");
        maxLen(toUser, 64, "to");

        location = notBlank(location, "location");
        maxLen(location, 64, "location");

        comment = norm(comment);
        if (comment != null && !comment.isEmpty()) {
            maxLen(comment, 128, "comment");
        }
    }
}