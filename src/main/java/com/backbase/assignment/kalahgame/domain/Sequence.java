package com.backbase.assignment.kalahgame.domain;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sequence")
@Getter
@Setter
@ToString
public class Sequence {

    private String name;

    private long seq;
}