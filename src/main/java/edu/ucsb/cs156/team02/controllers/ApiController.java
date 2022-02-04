package edu.ucsb.cs156.team02.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
import edu.ucsb.cs156.team02.models.SystemInfo;
import edu.ucsb.cs156.team02.services.CurrentUserService;
import edu.ucsb.cs156.team02.services.LoggingService;
import edu.ucsb.cs156.team02.services.SystemInfoService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ApiController {
  @Autowired
  private CurrentUserService currentUserService;

  @Autowired
  protected LoggingService loggingService;

  protected CurrentUser getCurrentUser() {
    return currentUserService.getCurrentUser();
  }

  
}
