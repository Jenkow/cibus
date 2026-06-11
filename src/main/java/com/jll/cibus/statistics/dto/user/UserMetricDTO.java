package com.jll.cibus.statistics.dto.user;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserMetricDTO {
    private Long dni;
    private String firstName;
    private String lastName;

}
