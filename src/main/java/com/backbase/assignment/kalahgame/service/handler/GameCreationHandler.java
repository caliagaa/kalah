package com.backbase.assignment.kalahgame.service.handler;

import com.backbase.assignment.kalahgame.configuration.GameConfiguration;
import com.backbase.assignment.kalahgame.domain.Game;
import com.backbase.assignment.kalahgame.domain.Pit;
import com.backbase.assignment.kalahgame.domain.PlayerTurn;
import com.backbase.assignment.kalahgame.domain.RawGame;
import com.backbase.assignment.kalahgame.repository.GameRepository;
import com.backbase.assignment.kalahgame.repository.GameStatusRepository;
import com.backbase.assignment.kalahgame.repository.PlayerTurnRepository;
import com.backbase.assignment.kalahgame.repository.SequenceRepository;
import com.backbase.assignment.kalahgame.util.CircularLinkedList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;
import java.util.stream.IntStream;

import static com.backbase.assignment.kalahgame.util.GameUtils.*;

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
     * Initialize game: obtain current id  and save it
     * @return a new instance of the game
     */
    public Game initializeGame() {
        long gameId = getGameId();
        Game game = saveNewGame(gameId);

        assignTurn(gameId);

        return game;
    }

    /**
     * Get new id from sequence
     * @return a new Id for a game
     */
    private long getGameId(){
        return  sequenceRepository.getNextSequenceId(GAME_SEQUENCE_ID);
    }

    /**
     * Helper method to initialize pits and its stones
     *
     * @param numberOfPits Number of Pits in the game
     * @param numberOfStones Number of stones in each pit
     * @param gameId Id of the current gmae
     * @return internal representation of the game and its pits
     */
    private RawGame setupPits(int numberOfPits, int numberOfStones, long gameId) {
        CircularLinkedList<Pit> gameBoard = new CircularLinkedList<>();
        int totalPits = numberOfPits * 2 + 2;
        IntStream.rangeClosed(1, totalPits).forEach(i -> {
            Pit pit = Pit.builder()
                    .pitId(i)
                    .stones(getStones(i, totalPits, numberOfStones))
                    .type(getTypeByPitId(i, totalPits))
                    .player(getPlayer(i, totalPits)).build();
            gameBoard.addNodes(pit);
        });
        return RawGame.builder()
                .gameId(gameId)
                .circularLinkedList(gameBoard)
                .build();

    }

    /**
     * Create a new game
     * @param gameId New Id of the game
     * @return a new Game instance
     */
    private Game saveNewGame(long gameId) {
        Game game = Game.builder()
                .id(gameId)
                .uri(URI + gameId)
                .build();
        gameRepository.save(game);

        createNewBoard(gameId);
        return game;
    }

    /**
     * When game starts create a new status
     * @param gameId Game Id
     */
    private void createNewBoard(long gameId){
        int pits = gameConfiguration.getNumberOfPits();
        int stones = gameConfiguration.getNumberOfStones();

        RawGame gameRaw = setupPits(pits, stones, gameId);
        gameStatusRepository.save(convertToGameStatus(gameRaw));
    }


    /**
     * When game starts choose player's turn
     * @param gameId Game Id
     */
    private void assignTurn(long gameId){
        PlayerTurn turn = PlayerTurn.builder()
                .gameId(gameId)
                .player(getRandomPlayer())
                .timestamp(new Date()).build();
        playerTurnRepository.save(turn);
    }


    /**
     * Get random player between PLAYER_ONE (1) or PLAYER_TWO(2), if this is not possible , assume PLAYER_ONE
     *
     * @return Player id
     */
    private int getRandomPlayer(){
        try {
            Random random = SecureRandom.getInstanceStrong();
            return random.nextBoolean()? PLAYER_ONE: PLAYER_TWO;
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(),e);
        }
        return PLAYER_ONE;
    }

}
