package com.kpolak.view;

import java.util.concurrent.atomic.AtomicInteger;

// TODO: Single instance of SimpleSequenceGenerator for each MainDisplay.
//  Handle case of loading frames - possibility of duplicated ids
public class SimpleSequenceGenerator {
    private static final AtomicInteger number = new AtomicInteger(0);

    public static String next() {
        return String.valueOf(number.incrementAndGet());
    }
}
