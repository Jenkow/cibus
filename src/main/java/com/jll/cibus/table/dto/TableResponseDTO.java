package com.jll.cibus.table.dto;

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
