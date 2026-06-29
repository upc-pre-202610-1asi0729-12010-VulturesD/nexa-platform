package com.nexa.platform.shared.interfaces.rest.errors;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiError(OffsetDateTime timestamp, int status, String error, List<String> details) { }
