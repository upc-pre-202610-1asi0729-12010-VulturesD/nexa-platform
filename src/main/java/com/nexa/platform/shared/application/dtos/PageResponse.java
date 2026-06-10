package com.nexa.platform.shared.application.dtos;

import java.util.List;

public record PageResponse<T>(List<T> items, long total) {
    public static <T> PageResponse<T> of(List<T> items) {
        return new PageResponse<>(items, items.size());
    }
}
