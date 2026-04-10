package com.iamhusrev.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AuthResponseDTO {

    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private UserDTO user;
}
