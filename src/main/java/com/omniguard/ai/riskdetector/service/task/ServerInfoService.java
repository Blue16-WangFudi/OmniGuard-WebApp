package com.omniguard.ai.riskdetector.service.task;

import com.omniguard.ai.riskdetector.repository.ServerStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServerInfoService {
    @Autowired
    public static ServerStatusRepository serverStatusRepository;
}
