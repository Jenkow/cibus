package com.jll.cibus.table;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class TableRequestDTO
{
    @NotNull (message ="The capacity is mandatory")
    @Min(value=1, message = "The minimun capacity is 1")
    private Integer capacity;

}
