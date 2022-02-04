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
    public void api_ucsbsubject_all__returns_all_ucsbsubjects() throws Exception {

        // arrange

        UCSBSubject subject1 = UCSBSubject.builder().subjectCode("SC 1").subjectTranslation("ST 1").deptCode("DC 1").collegeCode("CC 1").relatedDeptCode("RDC 1").inactive(false).id(1L).build();
        UCSBSubject subject2 = UCSBSubject.builder().subjectCode("SC 2").subjectTranslation("ST 2").deptCode("DC 2").collegeCode("CC 2").relatedDeptCode("RDC 2").inactive(false).id(2L).build();
        UCSBSubject subject3 = UCSBSubject.builder().subjectCode("SC 3").subjectTranslation("ST 3").deptCode("DC 3").collegeCode("CC 3").relatedDeptCode("RDC 3").inactive(false).id(3L).build();

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
                .subjectCode("Test Subject Code")
                .subjectTranslation("Test Subject Translation")
                .deptCode("Test Department Code")
                .collegeCode("Test College Code")
                .relatedDeptCode("Test related department code")
                .inactive(true)
                .id(0L)
                .build();

        when(ucsbsubjectRepository.save(eq(expectedSubject))).thenReturn(expectedSubject);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/UCSBSubjects/post?subjectCode=Test Subject Code&subjectTranslation=Test Subject Translation&deptCode=Test Department Code&collegeCode=Test College Code&relatedDeptCode=Test related department code&inactive=true")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(ucsbsubjectRepository, times(1)).save(expectedSubject);
        String expectedJson = mapper.writeValueAsString(expectedSubject);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }
}