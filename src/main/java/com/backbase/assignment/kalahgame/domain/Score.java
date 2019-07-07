package com.backbase.assignment.kalahgame.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Score {

    private int stoneCount;

    private int winnerPlayer;
}
