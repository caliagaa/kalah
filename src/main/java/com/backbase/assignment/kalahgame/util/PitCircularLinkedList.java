package com.backbase.assignment.kalahgame.util;

import com.backbase.assignment.kalahgame.domain.Pit;
import com.backbase.assignment.kalahgame.domain.PitType;
import lombok.Getter;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;

/**
 * Circular linked list to represent Kalah's board to facilitate calculation
 */
@Getter
public class PitCircularLinkedList implements Serializable {


    @Transient
    private Node current;

    private Node root;

    private int size;

    public void addNodes(Pit pit) {
        Node node = new Node(pit);
        if (root == null) {
            root = node;
            root.setNextNode(root);
        } else {
            current = root;
            while (current.getNextNode() != root) {
                current = current.getNextNode();
            }
            current.setNextNode(node);
            node.setNextNode(root);
        }
        size++;
    }

    public Node playerPlaysOnPit(int player, int pitId) {
        Node aux = root;
        do {
            if ((aux.getPit().getType().equals(PitType.PIT)) && aux.getPit().getPitId() == pitId && aux.getPit().getPlayer() == player) {
                return aux;
            }
            aux = aux.getNextNode();
        }
        while (aux != root);
        return null;
    }

}
