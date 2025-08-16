package com.aitranscripter.ai.transcripter.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.aitranscripter.ai.transcripter.Entity.messages;

@Repository
public interface messagesrepo extends MongoRepository<messages,ObjectId> {

    void save(Optional<messages> chat);

    Optional<messages> findById(String id);

    
} 