package com.backbase.assignment.kalahgame.service.handler;

import com.backbase.assignment.kalahgame.configuration.GameConfiguration;
import com.backbase.assignment.kalahgame.domain.Game;
import com.backbase.assignment.kalahgame.domain.GameInternalStatus;
import com.backbase.assignment.kalahgame.domain.GameStatus;
import com.backbase.assignment.kalahgame.domain.Pit;
import com.backbase.assignment.kalahgame.domain.PitType;
import com.backbase.assignment.kalahgame.domain.PlayerTurn;
import com.backbase.assignment.kalahgame.repository.GameRepository;
import com.backbase.assignment.kalahgame.repository.GameStatusRepository;
import com.backbase.assignment.kalahgame.repository.PlayerTurnRepository;
import com.backbase.assignment.kalahgame.repository.SequenceRepository;
import com.backbase.assignment.kalahgame.util.PitCircularLinkedList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;
import java.util.stream.IntStream;

import static com.backbase.assignment.kalahgame.util.PlayerEnum.PLAYER_ONE;
import static com.backbase.assignment.kalahgame.util.PlayerEnum.PLAYER_TWO;

@Component
@Slf4j
public class GameCreationHandler {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private SequenceRepository sequenceRepository;

    @Autowired
    private GameStatusRepository gameStatusRepository;

    @Autowired
    private GameConfiguration gameConfiguration;

    @Autowired
    private PlayerTurnRepository playerTurnRepository;

    /**
     * Initialize game: obtain :
     * - New sequence number for game
     * - Persist new game with new sequence
     * - Create new board with the acmount of stones and empty kalah's
     * - Choose witch player starts
     *
     * @return a new instance of the game
     */
    public Game initializeGame() {
        Game game = createNewGame();

        createNewBoard(game.getId());

        assignTurnToPlayer(game.getId());

        return game;
    }

    /**
     * Get new id from sequence
     *
     * @return a new Id for a game
     */
    private long getGameId() {
        return sequenceRepository.getNextSequenceId(gameConfiguration.getGameSequenceName());
    }

    /**
     * Helper method to initialize pits and its stones
     *
     * @param gameId Id of the current gmae
     * @return internal representation of the game and its pits
     */
    private GameInternalStatus setupPits(long gameId) {
        PitCircularLinkedList gameBoard = new PitCircularLinkedList();

        int pits = gameConfiguration.getNumberOfPits();
        int stones = gameConfiguration.getNumberOfStones();
        int boardSize = pits * 2 + 2;

        IntStream.rangeClosed(1, boardSize).forEach(i -> {
            Pit pit = Pit.builder()
                    .pitId(i)
                    .stones(i == boardSize || i == boardSize / 2 ? 0 : stones)
                    .type(i == boardSize || i == boardSize / 2 ? PitType.KALAH : PitType.PIT)
                    .player(i < boardSize / 2 + 1 ? PLAYER_ONE.getType() : PLAYER_TWO.getType())
                    .build();
            gameBoard.addNodes(pit);
        });
        return GameInternalStatus.allBuilder()
                .gameId(gameId)
                .circularLinkedList(gameBoard)
                .build();
    }

    /**
     * Create a new game
     *
     * @return a new Game instance
     */
    private Game createNewGame() {
        long gameId = getGameId();
        log.info("Saving new game with id: " + gameId);
        return gameRepository.save(Game.builder()
                .id(gameId)
                .uri(gameConfiguration.getServerUri() + gameId)
                .build());
    }

    /**
     * When game starts create a new status
     *
     * @param gameId Game Id
     */
    private void createNewBoard(long gameId) {
        log.info("Setting kalah's board");
        gameStatusRepository.save(GameStatus.builder()
                .gameInternalStatus(setupPits(gameId))
                .build());
    }


    /**
     * When game starts choose player's turn
     *
     * @param gameId Game Id
     */
    private void assignTurnToPlayer(long gameId) {
        log.info("Assign random turn for player one or player two");
        PlayerTurn turn = PlayerTurn.builder()
                .gameId(gameId)
                .player(getRandomPlayer())
                .timestamp(new Date()).build();

        log.info("Player " + turn.getPlayer() + "starts");
        playerTurnRepository.save(turn);
    }


    /**
     * Get random player between PLAYER_ONE (1) or PLAYER_TWO(2), if this is not possible , assume PLAYER_ONE
     *
     * @return Player id
     */
    private int getRandomPlayer() {
        try {
            Random random = SecureRandom.getInstanceStrong();
            return random.nextBoolean() ? PLAYER_ONE.getType() : PLAYER_TWO.getType();
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
        }
        return PLAYER_ONE.getType();
    }

}
