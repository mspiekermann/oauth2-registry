package com.example.didregistry;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.util.JSONObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/** Minimal resolver for did:web that returns the first verificationMethod.publicKeyJwk */
@Component
public class DidWebResolver {

    private final HttpClient http = HttpClient.newHttpClient();

    public JWK resolveToJwk(String did) {
        if (did == null || !did.startsWith("did:web:")) {
            throw new IllegalArgumentException("Only did:web is supported in this tutorial");
        }

        // did:web:example.com -> https://example.com/.well-known/did.json
        // did:web:example.com:users:alice -> https://example.com/users/alice/did.json
        String path = did.substring("did:web:".length()).replace(":", "/");
        String url = "https://" + path + "/did.json";

        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != HttpStatus.OK.value()) {
                throw new IllegalStateException("DID document fetch failed: HTTP " + resp.statusCode());
            }

            Map<String, Object> didDoc = JSONObjectUtils.parse(resp.body());
            var vms = (java.util.List<Map<String, Object>>) didDoc.get("verificationMethod");
            if (vms == null || vms.isEmpty()) {
                throw new IllegalStateException("No verificationMethod in DID doc");
            }
            var vm = vms.get(0);
            Map<String, Object> jwkMap = (Map<String, Object>) vm.get("publicKeyJwk");
            if (jwkMap == null) {
                throw new IllegalStateException("No publicKeyJwk in DID doc");
            }
            return JWK.parse(jwkMap);

        } catch (Exception e) {
            throw new RuntimeException("Failed to resolve DID: " + did, e);
        }
    }
}