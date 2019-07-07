package com.backbase.assignment.kalahgame.controller;

import com.backbase.assignment.kalahgame.domain.Game;
import com.backbase.assignment.kalahgame.domain.GameStatus;
import com.backbase.assignment.kalahgame.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class GameController {

    @Autowired
    private GameService gameService;

    @GetMapping(value = "/games", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Game> createGame() {
        Game game = gameService.initializeGame();
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @GetMapping(value = "/games/{gameId}/pits/{pitId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public GameStatus move(@PathVariable long gameId, @PathVariable int pitId){
        return gameService.move(gameId,pitId);
    }
}
