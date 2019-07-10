package com.backbase.assignment.kalahgame.domain;

import com.backbase.assignment.kalahgame.util.GameStatusSerializer;
import com.backbase.assignment.kalahgame.util.PitCircularLinkedList;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.backbase.assignment.kalahgame.util.PlayerEnum.PLAYER_ONE;
import static com.backbase.assignment.kalahgame.util.PlayerEnum.PLAYER_TWO;

@Getter
@NoArgsConstructor
public class GameInternalStatus {

    private long gameId;

    @JsonSerialize(using = GameStatusSerializer.class)
    private PitCircularLinkedList pitCircularLinkedList;

    @Builder
    public GameInternalStatus(GameStatus gameStatus) {
        this.gameId = gameStatus.getGameId();
        this.pitCircularLinkedList = getGameInternalStatus(gameStatus);
    }

    @Builder(builderMethodName = "allBuilder", builderClassName = "allBuilder")
    public GameInternalStatus(long gameId, PitCircularLinkedList circularLinkedList) {
        this.gameId = gameId;
        this.pitCircularLinkedList = circularLinkedList;
    }

    public PitCircularLinkedList getGameInternalStatus(GameStatus gameStatus) {
        PitCircularLinkedList pitList = new PitCircularLinkedList();
        int boardSize = gameStatus.getStatus().size();
        gameStatus.getStatus()
                .forEach((key, value) -> pitList.addNodes(
                        Pit.builder()
                                .pitId(key)
                                .stones(value)
                                .type(key == boardSize || key == boardSize / 2 ? PitType.KALAH : PitType.PIT)
                                .player(key < boardSize / 2 + 1 ? PLAYER_ONE.getType() : PLAYER_TWO.getType())
                                .build()));
        return pitList;
    }
}
