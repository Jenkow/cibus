package com.jll.cibus.table.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class TableUpdateDTO {

    @Min(value=1, message = "The minimun capacity is 1")
    private Integer capacity;

    @Positive
    private Long waiterId;

}
