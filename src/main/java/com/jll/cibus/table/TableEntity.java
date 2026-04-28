package com.jll.cibus.table;

import com.jll.cibus.branch.BranchEntity;
import com.jll.cibus.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table (name = "tables")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class TableEntity {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "capacity", nullable = false)
    private Integer capacity;

    @Column (name = "available", nullable = false)
    private Boolean available;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "waiter_id")
   private UserEntity waiter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private BranchEntity branch;


    //CREATE METHODS TO VERIFY THAT EVERY TIME THAT THE TABLE IS NOT AVALIABLE THERE IS A WAITER, AND IF ITS AVALIABLE, THE WAITER IS NULL
}
