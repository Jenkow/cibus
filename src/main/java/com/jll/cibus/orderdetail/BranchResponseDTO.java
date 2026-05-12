package com.jll.cibus.orderdetail;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BranchResponseDTO
{
    private Long id;
    private String name;
    private String street;
    private Integer number;

}
