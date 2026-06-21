package com.nexa.platform.shared.application.pagination;

import java.util.List;

public record PagedResult<T>(
    List<T> items,
    int page,
    int pageSize,
    int totalItems,
    int totalPages
) { }
