package com.backbase.assignment.kalahgame.util;

import com.backbase.assignment.kalahgame.domain.Pit;
import com.backbase.assignment.kalahgame.domain.RawGame;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PitSerializer extends JsonSerializer<RawGame> {

    private static final String GAME_ID_FIELD = "gameId";
    private static final String STATUS_FIELD = "status";

    @Override
    public void serialize(RawGame value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField(GAME_ID_FIELD, value.getGameId());
        Map<Integer, Integer> status = new HashMap<>();

        Node<Pit> root = value.getCircularLinkedList().getRoot();
        Node<Pit> current = root;
        do {
            int pitId= current.getItem().getPitId();
            int stones = current.getItem().getStones();
            status.put(pitId,stones);
            current = current.getNextNode();
        } while ( current != root);
        gen.writeObjectField(STATUS_FIELD, status);
        gen.writeEndObject();
    }
}
