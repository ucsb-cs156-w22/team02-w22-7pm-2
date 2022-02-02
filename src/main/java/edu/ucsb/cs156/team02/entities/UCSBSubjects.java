package edu.ucsb.cs156.team02.entities;

import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "ucsbsubjects")
public class UCSBSubjects {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

}