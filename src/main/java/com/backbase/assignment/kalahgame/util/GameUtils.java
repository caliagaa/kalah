package com.backbase.assignment.kalahgame.util;

import com.backbase.assignment.kalahgame.domain.GameStatus;
import com.backbase.assignment.kalahgame.domain.Pit;
import com.backbase.assignment.kalahgame.domain.PitType;
import com.backbase.assignment.kalahgame.domain.RawGame;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;

@Slf4j
public class GameUtils {

    public static final String GAME_SEQUENCE_ID = "game";

    public static final String URI = "http://localhost:8080/games/";

    public static final int PLAYER_ONE = 1;

    public static final int PLAYER_TWO = 2;

    public static int getStones(int pitId, int totalPits, int numberOfStones) {
        if (pitId == totalPits || pitId == totalPits / 2) {
            return 0;
        } else {
            return numberOfStones;
        }
    }

    public static int getPlayer(int pitId, int totalPits) {
        if (pitId >= totalPits / 2 + 1) {
            return PLAYER_TWO;
        } else {
            return PLAYER_ONE;
        }
    }

    public static PitType getTypeByPitId(int pidId, int totalPits) {
        if (pidId == totalPits || pidId == totalPits / 2) {
            return PitType.KALAH;
        } else {
            return PitType.PIT;
        }
    }


    public static GameStatus convertToGameStatus(RawGame rawGame) {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("custom", Version.unknownVersion());
        module.addSerializer(RawGame.class, new PitSerializer());
        mapper.registerModule(module);
        try {
            String rawStatus = mapper.writeValueAsString(rawGame);
            GameStatus status = mapper.readValue(rawStatus, GameStatus.class);
            status.setTimestamp(new Date());
            return status;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return GameStatus.builder().build();
    }

    public static RawGame convertToRawGame(GameStatus gameStatus){
        RawGame rawGame  =new RawGame();
        CircularLinkedList<Pit> pitList = new CircularLinkedList<>();
        int total = gameStatus.getStatus().size();
        gameStatus.getStatus().forEach((key, value) -> {
            Pit p = new Pit(key, value, getTypeByPitId(key, total), getPlayer(key, total));
            pitList.addNodes(p);
        });
        rawGame.setGameId(gameStatus.getGameId());
        rawGame.setCircularLinkedList(pitList);
        return rawGame;
    }


}
