package ru.itmo.ekairaliev.cli;

import java.util.ArrayList;
import java.util.List;

public final class CommandTokenizer {
    private CommandTokenizer() {
    }

    public static List<String> tokenize(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        char quoteChar = '\0';

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (quoted) {
                if (ch == quoteChar) {
                    quoted = false;
                } else {
                    current.append(ch);
                }
                continue;
            }

            if (ch == '"' || ch == '\'') {
                quoted = true;
                quoteChar = ch;
                continue;
            }

            if (Character.isWhitespace(ch)) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
                continue;
            }

            current.append(ch);
        }

        if (quoted) {
            throw new CommandException("Ошибка: незакрытая кавычка в команде");
        }

        if (current.length() > 0) {
            tokens.add(current.toString());
        }
        return tokens;
    }
}
