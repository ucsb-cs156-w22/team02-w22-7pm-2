package edu.ucsb.cs156.team02.entities;

import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import lombok.*;
import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "ucsb_subjects")
public class UCSBSubjects {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String subjectCode;
    private String subjectTranslation;
    private String deptCode;
    private String collegeCode;
    private String relatedDeptCode;
    private String inactive;
    // etc.
}