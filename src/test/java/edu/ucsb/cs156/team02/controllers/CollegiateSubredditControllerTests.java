package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.CollegiateSubreddit;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.repositories.CollegiateSubredditRepository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = CollegiateSubredditController.class)
@Import(TestConfig.class)

public class CollegiateSubredditControllerTests extends ControllerTestCase{
    
    @MockBean
    CollegiateSubredditRepository collegiateSubredditRepository;

    @MockBean
    UserRepository userRepository;

    // ah - No authorization tests since the user doesn't have to be logged in
    // to get all collegiate subreddits with /api/collegiateSubreddits/all


    // __________________________________________________________________________________________________________
    // Authorization tests for /api/collegiateSubreddits/post

    @Test
    public void api_collegiateSubreddits_post__logged_out__returns_403() throws Exception {
        mockMvc.perform(post("/api/collegiateSubreddits/post"))
                .andExpect(status().is(403));
    }

    // __________________________________________________________________________________________________________
    // Functionality test for /api/collegiateSubreddits/all
    @Test
    public void api_collegiateSubreddits_all__returns_all_collegiateSubreddits() throws Exception {

        // arrange

        CollegiateSubreddit reddit1 = CollegiateSubreddit.builder().name("College 1").location("Location 1").subreddit("Subreddit 1").id(1L).build();
        CollegiateSubreddit reddit2 = CollegiateSubreddit.builder().name("College 2").location("Location 2").subreddit("Subreddit 2").id(2L).build();
        CollegiateSubreddit reddit3 = CollegiateSubreddit.builder().name("College 3").location("Location 3").subreddit("Subreddit 3").id(3L).build();

        ArrayList<CollegiateSubreddit> expectedReddits = new ArrayList<>();
        expectedReddits.addAll(Arrays.asList(reddit1, reddit2, reddit3));

        when(collegiateSubredditRepository.findAll()).thenReturn(expectedReddits);

        // act
        MvcResult response = mockMvc.perform(get("/api/collegiateSubreddits/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(collegiateSubredditRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedReddits);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    // __________________________________________________________________________________________________________
    // Functionality test for /api/collegiateSubreddits/post
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiateSubreddits_post__user_logged_in() throws Exception {
        // arrange

        CollegiateSubreddit expectedReddit = CollegiateSubreddit.builder()
                .name("Test Name")
                .location("Test Location")
                .subreddit("Test Subreddit")
                .id(0L)
                .build();

        when(collegiateSubredditRepository.save(eq(expectedReddit))).thenReturn(expectedReddit);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/collegiateSubreddits/post?name=Test Name&location=Test Location&subreddit=Test Subreddit")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(collegiateSubredditRepository, times(1)).save(expectedReddit);
        String expectedJson = mapper.writeValueAsString(expectedReddit);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    // __________________________________________________________________________________________________________
    
}
