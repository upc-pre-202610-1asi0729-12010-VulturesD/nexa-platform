package com.nexa.platform.catalog.domain.model;

import jakarta.persistence.*;

@Embeddable
public class ColdChainRequirement {
    @Column(name = "min_celsius")
    private Integer minCelsius;
    @Column(name = "max_celsius")
    private Integer maxCelsius;
    @Column(name = "handling_notes", length = 240)
    private String handlingNotes;
    protected ColdChainRequirement() { }
    public ColdChainRequirement(Integer minCelsius, Integer maxCelsius, String handlingNotes) {
        this.minCelsius = minCelsius; this.maxCelsius = maxCelsius; this.handlingNotes = handlingNotes;
    }
    public Integer getMinCelsius() { return minCelsius; }
    public Integer getMaxCelsius() { return maxCelsius; }
    public String getHandlingNotes() { return handlingNotes; }
}
