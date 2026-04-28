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

    @Column (name = "idWaiter")
    private Long idWaiter;

    @Column (name= "idBranch", nullable = false)
    private Long idBranch;
}
