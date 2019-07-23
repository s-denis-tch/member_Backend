package org.tc.demo.controllers;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.tc.demo.dto.MemberDto;
import org.tc.demo.services.MemberService;

@RestController
public class MemberController {
  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(MemberController.class);
  
  private @Autowired MemberService _memberService;
  
  @RequestMapping(value = "/api/members/{member-id}", method = RequestMethod.GET)
  public ResponseEntity<?> getMember(@PathVariable("member-id") long memberId) {
    MemberDto member = _memberService.getMember(memberId); 
    return ResponseEntity.ok(member);
  }
  
  @RequestMapping(value = "/api/members", method = RequestMethod.GET)
  public ResponseEntity<?> listMembers() {
    return ResponseEntity.ok(_memberService.listAll());
  }
  
  @RequestMapping(value = "/api/members/{member-id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.OK)
  public void deleteMember(@PathVariable("member-id") long memberId) {
    _memberService.delete(memberId);
  }
  
  @RequestMapping(value = "/api/members", method = RequestMethod.POST)
  public ResponseEntity<?> createMember(@RequestBody MemberDto dto) {
    MemberDto resultDto = _memberService.createNew(dto);
    return ResponseEntity.created(uri("/api/members/" + resultDto.getId())).body(resultDto);
  }
  
  @RequestMapping(value = "/api/members/{member-id}", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.OK)
  public void updateMember(@PathVariable("member-id") long memberId,
      @RequestBody MemberDto dto) {
    dto.setId(memberId);
    _memberService.update(dto);
  }
  
  private static URI uri(String uri) {
    try {
      return new URI(uri);
    } catch (URISyntaxException e) {
      return null;
    }
  }
}
