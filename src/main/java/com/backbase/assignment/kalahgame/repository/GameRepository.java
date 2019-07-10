package com.backbase.assignment.kalahgame.repository;

import com.backbase.assignment.kalahgame.domain.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends MongoRepository<Game, String> {

    Game findGameById(long id);
}
