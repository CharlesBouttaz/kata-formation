package com.equipement;

public class JTAUtils {
    public static void commit() {
        throw new RuntimeException("Not allowed during tests");
    }

    public static void rollback() {
        throw new RuntimeException("Not allowed during tests");
    }

    public static void initialiserTransaction(EnteteTechnique pEnteteTechnique) {
        throw new RuntimeException("Not allowed during tests");
    }
}
