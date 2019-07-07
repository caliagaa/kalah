package com.backbase.assignment.kalahgame.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(value = "playerTurns")
@Getter
@Setter
public class PlayerTurn {

    private long gameId;

    private int player;

    private Date timestamp;

    @Builder
    public PlayerTurn(long gameId, int player, Date timestamp) {
        this.gameId = gameId;
        this.player = player;
        this.timestamp = timestamp;
    }
}
