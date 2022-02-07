package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.UCSBRequirement;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
import edu.ucsb.cs156.team02.repositories.UCSBRequirementRepository;
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

@Api(description = "UCSBRequirements")
@RequestMapping("/api/UCSBRequirements")
@RestController
@Slf4j
public class UCSBRequirementController extends ApiController {

    /**
     * This inner class helps us factor out some code for checking
     * whether UCSBRequirements exist, and whether they belong to the current user,
     * along with the error messages pertaining to those situations. It
     * bundles together the state needed for those checks.
     */
    public class UCSBRequirementOrError {
        Long id;
        UCSBRequirement UCSBRequirement;
        ResponseEntity<String> error;

        public UCSBRequirementOrError(Long id) {
            this.id = id;
        }
    }

    @Autowired
    UCSBRequirementRepository UCSBRequirementRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "List all UCSBRequirements")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/all")
    public Iterable<UCSBRequirement> allUsersUCSBRequirements() {
        loggingService.logMethod();
        Iterable<UCSBRequirement> UCSBRequirements = UCSBRequirementRepository.findAll();
        return UCSBRequirements;
    }

    @ApiOperation(value = "List this user's UCSBRequirements")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBRequirement> thisUsersUCSBRequirements() {
        loggingService.logMethod();
        CurrentUser currentUser = getCurrentUser();
        Iterable<UCSBRequirement> UCSBRequirements = UCSBRequirementRepository
                .findAllByUserId(currentUser.getUser().getId());
        return UCSBRequirements;
    }

    @ApiOperation(value = "Get a single UCSBRequirement (if it belongs to current user)")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<String> getUCSBRequirementById(
            @ApiParam("id") @RequestParam Long id) throws JsonProcessingException {
        loggingService.logMethod();
        UCSBRequirementOrError toe = new UCSBRequirementOrError(id);

        toe = doesUCSBRequirementExist(toe);
        if (toe.error != null) {
            return toe.error;
        }
        toe = doesUCSBRequirementBelongToCurrentUser(toe);
        if (toe.error != null) {
            return toe.error;
        }
        String body = mapper.writeValueAsString(toe.UCSBRequirement);
        return ResponseEntity.ok().body(body);
    }

    @ApiOperation(value = "Get a single UCSBRequirement (no matter who it belongs to, admin only)")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<String> getUCSBRequirementById_admin(
            @ApiParam("id") @RequestParam Long id) throws JsonProcessingException {
        loggingService.logMethod();

        UCSBRequirementOrError toe = new UCSBRequirementOrError(id);

        toe = doesUCSBRequirementExist(toe);
        if (toe.error != null) {
            return toe.error;
        }

        String body = mapper.writeValueAsString(toe.UCSBRequirement);
        return ResponseEntity.ok().body(body);
    }

    @ApiOperation(value = "Create a new UCSBRequirement")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public UCSBRequirement postUCSBRequirement(
            @ApiParam("title") @RequestParam String title,
            @ApiParam("details") @RequestParam String details,
            @ApiParam("done") @RequestParam Boolean done) {
        loggingService.logMethod();
        CurrentUser currentUser = getCurrentUser();
        log.info("currentUser={}", currentUser);

        UCSBRequirement UCSBRequirement = new UCSBRequirement();
        UCSBRequirement.setUser(currentUser.getUser());
        UCSBRequirement.setTitle(title);
        UCSBRequirement.setDetails(details);
        UCSBRequirement.setDone(done);
        UCSBRequirement savedUCSBRequirement = UCSBRequirementRepository.save(UCSBRequirement);
        return savedUCSBRequirement;
    }

    @ApiOperation(value = "Delete a UCSBRequirement owned by this user")
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("")
    public ResponseEntity<String> deleteUCSBRequirement(
            @ApiParam("id") @RequestParam Long id) {
        loggingService.logMethod();

        UCSBRequirementOrError toe = new UCSBRequirementOrError(id);

        toe = doesUCSBRequirementExist(toe);
        if (toe.error != null) {
            return toe.error;
        }

        toe = doesUCSBRequirementBelongToCurrentUser(toe);
        if (toe.error != null) {
            return toe.error;
        }
        UCSBRequirementRepository.deleteById(id);
        return ResponseEntity.ok().body(String.format("UCSBRequirement with id %d deleted", id));

    }

    @ApiOperation(value = "Delete another user's UCSBRequirement")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/admin")
    public ResponseEntity<String> deleteUCSBRequirement_Admin(
            @ApiParam("id") @RequestParam Long id) {
        loggingService.logMethod();

        UCSBRequirementOrError toe = new UCSBRequirementOrError(id);

        toe = doesUCSBRequirementExist(toe);
        if (toe.error != null) {
            return toe.error;
        }

        UCSBRequirementRepository.deleteById(id);

        return ResponseEntity.ok().body(String.format("UCSBRequirement with id %d deleted", id));

    }

    @ApiOperation(value = "Update a single UCSBRequirement (if it belongs to current user)")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("")
    public ResponseEntity<String> putUCSBRequirementById(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid UCSBRequirement incomingUCSBRequirement) throws JsonProcessingException {
        loggingService.logMethod();

        CurrentUser currentUser = getCurrentUser();
        User user = currentUser.getUser();

        UCSBRequirementOrError toe = new UCSBRequirementOrError(id);

        toe = doesUCSBRequirementExist(toe);
        if (toe.error != null) {
            return toe.error;
        }
        toe = doesUCSBRequirementBelongToCurrentUser(toe);
        if (toe.error != null) {
            return toe.error;
        }

        incomingUCSBRequirement.setUser(user);
        UCSBRequirementRepository.save(incomingUCSBRequirement);

        String body = mapper.writeValueAsString(incomingUCSBRequirement);
        return ResponseEntity.ok().body(body);
    }

    @ApiOperation(value = "Update a single UCSBRequirement (regardless of ownership, admin only, can't change ownership)")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/admin")
    public ResponseEntity<String> putUCSBRequirementById_admin(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid UCSBRequirement incomingUCSBRequirement) throws JsonProcessingException {
        loggingService.logMethod();

        UCSBRequirementOrError toe = new UCSBRequirementOrError(id);

        toe = doesUCSBRequirementExist(toe);
        if (toe.error != null) {
            return toe.error;
        }

        // Even the admin can't change the user; they can change other details
        // but not that.

        User previousUser = toe.UCSBRequirement.getUser();
        incomingUCSBRequirement.setUser(previousUser);
        UCSBRequirementRepository.save(incomingUCSBRequirement);

        String body = mapper.writeValueAsString(incomingUCSBRequirement);
        return ResponseEntity.ok().body(body);
    }

    /**
     * Pre-conditions: toe.id is value to look up, toe.UCSBRequirement and toe.error
     * are null
     * 
     * Post-condition: if UCSBRequirement with id toe.id exists, toe.UCSBRequirement
     * now refers to it, and
     * error is null.
     * Otherwise, UCSBRequirement with id toe.id does not exist, and error is a
     * suitable return
     * value to
     * report this error condition.
     */
    public UCSBRequirementOrError doesUCSBRequirementExist(UCSBRequirementOrError toe) {

        Optional<UCSBRequirement> optionalUCSBRequirement = UCSBRequirementRepository.findById(toe.id);

        if (optionalUCSBRequirement.isEmpty()) {
            toe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("UCSBRequirement with id %d not found", toe.id));
        } else {
            toe.UCSBRequirement = optionalUCSBRequirement.get();
        }
        return toe;
    }

    /**
     * Pre-conditions: toe.UCSBRequirement is non-null and refers to the
     * UCSBRequirement with id toe.id,
     * and toe.error is null
     * 
     * Post-condition: if UCSBRequirement belongs to current user, then error is
     * still null.
     * Otherwise error is a suitable
     * return value.
     */
    public UCSBRequirementOrError doesUCSBRequirementBelongToCurrentUser(UCSBRequirementOrError toe) {
        CurrentUser currentUser = getCurrentUser();
        log.info("currentUser={}", currentUser);

        Long currentUserId = currentUser.getUser().getId();
        Long UCSBRequirementUserId = toe.UCSBRequirement.getUser().getId();
        log.info("currentUserId={} UCSBRequirementUserId={}", currentUserId, UCSBRequirementUserId);

        if (UCSBRequirementUserId != currentUserId) {
            toe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("UCSBRequirement with id %d not found", toe.id));
        }
        return toe;
    }

}