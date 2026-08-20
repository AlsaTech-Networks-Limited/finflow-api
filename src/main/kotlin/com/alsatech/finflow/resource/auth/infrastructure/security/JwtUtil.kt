package com.alsatech.finflow.resource.auth.infrastructure.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

/**
 * JWT utility for token generation and validation.
 *
 * Note: In production, the secret key should be loaded from environment variables
 * or a secure configuration service.
 */
@Component
class JwtUtil {

    // In production, load this from application.yml or environment variable
    private val SECRET_KEY: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    private val EXPIRATION_TIME: Long = 86400000 // 24 hours in milliseconds

    /**
     * Generate JWT token for a user.
     */
    fun generateToken(userId: Long, email: String, companyId: Long, role: String): String {
        val now = Date()
        val expiryDate = Date(now.time + EXPIRATION_TIME)

        return Jwts.builder()
            .setSubject(userId.toString())
            .claim("email", email)
            .claim("companyId", companyId)
            .claim("role", role)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SECRET_KEY)
            .compact()
    }

    /**
     * Extract user ID from token.
     */
    fun getUserIdFromToken(token: String): Long {
        val claims = getAllClaimsFromToken(token)
        return claims.subject.toLong()
    }

    /**
     * Extract email from token.
     */
    fun getEmailFromToken(token: String): String {
        val claims = getAllClaimsFromToken(token)
        return claims["email"] as String
    }

    /**
     * Extract company ID from token.
     */
    fun getCompanyIdFromToken(token: String): Long {
        val claims = getAllClaimsFromToken(token)
        return (claims["companyId"] as Int).toLong()
    }

    /**
     * Extract role from token.
     */
    fun getRoleFromToken(token: String): String {
        val claims = getAllClaimsFromToken(token)
        return claims["role"] as String
    }

    /**
     * Validate token.
     */
    fun validateToken(token: String): Boolean {
        return try {
            val claims = getAllClaimsFromToken(token)
            !isTokenExpired(claims)
        } catch (e: Exception) {
            false
        }
    }

    // jjwt 0.12.x renamed parserBuilder()/setSigningKey()/parseClaimsJws().body
    // to parser()/verifyWith()/parseSignedClaims().payload — this project pins
    // jjwt-api 0.12.6 (see pom.xml), so the 0.11.x-style API used here
    // originally doesn't exist on the classpath.
    private fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parser()
            .verifyWith(SECRET_KEY)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    private fun isTokenExpired(claims: Claims): Boolean {
        return claims.expiration.before(Date())
    }
}
