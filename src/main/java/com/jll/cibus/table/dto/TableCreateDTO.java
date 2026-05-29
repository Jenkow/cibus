package com.jll.cibus.table.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TableCreateDTO {

    @NotNull(message ="The branch ID is mandatory")
    private Long branchId;

    @NotNull(message ="The capacity is mandatory")
    @Min(value=1, message = "The minimun capacity is 1")
    private Integer capacity;

}
