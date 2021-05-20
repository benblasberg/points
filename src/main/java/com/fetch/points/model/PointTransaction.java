package com.fetch.points.model;

import java.time.Instant;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class PointTransaction {

    @JsonIgnore
    @Id
    @GeneratedValue
    private long id;

    private String payer;

    private int points;

    private Instant timestamp;

    @JsonIgnore
    private boolean processed;
}
