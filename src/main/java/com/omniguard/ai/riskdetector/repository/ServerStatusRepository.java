package com.omniguard.ai.riskdetector.repository;

import com.omniguard.ai.riskdetector.utils.websocket.dto.model.ServerInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerStatusRepository extends MongoRepository<ServerInfo,String>{
    List<ServerInfo> findByServerId(String serverId);
}
