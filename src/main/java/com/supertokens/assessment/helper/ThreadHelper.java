package com.supertokens.assessment.helper;

import java.util.concurrent.TimeUnit;

public class ThreadHelper {

	public static void sleep(int ms) {
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
