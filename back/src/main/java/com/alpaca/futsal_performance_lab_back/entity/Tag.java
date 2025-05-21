package com.alpaca.futsal_performance_lab_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tag")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Integer tagId;

    @Column(name = "is_assigned")
    private boolean isAssigned;

    @ManyToOne
    @JoinColumn(name = "stadium_id", referencedColumnName = "stadium_id", nullable = false)
    private Stadium stadium;
}
