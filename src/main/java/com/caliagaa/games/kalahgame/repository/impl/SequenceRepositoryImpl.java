package com.caliagaa.games.kalahgame.repository.impl;

import com.caliagaa.games.kalahgame.domain.Sequence;
import com.caliagaa.games.kalahgame.repository.SequenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class SequenceRepositoryImpl implements SequenceRepository {

    @Autowired
    private MongoOperations mongoOperation;

    @Override
    public long getNextSequenceId(String key) {
        Query query = new Query(Criteria.where("name").is(key));
        Update update = new Update();
        update.inc("seq", 1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);

        Sequence seqId = mongoOperation.findAndModify(query, update, options, Sequence.class);

        return seqId.getSeq();
    }
}
