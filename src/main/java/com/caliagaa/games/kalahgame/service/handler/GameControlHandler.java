package com.caliagaa.games.kalahgame.service.handler;

import com.caliagaa.games.kalahgame.configuration.GameConfiguration;
import com.caliagaa.games.kalahgame.domain.GameInternalStatus;
import com.caliagaa.games.kalahgame.domain.GameStatus;
import com.caliagaa.games.kalahgame.domain.Pit;
import com.caliagaa.games.kalahgame.domain.PitType;
import com.caliagaa.games.kalahgame.domain.PlayerTurn;
import com.caliagaa.games.kalahgame.repository.GameStatusRepository;
import com.caliagaa.games.kalahgame.repository.PlayerTurnRepository;
import com.caliagaa.games.kalahgame.util.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class GameControlHandler {


    @Autowired
    private GameStatusRepository gameStatusRepository;

    @Autowired
    private PlayerTurnRepository playerTurnRepository;

    @Autowired
    private GameConfiguration gameConfiguration;

    /**
     * Entry point to handle stone movements
     *
     * @param gameId Game id
     * @param pitId  Pit number
     * @return A new game status
     */
    public GameStatus handleMovement(long gameId, int pitId) {
        log.info("Game id: " + gameId + ", move stones from pit " + pitId);
        Optional<GameStatus> currentGameStatus = getCurrentGameStatus(gameId);
        return currentGameStatus.map(c -> {
            log.info("Current game status: " + c.toString());
            if (!isGameOver(c)) {
                GameStatus newGameStatus = moveStones(c, pitId);
                gameStatusRepository.save(newGameStatus);
                return newGameStatus;
            } else {
                return GameStatus.overBuilder().isOver(true).build();
            }
        }).orElse(GameStatus.defaultBuilder().build());
    }

    private Optional<GameStatus> getCurrentGameStatus(long gameId) {
        return Optional.ofNullable(gameStatusRepository.findTop1ByGameIdOrderByTimestampDesc(gameId));
    }

    /**
     * Wrapper method to move the stones
     *
     * @param status Current status of the game
     * @param pitId  Pit number
     * @return A new game status
     */
    private GameStatus moveStones(GameStatus status, int pitId) {
        GameInternalStatus gameInternalStatus = GameInternalStatus.builder().gameStatus(status).build();
        return GameStatus.builder().gameInternalStatus(sowStonesCounterClockwise(gameInternalStatus, pitId)).build();
    }

    /**
     * Proceed to sow the stones in the rest of the pits , counter clockwise mmode
     *
     * @param gameInternalStatus Internal status of the game
     * @param pitId              Number of the pit
     * @return A modified internal status
     */
    private GameInternalStatus sowStonesCounterClockwise(GameInternalStatus gameInternalStatus, int pitId) {
        log.info("Sowing stones in pits, starting from pitId: " + pitId);

        int playerInTurn = getPlayerTurn(gameInternalStatus.getGameId());
        Node pitNode = gameInternalStatus.getPitCircularLinkedList().playerPlaysOnPit(playerInTurn, pitId);
        if (null != pitNode) {
            int stones = pitNode.getPit().getStones();
            pitNode.getPit().setStones(0);
            while (stones > 0) {
                Node nextNode = pitNode.getNextNode();
                if (!isInvalidPitToSow(nextNode.getPit(), playerInTurn)) {
                    int nextStones = nextNode.getPit().getStones() + 1;
                    pitNode.getNextNode().getPit().setStones(nextStones);
                    pitNode = nextNode;
                    stones--;
                } else {
                    //Skipe opponent's Kalah
                    pitNode = nextNode;
                }
            }
            savePlayerTurn(gameInternalStatus.getGameId(), playerInTurn, pitNode);
            int stonesInCurrentPit = getStonesFromPitIfWasEmtpty(pitNode);
            addStonesToKalah(gameInternalStatus, stonesInCurrentPit, playerInTurn);
        }
        return gameInternalStatus;
    }


    /**
     * Add stones to specified Kalah
     *
     * @param gameInternalStatus Internal representation of the game
     * @param stonesToAdd        Number of stones to add
     * @param playerInTurn       Playaer id
     */
    private void addStonesToKalah(GameInternalStatus gameInternalStatus, int stonesToAdd, int playerInTurn) {
        Node kalah = findPlayerKalah(gameInternalStatus, playerInTurn);
        int currentStones = kalah.getPit().getStones();
        kalah.getPit().setStones(currentStones + stonesToAdd);
    }


    /**
     * Find kalah by player if
     *
     * @param gameInternalStatus Game representation
     * @param playerInTurn       Current player turn
     * @return Node containing Kalah for the indicated player
     */
    private Node findPlayerKalah(GameInternalStatus gameInternalStatus, int playerInTurn) {
        Node root = gameInternalStatus.getPitCircularLinkedList().getRoot();
        Node current = root;
        do {
            if (isKalah(current) && current.getPit().getPlayer() == playerInTurn) {
                return current;
            }
            current = current.getNextNode();
        } while (current != root);

        //This should never happen
        return root;
    }


    /**
     * Check if landing pit from same player has one stone, after finish sowing
     *
     * @param currentNode Current node when sowing finished
     * @return Number of Stones
     */
    private int getStonesFromPitIfWasEmtpty(Node currentNode) {
        int stones = 0;
        if (currentNode.getPit().getStones() == 1 && !isKalah(currentNode)) {
            int pitId = currentNode.getPit().getPitId();
            currentNode.getPit().setStones(0);
            int movements = getMovementsFromPit(pitId);
            while (movements > 0) {
                currentNode = currentNode.getNextNode();
                movements--;
            }
            stones = currentNode.getPit().getStones() + 1;
            currentNode.getPit().setStones(0);
        }
        return stones;
    }


    /**
     * How many movements from Pit to mirror pit (opposite player)
     *
     * @param pitId Pit id
     * @return Number of movement to get to opponent's pit
     */
    private int getMovementsFromPit(int pitId) {
        int totalPits = gameConfiguration.getNumberOfPits() * 2 + 2;
        int middle = gameConfiguration.getNumberOfPits() + 1;
        if (pitId < middle) {
            return totalPits - 2 * pitId;
        } else {
            return (totalPits - pitId) * 2;
        }
    }

    /**
     * Is the node a Kalah?
     *
     * @param currentNode Node to analyze
     * @return true if it is a Kalah
     */
    private boolean isKalah(Node currentNode) {
        return currentNode.getPit().getType().equals(PitType.KALAH);
    }


    /**
     * Save player turn
     *
     * @param gameId Game Id
     * @param node   Current Pit
     */
    private void savePlayerTurn(long gameId, int recentPlayer, Node node) {
        int playerInLandingPit = node.getPit().getPlayer();
        int newTurn = (recentPlayer != playerInLandingPit) ? playerInLandingPit : recentPlayer;
        log.info("Saving player turn : " + newTurn);
        PlayerTurn turn = PlayerTurn.builder()
                .gameId(gameId)
                .player(newTurn)
                .timestamp(new Date())
                .build();
        playerTurnRepository.save(turn);
    }

    /**
     * Check if a stone can be placed in selected pit
     *
     * @param pit    Actual pit to sow
     * @param player Player Id
     * @return True if is not another's player Kalah
     */
    private boolean isInvalidPitToSow(Pit pit, int player) {
        return (pit.getPlayer() != player && pit.getType().equals(PitType.KALAH));
    }


    /**
     * Determine player's turn
     *
     * @param gameId Game Id
     * @return true if it's selected player's turn
     */
    private int getPlayerTurn(long gameId) {
        PlayerTurn turn = playerTurnRepository.findTop1ByGameIdOrderByTimestampDesc(gameId);
        return turn.getPlayer();
    }

    private int getMiddleIndex(GameStatus status) {
        return status.getStatus().size() / 2;
    }

    private int getLastIndex(GameStatus status) {
        return status.getStatus().size() - 1;
    }

    /**
     * Check if game is over (all player pits are empty)
     *
     * @param status Current game status
     * @return True if any player's pit  sum is zero
     */
    private boolean isGameOver(GameStatus status) {
        int middle = getMiddleIndex(status);
        int last = getLastIndex(status);

        int firstSum = status.getStatus().entrySet().stream().filter(f -> f.getKey() < middle).map(Map.Entry::getValue).mapToInt(i -> i).sum();
        int secondSum = status.getStatus().entrySet().stream().filter(f -> f.getKey() > middle && f.getKey() <= last).map(Map.Entry::getValue).mapToInt(i -> i).sum();

        if (firstSum == 0 || secondSum == 0) {
            log.info("Game is over!");
            return true;
        }
        return false;
    }


}
