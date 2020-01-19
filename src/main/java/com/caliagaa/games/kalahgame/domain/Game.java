package com.caliagaa.games.kalahgame.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@ToString
@Document(collection = "games")
public class Game {
    @Id
    private long id;

    @NotNull
    private String uri;


    @Builder
    public Game(long id, String uri) {
        this.id = id;
        this.uri = uri;
    }
}
