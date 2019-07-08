package com.backbase.assignment.kalahgame.util;

import lombok.Getter;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;

/**
 * Circular linked list to represent Kalah's board to facilitate calculation
 * @param <T>
 */
@Getter
public class CircularLinkedList<T> implements Serializable {


    @Transient
    private Node<T> current;

    private Node<T> root;

    private int size;

    public void addNodes(T pit ){

        Node<T> node = new Node<>(pit);
        if(root == null){
            root = node;
            root.setNextNode(root);
        } else {
            current = root;
            while(current.getNextNode()!=root){
                current = current.getNextNode();
            }
            current.setNextNode(node);
            node.setNextNode(root);
        }
        size++;
    }

}
