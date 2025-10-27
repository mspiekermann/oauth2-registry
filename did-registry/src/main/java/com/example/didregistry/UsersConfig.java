package com.example.didregistry;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Demo directory: map DID -> connector endpoint */
@Component
public class UsersConfig {
    private static final Map<String, String> DID_TO_ENDPOINT = new ConcurrentHashMap<>();
    static {
        DID_TO_ENDPOINT.put("did:web:example.com", "https://connector.example.com/endpoint");
        DID_TO_ENDPOINT.put("did:web:alice.example", "https://connector.alice.example/endpoint");
    }
    public String connectorEndpointForDid(String did) {
        return DID_TO_ENDPOINT.getOrDefault(did, "https://connector.default/endpoint");
    }
}