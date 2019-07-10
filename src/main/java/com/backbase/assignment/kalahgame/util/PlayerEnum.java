package com.backbase.assignment.kalahgame.util;

import lombok.Getter;

@Getter
public enum PlayerEnum {

    PLAYER_ONE(1),

    PLAYER_TWO(2);

    int type;

    PlayerEnum(int type) {
        this.type = type;
    }
}
