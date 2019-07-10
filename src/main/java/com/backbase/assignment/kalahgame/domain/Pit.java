package com.backbase.assignment.kalahgame.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Pit {

    private int pitId;

    private int stones;

    private PitType type;

    private int player;

    @Builder
    public Pit(int pitId, int stones, PitType type, int player) {
        this.pitId = pitId;
        this.stones = stones;
        this.type = type;
        this.player = player;
    }

}
