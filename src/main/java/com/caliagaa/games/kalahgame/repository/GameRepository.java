package com.caliagaa.games.kalahgame.repository;

import com.caliagaa.games.kalahgame.domain.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends MongoRepository<Game, String> {

    Game findGameById(long id);
}
