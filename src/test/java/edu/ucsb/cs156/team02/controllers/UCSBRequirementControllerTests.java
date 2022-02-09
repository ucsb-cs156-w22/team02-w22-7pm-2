package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.UCSBRequirement;
import edu.ucsb.cs156.team02.repositories.UCSBRequirementRepository;
import edu.ucsb.cs156.team02.controllers.UCSBRequirementController;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.repositories.UserRepository;

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

@WebMvcTest(controllers = UCSBRequirementController.class)
@Import(TestConfig.class)
public class UCSBRequirementControllerTests extends ControllerTestCase {

        @MockBean
        UCSBRequirementRepository UCSBrequirementRepository;
        @MockBean
        UserRepository userRepository;

        // Tests with mocks for database actions

        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void api_UCSBRequirement_returns_a_UCSBRequirement_that_exists() throws Exception {

                // arrange

                UCSBRequirement UCSBRequirement1 = UCSBRequirement.builder().id(1L).requirementCode("A1")
                                .requirementTranslation("English Reading & Composition").collegeCode("ENGR")
                                .objCode("BA").courseCount(1).units(4).inactive(false).build();
                when(UCSBrequirementRepository.findById(eq(1L))).thenReturn(Optional.of(UCSBRequirement1));

                // act
                MvcResult response = mockMvc.perform(get("/api/UCSBRequirements?id=1"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(UCSBrequirementRepository, times(1)).findById(eq(1L));
                String expectedJson = mapper.writeValueAsString(UCSBRequirement1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void api_UCSBRequirement_gets_a_UCSBRequirement_that_does_not_exist()
                        throws Exception {

                when(UCSBrequirementRepository.findById(eq(7L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/UCSBRequirements?id=7"))
                                .andExpect(status().isBadRequest()).andReturn();

                // assert

                verify(UCSBrequirementRepository, times(1)).findById(eq(7L));
                String responseString = response.getResponse().getContentAsString();
                assertEquals("UCSB requirement with id 7 not found", responseString);
        }

        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void api_UCSBRequirement_returns_all_UCSBRequirements_that_exists() throws Exception {

                // arrange

                UCSBRequirement UCSBRequirement1 = UCSBRequirement.builder().id(1L).requirementCode("A1")
                                .requirementTranslation("English Reading & Composition").collegeCode("ENGR")
                                .objCode("BA").courseCount(1).units(4).inactive(false).build();
                UCSBRequirement UCSBRequirement2 = UCSBRequirement.builder().requirementCode("B")
                                .requirementTranslation("Foreign Language - L&S").collegeCode("L&S")
                                .objCode("BA").courseCount(1).units(4).inactive(false).build();
                UCSBRequirement UCSBRequirement3 = UCSBRequirement.builder().requirementCode("C")
                                .requirementTranslation("Science, Math and Technology").collegeCode("L&S")
                                .objCode("BS").courseCount(1).units(5).inactive(false).build();

                ArrayList<UCSBRequirement> requirements = new ArrayList<>();
                requirements.add(UCSBRequirement1);
                requirements.add(UCSBRequirement2);
                requirements.add(UCSBRequirement3);
                when(UCSBrequirementRepository.findAll()).thenReturn(requirements);

                // act
                MvcResult response = mockMvc.perform(get("/api/UCSBRequirements/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(UCSBrequirementRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(requirements);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void api_UCSBRequirement_post() throws Exception {
                // arrange

                UCSBRequirement expectedUCSBRequirement = UCSBRequirement.builder().id(0L)
                                .requirementCode("Test requirement code")
                                .requirementTranslation("Test requirement translation").collegeCode("Test college code")
                                .objCode("Test object code").courseCount(1).units(4).inactive(true).build();

                when(UCSBrequirementRepository.save(eq(expectedUCSBRequirement))).thenReturn(expectedUCSBRequirement);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/UCSBRequirements/post?requirementCode=Test requirement code&requirementTranslation=Test requirement translation&collegeCode=Test college code&objCode=Test object code&courseCount=1&units=4&inactive=true&id=0L")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(UCSBrequirementRepository, times(1)).save(expectedUCSBRequirement);
                String expectedJson = mapper.writeValueAsString(expectedUCSBRequirement);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void api_UCSBRequirement_delete_UCSBRequirement() throws Exception {
                // arrange

                UCSBRequirement UCSBRequirement1 = UCSBRequirement.builder().id(1L).requirementCode("A1")
                                .requirementTranslation("English Reading & Composition").collegeCode("ENGR")
                                .objCode("BA").courseCount(1).units(4).inactive(false).build();
                when(UCSBrequirementRepository.findById(eq(1L))).thenReturn(Optional.of(UCSBRequirement1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/UCSBRequirements?id=1")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(UCSBrequirementRepository, times(1)).findById(1L);
                verify(UCSBrequirementRepository, times(1)).deleteById(1L);
                String responseString = response.getResponse().getContentAsString();
                assertEquals("UCSB requirement with id 1 deleted", responseString);
        }

        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void api_UCSBRequirement_delete_UCSBRequirement_that_does_not_exist() throws Exception {
                // arrange

                UCSBRequirement UCSBRequirement1 = UCSBRequirement.builder().id(1L).requirementCode("A1")
                                .requirementTranslation("English Reading & Composition").collegeCode("ENGR")
                                .objCode("BA").courseCount(1).units(4).inactive(false).build();
                when(UCSBrequirementRepository.findById(eq(1L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/UCSBRequirements?id=1")
                                                .with(csrf()))
                                .andExpect(status().isBadRequest()).andReturn();

                // assert
                verify(UCSBrequirementRepository, times(1)).findById(1L);
                String responseString = response.getResponse().getContentAsString();
                assertEquals("UCSB requirement with id 1 not found", responseString);
        }

        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void api_UCSBRequirement_put_UCSBRequirement() throws Exception {

                UCSBRequirement UCSBRequirement1 = UCSBRequirement.builder().id(1L).requirementCode("A1")
                                .requirementTranslation("English Reading & Composition").collegeCode("ENGR")
                                .objCode("BA").courseCount(1).units(4).inactive(false).build();

                UCSBRequirement updatedUCSBRequirement = UCSBRequirement.builder().id(1L).requirementCode("B")
                                .requirementTranslation("Foreign Language - L&S").collegeCode("L&S")
                                .objCode("BA").courseCount(1).units(4).inactive(false)
                                .build();
                UCSBRequirement correctUCSBRequirement = UCSBRequirement.builder().id(1L).requirementCode("B")
                                .requirementTranslation("Foreign Language - L&S").collegeCode("L&S")
                                .objCode("BA").courseCount(1).units(4).inactive(false)
                                .build();

                String requestBody = mapper.writeValueAsString(updatedUCSBRequirement);
                String expectedReturn = mapper.writeValueAsString(correctUCSBRequirement);

                when(UCSBrequirementRepository.findById(eq(1L))).thenReturn(Optional.of(UCSBRequirement1));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/UCSBRequirements?id=1")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(UCSBrequirementRepository, times(1)).findById(1L);
                verify(UCSBrequirementRepository, times(1)).save(correctUCSBRequirement); // should be saved with
                                                                                          // correct user
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedReturn, responseString);
        }

        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void api_UCSBRequirement_cannot_put_UCSBRequirement_that_does_not_exist()
                        throws Exception {
                // arrange

                UCSBRequirement updatedUCSBRequirement = UCSBRequirement.builder().id(1L).requirementCode("B")
                                .requirementTranslation("Foreign Language - L&S").collegeCode("L&S")
                                .objCode("BA").courseCount(1).units(4).inactive(false)
                                .build();

                String requestBody = mapper.writeValueAsString(updatedUCSBRequirement);

                when(UCSBrequirementRepository.findById(eq(1L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/UCSBRequirements?id=1")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isBadRequest()).andReturn();

                // assert
                verify(UCSBrequirementRepository, times(1)).findById(1L);
                String responseString = response.getResponse().getContentAsString();
                assertEquals("UCSB requirement with id 1 not found", responseString);
        }
}