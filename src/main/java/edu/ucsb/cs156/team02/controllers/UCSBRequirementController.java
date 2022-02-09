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

    @ApiOperation(value = "List all UCSB requirements")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/all")
    public Iterable<UCSBRequirement> allUsersUCSBRequirements() {
        loggingService.logMethod();
        Iterable<UCSBRequirement> UCSBRequirements = UCSBRequirementRepository.findAll();
        return UCSBRequirements;
    }

    @ApiOperation(value = "Get a single UCSB requirement")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("")
    public ResponseEntity<String> getUCSBRequirementById(
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

    @ApiOperation(value = "Create a new UCSB requirement")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBRequirement postUCSBRequirement(
            @ApiParam("requirement code") @RequestParam String requirementCode,
            @ApiParam("requirement translation") @RequestParam String requirementTranslation,
            @ApiParam("college code") @RequestParam String collegeCode,
            @ApiParam("object code") @RequestParam String objCode,
            @ApiParam("course count") @RequestParam int courseCount,
            @ApiParam("units") @RequestParam int units,
            @ApiParam("inactive") @RequestParam boolean inactive) {
        loggingService.logMethod();

        UCSBRequirement UCSBRequirement = new UCSBRequirement();
        UCSBRequirement.setRequirementCode(requirementCode);
        UCSBRequirement.setRequirementTranslation(requirementTranslation);
        UCSBRequirement.setCollegeCode(collegeCode);
        UCSBRequirement.setObjCode(objCode);
        UCSBRequirement.setCourseCount(courseCount);
        UCSBRequirement.setUnits(units);
        UCSBRequirement.setInactive(inactive);
        UCSBRequirement savedUCSBRequirement = UCSBRequirementRepository.save(UCSBRequirement);
        return savedUCSBRequirement;
    }

    @ApiOperation(value = "Delete a UCSB requirement")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public ResponseEntity<String> deleteUCSBRequirement(
            @ApiParam("id") @RequestParam Long id) {
        loggingService.logMethod();

        UCSBRequirementOrError toe = new UCSBRequirementOrError(id);

        toe = doesUCSBRequirementExist(toe);
        if (toe.error != null) {
            return toe.error;
        }

        UCSBRequirementRepository.deleteById(id);
        return ResponseEntity.ok().body(String.format("UCSB requirement with id %d deleted", id));

    }

    @ApiOperation(value = "Update a single UCSB requirement")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public ResponseEntity<String> putUCSBRequirementById(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid UCSBRequirement incomingUCSBRequirement) throws JsonProcessingException {
        loggingService.logMethod();

        UCSBRequirementOrError toe = new UCSBRequirementOrError(id);

        toe = doesUCSBRequirementExist(toe);
        if (toe.error != null) {
            return toe.error;
        }

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
                    .body(String.format("UCSB requirement with id %d not found", toe.id));
        } else {
            toe.UCSBRequirement = optionalUCSBRequirement.get();
        }
        return toe;
    }

}