package edu.ucsb.cs156.team02;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import edu.ucsb.cs156.team02.services.CurrentUserService;
import edu.ucsb.cs156.team02.services.GrantedAuthoritiesService;
import edu.ucsb.cs156.team02.testconfig.TestConfig;

@ActiveProfiles("test")
@Import(TestConfig.class)
public abstract class ControllerTestCase {
  @Autowired
  public CurrentUserService currentUserService;

  @Autowired
  public GrantedAuthoritiesService grantedAuthoritiesService;

  @Autowired
  public MockMvc mockMvc;

  @Autowired
  public ObjectMapper mapper;
  
}
