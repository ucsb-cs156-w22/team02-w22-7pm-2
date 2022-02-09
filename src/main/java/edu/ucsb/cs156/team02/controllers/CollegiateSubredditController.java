package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.CollegiateSubreddit;
import edu.ucsb.cs156.team02.repositories.CollegiateSubredditRepository;
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

@Api(description = "Table of Collegiate Subreddits from https://github.com/karlding/college-subreddits")
@Slf4j
@RestController
@RequestMapping("/api/collegiateSubreddits")
public class CollegiateSubredditController extends ApiController {

	public class CollegiateSubredditOrError {
        Long id;
        CollegiateSubreddit reddit;
        ResponseEntity<String> error;

        public CollegiateSubredditOrError(Long id) {
            this.id = id;
        }
    }

    @Autowired
    CollegiateSubredditRepository collegiateSubredditRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "List all collegiate subreddits in the database")
    //@PreAuthorize("hasRole('ROLE_USER')")  // ah - "Allowing everyone to find all the subreddits, even if not logged in"
    @GetMapping("/all")
    public Iterable<CollegiateSubreddit> allCollegiateSubreddits() {
        //loggingService.logMethod();
        Iterable<CollegiateSubreddit> reddits = collegiateSubredditRepository.findAll();
        return reddits;
    }

    @ApiOperation(value = "Create a new Collegiate subreddit")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public CollegiateSubreddit postCollegiateSubreddit(
            @ApiParam("name") @RequestParam String name,
            @ApiParam("location") @RequestParam String location,
            @ApiParam("subreddit") @RequestParam String subreddit) {
        loggingService.logMethod();

        CollegiateSubreddit reddit = new CollegiateSubreddit();
        reddit.setName(name);
        reddit.setLocation(location);
        reddit.setSubreddit(subreddit);
        CollegiateSubreddit savedReddit = collegiateSubredditRepository.save(reddit);
        return savedReddit;
    }

	@ApiOperation(value = "Get Collegiate Subreddit by ID")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<String> getCollegiateSubredditById(
            @ApiParam("id") @RequestParam Long id) throws JsonProcessingException {
        loggingService.logMethod();
        CollegiateSubredditOrError cse = new CollegiateSubredditOrError(id);

        cse = doesCollegiateSubredditOrErrorExist(cse);
        if (cse.error != null) {
            return cse.error;
        }
		
        String body = mapper.writeValueAsString(cse.reddit);
        return ResponseEntity.ok().body(body);
    }

	@ApiOperation(value = "Update Collegiate Subreddit by ID)")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("")
    public ResponseEntity<String> putCollegiateSubredditById(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid CollegiateSubreddit incomingCollegiateSubreddit) throws JsonProcessingException {
        loggingService.logMethod();

        CollegiateSubredditOrError cse = new CollegiateSubredditOrError(id);

        cse = doesCollegiateSubredditOrErrorExist(cse);
        if (cse.error != null) {
            return cse.error;
        }
      
        incomingCollegiateSubreddit.setId(id);
        collegiateSubredditRepository.save(incomingCollegiateSubreddit);

        String body = mapper.writeValueAsString(incomingCollegiateSubreddit);
        return ResponseEntity.ok().body(body);
    }

	@ApiOperation(value = "Delete a Collegiate Subreddit by ID")
	@PreAuthorize("hasRole('ROLE_USER')")
	@DeleteMapping("")
	public ResponseEntity<String> deleteCollegiateSubredditbyID(
		@ApiParam("id") @RequestParam Long id) {
	loggingService.logMethod();

	CollegiateSubredditOrError cse = new CollegiateSubredditOrError(id);

	cse = doesCollegiateSubredditOrErrorExist(cse);
	if (cse.error != null) {
		return cse.error;
	}

	collegiateSubredditRepository.deleteById(id);

	return ResponseEntity.ok().body(String.format("CollegiateSubreddit with id " + id + " deleted"));

}

	public CollegiateSubredditOrError doesCollegiateSubredditOrErrorExist (CollegiateSubredditOrError cse)
	{
		Optional<CollegiateSubreddit> optionalCollegiateSubreddit = collegiateSubredditRepository.findById(cse.id);

		if(optionalCollegiateSubreddit.isEmpty())
		{
			cse.error = ResponseEntity
					.badRequest()
					.body(String.format("CollegiateSubreddit with id "+ cse.id + " not found"));
		}
		return cse;
	}

}