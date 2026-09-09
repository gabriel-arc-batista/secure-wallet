package com.gabrielarcanjo.securewallet.user;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.UUID;
import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)     // marca a chave primária e gera o identificador
    private UUID id;
    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;
    @Column(name = "password_hash", nullable = false, length = 255)   // liga o atributo ao hash armazenado
    private String passwordHash;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)    // preenche a data na hora da criação
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)    // Atualiza a data quando o objeto mudar
    private OffsetDateTime updatedAt;

    public User(String fullName, String email, String passwordHash) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    protected User() {

    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public UUID getId() {
        return id;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
