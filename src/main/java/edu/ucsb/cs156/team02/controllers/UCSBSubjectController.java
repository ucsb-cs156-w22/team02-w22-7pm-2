package edu.ucsb.cs156.team02.controllers;
import edu.ucsb.cs156.team02.entities.UCSBSubject;
import edu.ucsb.cs156.team02.repositories.UCSBSubjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import edu.ucsb.cs156.team02.services.LoggingService;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
import org.springframework.web.bind.annotation.DeleteMapping;

import edu.ucsb.cs156.team02.services.CurrentUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.validation.Valid;
import java.lang.String;
import java.lang.Boolean;
import java.util.Optional;



@Api(description="UCSB Subject Information")
@RequestMapping("/api/UCSBSubjects")
@RestController
@Slf4j
public class UCSBSubjectController extends ApiController{
<<<<<<< HEAD
=======
    public class UCSBSubjectOrError {
        Long id;
        UCSBSubject ucsbsubject;
        ResponseEntity<String> error;

        public UCSBSubjectOrError(Long id) {
            this.id = id;
        }
    }

    @ApiOperation(value = "Get a list of UCSB subjects")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("")
    public CurrentUser getCurrentUser() {
        return super.getCurrentUser();
    }


>>>>>>> de5ccb0e193182df7b02b893807d139a21da16c8
    @Autowired
    UCSBSubjectRepository ucsbsubjectRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "Get a list of UCSB subjects")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
        public Iterable<UCSBSubject> allUCSBSubjects() {
        //loggingService.logMethod();
        Iterable<UCSBSubject> subjects = ucsbsubjectRepository.findAll();
        return subjects;
    }

    @ApiOperation(value = "Create a new UCSB subject")
    //@PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public UCSBSubject postUCSBSubject(
<<<<<<< HEAD
            @ApiParam("subjectTranslation") @RequestParam String subjectTranslation,
            @ApiParam("deptCode") @RequestParam String deptCode,
            @ApiParam("collegeCode") @RequestParam String collegeCode,
            @ApiParam("subjectCode") @RequestParam String subjectCode,
            @ApiParam("relatedDeptCode") @RequestParam String relatedDeptCode,
=======
            @ApiParam("subject Translation") @RequestParam String subjectTranslation,
            @ApiParam("id") @RequestParam Long id,
            @ApiParam("dept Code") @RequestParam String deptCode,
            @ApiParam("college Code") @RequestParam String collegeCode,
            @ApiParam("subject Code") @RequestParam String subjectCode,
            @ApiParam("related Dept Code") @RequestParam String relatedDeptCode,
>>>>>>> de5ccb0e193182df7b02b893807d139a21da16c8
            @ApiParam("inactive") @RequestParam Boolean inactive) {
        loggingService.logMethod();
        //CurrentUser currentUser = getCurrentUser();
        //log.info("currentUser={}", currentUser);

        UCSBSubject ucsbsubject = new UCSBSubject();
<<<<<<< HEAD
        //ucsbsubject.setUser(currentUser.getUser());
=======
        ucsbsubject.setUser(currentUser.getUser());
        ucsbsubject.setId(id);
        ucsbsubject.setSubjectCode(subjectCode);
>>>>>>> de5ccb0e193182df7b02b893807d139a21da16c8
        ucsbsubject.setSubjectTranslation(subjectTranslation);
        ucsbsubject.setDeptCode(deptCode);
        ucsbsubject.setCollegeCode(collegeCode);
        ucsbsubject.setSubjectCode(subjectCode);
        ucsbsubject.setRelatedDeptCode(relatedDeptCode);
        ucsbsubject.setInactive(inactive);
        UCSBSubject savedUCSBSubject = ucsbsubjectRepository.save(ucsbsubject);
        return savedUCSBSubject;
    }

    @ApiOperation(value = "Update a UCSB subject (if it belongs to current user)")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("")
    public ResponseEntity<String> putUCSBSubjectById(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid UCSBSubject incomingUCSBsubject) throws JsonProcessingException {
        loggingService.logMethod();

        CurrentUser currentUser = getCurrentUser();
        User user = currentUser.getUser();


        UCSBSubjectOrError ucsbsub = new UCSBSubjectOrError(id);

        ucsbsub = doesUCSBSubjectExist(ucsbsub);
        if (ucsbsub.error != null) {
            return ucsbsub.error;
        }
        ucsbsub = doesUCSBSubjectBelongToCurrentUser(ucsbsub);
        if (ucsbsub.error != null) {
            return ucsbsub.error;
        }

        incomingUCSBsubject.setUser(user);
        ucsbsubjectRepository.save(incomingUCSBsubject);

        String body = mapper.writeValueAsString(incomingUCSBsubject);
        return ResponseEntity.ok().body(body);
    }

        public UCSBSubjectOrError doesUCSBSubjectExist(UCSBSubjectOrError ucsbsub_error) {

            Optional<UCSBSubject> optionalUCSBSubject = ucsbsubjectRepository.findById(ucsbsub_error.id);

            if (optionalUCSBSubject.isEmpty()) {
                ucsbsub_error.error = ResponseEntity
                        .badRequest()
                        .body(String.format("ucsb subject with id %d not found", ucsbsub_error.id));
            } else {
                ucsbsub_error.ucsbsubject = optionalUCSBSubject.get();
            }
            return ucsbsub_error;
        }


    public UCSBSubjectOrError doesUCSBSubjectBelongToCurrentUser(UCSBSubjectOrError ucsbsub_error) {
        CurrentUser currentUser = getCurrentUser();
        log.info("currentUser={}", currentUser);

        Long currentUserId = currentUser.getUser().getId();
        Long UCSBSubjectUserId = ucsbsub_error.ucsbsubject.getUser().getId();
        log.info("currentUserId={} UCSBSubjectUserId={}", currentUserId, UCSBSubjectUserId);

        if (UCSBSubjectUserId != currentUserId) {
            ucsbsub_error.error = ResponseEntity
                    .badRequest()
                    .body(String.format("UCSB subject with id %d not found", ucsbsub_error.id));
        }
        return ucsbsub_error;
    }

}
