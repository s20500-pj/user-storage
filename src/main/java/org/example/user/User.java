package org.example.user;

import jakarta.persistence.*;
import lombok.*;
import org.example.dto.UserDto;
import org.example.enums.Gender;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(updatable = false)
    private String username;
    @Column(updatable = false)
    private String email;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Integer age;
    @Column(updatable = false)
    private Timestamp createdAt;
    private boolean activated;
    private String token;

    @PrePersist
    protected void onCreate() {
        this.activated = false;
        this.token = UUID.randomUUID().toString();
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public User toEntity(UserDto dto) {
        this.id = dto.getId();
        this.username = dto.getUsername();
        this.email = dto.getEmail();
        this.gender = dto.getGender();
        this.age = dto.getAge();
        this.createdAt = dto.getCreatedAt();
        return this;
    }

    public UserDto toDto() {
        return UserDto.builder()
                .id(id)
                .username(username)
                .email(email)
                .gender(gender)
                .age(age)
                .createdAt(createdAt)
                .activated(activated)
                .build();
    }
}
