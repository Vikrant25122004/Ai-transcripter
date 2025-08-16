package com.aitranscripter.ai.transcripter.Repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.aitranscripter.ai.transcripter.Entity.user;

@Repository
public interface UserRepository extends MongoRepository<user,ObjectId>{
    user findByemail(String email);

  

    
}
