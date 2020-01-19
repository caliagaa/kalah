package com.caliagaa.games.kalahgame.util;

import com.caliagaa.games.kalahgame.domain.GameInternalStatus;
import com.caliagaa.games.kalahgame.domain.GameStatus;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Custom JSON serializer to transform Map to PitCircularLinkedList
 */
public class GameStatusSerializer extends JsonSerializer<GameInternalStatus> {

    private static final String GAME_ID_FIELD = "gameId";
    private static final String STATUS_FIELD = "status";

    @Override
    public void serialize(GameInternalStatus value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        GameStatus status = GameStatus.builder()
                .gameInternalStatus(value)
                .build();
        gen.writeStartObject();
        gen.writeNumberField(GAME_ID_FIELD, status.getGameId());
        gen.writeObjectField(STATUS_FIELD, status.getStatus());
        gen.writeEndObject();
    }
}
