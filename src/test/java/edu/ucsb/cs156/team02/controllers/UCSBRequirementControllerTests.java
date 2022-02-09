package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.entities.UCSBRequirement;
import edu.ucsb.cs156.team02.repositories.UCSBRequirementRepository;
import edu.ucsb.cs156.team02.controllers.UCSBRequirementController;

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
        UserRepository userRepository;

        // Authorization tests for /api/UCSBRequirement/admin/all

        @Test
        public void api_UCSBRequirement_admin_all__logged_out__returns_403() throws Exception {
                mockMvc.perform(get("/api/UCSBRequirement/admin/all"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void api_UCSBRequirement_admin_all__user_logged_in__returns_403() throws Exception {
                mockMvc.perform(get("/api/UCSBRequirement/admin/all"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void api_UCSBRequirement_admin__user_logged_in__returns_403() throws Exception {
                mockMvc.perform(get("/api/UCSBRequirement/admin?id=7"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void api_UCSBRequirement_admin_all__admin_logged_in__returns_200() throws Exception {
                mockMvc.perform(get("/api/UCSBRequirement/admin/all"))
                                .andExpect(status().isOk());
        }

        // Authorization tests for /api/UCSBRequirement/all

        @Test
        public void api_UCSBRequirement_all__logged_out__returns_403() throws Exception {
                mockMvc.perform(get("/api/UCSBRequirement/all"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void api_UCSBRequirement_all__user_logged_in__returns_200() throws Exception {
                mockMvc.perform(get("/api/UCSBRequirement/all"))
                                .andExpect(status().isOk());
        }

        // Authorization tests for /api/UCSBRequirement/post

        @Test
        public void api_UCSBRequirement_post__logged_out__returns_403() throws Exception {
                mockMvc.perform(post("/api/UCSBRequirement/post"))
                                .andExpect(status().is(403));
        }

        // Tests with mocks for database actions

        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void api_UCSBRequirement_returns_a_UCSBRequirement_that_exists() throws Exception {

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
                when(UCSBRequirementRepository.findById(eq(1L))).thenReturn(Optional.of(UCSBRequirement1));

                // act
                MvcResult response = mockMvc.perform(get("/api/UCSBRequirement?id=7"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(UCSBrequirementRepository, times(1)).findById(eq(7L));
                String expectedJson = mapper.writeValueAsString(UCSBRequirement1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void api_UCSBRequirement__user_logged_in__search_for_UCSBRequirement_that_does_not_exist()
                        throws Exception {

                // arrange

                User u = currentUserService.getCurrentUser().getUser();

                when(UCSBRequirementRepository.findById(eq(7L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/UCSBRequirement?id=7"))
                                .andExpect(status().isBadRequest()).andReturn();

                // assert

                verify(UCSBrequirementRepository, times(1)).findById(eq(7L));
                String responseString = response.getResponse().getContentAsString();
                assertEquals("UCSBRequirement with id 7 not found", responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void api_UCSBRequirement__user_logged_in__search_for_UCSBRequirement_that_belongs_to_another_user()
                        throws Exception {

                // arrange

                User u = currentUserService.getCurrentUser().getUser();
                User otherUser = User.builder().id(999L).build();
                UCSBRequirement otherUsersUCSBRequirement = UCSBRequirement.builder().title("UCSBRequirement 1")
                                .details("UCSBRequirement 1").done(false).user(otherUser)
                                .id(13L)
                                .build();

                when(UCSBRequirementRepository.findById(eq(13L))).thenReturn(Optional.of(otherUsersUCSBRequirement));

                // act
                MvcResult response = mockMvc.perform(get("/api/UCSBRequirement?id=13"))
                                .andExpect(status().isBadRequest()).andReturn();

                // assert

                verify(UCSBRequirementRepository, times(1)).findById(eq(13L));
                String responseString = response.getResponse().getContentAsString();
                assertEquals("UCSBRequirement with id 13 not found", responseString);
        }

        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void api_UCSBRequirement__admin_logged_in__search_for_UCSBRequirement_that_belongs_to_another_user()
                        throws Exception {

                // arrange

                User u = currentUserService.getCurrentUser().getUser();
                User otherUser = User.builder().id(999L).build();
                UCSBRequirement otherUsersUCSBRequirement = UCSBRequirement.builder().title("UCSBRequirement 1")
                                .details("UCSBRequirement 1").done(false).user(otherUser)
                                .id(27L)
                                .build();

                when(UCSBRequirementRepository.findById(eq(27L))).thenReturn(Optional.of(otherUsersUCSBRequirement));

                // act
                MvcResult response = mockMvc.perform(get("/api/UCSBRequirement/admin?id=27"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(UCSBRequirementRepository, times(1)).findById(eq(27L));
                String expectedJson = mapper.writeValueAsString(otherUsersUCSBRequirement);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void api_UCSBRequirement__admin_logged_in__search_for_UCSBRequirement_that_does_not_exist()
                        throws Exception {

                // arrange

                when(UCSBRequirementRepository.findById(eq(29L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/UCSBRequirement/admin?id=29"))
                                .andExpect(status().isBadRequest()).andReturn();

                // assert

                verify(UCSBRequirementRepository, times(1)).findById(eq(29L));
                String responseString = response.getResponse().getContentAsString();
                assertEquals("UCSBRequirement with id 29 not found", responseString);
        }

        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void api_UCSBRequirement_admin_all__admin_logged_in__returns_all_UCSBRequirement() throws Exception {

                // arrange

                User u1 = User.builder().id(1L).build();
                User u2 = User.builder().id(2L).build();
                User u = currentUserService.getCurrentUser().getUser();

                UCSBRequirement UCSBRequirement1 = UCSBRequirement.builder().title("UCSBRequirement 1")
                                .details("UCSBRequirement 1").done(false).user(u1).id(1L).build();
                UCSBRequirement UCSBRequirement2 = UCSBRequirement.builder().title("UCSBRequirement 2")
                                .details("UCSBRequirement 2").done(false).user(u2).id(2L).build();
                UCSBRequirement UCSBRequirement3 = UCSBRequirement.builder().title("UCSBRequirement 3")
                                .details("UCSBRequirement 3").done(false).user(u).id(3L).build();

                ArrayList<UCSBRequirement> expectedUCSBRequirement = new ArrayList<>();
                expectedUCSBRequirement.addAll(Arrays.asList(UCSBRequirement1, UCSBRequirement2, UCSBRequirement3));

                when(UCSBRequirementRepository.findAll()).thenReturn(expectedUCSBRequirement);

                // act
                MvcResult response = mockMvc.perform(get("/api/UCSBRequirement/admin/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(UCSBRequirementRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedUCSBRequirement);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void api_UCSBRequirement_all__user_logged_in__returns_only_UCSBRequirement_for_user() throws Exception {

                // arrange

                User thisUser = currentUserService.getCurrentUser().getUser();

                UCSBRequirement UCSBRequirement1 = UCSBRequirement.builder().title("UCSBRequirement 1")
                                .details("UCSBRequirement 1").done(false).user(thisUser).id(1L).build();
                UCSBRequirement UCSBRequirement2 = UCSBRequirement.builder().title("UCSBRequirement 2")
                                .details("UCSBRequirement 2").done(false).user(thisUser).id(2L).build();

                ArrayList<UCSBRequirement> expectedUCSBRequirement = new ArrayList<>();
                expectedUCSBRequirement.addAll(Arrays.asList(UCSBRequirement1, UCSBRequirement2));
                when(UCSBRequirementRepository.findAllByUserId(thisUser.getId())).thenReturn(expectedUCSBRequirement);

                // act
                MvcResult response = mockMvc.perform(get("/api/UCSBRequirement/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(UCSBRequirementRepository, times(1)).findAllByUserId(eq(thisUser.getId()));
                String expectedJson = mapper.writeValueAsString(expectedUCSBRequirement);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void api_UCSBRequirement_post__user_logged_in() throws Exception {
                // arrange

                User u = currentUserService.getCurrentUser().getUser();

                UCSBRequirement expectedUCSBRequirement = UCSBRequirement.builder()
                                .title("Test Title")
                                .details("Test Details")
                                .done(true)
                                .user(u)
                                .id(0L)
                                .build();

                when(UCSBRequirementRepository.save(eq(expectedUCSBRequirement))).thenReturn(expectedUCSBRequirement);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/UCSBRequirement/post?title=Test Title&details=Test Details&done=true")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(UCSBRequirementRepository, times(1)).save(expectedUCSBRequirement);
                String expectedJson = mapper.writeValueAsString(expectedUCSBRequirement);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void api_UCSBRequirement__user_logged_in__delete_UCSBRequirement() throws Exception {
                // arrange

                User u = currentUserService.getCurrentUser().getUser();
                UCSBRequirement UCSBRequirement1 = UCSBRequirement.builder().title("UCSBRequirement 1")
                                .details("UCSBRequirement 1").done(false).user(u).id(15L).build();
                when(UCSBRequirementRepository.findById(eq(15L))).thenReturn(Optional.of(UCSBRequirement1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/UCSBRequirement?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(UCSBRequirementRepository, times(1)).findById(15L);
                verify(UCSBRequirementRepository, times(1)).deleteById(15L);
                String responseString = response.getResponse().getContentAsString();
                assertEquals("UCSBRequirement with id 15 deleted", responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void api_UCSBRequirement__user_logged_in__delete_UCSBRequirement_that_does_not_exist() throws Exception {
                // arrange

                User otherUser = User.builder().id(98L).build();
                UCSBRequirement UCSBRequirement1 = UCSBRequirement.builder().title("UCSBRequirement 1")
                                .details("UCSBRequirement 1").done(false).user(otherUser).id(15L)
                                .build();
                when(UCSBRequirementRepository.findById(eq(15L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/UCSBRequirement?id=15")
                                                .with(csrf()))
                                .andExpect(status().isBadRequest()).andReturn();

                // assert
                verify(UCSBRequirementRepository, times(1)).findById(15L);
                String responseString = response.getResponse().getContentAsString();
                assertEquals("UCSBRequirement with id 15 not found", responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void api_UCSBRequirement__user_logged_in__cannot_delete_UCSBRequirement_belonging_to_another_user()
                        throws Exception {
                // arrange

                User otherUser = User.builder().id(98L).build();
                UCSBRequirement UCSBRequirement1 = UCSBRequirement.builder().title("UCSBRequirement 1")
                                .details("UCSBRequirement 1").done(false).user(otherUser).id(31L)
                                .build();
                when(UCSBRequirementRepository.findById(eq(31L))).thenReturn(Optional.of(UCSBRequirement1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/UCSBRequirement?id=31")
                                                .with(csrf()))
                                .andExpect(status().isBadRequest()).andReturn();

                // assert
                verify(UCSBRequirementRepository, times(1)).findById(31L);
                String responseString = response.getResponse().getContentAsString();
                assertEquals("UCSBRequirement with id 31 not found", responseString);
        }

        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void api_UCSBRequirement__admin_logged_in__delete_UCSBRequirement() throws Exception {
                // arrange

                User otherUser = User.builder().id(98L).build();
                UCSBRequirement UCSBRequirement1 = UCSBRequirement.builder().title("UCSBRequirement 1")
                                .details("UCSBRequirement 1").done(false).user(otherUser).id(16L)
                                .build();
                when(UCSBRequirementRepository.findById(eq(16L))).thenReturn(Optional.of(UCSBRequirement1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/UCSBRequirement/admin?id=16")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(UCSBRequirementRepository, times(1)).findById(16L);
                verify(UCSBRequirementRepository, times(1)).deleteById(16L);
                String responseString = response.getResponse().getContentAsString();
                assertEquals("UCSBRequirement with id 16 deleted", responseString);
        }

        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void api_UCSBRequirement__admin_logged_in__cannot_delete_UCSBRequirement_that_does_not_exist()
                        throws Exception {
                // arrange

                when(UCSBRequirementRepository.findById(eq(17L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/UCSBRequirement/admin?id=17")
                                                .with(csrf()))
                                .andExpect(status().isBadRequest()).andReturn();

                // assert
                verify(UCSBRequirementRepository, times(1)).findById(17L);
                String responseString = response.getResponse().getContentAsString();
                assertEquals("UCSBRequirement with id 17 not found", responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void api_UCSBRequirement__user_logged_in__put_UCSBRequirement() throws Exception {
                // arrange

                User u = currentUserService.getCurrentUser().getUser();
                User otherUser = User.builder().id(999).build();
                UCSBRequirement UCSBRequirement1 = UCSBRequirement.builder().title("UCSBRequirement 1")
                                .details("UCSBRequirement 1").done(false).user(u).id(67L).build();
                // We deliberately set the user information to another user
                // This shoudl get ignored and overwritten with currrent user when
                // UCSBRequirement is saved

                UCSBRequirement updatedUCSBRequirement = UCSBRequirement.builder().title("New Title")
                                .details("New Details").done(true).user(otherUser)
                                .id(67L)
                                .build();
                UCSBRequirement correctUCSBRequirement = UCSBRequirement.builder().title("New Title")
                                .details("New Details").done(true).user(u).id(67L)
                                .build();

                String requestBody = mapper.writeValueAsString(updatedUCSBRequirement);
                String expectedReturn = mapper.writeValueAsString(correctUCSBRequirement);

                when(UCSBRequirementRepository.findById(eq(67L))).thenReturn(Optional.of(UCSBRequirement1));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/UCSBRequirement?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(UCSBRequirementRepository, times(1)).findById(67L);
                verify(UCSBRequirementRepository, times(1)).save(correctUCSBRequirement); // should be saved with
                                                                                          // correct user
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedReturn, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void api_UCSBRequirement__user_logged_in__cannot_put_UCSBRequirement_that_does_not_exist()
                        throws Exception {
                // arrange

                UCSBRequirement updatedUCSBRequirement = UCSBRequirement.builder().title("New Title")
                                .details("New Details").done(true).id(67L).build();

                String requestBody = mapper.writeValueAsString(updatedUCSBRequirement);

                when(UCSBRequirementRepository.findById(eq(67L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/UCSBRequirement?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isBadRequest()).andReturn();

                // assert
                verify(UCSBRequirementRepository, times(1)).findById(67L);
                String responseString = response.getResponse().getContentAsString();
                assertEquals("UCSBRequirement with id 67 not found", responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void api_UCSBRequirement__user_logged_in__cannot_put_UCSBRequirement_for_another_user()
                        throws Exception {
                // arrange

                User otherUser = User.builder().id(98L).build();
                UCSBRequirement UCSBRequirement1 = UCSBRequirement.builder().title("UCSBRequirement 1")
                                .details("UCSBRequirement 1").done(false).user(otherUser).id(31L)
                                .build();
                UCSBRequirement updatedUCSBRequirement = UCSBRequirement.builder().title("New Title")
                                .details("New Details").done(true).id(31L).build();

                when(UCSBRequirementRepository.findById(eq(31L))).thenReturn(Optional.of(UCSBRequirement1));

                String requestBody = mapper.writeValueAsString(updatedUCSBRequirement);

                when(UCSBRequirementRepository.findById(eq(67L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/UCSBRequirement?id=31")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isBadRequest()).andReturn();

                // assert
                verify(UCSBRequirementRepository, times(1)).findById(31L);
                String responseString = response.getResponse().getContentAsString();
                assertEquals("UCSBRequirement with id 31 not found", responseString);
        }

        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void api_UCSBRequirement__admin_logged_in__put_UCSBRequirement() throws Exception {
                // arrange

                User otherUser = User.builder().id(255L).build();
                UCSBRequirement UCSBRequirement1 = UCSBRequirement.builder().title("UCSBRequirement 1")
                                .details("UCSBRequirement 1").done(false).user(otherUser).id(77L)
                                .build();
                User yetAnotherUser = User.builder().id(512L).build();
                // We deliberately put the wrong user on the updated UCSBRequirement
                // We expect the controller to ignore this and keep the user the same
                UCSBRequirement updatedUCSBRequirement = UCSBRequirement.builder().title("New Title")
                                .details("New Details").done(true)
                                .user(yetAnotherUser)
                                .id(77L)
                                .build();
                UCSBRequirement correctUCSBRequirement = UCSBRequirement.builder().title("New Title")
                                .details("New Details").done(true).user(otherUser)
                                .id(77L)
                                .build();

                String requestBody = mapper.writeValueAsString(updatedUCSBRequirement);
                String expectedJson = mapper.writeValueAsString(correctUCSBRequirement);

                when(UCSBRequirementRepository.findById(eq(77L))).thenReturn(Optional.of(UCSBRequirement1));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/UCSBRequirement/admin?id=77")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(UCSBRequirementRepository, times(1)).findById(77L);
                verify(UCSBRequirementRepository, times(1)).save(correctUCSBRequirement);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void api_UCSBRequirement__admin_logged_in__cannot_put_UCSBRequirement_that_does_not_exist()
                        throws Exception {
                // arrange

                User otherUser = User.builder().id(345L).build();
                UCSBRequirement updatedUCSBRequirement = UCSBRequirement.builder().title("New Title")
                                .details("New Details").done(true).user(otherUser)
                                .id(77L)
                                .build();

                String requestBody = mapper.writeValueAsString(updatedUCSBRequirement);

                when(UCSBRequirementRepository.findById(eq(77L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/UCSBRequirement/admin?id=77")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isBadRequest()).andReturn();

                // assert
                verify(UCSBRequirementRepository, times(1)).findById(77L);
                String responseString = response.getResponse().getContentAsString();
                assertEquals("UCSBRequirement with id 77 not found", responseString);
        }

}
