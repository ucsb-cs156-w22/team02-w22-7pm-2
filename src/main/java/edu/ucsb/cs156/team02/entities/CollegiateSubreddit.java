package edu.ucsb.cs156.team02.entities;

import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data //idk if i need to add this line
@AllArgsConstructor //idk if i need to add this line
@NoArgsConstructor //idk if i need to add this line
@Builder //idk if i need to add this line
@Entity(name = "collegiate_subreddits")
public class CollegiateSubreddit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    private String name;
    private String location;
    private String subreddit;
}
