package edu.ucsb.cs156.team02.controllers;
import edu.ucsb.cs156.team02.entities.UCSBSubject;
import edu.ucsb.cs156.team02.repositories.UCSBSubjectRepository;

import org.springframework.beans.factory.annotation.Autowired;

import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
import edu.ucsb.cs156.team02.services.CurrentUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.lang.String;
import java.lang.Boolean;



@Api(description="UCSB Subject Information")
@RequestMapping("/api/UCSBSubjects")
@RestController
@Slf4j
public class UCSBSubjectController extends ApiController{
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


}