package com.team1.dodam.shared;

import lombok.Getter;

public enum Category {
    DEVICE("디지털기기"),
    APPLIANCE("생활가전"),
    KITCHEN("생활/주방"),
    WOMEN("여성의류/잡화"),
    MEN("남성의류/잡화"),
    BEAUTY("뷰티/미용"),
    GAME("취미/게임"),
    BOOK("도서"),
    TICKET("티켓");

    @Getter
    private final String description;

    Category(String description) {
        this.description = description;
    }
}
