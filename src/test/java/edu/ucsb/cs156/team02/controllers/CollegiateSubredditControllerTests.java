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
    
	// Authorization tests for /api/collegiateSubreddits

    @Test
    public void api_collegiateSubreddits__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/collegiateSubreddits"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiateSubreddits__logged_in__returns__200() throws Exception {
        mockMvc.perform(get("/api/collegiateSubreddits"))
                .andExpect(status().isOk());
    }

	//delet test 200
	@WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddit_logged_in__delete_collegiateSubreddit() throws Exception {
        // arrange

        when(collegiateSubredditRepository.findById(eq(1L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(delete("/api/collegiateSubreddits?id=1").with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(collegiateSubredditRepository, times(1)).findById(1L);
        verify(collegiateSubredditRepository, times(1)).deleteById(1L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("CollegiateSubreddit with id 1 deleted", responseString);
    }

	//delete test 403
	@WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddit_logged_in__delete_collegiateSubreddit_that_does_not_exist() throws Exception {
        // arrange

    
        when(collegiateSubredditRepository.findById(eq(15L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(delete("/api/collegiateSubreddits?id=15").with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(collegiateSubredditRepository, times(1)).findById(15L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("CollegiateSubreddit with id 15 not found", responseString);
    }

	//put test 200
	@WithMockUser(roles = { "USER" })
    @Test
    public void api_CollegiateSubreddit_logged_in__put_collegiateSubreddit() throws Exception {
        // arrange

        //User u = currentUserService.getCurrentUser().getUser();
        //User otherUser = User.builder().id(999).build();
        CollegiateSubreddit reddit1 = CollegiateSubreddit.builder().name("College 1").location("Location 1").subreddit("Subreddit 1").id(67L).build();
        // We deliberately set the user information to another user
        // This shoudl get ignored and overwritten with currrent user when todo is saved

        CollegiateSubreddit updatedCollegiateSubreddit = CollegiateSubreddit.builder().name("College 1").location("Location 1").subreddit("Subreddit 1").id(67L).build();
        CollegiateSubreddit correctCollegiateSubreddit = CollegiateSubreddit.builder().name("College 1").location("Location 1").subreddit("Subreddit 1").id(67L).build();

        String requestBody = mapper.writeValueAsString(updatedCollegiateSubreddit);
        String expectedReturn = mapper.writeValueAsString(correctCollegiateSubreddit);

        when(collegiateSubredditRepository.findById(eq(67L))).thenReturn(Optional.of(reddit1));

        // act
        MvcResult response = mockMvc.perform(put("api/collegiateSubreddits?id=67").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(collegiateSubredditRepository, times(1)).findById(67L);
        verify(collegiateSubredditRepository, times(1)).save(correctCollegiateSubreddit); // should be saved with correct user
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedReturn, responseString);
    }

	//put test 403
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiateSubreddit_logged_in__cannot_put_collegiateSubreddit_that_does_not_exist() throws Exception {
        // arrange

		CollegiateSubreddit updatedCollegiateSubreddit = CollegiateSubreddit.builder().name("College 1").location("Location 1").subreddit("Subreddit 1").id(67L).build();

        String requestBody = mapper.writeValueAsString(updatedCollegiateSubreddit);

        when(collegiateSubredditRepository.findById(eq(67L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                put("api/collegiateSubreddits?id=67")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(collegiateSubredditRepository, times(1)).findById(67L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("CollegiateSubreddit with id 67 not found", responseString);
    }

}
