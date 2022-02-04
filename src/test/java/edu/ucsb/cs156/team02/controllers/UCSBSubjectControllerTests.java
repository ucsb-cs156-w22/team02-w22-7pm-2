package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.UCSBSubject;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.repositories.UCSBSubjectRepository;

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

@WebMvcTest(controllers = UCSBSubjectController.class)
@Import(TestConfig.class)
public class UCSBSubjectControllerTests extends ControllerTestCase {

    @MockBean
    UCSBSubjectRepository ucsbsubjectRepository;

    @MockBean
    UserRepository userRepository;

    // Authorization tests for /api/UCSBSubjects/post

    @Test
    public void api_ucsbsubject_post__logged_out__returns_403() throws Exception {
        mockMvc.perform(post("/api/UCSBSubjects/post"))
                .andExpect(status().is(403));
    }


    @Test
    public void api_ucsbsubject_all__admin_logged_in__returns_all_ucsbsubjects() throws Exception {

        // arrange

        UCSBSubject subject1 = UCSBSubject.builder().subjectcode("SC 1").subjecttranslation("ST 1").deptcode("DC 1").collegecode("CC 1").relateddeptcode("RDC 1").user(u1).inactive(false).id(1L).build();
        UCSBSubject subject2 = UCSBSubject.builder().subjectcode("SC 2").subjecttranslation("ST 2").deptcode("DC 2").collegecode("CC 2").relateddeptcode("RDC 2").user(u2).inactive(false).id(2L).build();
        UCSBSubject subject3 = UCSBSubject.builder().subjectcode("SC 3").subjecttranslation("ST 3").deptcode("DC 3").collegecode("CC 3").relateddeptcode("RDC 3").user(u).inactive(false).id(3L).build();

        ArrayList<UCSBSubject> expectedUCSBSubjects = new ArrayList<>();
        expectedUCSBSubjects.addAll(Arrays.asList(subject1, subject2, subject3));

        when(ucsbsubjectRepository.findAll()).thenReturn(expectedUCSBSubjects);

        // act
        MvcResult response = mockMvc.perform(get("/api/ucsbsubjects/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(ucsbsubjectRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedUCSBSubjects);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }



    @WithMockUser(roles = { "USER" })
    @Test
    public void api_ucsbsubject_post__user_logged_in() throws Exception {
        // arrange

        UCSBSubject expectedSubject = UCSBSubject.builder()
                .subjectcode("Test Subject Code")
                .subjecttranslation("Test Subject Translation")
                .deptcode("Test Department Code")
                .collegecode("Test College Code")
                .relateddeptcode("Test related department code")
                .inactive(true)
                .id(0L)
                .build();

        when(ucsbsubjectRepository.save(eq(expectedSubject))).thenReturn(expectedSubject);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/UCSBSubjects/post?subjectcode=Test Subject Code&subjecttranslation=Test Subject Translation&deptcode=Test Department Code&collegecode=Test College Code&relateddeptcode=Test related department code")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(ucsbsubjectRepository, times(1)).save(expectedSubject);
        String expectedJson = mapper.writeValueAsString(expectedSubject);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }
}