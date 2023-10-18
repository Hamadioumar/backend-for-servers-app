package com.example.servers.service.implementation;

import com.example.servers.enumaration.Status;
import com.example.servers.model.Server;
import com.example.servers.repository.ServerRepository;
import com.example.servers.service.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Random;

import static java.util.List.of;

@RequiredArgsConstructor
@org.springframework.stereotype.Service
@Transactional
@Slf4j

public class ServiceImplementation implements ServerService {
    private final ServerRepository serverRepository;
    @Override
    public Server create(Server server) {
        log.info("Pinging server IP: {}", server.getName());
        server.setImageUrl(setServerImageUrl());
        return serverRepository.save(server);
    }



    @Override
    public Server ping(String ipAddress) throws IOException {
        log.info("Pinging server IP: {}", ipAddress);
        Server server = serverRepository.findByIpAddress(ipAddress);
        InetAddress address = InetAddress.getByName(ipAddress);
        server.setStatus(address.isReachable(10000)? Status.SERVER_UP :Status.SERVER_DOWN);
        serverRepository.save(server);
        return server;
    }

    @Override
    public Collection<Server> list(int limit) {
        log.info("Fetching all servers");
        return serverRepository.findAll(PageRequest.of(0,limit)).toList();
    }

    @Override
    public Server get(Long id) {
        log.info("Fetching server by id: {}", id);
        return serverRepository.findById(id).get();
    }

    @Override
    public Server update(Server server) {
        log.info("Updating server",
                server.getName());
        return serverRepository.save(server);
    }

    @Override
    public Boolean delete(Long id) {
        log.info("Deleting server by id: {}", id);
        serverRepository.deleteById(id);
        return null;
    }

    private String setServerImageUrl() {
        String[] imagesName = {"server1.jpg","server2.jpg"};
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("server/image/" +  imagesName[new Random().nextInt(2)]).toUriString();
    }
}
