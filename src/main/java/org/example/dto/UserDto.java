package org.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.enums.Gender;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    @NotNull
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Username must not contain special characters")
    private String username;
    @NotNull
    @Email
    private String email;
    @NotNull
    private Gender gender;
    @NotNull
    @Size
    private Integer age;
    private Timestamp createdAt;
    private boolean activated;
}
