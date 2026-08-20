package com.alsatech.finflow.resource.auth.infrastructure.service.impl

import com.alsatech.finflow.resource.auth.dto.AuthResponseDto
import com.alsatech.finflow.resource.auth.dto.LoginCommand
import com.alsatech.finflow.resource.auth.dto.RegisterCommand
import com.alsatech.finflow.resource.auth.dto.UserInfoDto
import com.alsatech.finflow.resource.auth.infrastructure.security.JwtUtil
import com.alsatech.finflow.resource.auth.infrastructure.service.AuthService
import com.alsatech.finflow.shared.domain.entity.Company
import com.alsatech.finflow.shared.domain.entity.Role
import com.alsatech.finflow.shared.domain.entity.User
import com.alsatech.finflow.shared.domain.repository.CompanyRepository
import com.alsatech.finflow.shared.domain.repository.UserRepository
import com.collicode.common.exception.BusinessException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val companyRepository: CompanyRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) : AuthService {

    override fun login(command: LoginCommand): Mono<AuthResponseDto> {
        return userRepository.findByEmail(command.email)
            .switchIfEmpty(Mono.error(BusinessException.exception(
                "INVALID_CREDENTIALS",
                "Invalid email or password"
            )))
            .flatMap { user ->
                if (!passwordEncoder.matches(command.password, user.passwordHash)) {
                    Mono.error(BusinessException.exception(
                        "INVALID_CREDENTIALS",
                        "Invalid email or password"
                    ))
                } else {
                    companyRepository.findById(user.companyId)
                        .map { company ->
                            val token = jwtUtil.generateToken(
                                userId = user.id!!,
                                email = user.email,
                                companyId = user.companyId,
                                role = user.role.name
                            )

                            AuthResponseDto(
                                token = token,
                                user = UserInfoDto(
                                    id = user.id!!,
                                    email = user.email,
                                    fullName = null, // TODO: Add fullName to User entity
                                    role = user.role,
                                    companyId = user.companyId,
                                    companyName = company.name
                                ),
                                expiresIn = 86400
                            )
                        }
                }
            }
    }

    override fun register(command: RegisterCommand): Mono<AuthResponseDto> {
        // Check if user already exists
        return userRepository.findByEmail(command.email)
            .flatMap<AuthResponseDto> {
                Mono.error(BusinessException.exception(
                    "USER_ALREADY_EXISTS",
                    "User with email ${command.email} already exists"
                ))
            }
            .switchIfEmpty(
                // Create company first
                createCompany(command.companyName)
                    .flatMap { company ->
                        // Then create user
                        createUser(command.email, command.password, company.id!!)
                            .flatMap { user ->
                                val token = jwtUtil.generateToken(
                                    userId = user.id!!,
                                    email = user.email,
                                    companyId = user.companyId,
                                    role = user.role.name
                                )

                                Mono.just(AuthResponseDto(
                                    token = token,
                                    user = UserInfoDto(
                                        id = user.id!!,
                                        email = user.email,
                                        fullName = command.fullName,
                                        role = user.role,
                                        companyId = user.companyId,
                                        companyName = company.name
                                    ),
                                    expiresIn = 86400
                                ))
                            }
                    }
            )
    }

    private fun createCompany(name: String): Mono<Company> {
        val company = Company(
            id = null,
            name = name,
            createdAt = LocalDateTime.now()
        )
        return companyRepository.save(company)
    }

    private fun createUser(email: String, password: String, companyId: Long): Mono<User> {
        val user = User(
            id = null,
            companyId = companyId,
            email = email,
            passwordHash = passwordEncoder.encode(password),
            role = Role.ADMIN, // First user is admin
            createdAt = LocalDateTime.now()
        )
        return userRepository.save(user)
    }
}
