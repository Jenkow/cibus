package com.jll.cibus.statistics.dto.user;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInsightDTO {
    private List<UserMetricDTO> FacturationRanking;
    private List<UserMetricDTO> OrdersQuantityRanking;
    private Double averageServe;
}
