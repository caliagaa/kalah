package com.caliagaa.games.kalahgame.domain;


import lombok.Getter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sequence")
@Getter
@ToString
public class Sequence {

    private String name;

    private long seq;
}