package com.caliagaa.games.kalahgame.service.handler;

import com.caliagaa.games.kalahgame.configuration.GameConfiguration;
import com.caliagaa.games.kalahgame.domain.Game;
import com.caliagaa.games.kalahgame.domain.GameStatus;
import com.caliagaa.games.kalahgame.domain.PlayerTurn;
import com.caliagaa.games.kalahgame.repository.GameRepository;
import com.caliagaa.games.kalahgame.repository.GameStatusRepository;
import com.caliagaa.games.kalahgame.repository.PlayerTurnRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Scanner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class GameControlHandlerTest {

    @InjectMocks
    private GameControlHandler gameControlHandler;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameStatusRepository gameStatusRepository;

    @Mock
    private PlayerTurnRepository playerTurnRepository;

    @Mock
    private GameConfiguration gameConfiguration;

    private Game game;

    @Before
    public void setup() {
        long gameId = 1L;
        game = getGame(gameId);
        lenient().when(gameRepository.findGameById(gameId)).thenReturn(game);

    }

    @Test
    public void handleMovement_pitOne() {
        int pitId = 1;
        int stones = 6;

        when(playerTurnRepository.save(any(PlayerTurn.class))).thenReturn(getPlayerOneTurn());
        when(gameStatusRepository.findTop1ByGameIdOrderByTimestampDesc(game.getId())).thenReturn(getGameOneStatus());
        when(playerTurnRepository.findTop1ByGameIdOrderByTimestampDesc(game.getId())).thenReturn(getPlayerOneTurn());

        GameStatus newStatus = gameControlHandler.handleMovement(game.getId(), 1);
        int stonesInNextPit = newStatus.getStatus().get(pitId + 1);
        assertTrue("Next pit should have one more stone", stonesInNextPit == stones + 1);
    }


    @Test
    public void handleMovement_playerOneGameOver() {
        when(gameStatusRepository.findTop1ByGameIdOrderByTimestampDesc(game.getId())).thenReturn(getGamePlayerOneOverStatus());
        GameStatus newStatus = gameControlHandler.handleMovement(game.getId(), 1);
        assertTrue("Game should be over", newStatus.isOver());
    }

    @Test
    public void handleMovement_playerTwoGameOver() {
        when(gameStatusRepository.findTop1ByGameIdOrderByTimestampDesc(game.getId())).thenReturn(getGamePlayerOneOverStatus());
        GameStatus newStatus = gameControlHandler.handleMovement(game.getId(), 1);
        assertTrue("Game should be over", newStatus.isOver());
    }


    @Test
    public void handleMovement_stealStones() {
        int pitId = 8;
        int stones = 6;
        int playerTwoKalahPosition = 14;
        GameStatus previous = getGameTurnEnds();
        int stonesInKalah = previous.getStatus().get(playerTwoKalahPosition);

        when(gameStatusRepository.findTop1ByGameIdOrderByTimestampDesc(game.getId())).thenReturn(previous);
        when(playerTurnRepository.findTop1ByGameIdOrderByTimestampDesc(game.getId())).thenReturn(getPlayerTwoTurn());
        lenient().when(gameConfiguration.getNumberOfPits()).thenReturn(stones);
        lenient().when(gameConfiguration.getNumberOfStones()).thenReturn(stones);
        when(playerTurnRepository.save(any(PlayerTurn.class))).thenReturn(getPlayerTwoTurn());
        GameStatus newStatus = gameControlHandler.handleMovement(game.getId(), pitId);
        int newStonesInKalah = newStatus.getStatus().get(playerTwoKalahPosition);

        assertTrue("Amount of stones in second player's kalah should increase", newStonesInKalah > stonesInKalah);

    }


    @Test
    public void handleMovement_invalidPit() {
        int pitId = 13;
        int stones = 6;
        GameStatus previous = getGameInvalidPit();
        int stonesInPit = previous.getStatus().get(8);

        when(gameStatusRepository.findTop1ByGameIdOrderByTimestampDesc(game.getId())).thenReturn(previous);
        when(playerTurnRepository.findTop1ByGameIdOrderByTimestampDesc(game.getId())).thenReturn(getPlayerTwoTurn());
        lenient().when(gameConfiguration.getNumberOfPits()).thenReturn(stones);
        lenient().when(gameConfiguration.getNumberOfStones()).thenReturn(stones);
        when(playerTurnRepository.save(any(PlayerTurn.class))).thenReturn(getPlayerTwoTurn());
        GameStatus newStatus = gameControlHandler.handleMovement(game.getId(), pitId);
        int newStonesInPit = newStatus.getStatus().get(8);

        assertTrue("Amount of stones in second player's kalah should increase", newStonesInPit > stonesInPit);

    }


    @Test
    public void handleMovement_getUpperMovements() {
        int pitId = 1;
        int stones = 6;
        int playerTwoKalahPosition = 7;

        GameStatus previous = getGameSteal();
        int stonesInKalah = previous.getStatus().get(playerTwoKalahPosition);

        when(gameStatusRepository.findTop1ByGameIdOrderByTimestampDesc(game.getId())).thenReturn(previous);
        when(playerTurnRepository.findTop1ByGameIdOrderByTimestampDesc(game.getId())).thenReturn(getPlayerOneTurn());
        lenient().when(gameConfiguration.getNumberOfPits()).thenReturn(stones);
        lenient().when(gameConfiguration.getNumberOfStones()).thenReturn(stones);
        when(playerTurnRepository.save(any(PlayerTurn.class))).thenReturn(getPlayerOneTurn());
        GameStatus newStatus = gameControlHandler.handleMovement(game.getId(), pitId);
        int newStonesInKalah = newStatus.getStatus().get(playerTwoKalahPosition);

        assertTrue("Amount of stones in second player's kalah should increase", newStonesInKalah > stonesInKalah);
    }


    //Helper methods
    private PlayerTurn getPlayerOneTurn() {
        return PlayerTurn.builder()
                .player(1)
                .timestamp(new Date())
                .gameId(1).build();
    }

    private PlayerTurn getPlayerTwoTurn() {
        return PlayerTurn.builder()
                .player(2)
                .timestamp(new Date())
                .gameId(1).build();
    }

    //Helper
    private Game getGame(long gameId) {
        return Game.builder()
                .id(gameId)
                .uri("http://localhost:8080/games" + gameId)
                .build();
    }

    private GameStatus getGameOneStatus() {
        InputStream in = this.getClass().getResourceAsStream("/fixture/game_status_one.json");
        Scanner s = new Scanner(in).useDelimiter("\\A");
        String jsonStatus = s.hasNext() ? s.next() : "";
        return parseStatus(jsonStatus);
    }

    private GameStatus getGamePlayerOneOverStatus() {
        InputStream in = this.getClass().getResourceAsStream("/fixture/game_status_one_over.json");
        Scanner s = new Scanner(in).useDelimiter("\\A");
        String jsonStatus = s.hasNext() ? s.next() : "";
        return parseStatus(jsonStatus);
    }

    private GameStatus getGameTurnEnds() {
        InputStream in = this.getClass().getResourceAsStream("/fixture/game_status_empty_pit.json");
        Scanner s = new Scanner(in).useDelimiter("\\A");
        String jsonStatus = s.hasNext() ? s.next() : "";
        return parseStatus(jsonStatus);
    }

    private GameStatus getGameInvalidPit() {
        InputStream in = this.getClass().getResourceAsStream("/fixture/game_status_invalid_pit.json");
        Scanner s = new Scanner(in).useDelimiter("\\A");
        String jsonStatus = s.hasNext() ? s.next() : "";
        return parseStatus(jsonStatus);
    }

    private GameStatus getGameSteal() {
        InputStream in = this.getClass().getResourceAsStream("/fixture/game_status_steal.json");
        Scanner s = new Scanner(in).useDelimiter("\\A");
        String jsonStatus = s.hasNext() ? s.next() : "";
        return parseStatus(jsonStatus);
    }


    private GameStatus parseStatus(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, GameStatus.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return GameStatus.builder().build();
    }

    //gameStatusRepository.findTop1ByGameIdOrderByTimestampDesc(game.getId())
}
