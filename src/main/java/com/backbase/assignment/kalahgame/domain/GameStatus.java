package com.backbase.assignment.kalahgame.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

@Document(value = "gameStatus")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class GameStatus {

    private long gameId;

    private Map<Integer, Integer> status;

    private Date timestamp;

    @Builder
    public GameStatus(long gameId, Map<Integer, Integer> status, Date timestamp) {
        this.gameId = gameId;
        this.status = status;
        this.timestamp = timestamp;
    }
}

