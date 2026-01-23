package org.nowstart.autotrade.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UpbitAuthService {

    @Value("${upbit.access-key}")
    private String accessKey;

    @Value("${upbit.secret-key}")
    private String secretKey;

    public String createJwt() {
        return createJwt(null);
    }

    public String createJwt(Map<String, Object> params) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            var builder = JWT.create()
                    .withClaim("access_key", accessKey)
                    .withClaim("nonce", UUID.randomUUID().toString());

            if (params != null && !params.isEmpty()) {
                String queryString = params.entrySet().stream()
                        .map(e -> e.getKey() + "=" + e.getValue())
                        .collect(Collectors.joining("&"));

                MessageDigest md = MessageDigest.getInstance("SHA-512");
                md.update(queryString.getBytes(StandardCharsets.UTF_8));

                String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));
                builder.withClaim("query_hash", queryHash)
                        .withClaim("query_hash_alg", "SHA512");
            }

            return builder.sign(algorithm);
        } catch (Exception e) {
            throw new RuntimeException("JWT token generation failed", e);
        }
    }
}
