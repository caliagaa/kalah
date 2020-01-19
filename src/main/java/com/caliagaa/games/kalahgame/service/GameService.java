package com.caliagaa.games.kalahgame.service;

import com.caliagaa.games.kalahgame.domain.Game;
import com.caliagaa.games.kalahgame.domain.GameStatus;
import com.caliagaa.games.kalahgame.service.handler.GameControlHandler;
import com.caliagaa.games.kalahgame.service.handler.GameCreationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GameService {

    @Autowired
    private GameCreationHandler creationHandler;

    @Autowired
    private GameControlHandler controlHandler;

    /**
     * Game initialization
     *
     * @return a new instance of Game
     */
    public Game initializeGame() {
        return creationHandler.initializeGame();
    }


    /**
     * Make a move indicating a pit and a game id
     *
     * @param gameId Id of the game
     * @param pitId  Id of the pit
     */
    public GameStatus move(long gameId, int pitId) {
        return controlHandler.handleMovement(gameId, pitId);
    }


}
