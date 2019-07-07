package com.backbase.assignment.kalahgame.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "games")
public class Game {
    @Id
    private long id;

    private String uri;

    @JsonIgnore
    private boolean over;

    @Builder
    public Game(long id, String uri) {
        this.id = id;
        this.uri = uri;
    }
}
