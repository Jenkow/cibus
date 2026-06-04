package com.jll.cibus.table.entity;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tables", uniqueConstraints = {@UniqueConstraint(columnNames = {"branch_id", "number"})})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class TableEntity {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "number", nullable = false)
    private Integer number;

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

    public Boolean isAvailable(){
        return this.getAvailable();
    }

}
