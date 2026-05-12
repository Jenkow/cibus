package com.jll.cibus.branch;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class BranchRequestDTO
{
    @NotBlank (message = "Name is mandatory")
    private String name;

    @NotBlank (message = "Street is mandatory")
    private String street;

    @NotNull(message = "Number is mandatory")
    @Length(min =2, max= 4, message = "Number must have between 2 and 4 digits")
    private Integer number;

}
