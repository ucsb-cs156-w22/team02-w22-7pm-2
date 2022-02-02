package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.CollegeSubreddit;
import edu.ucsb.cs156.team02.repositories.CollegeSubredditRepository;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@Api(description = "Table of College Subreddits from https://github.com/karlding/college-subreddits")
@Slf4j
@RestController
@RequestMapping("/api/collegiateSubreddits")
public class CollegiateSubredditController extends ApiController {

    @Autowired
    CollegeSubredditRepository CollegeSubredditRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "List all subreddits in the database")
    //@PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<CollegeSubreddit> allCollegeSubreddits() {
        //loggingService.logMethod();
        Iterable<CollegeSubreddit> coSubs = CollegeSubredditRepository.findAll();
        return coSubs;
    }

    @ApiOperation(value = "Create a new College Subreddit")
    //@PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public CollegeSubreddit postCollegeSubreddit(
            @ApiParam("name") @RequestParam String name,
            @ApiParam("location") @RequestParam String location,
            @ApiParam("subreddit") @RequestParam String subreddit) {
        //loggingService.logMethod();

        CollegeSubreddit coSub = new CollegeSubreddit();
        coSub.setName(name);
        coSub.setLocation(location);
        coSub.setSubreddit(subreddit);
        CollegeSubreddit savedCoSub = CollegeSubredditRepository.save(coSub);
        return savedCoSub;
    }

}