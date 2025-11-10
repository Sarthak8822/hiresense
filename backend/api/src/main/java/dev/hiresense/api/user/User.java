package dev.hiresense.api.user;

import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name = "users")
public class User {
    @Id @Column(columnDefinition = "uuid") private UUID id = UUID.randomUUID();

    @Column(unique = true, nullable = false) private String email;
    @Column(name = "password_hash", nullable = false) private String passwordHash;

    protected User() {}
    public User(String email, String passwordHash) { this.email = email; this.passwordHash = passwordHash; }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public void setEmail(String e) { this.email = e; }
    public void setPasswordHash(String p) { this.passwordHash = p; }
}
