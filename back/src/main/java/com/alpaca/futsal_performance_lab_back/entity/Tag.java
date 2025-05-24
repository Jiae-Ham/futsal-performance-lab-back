package com.alpaca.futsal_performance_lab_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tag")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @Column(name = "tag_id")
    private String tagId;

    @Column(name = "assigned")
    private boolean assigned;

    @ManyToOne
    @JoinColumn(name = "stadium_id", referencedColumnName = "stadium_id", nullable = false)
    private Stadium stadium;
}
