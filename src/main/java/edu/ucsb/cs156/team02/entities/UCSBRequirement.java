package edu.ucsb.cs156.team02.entities;

import javax.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity(name = "ucsb_requirements")
public class UCSBRequirement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String requirementCode;
    private String requirementTranslation;
    private String collegeCode;
    private String objCode;
    private int courseCount;
    private int units;
    private boolean inactive;
}
