package com.nexa.platform.shared.interfaces.rest.resources;

public record ReferenceOptionResource(Long id, String code, String label, String parentCode, boolean active) { }
