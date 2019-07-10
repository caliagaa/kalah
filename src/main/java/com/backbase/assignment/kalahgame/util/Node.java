package com.backbase.assignment.kalahgame.util;


import com.backbase.assignment.kalahgame.domain.Pit;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Container for Pits nodes
 */

@Getter
public class Node implements Serializable {

    private Pit pit;

    @Setter
    private Node nextNode;

    public Node(Pit pit) {
        this.pit = pit;
    }
}