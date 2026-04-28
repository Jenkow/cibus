package com.jll.cibus.table;

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
    private int capacity;

    @Column (name = "isAvailable", nullable = false)
    private boolean isAvailable;

    @ManyToOne
    @Column (name = "id_waiter")
    private Long id_waiter;

    //    @ManyToOne(fetch = FetchType.LAZY)
    //    @JoinColumn(name = "id_waiter")
    //    private UserEntity waiter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name= "id_branch", nullable = false)
    private Long idBranch; //esto no va
    //private BranchEntity branch;
    //CREATE METHODS TO VERIFY THAT EVERY TIME THAT THE TABLE IS NOT AVALIABLE THERE IS A WAITER, AND IF ITS AVALIABLE, THE WAITER IS NULL
}
