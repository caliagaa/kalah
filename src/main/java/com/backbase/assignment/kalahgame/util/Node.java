package com.backbase.assignment.kalahgame.util;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Container for Pits nodes
 *
 * @param <T>
 */
public class Node<T>  implements Serializable {

    static int noOfLinkedList = 0;

    @Getter
    private T item;

    @Getter
    @Setter
    private Node<T> nextNode;

    Node(T item){

        this.item = item;
        noOfLinkedList++;

    }
}