package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.UCSBSubject;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.repositories.UCSBSubjectRepository;
import edu.ucsb.cs156.team02.controllers.UCSBSubjectController;

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


        //test for /all endpoint
    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_all() throws Exception {

        // arrange
        UCSBSubject subject1 = UCSBSubject.builder().subjectTranslation("ST 1").deptCode("DC 1").collegeCode("CC 1").subjectCode("SC 1").relatedDeptCode("RDC 1").inactive(false).build();
        UCSBSubject subject2 = UCSBSubject.builder().subjectTranslation("ST 2").deptCode("DC 2").collegeCode("CC 2").subjectCode("SC 2").relatedDeptCode("RDC 2").inactive(false).build();
        UCSBSubject subject3 = UCSBSubject.builder().subjectTranslation("ST 3").deptCode("DC 3").collegeCode("CC 3").subjectCode("SC 3").relatedDeptCode("RDC 3").inactive(false).build();

        ArrayList<UCSBSubject> expectedUCSBSubjects = new ArrayList<>();
        expectedUCSBSubjects.addAll(Arrays.asList(subject1, subject2, subject3));

        when(ucsbsubjectRepository.findAll()).thenReturn(expectedUCSBSubjects);

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBSubjects/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(ucsbsubjectRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedUCSBSubjects);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }


        //tests for /post endpoint
    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_post() throws Exception {
        // arrange
        UCSBSubject expectedSubject = UCSBSubject.builder()
                .subjectTranslation("Test Subject Translation")
                .deptCode("Test Department Code")
                .collegeCode("Test College Code")
                .subjectCode("Test Subject Code")
                .relatedDeptCode("Test related department code")
                .inactive(true)
                .id(0L)
                .build();

        when(ucsbsubjectRepository.save(eq(expectedSubject))).thenReturn(expectedSubject);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/UCSBSubjects/post?subjectTranslation=Test Subject Translation&deptCode=Test Department Code&collegeCode=Test College Code&subjectCode=Test Subject Code&relatedDeptCode=Test related department code&inactive=true&id=0L")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(ucsbsubjectRepository, times(1)).save(expectedSubject);
        String expectedJson = mapper.writeValueAsString(expectedSubject);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }


        //test for /get with id
    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_get_id_returns_a_subject_that_exists() throws Exception {

        // arrange
        UCSBSubject expectedUCSBSubject = UCSBSubject.builder().id(1L).subjectCode("code 1").subjectTranslation("translation 1").deptCode("dept code 1").collegeCode("college code 1").relatedDeptCode("related dept code 1").inactive(true).build();
        when(ucsbsubjectRepository.findById(eq(1L))).thenReturn(Optional.of(expectedUCSBSubject));

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBSubjects?id=1"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(ucsbsubjectRepository, times(1)).findById(eq(1L));
        String expectedJson = mapper.writeValueAsString(expectedUCSBSubject);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    //Test api /get UCSB Subject id that doesn't exist
    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_get_id_returns_a_subject_that_does_not_exist() throws Exception {

        // arrange
        when(ucsbsubjectRepository.findById(eq(7L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBSubjects?id=7"))
                .andExpect(status().isBadRequest()).andReturn();

        // assert

        verify(ucsbsubjectRepository, times(1)).findById(eq(7L));
        String responseString = response.getResponse().getContentAsString();
        assertEquals("ucsb subject with id 7 not found", responseString);
    }


    //Test api /put subject given id
    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_subject_put_subject() throws Exception {
        // arrange

        UCSBSubject ucsbsubject1 = UCSBSubject.builder()
                .subjectTranslation("Test Subject Translation")
                .deptCode("Test Department Code")
                .collegeCode("Test College Code")
                .subjectCode("Test Subject Code")
                .relatedDeptCode("Test related department code")
                .inactive(true)
                .id(77L)
                .build();

        UCSBSubject updatedUCSBSubject = UCSBSubject.builder().subjectTranslation("translation 1").deptCode("dept code 1").collegeCode("college code 1").subjectCode("code 2").relatedDeptCode("related dept code 1").inactive(false).id(77L).build();
        UCSBSubject correctUCSBSubject = UCSBSubject.builder().subjectTranslation("translation 1").deptCode("dept code 1").collegeCode("college code 1").subjectCode("code 2").relatedDeptCode("related dept code 1").inactive(false).id(77L).build();

        String requestBody = mapper.writeValueAsString(updatedUCSBSubject);
        String expectedReturn = mapper.writeValueAsString(correctUCSBSubject);

        when(ucsbsubjectRepository.findById(eq(77L))).thenReturn(Optional.of(ucsbsubject1));

        // act
        MvcResult response = mockMvc.perform(
                put("/api/UCSBSubjects?id=77")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(ucsbsubjectRepository, times(1)).findById(77L);
        verify(ucsbsubjectRepository, times(1)).save(correctUCSBSubject);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedReturn, responseString);
    }

    // Test api /put with Subject id that doesn't exist
    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_subject_put_that_does_not_exist() throws Exception {
        // arrange

        UCSBSubject updatedUCSBSubject = UCSBSubject.builder()
                .subjectTranslation("Test Subject Translation")
                .deptCode("Test Department Code")
                .collegeCode("Test College Code")
                .subjectCode("Test Subject Code")
                .relatedDeptCode("Test related department code")
                .inactive(true)
                .id(77L)
                .build();

        String requestBody = mapper.writeValueAsString(updatedUCSBSubject);

        when(ucsbsubjectRepository.findById(eq(77L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                put("/api/UCSBSubjects?id=77")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(ucsbsubjectRepository, times(1)).findById(77L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("ucsb subject with id 77 not found", responseString);
    }

        //test delete endpoint
        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void api_subject__delete_subject() throws Exception {
                // arrange

                UCSBSubject subject1 = UCSBSubject.builder()
                .subjectTranslation("Test Subject Translation")
                .deptCode("Test Department Code")
                .collegeCode("Test College Code")
                .subjectCode("Test Subject Code")
                .relatedDeptCode("Test related department code")
                .inactive(true)
                .id(15L)
                .build();

                when(ucsbsubjectRepository.findById(eq(15L))).thenReturn(Optional.of(subject1));

                // act
                MvcResult response = mockMvc.perform(
                        delete("/api/UCSBSubjects?id=15")
                                .with(csrf()))
                        .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbsubjectRepository, times(1)).findById(15L);
                verify(ucsbsubjectRepository, times(1)).deleteById(15L);
                String responseString = response.getResponse().getContentAsString();
                assertEquals("ucsb subject with id 15 deleted", responseString);
        }

        // Test api /delete with Subject id that doesn't exist
        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void api_subject__delete_subject_that_does_not_exist() throws Exception {
                // arrange

                        UCSBSubject subject1 = UCSBSubject.builder()
                        .subjectTranslation("Test Subject Translation")
                        .deptCode("Test Department Code")
                        .collegeCode("Test College Code")
                        .subjectCode("Test Subject Code")
                        .relatedDeptCode("Test related department code")
                        .inactive(true)
                        .id(15L)
                        .build();
                        when(ucsbsubjectRepository.findById(eq(15L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                        delete("/api/UCSBSubjects?id=15")
                                .with(csrf()))
                        .andExpect(status().isBadRequest()).andReturn();

                // assert
                verify(ucsbsubjectRepository, times(1)).findById(15L);
                String responseString = response.getResponse().getContentAsString();
                assertEquals("ucsb subject with id 15 not found", responseString);
        }




}
