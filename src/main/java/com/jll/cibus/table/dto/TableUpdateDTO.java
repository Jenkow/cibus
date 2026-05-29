package com.jll.cibus.table.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class TableUpdateDTO
{
    @NotNull (message ="The capacity is mandatory")
    @Min(value=1, message = "The minimun capacity is 1")
    private Integer capacity;
    @NotNull (message = "The availability status is mandatory")
    private Boolean available;
    private Long waiterId;

}
