package com.caliagaa.games.kalahgame.domain;

import com.caliagaa.games.kalahgame.util.Node;
import com.caliagaa.games.kalahgame.util.PitCircularLinkedList;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Document(value = "gameStatus")
@Getter
@ToString
public class GameStatus {

    private long gameId;

    @Field(value = "status")
    private Map<Integer, Integer> status;

    @JsonIgnore
    private Date timestamp;

    @JsonIgnore
    private boolean over;

    @Builder(builderMethodName = "overBuilder", builderClassName = "overBuilder")
    public GameStatus(boolean isOver) {
        this.over = isOver;
    }

    @Builder()
    public GameStatus(GameInternalStatus gameInternalStatus) {
        this.gameId = gameInternalStatus.getGameId();
        this.timestamp = new Date();
        this.status = convertCircularLinkedListToMap(gameInternalStatus.getPitCircularLinkedList());
    }

    @Builder(builderMethodName = "defaultBuilder", builderClassName = "defaultBuilder")
    public GameStatus() {
        this.gameId = 0L;
        this.status = new HashMap<>();
        this.timestamp = new Date();
    }

    private Map<Integer, Integer> convertCircularLinkedListToMap(PitCircularLinkedList circularLinkedList) {
        final Map<Integer, Integer> statusMap = new HashMap<>();
        final Node root = circularLinkedList.getRoot();
        Node current = root;
        do {
            int pitId = current.getPit().getPitId();
            int stones = current.getPit().getStones();
            statusMap.put(pitId, stones);
            current = current.getNextNode();
        } while (current != root);
        return statusMap;
    }
}

