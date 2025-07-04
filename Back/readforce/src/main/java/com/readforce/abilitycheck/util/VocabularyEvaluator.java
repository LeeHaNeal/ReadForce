package com.readforce.abilitycheck.util;

public class VocabularyEvaluator {
    public static int evaluate(int level, boolean correct) {
        return switch (level) {
            case 6 -> correct ? 8 : 4;
            case 8 -> correct ? 10 : 7;
            case 10 -> correct ? -10 : 9;
            case 9 -> correct ? -9 : -8;
            case 7 -> correct ? -7 : -6;
            case 4 -> correct ? 5 : 3;
            case 5 -> correct ? -5 : -4;
            case 3 -> correct ? 2 : -2;
            case 2 -> correct ? -2 : -1;
            default -> -1;
        };
    }
}

