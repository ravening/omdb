package com.ravening.omdbApplication.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_generator")
    @SequenceGenerator(name = "seq_generator", sequenceName = "my_seq", allocationSize = 1)
    @Column(name = "id", nullable = false, updatable = false)
    Long id;

    String year;

    String category;

    String nominee;

    String description;

    boolean won;

    double ratings;

    double votes;

    @Column(name = "boxofficevalue")
    double boxOfficeValue;

}
