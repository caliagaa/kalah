package com.backbase.assignment.kalahgame.domain;

import com.backbase.assignment.kalahgame.util.CircularLinkedList;
import com.backbase.assignment.kalahgame.util.PitSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RawGame {

    private long gameId;

    @JsonSerialize(using = PitSerializer.class)
    private CircularLinkedList<Pit> circularLinkedList;

    @Builder
    public RawGame(long gameId, CircularLinkedList<Pit> circularLinkedList) {
        this.gameId = gameId;
        this.circularLinkedList = circularLinkedList;
    }

}
