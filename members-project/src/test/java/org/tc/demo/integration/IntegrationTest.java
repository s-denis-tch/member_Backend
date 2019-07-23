package org.tc.demo.integration;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class IntegrationTest {
  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(IntegrationTest.class);
  
  private TestRestTemplate _template = new TestRestTemplate();

  /**
   * Just test if the server responds with http status ok to getting list of members 
   * @throws Exception
   */
  //@Test
  public void testServerAvailability() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

    HttpEntity<?> entity = new HttpEntity<>(headers);

    // test that server responds with empty JSON array
    ResponseEntity<String> result = _template.exchange(new URI("http://localhost:8080/api/members"),
        HttpMethod.GET, entity, String.class);
    assertEquals(HttpStatus.OK, result.getStatusCode());
  }

}
