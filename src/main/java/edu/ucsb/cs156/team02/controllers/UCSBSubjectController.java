package edu.ucsb.cs156.team02.controllers;
import edu.ucsb.cs156.team02.entities.UCSBSubject;
import edu.ucsb.cs156.team02.repositories.UCSBSubjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import edu.ucsb.cs156.team02.services.LoggingService;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
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
    public class UCSBSubjectOrError {
        Long id;
        UCSBSubject sub;
        ResponseEntity<String> error;

        public UCSBSubjectOrError(Long id) {
            this.id = id;
        }
    }

    @Autowired
    UCSBSubjectRepository ucsbsubjectRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "Get a list of UCSB subjects")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
        public Iterable<UCSBSubject> UCSBSubjectInfo() {
            loggingService.logMethod();
            //UCSBSubject ucsbsub = getUCSBSubject();
            Iterable<UCSBSubject> subjects = ucsbsubjectRepository.findAll();
            return subjects;
        }

    @ApiOperation(value = "Create a new UCSB subject")
    //@PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public UCSBSubject postUCSBSubject(
            @ApiParam("subject Translation") @RequestParam String subjectTranslation,
            @ApiParam("dept Code") @RequestParam String deptCode,
            @ApiParam("college Code") @RequestParam String collegeCode,
            @ApiParam("subject Code") @RequestParam String subjectCode,
            @ApiParam("related Dept Code") @RequestParam String relatedDeptCode,
            @ApiParam("inactive") @RequestParam Boolean inactive) {
        loggingService.logMethod();
        //CurrentUser currentUser = getCurrentUser();
        //log.info("currentUser={}", currentUser);

        UCSBSubject ucsbsubject = new UCSBSubject();
        //ucsbsubject.setUser(currentUser.getUser());
        ucsbsubject.setSubjectTranslation(subjectTranslation);
        ucsbsubject.setDeptCode(deptCode);
        ucsbsubject.setCollegeCode(collegeCode);
        ucsbsubject.setSubjectCode(subjectCode);
        ucsbsubject.setRelatedDeptCode(relatedDeptCode);
        ucsbsubject.setInactive(inactive);
        UCSBSubject savedUCSBSubject = ucsbsubjectRepository.save(ucsbsubject);
        return savedUCSBSubject;
    }

    @ApiOperation(value = "Get a UCSB Subject with given id")
    //@PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<String> getUCSBSubjectById(
        @ApiParam("id") @RequestParam Long id) throws JsonProcessingException {
        loggingService.logMethod();

        UCSBSubjectOrError ucsbsub_error = new UCSBSubjectOrError(id);

        ucsbsub_error = doesUCSBSubjectExist(ucsbsub_error);
        if (ucsbsub_error.error != null) {
            return ucsbsub_error.error;
        }

        String body = mapper.writeValueAsString(ucsbsub_error.sub);
        return ResponseEntity.ok().body(body);
    }

    @ApiOperation(value = "Update a single UCSBSubject")
    @PutMapping("")
    public ResponseEntity<String> putSubjectById(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid UCSBSubject incomingUCSBSubject) throws JsonProcessingException {
        loggingService.logMethod();

        UCSBSubjectOrError ucsbsub_error = new UCSBSubjectOrError(id);

        ucsbsub_error = doesUCSBSubjectExist(ucsbsub_error);
        if (ucsbsub_error.error != null) {
            return ucsbsub_error.error;
        }

        ucsbsubjectRepository.save(incomingUCSBSubject);

        String body = mapper.writeValueAsString(incomingUCSBSubject);
        return ResponseEntity.ok().body(body);
    }

        public UCSBSubjectOrError doesUCSBSubjectExist(UCSBSubjectOrError ucsbsub_error) {

            Optional<UCSBSubject> optionalUCSBSubject = ucsbsubjectRepository.findById(ucsbsub_error.id);

            if (optionalUCSBSubject.isEmpty()) {
                ucsbsub_error.error = ResponseEntity
                        .badRequest()
                        .body(String.format("ucsb subject with id %d not found", ucsbsub_error.id));
            } else {
                ucsbsub_error.sub = optionalUCSBSubject.get();
            }
            return ucsbsub_error;
        }



}
