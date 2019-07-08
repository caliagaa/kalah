package com.backbase.assignment.kalahgame.service.handler;

import com.backbase.assignment.kalahgame.configuration.GameConfiguration;
import com.backbase.assignment.kalahgame.domain.Game;
import com.backbase.assignment.kalahgame.domain.GameStatus;
import com.backbase.assignment.kalahgame.domain.Pit;
import com.backbase.assignment.kalahgame.domain.PitType;
import com.backbase.assignment.kalahgame.domain.PlayerTurn;
import com.backbase.assignment.kalahgame.domain.RawGame;
import com.backbase.assignment.kalahgame.repository.GameRepository;
import com.backbase.assignment.kalahgame.repository.GameStatusRepository;
import com.backbase.assignment.kalahgame.repository.PlayerTurnRepository;
import com.backbase.assignment.kalahgame.util.CircularLinkedList;
import com.backbase.assignment.kalahgame.util.GameUtils;
import com.backbase.assignment.kalahgame.util.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static com.backbase.assignment.kalahgame.util.GameUtils.convertToRawGame;

@Component
@Slf4j
public class GameControlHandler {

    @Autowired
    private GameRepository gameRepository;

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
     * @param pitId Pit number
     * @return A new game status
     */
    public GameStatus handleMovement(long gameId, int pitId) {
        log.info("Game id: " + gameId + ", move stones from pit " + pitId);
        Game game = gameRepository.findGameById(gameId);
        if ( null != game ) {
            GameStatus currentGameStatus = getCurrentGameStatus(game);
            log.info("Current game status: " + currentGameStatus.toString());

            int middle = getMiddleIndex(currentGameStatus);
            int last = getLastIndex(currentGameStatus);
            if (!isGameOver(currentGameStatus, middle, last)) {
                GameStatus newGameStatus = moveStones(currentGameStatus, pitId);
                gameStatusRepository.save(newGameStatus);
                return newGameStatus;
            } else {
                game.setOver(true);
            }
        }
        return GameStatus.builder().build();
    }

    /**
     * Wrapper method to move the stones
     *
     * @param status Current status of the game
     * @param pitId Pit number
     * @return A new game status
     */
    private GameStatus moveStones(GameStatus status, int pitId) {
        RawGame rawGame = convertToRawGame(status);
        GameStatus newStatus = GameUtils.convertToGameStatus(sowStonesCounterClockwise(rawGame, pitId));
        newStatus.setTimestamp(new Date());
        return newStatus;
    }

    /**
     * Proceed to sow the stones in the rest of the pits , counter clockwise mmode
     *
     * @param rawGame Internal status of the game
     * @param pitId Number of the pit
     * @return A modified internal status
     */
    private RawGame sowStonesCounterClockwise(RawGame rawGame, int pitId) {
        log.info("Sowing stones in pits, starting from pitId: " + pitId);
        CircularLinkedList<Pit> originalPitList = rawGame.getCircularLinkedList();
        int playerInTurn = getPlayerTurn(rawGame.getGameId());
        Optional<Node<Pit>> pitNode = Optional.ofNullable(getPitById(originalPitList, pitId, playerInTurn));
        pitNode.ifPresent(n -> {
            int stones = n.getItem().getStones();
            n.getItem().setStones(0);
            while (stones > 0) {
                Node<Pit> nextNode = n.getNextNode();
                if (!isInvalidPitToSow(nextNode.getItem(), playerInTurn)) {
                    int nextStones = nextNode.getItem().getStones() + 1;
                    n.getNextNode().getItem().setStones(nextStones);
                    n = nextNode;
                    stones--;
                } else {
                    //Skipe opponent's Kalah
                    n = nextNode;
                }
            }
            savePlayerTurn(rawGame.getGameId(), playerInTurn, n);
            int stonesInCurrentPit = getStonesFromPitIfWasEmtpty(n);
            addStonesToKalah(rawGame,stonesInCurrentPit,playerInTurn);
        });

        return rawGame;
    }

    /**
     * Add stones to specified Kalah
     * @param rawGame Internal representation of the game
     * @param stonesToAdd Number of stones to add
     * @param playerInTurn Playaer id
     */
    private void addStonesToKalah(RawGame rawGame, int stonesToAdd, int playerInTurn){
       Node<Pit> kalah = findPlayerKalah(rawGame,playerInTurn);
       int currentStones = kalah.getItem().getStones();
       kalah.getItem().setStones(currentStones + stonesToAdd);
    }


    /**
     * Find kalah by player if
     * @param rawGame Game representation
     * @param playerInTurn Current player turn
     * @return Node containing Kalah for the indicated player
     */
    private Node<Pit> findPlayerKalah(RawGame rawGame,  int playerInTurn){
        Node<Pit> root = rawGame.getCircularLinkedList().getRoot();
        Node<Pit> current = root;
        do {
            if ( isKalah(current) && current.getItem().getPlayer()== playerInTurn){
                return current;
            }
            current = current.getNextNode();
        } while ( current != root);

        //This should never happen
        return root;
    }


    /**
     * Check if landing pit from same player has one stone, after finish sowing
     * @param currentNode Current node when sowing finished
     * @return Number of Stones
     */
    private int getStonesFromPitIfWasEmtpty(Node<Pit>  currentNode){
        int stones = 0;
        if ( currentNode.getItem().getStones() == 1 && !isKalah(currentNode) ) {
            int pitId = currentNode.getItem().getPitId();
            currentNode.getItem().setStones(0);
            int movements = getMovementsFromPit(pitId);
            while (  movements > 0) {
                currentNode = currentNode.getNextNode();
                movements--;
            }
            stones = currentNode.getItem().getStones() + 1;
            currentNode.getItem().setStones(0);
        }
        return stones;
    }


    /**
     * How many movements from Pit to mirror pit (opposite player)
     * @param pitId Pit id
     * @return Number of movement to get to opponent's pit
     */
    private int getMovementsFromPit(int pitId){
        int totalPits = gameConfiguration.getNumberOfPits() *2 + 2;
        int middle = gameConfiguration.getNumberOfPits() + 1;
        if ( pitId < middle ){
            return totalPits - 2*pitId;
        } else{
            return (totalPits - pitId)*2;
        }
    }

    /** Is the node a Kalah?
     *
     * @param currentNode Node to analyze
     * @return true if it is a Kalah
     */
    private boolean isKalah(Node<Pit>  currentNode){
        return currentNode.getItem().getType().equals(PitType.KALAH);
    }



    /**
     * Save player turn
     *
     * @param gameId Game Id
     * @param node Current Pit
     */
    private void savePlayerTurn(long gameId, int playerInTurn, Node<Pit> node){
        log.info("Saving player turn : " + playerInTurn);
        int playerInNode = node.getItem().getPlayer();
        int newTurn = ( playerInTurn != playerInNode )? playerInTurn:playerInNode;
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
     * @param pit Actual pit to sow
     * @param player Player Id
     * @return True if is not another's player Kalah
     */
    private boolean isInvalidPitToSow(Pit pit, int player) {
        return ( pit.getPlayer() != player && pit.getType().equals(PitType.KALAH) );
    }

    /**
     * Locate pit according to Pit Number
     *
     * @param pitList Internal representation of pits linked to each other
     * @param pitId Pit Number
     * @return Node with Pit matched
     */
    private Node<Pit> getPitById(CircularLinkedList<Pit> pitList, int pitId, int player) {
        Node<Pit> root = pitList.getRoot();
        Node<Pit> current = root;
        do {
            if (current.getItem().getPitId() == pitId && current.getItem().getPlayer() == player) {
                return current;
            }
            current = current.getNextNode();
        }
        while (current != root);

        //This has no side effects, status remains the same
        return null;
    }

    /**
     * Check if game is over (all player pits are empty)
     *
     * @param status Current game status
     * @param middle Middle position
     * @param last Last position
     * @return True if any player's pit  sum is zero
     */
    private boolean isGameOver(GameStatus status, int middle, int last) {
        int firstSum = status.getStatus().entrySet().stream().filter(f -> f.getKey() < middle).map(Map.Entry::getValue).mapToInt(i -> i).sum();
        int secondSum = status.getStatus().entrySet().stream().filter(f -> f.getKey() > middle && f.getKey() <= last).map(Map.Entry::getValue).mapToInt(i -> i).sum();

        if (firstSum == 0 || secondSum == 0) {
            log.info("Game is over!");
            return true;
        }
        return false;
    }


    /**
     * Determine player's turn
     *
     * @param gameId  Game Id
     * @return true if it's selected player's turn
     */
    private int getPlayerTurn(long gameId){
        PlayerTurn turn = playerTurnRepository.findTop1ByGameIdOrderByTimestampDesc(gameId);
        return turn.getPlayer();
    }

    private int getMiddleIndex(GameStatus status) {
        return status.getStatus().size() / 2;
    }

    private int getLastIndex(GameStatus status) {
        return status.getStatus().size() - 1;
    }

    private GameStatus getCurrentGameStatus(Game game) {
        return gameStatusRepository.findTop1ByGameIdOrderByTimestampDesc(game.getId());
    }

}
