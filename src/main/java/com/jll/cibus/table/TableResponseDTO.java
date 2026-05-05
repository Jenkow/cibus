package com.jll.cibus.table;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class TableResponseDTO
{
    private Long id;
    private Integer capacity;
    private Boolean available;
    private Long waiterId;
    private Long branchId;

}
