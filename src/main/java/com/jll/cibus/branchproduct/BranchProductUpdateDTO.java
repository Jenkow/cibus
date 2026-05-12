package com.jll.cibus.branchproduct;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class BranchProductUpdateDTO {

    @Positive
    private BigDecimal price;

    private Boolean available;
}
