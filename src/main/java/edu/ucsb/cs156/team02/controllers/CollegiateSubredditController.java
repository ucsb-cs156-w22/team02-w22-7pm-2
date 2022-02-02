package edu.ucsb.cs156.team02.controllers;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(description = "Reddit from https://www.reddit.com/dev")
@Slf4j
@RestController
@RequestMapping("/api/reddit")
public class CollegiateSubredditController {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    CollegiateSubredditController redditQueryService;

    @ApiOperation(value = "Get posts from a subreddit of Reddit.com", notes = "Parameters, e.g. UCSantaBarbara")
    @GetMapping("/get")
    public ResponseEntity<String> getReddit(@ApiParam("Parameters, e.g. UCSantaBarbara") @RequestParam String subreddit)
            throws JsonProcessingException {
        log.info("getReddit: reddit={}", subreddit);
        String result = redditQueryService.getJSON(subreddit);
        return ResponseEntity.ok().body(result);
    }
}