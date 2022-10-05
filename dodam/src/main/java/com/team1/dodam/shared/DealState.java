package com.team1.dodam.shared;

import lombok.Getter;

public enum DealState {

    ONGOING("거래중"),
    COMPLETED("거래완료");

    @Getter
    private final String description;

    DealState(String description) {
        this.description = description;
    }
}

