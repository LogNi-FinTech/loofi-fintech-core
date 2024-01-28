package com.logni.account.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;
import java.util.function.Function;

import javax.crypto.SecretKey;

@Component
public class JwtTokenUtil {

    private final String key ="authenticationSecret&KEYWILLBESTOREDSSMKMSHSM";
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    public String getIdFromToken(String token) {
        return getClaimFromToken(token, Claims::getId);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    //for retrieving any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
          .verifyWith(getSecretKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();
    }

    private SecretKey getSecretKey(){
      return Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
    }
}
