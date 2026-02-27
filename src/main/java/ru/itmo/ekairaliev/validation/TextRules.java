package ru.itmo.ekairaliev.validation;

public final class TextRules {
    private TextRules() {}

    public static String norm(String s) {
        return s == null ? null : s.trim();
    }

    public static String notBlank(String s, String field) {
        if (s == null || s.trim().isEmpty()) {
            throw new ValidationException("Ошибка: " + field + " не может быть пустым");
        }
        return s.trim();
    }

    public static String maxLen(String s, int max, String field) {
        if (s != null && s.length() > max) {
            throw new ValidationException("Ошибка: " + field + " слишком длинное (макс. " + max + ")");
        }
        return s;
    }
}