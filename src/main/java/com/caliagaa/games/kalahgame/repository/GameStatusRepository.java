package com.caliagaa.games.kalahgame.repository;

import com.caliagaa.games.kalahgame.domain.GameStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameStatusRepository extends MongoRepository<GameStatus, String> {

    GameStatus findTop1ByGameIdOrderByTimestampDesc(long gameId);

}
