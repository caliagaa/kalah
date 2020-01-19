package com.caliagaa.games.kalahgame.repository;

import com.caliagaa.games.kalahgame.domain.PlayerTurn;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerTurnRepository extends MongoRepository<PlayerTurn, String> {
    PlayerTurn findTop1ByGameIdOrderByTimestampDesc(long gameId);
}
