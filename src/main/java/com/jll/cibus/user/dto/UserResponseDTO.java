package com.jll.cibus.user.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class UserResponseDTO
{
    private Long id;
    private Long dni;
    private String firstName;
    private String lastName;
    private Integer clave;
    private String phoneNumber;
    private String email;
    private Long branchId;
    private String branchName;
    private Long userRoleId;
    private String userRoleName;
}
