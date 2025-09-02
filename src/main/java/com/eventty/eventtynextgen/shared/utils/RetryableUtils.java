package com.eventty.eventtynextgen.shared.utils;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class RetryableUtils {

    public static <T> T executeWithRetry(Supplier<T> supplier, int maxRetries, long baseDelayMs, long maxDelayMs) throws InterruptedException {
        Exception[] exceptions = new Exception[maxRetries];

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                return supplier.get();
            } catch (Exception e) {
                exceptions[attempt - 1] = e;

                if (attempt == maxRetries) {
                    break;
                }

                long expDelay = baseDelayMs << (attempt - 1);
                long cappedDelay = Math.min(expDelay, maxDelayMs);

                long jitterDelay = ThreadLocalRandom.current().nextLong(cappedDelay + 1);

                log.warn("Retry attempt {} failed: {}. Backing off {} ms (cap {} ms).",
                    attempt, e.getMessage(), jitterDelay, maxDelayMs);

                Thread.sleep(jitterDelay);
            }
        }

        String errorMsg = errorMessageBuild(exceptions, maxRetries);

        throw new RuntimeException(errorMsg);
    }

    private static String errorMessageBuild(Exception[] exceptions, int maxRetries) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Failed to execute after ").append(maxRetries).append(" attempts. ");
        errorMessage.append("Exception details:\n");

        for (int i = 0; i < maxRetries; i++) {
            if (exceptions[i] != null) {
                errorMessage.append("Attempt ").append(i + 1).append(": ")
                    .append(exceptions[i].getClass().getSimpleName())
                    .append(" - ").append(exceptions[i].getMessage());
                if (i < maxRetries - 1) {
                    errorMessage.append("\n");
                }
            }
        }

        return errorMessage.toString();
    }
}
