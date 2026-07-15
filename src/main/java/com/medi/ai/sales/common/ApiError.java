package com.medi.ai.sales.common;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        int status,
        String message,
        List<String> errors
) {
}

