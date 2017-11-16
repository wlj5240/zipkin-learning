package org.mozhu.zipkin.springmvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class Backend {

  private final static Logger LOGGER = LoggerFactory.getLogger(Backend.class);

  @RequestMapping("/api")
  public String printDate(@RequestHeader(name = "user-name", required = false) String username) {
    LOGGER.info("backend receive request");
    if (username != null) {
      return new Date().toString() + " " + username;
    }
    return new Date().toString();
  }
}
