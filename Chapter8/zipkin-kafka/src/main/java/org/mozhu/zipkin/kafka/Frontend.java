package org.mozhu.zipkin.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class Frontend {
    private final static Logger LOGGER = LoggerFactory.getLogger(Frontend.class);
    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/")
    public String callBackend() {
        LOGGER.info("frontend receive request");
        return restTemplate.getForObject("http://localhost:9001/api", String.class);
    }

}
