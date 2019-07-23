package org.tc.demo.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.tc.demo.dto.MemberDto;
import org.tc.demo.services.MemberService;
import org.tc.demo.utils.NotFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.tc.demo.testutils.TestObjectBuilder.buildMemberDto;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;

@RunWith(SpringRunner.class)
@WebMvcTest(secure = false, value = MemberController.class)
@ContextConfiguration(classes=TestMemberController.ControllerDbConfig.class)
@ActiveProfiles("test")
public class TestMemberController {
  @Configuration 
  @ComponentScan(
      basePackageClasses = MemberController.class, useDefaultFilters = false, 
      includeFilters = {
          @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
              value = MemberController.class) 
          })
  public static class ControllerDbConfig {
  }
  
  @Autowired
  private MockMvc mvc;
  
  @MockBean
  private MemberService _memberService;
  
  /**
   * Tests how GET returns JSON, XML and 404
   * 
   * @throws Exception
   */
  @Test
  public void testGetMember() throws Exception {
    MemberDto member = new MemberDto();
    member.setId(1L);
    member.setFirstName("A");
    member.setPostalCode("12345");
    
    Mockito.when(_memberService.getMember(1L)).thenReturn(member);
    Mockito.when(_memberService.getMember(2L)).thenThrow(new NotFoundException(""));

    mvc.perform(get("/api/members/2")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
        
    mvc.perform(get("/api/members/1")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.firstName", is("A")))
        .andExpect(jsonPath("$.postalCode", is("12345")))
        ;
    
    mvc.perform(get("/api/members/1")
        .accept(MediaType.APPLICATION_XML)
        .contentType(MediaType.APPLICATION_XML))
    
        //.andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
        .andExpect(xpath("MemberDto/id").string(is("1")))
        .andExpect(xpath("MemberDto/firstName").string(is("A")))
        .andExpect(xpath("MemberDto/postalCode").string(is("12345")))
        ;
  }

  @Test
  public void testListMembers() throws Exception {
    Date born1 = java.sql.Date.valueOf(LocalDate.of(2000, 1, 31));
    Date born2 = java.sql.Date.valueOf(LocalDate.of(2001, 1, 31));
    
    Mockito.when(_memberService.listAll()).thenReturn(Arrays.asList(
        buildMemberDto("f1", "l1", born1, "12345"), 
        buildMemberDto("f2", "l2", born2, "67890")));
    
    // test xml
    mvc.perform(get("/api/members")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
    
        //.andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].firstName", is("f1")))
        .andExpect(jsonPath("$[1].firstName", is("f2")))
        ;
    
    // test xml
    mvc.perform(get("/api/members")
        .accept(MediaType.APPLICATION_XML)
        .contentType(MediaType.APPLICATION_JSON))
    
        //.andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
        .andExpect(xpath("/ArrayList/item").nodeCount(2))
        .andExpect(xpath("/ArrayList/item[1]/firstName").string("f1"))
        .andExpect(xpath("/ArrayList/item[2]/firstName").string("f2"))
        ;
  }

  @Test
  public void testCreateMember() throws Exception {
    Mockito.when(_memberService.createNew(Mockito.any(MemberDto.class)))
        .thenAnswer(o -> {
          MemberDto arg = o.getArgumentAt(0, MemberDto.class);
          arg.setId(42);
          return arg;
        });
    
    Date born = java.sql.Date.valueOf(LocalDate.of(2000, 1, 31));
    MemberDto srcDto = buildMemberDto("f1", "l1", born, "12345");
    
    mvc.perform(post("/api/members")
        .content(new ObjectMapper().writeValueAsString(srcDto))
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(header().string("Location", "/api/members/42"))
        .andExpect(jsonPath("$.id", is(42)))
        .andExpect(jsonPath("$.firstName", is("f1")))
        .andExpect(jsonPath("$.lastName", is("l1")))
        ;
    
    mvc.perform(post("/api/members")
        .content(new XmlMapper().writeValueAsString(srcDto))
        .accept(MediaType.APPLICATION_XML)
        .contentType(MediaType.APPLICATION_XML))
    
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/api/members/42"))
        .andExpect(xpath("MemberDto/id").string("42"))
        .andExpect(xpath("MemberDto/firstName").string("f1"))
        .andExpect(xpath("MemberDto/lastName").string("l1"))
        ;
  }

  @Test
  public void testUpdateMember() throws Exception {
    // test if the input is really parsed by spring/controller
    Mockito.doAnswer(new Answer<String>() {
      @Override
      public String answer(InvocationOnMock invocation) throws Throwable {
        MemberDto arg = invocation.getArgumentAt(0, MemberDto.class);
        if (arg.getId() != 1L || !"newname".equals(arg.getFirstName())) {
          throw new IllegalArgumentException();
        }
          return null;
        }    
    }).when(_memberService).update(Mockito.any(MemberDto.class));
    
    MemberDto sample = new MemberDto();
    sample.setId(123);// should not matter, must be taken from URL
    sample.setFirstName("newname");
    
    mvc.perform(put("/api/members/1")
        .content(new ObjectMapper().writeValueAsString(sample))
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        ;
    
    mvc.perform(put("/api/members/1")
        .content(new XmlMapper().writeValueAsString(sample))
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_XML))
        .andExpect(status().isOk())
        ;
  }

  @Test
  public void testDeleteMember() throws Exception {
    Mockito.doThrow(new NotFoundException("no such member")).when(_memberService).delete(
        AdditionalMatchers.not(Matchers.eq(1L)));
    
    mvc.perform(delete("/api/members/2")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    mvc.perform(get("/api/members/1")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        ;
  }
}
