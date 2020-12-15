package com.stephenschafer.ami;

import static com.stephenschafer.ami.Constants.ACCESS_TOKEN_VALIDITY_SECONDS;
import static com.stephenschafer.ami.Constants.SIGNING_KEY;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Function;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.stephenschafer.ami.jpa.UserEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {
	private static final long serialVersionUID = 1L;

	public String getUsernameFromToken(final String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public Date getExpirationDateFromToken(final String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(final String token, final Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(final String token) {
		return Jwts.parser().setSigningKey(SIGNING_KEY).parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(final String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	public String generateToken(final UserEntity user) {
		return doGenerateToken(user.getUsername());
	}

	private String doGenerateToken(final String subject) {
		final Claims claims = Jwts.claims().setSubject(subject).setExpiration(
			new Date(System.currentTimeMillis() + 1000L * 60L * 60L * 24L));
		claims.put("scopes", Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
		return Jwts.builder().setClaims(claims).setIssuer("http://stephenschafer.com").setIssuedAt(
			new Date(System.currentTimeMillis())).setExpiration(
				new Date(System.currentTimeMillis()
					+ ACCESS_TOKEN_VALIDITY_SECONDS * 1000)).signWith(SignatureAlgorithm.HS256,
						SIGNING_KEY).compact();
	}

	public Boolean validateToken(final String token, final UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
}
