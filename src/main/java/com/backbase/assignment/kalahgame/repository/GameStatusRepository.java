package com.backbase.assignment.kalahgame.repository;

import com.backbase.assignment.kalahgame.domain.GameStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameStatusRepository extends MongoRepository<GameStatus, String> {

    GameStatus findTop1ByGameIdOrderByTimestampDesc(long gameId);

}
