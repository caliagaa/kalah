package com.caliagaa.games.kalahgame.controller;

import com.caliagaa.games.kalahgame.domain.Game;
import com.caliagaa.games.kalahgame.domain.GameStatus;
import com.caliagaa.games.kalahgame.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Game> createGame() {
        Game game = gameService.initializeGame();
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @GetMapping(value = "/{gameId}/pits/{pitId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GameStatus> move(@PathVariable long gameId, @PathVariable int pitId) {
        return new ResponseEntity<>(gameService.move(gameId, pitId), HttpStatus.OK);
    }
}
