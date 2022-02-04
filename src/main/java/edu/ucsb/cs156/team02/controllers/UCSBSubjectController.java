package edu.ucsb.cs156.team02.controllers;
import edu.ucsb.cs156.team02.entities.UCSBSubject;
import edu.ucsb.cs156.team02.repositories.UCSBSubjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

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
        UCSBSubject subjectcode;
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


    @Autowired
    UCSBSubjectRepository ucsbsubjectRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "Create a new UCSB subject")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public UCSBSubject postUCSBSubject(
            @ApiParam("subject Translation") @RequestParam String subjectTranslation,
            @ApiParam("dept Code") @RequestParam String deptCode,
            @ApiParam("college Code") @RequestParam String collegeCode,
            @ApiParam("subject Code") @RequestParam String subjectCode,
            @ApiParam("related Dept Code") @RequestParam String relatedDeptCode,
            @ApiParam("inactive") @RequestParam Boolean inactive) {
        loggingService.logMethod();
        CurrentUser currentUser = getCurrentUser();
        log.info("currentUser={}", currentUser);

        UCSBSubject ucsbsubject = new UCSBSubject();
        ucsbsubject.setSubjectCode(subjectCode);
        ucsbsubject.setSubjectTranslation(subjectTranslation);
        ucsbsubject.setDeptCode(deptCode);
        ucsbsubject.setCollegeCode(collegeCode);
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

        public UCSBSubjectOrError doesUCSBSubjectExist(UCSBSubjectOrError ucsbsub) {

        Optional<UCSBSubject> optionalUCSBSubject = UCSBSubjectRepository.findById(ucsbsub.id);

        if (optionalUCSBSubject.isEmpty()) {
            ucsbsub.error = ResponseEntity
                    .badRequest()
                    .body(String.format("ucsb subject with id %d not found", ucsbsub.id));
        } else {
            ucsbsub.subjectCode = optionalUCSBSubject.get();
        }
        return ucsbsub;
    }

    /**
     * Pre-conditions: toe.todo is non-null and refers to the todo with id toe.id,
     * and toe.error is null
     * 
     * Post-condition: if todo belongs to current user, then error is still null.
     * Otherwise error is a suitable
     * return value.
     */
    public UCSBSubjectOrError doesUCSBSubjectBelongToCurrentUser(UCSBSubjectOrError ucsbsub) {
        CurrentUser currentUser = getCurrentUser();
        log.info("currentUser={}", currentUser);

        Long currentUserId = currentUser.getUser().getId();
        Long todoUserId = ucsbsub.subjectCode.getUser().getId();
        log.info("currentUserId={} todoUserId={}", currentUserId, todoUserId);

        if (todoUserId != currentUserId) {
            ucsbsub.error = ResponseEntity
                    .badRequest()
                    .body(String.format("todo with id %d not found", ucsbsub.id));
        }
        return ucsbsub;
    }

}