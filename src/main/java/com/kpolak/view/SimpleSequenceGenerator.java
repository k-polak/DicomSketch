package com.kpolak.view;

import java.util.concurrent.atomic.AtomicInteger;

public class SimpleSequenceGenerator {
    private static final AtomicInteger number = new AtomicInteger(0);

    public static String next() {
        return String.valueOf(number.incrementAndGet());
    }
}
