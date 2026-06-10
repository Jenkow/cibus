package com.jll.cibus.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePinDTO {

    @NotBlank(message = "PIN is mandatory")
    @Pattern(regexp = "\\d{6}")
    private String pin;

}
