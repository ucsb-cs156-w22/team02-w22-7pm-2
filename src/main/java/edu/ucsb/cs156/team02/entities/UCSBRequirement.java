package edu.ucsb.cs156.team02.entities;

import javax.persistence.*;
import lombok.*;

@Data //idk if i need to add this line
@AllArgsConstructor //idk if i need to add this line
@NoArgsConstructor //idk if i need to add this line
@Builder //idk if i need to add this line

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
