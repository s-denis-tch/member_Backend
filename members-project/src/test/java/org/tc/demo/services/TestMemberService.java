package org.tc.demo.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.tc.demo.dto.MemberDto;
import org.tc.demo.model.Member;
import org.tc.demo.repo.MemberRepository;
import org.tc.demo.testutils.DbTestConfiguration;
import org.tc.demo.testutils.TestUtils;
import org.tc.demo.utils.NotFoundException;

import static org.tc.demo.testutils.TestObjectBuilder.buildMember;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = { DbTestConfiguration.class })
public class TestMemberService {
  
  @Autowired 
  private MemberRepository _memberRepo;
  
  @Autowired
  private MemberService _memberService;
  
  @Before
  public void clearDb() {
    _memberRepo.deleteAll();
  }
  
  @Test
  public void testCreate() {
    Date born = java.sql.Date.valueOf(LocalDate.of(2000, 1, 31));
    
    MemberDto example = new MemberDto();
    example.setFirstName("a");
    example.setLastName("v");
    example.setBirthDate(born);
    example.setPostalCode("12345");
    
    long newId = _memberService.createNew(example).getId();
    
    List<Member> all = _memberRepo.findAll();
    assertEquals(1, all.size());
    
    assertEquals(newId, all.get(0).getId().longValue());
    assertEquals("a", all.get(0).getFirstName());
    assertEquals("v", all.get(0).getLastName());
    assertEquals("12345", all.get(0).getPostalCode());
    assertEquals(born, all.get(0).getBirthDate());
  }
  
  @Test
  public void test_getMember() {
    // no entities, so we expect NFE
    assertThat(TestUtils.expectThrows(() -> _memberService.getMember(1L)),
        is(instanceOf(NotFoundException.class)));
    
    Date born = java.sql.Date.valueOf(LocalDate.of(2000, 1, 31));
    long newId = _memberRepo.save(buildMember("a", "v", born, "12345")).getId();
    
    MemberDto dto = _memberService.getMember(newId);
    
    assertEquals(newId, dto.getId());
    assertEquals("a", dto.getFirstName());
    assertEquals("v", dto.getLastName());
    assertEquals("12345", dto.getPostalCode());
    assertEquals(born, dto.getBirthDate());
  }

  @Test
  public void test_listMembers() {
    Date born1 = java.sql.Date.valueOf(LocalDate.of(2000, 1, 31));
    Date born2 = java.sql.Date.valueOf(LocalDate.of(2001, 1, 31));
    Member bo1 = buildMember("a", "v", born1, "12345");
    Member bo2 = buildMember("x", "y", born2, "67890");
    List<Member> src = _memberRepo.save(Arrays.asList(bo1, bo2));
    
    List<MemberDto> list = _memberService.listAll();
    assertEquals(2, list.size());
    
    MemberDto member1 = list.stream().filter(m->m.getId() == src.get(0).getId()).findFirst().orElse(null);
    MemberDto member2 = list.stream().filter(m->m.getId() == src.get(1).getId()).findFirst().orElse(null);
    
    assertEquals(src.get(0).getId().longValue(), member1.getId());
    assertEquals("a", member1.getFirstName());
    assertEquals("v", member1.getLastName());
    assertEquals("12345", member1.getPostalCode());
    assertEquals(born1, member1.getBirthDate());
    
    assertEquals(src.get(1).getId().longValue(), member2.getId());
    assertEquals("x", member2.getFirstName());
    assertEquals("y", member2.getLastName());
    assertEquals("67890", member2.getPostalCode());
    assertEquals(born2, member2.getBirthDate());
  }
  
  @Test
  public void test_updateMember_allFields() {
    MemberDto notFoundExample = new MemberDto();
    notFoundExample.setId(123);
    assertThat(TestUtils.expectThrows(() -> _memberService.update(notFoundExample)),
        is(instanceOf(NotFoundException.class)));

    // create one to test update
    Date born = java.sql.Date.valueOf(LocalDate.of(2000, 1, 31));
    long newId = _memberRepo.save(buildMember("a", "v", born, "12345")).getId();

    // test updating properties one by one
    MemberDto updateFields = new MemberDto();
    updateFields.setId(newId);

    updateFields.setFirstName("new fn");
    updateFields.setLastName("new ln");
    updateFields.setPostalCode("67890");
    Date newBorn = java.sql.Date.valueOf(LocalDate.of(2001, 1, 31));
    updateFields.setBirthDate(newBorn);

    _memberService.update(updateFields);// only fn must be updated

    Member dbMember = _memberRepo.findOne(newId);
    assertEquals("new fn", dbMember.getFirstName());
    assertEquals("new ln", dbMember.getLastName()); // only one field changed others remain intact
    assertEquals("67890", dbMember.getPostalCode());
    assertEquals(newBorn, dbMember.getBirthDate());

    //      Member dbMember = _memberRepo.findOne(newId);
    //      assertEquals("a", dbMember.getFirstName());
    //      assertEquals("b", dbMember.getLastName()); // only one field changed others remain intact
    //      assertEquals("12345", dbMember.getPostalCode());
    //      assertEquals(born, dbMember.getBirthDate());

  }
  
  /**
   * Test ability to overwrite single field by omitting fields that have not been changed
   */
  @Test
  public void test_updateMember_someFields() {
    MemberDto notFoundExample = new MemberDto();
    notFoundExample.setId(123);
    assertThat(TestUtils.expectThrows(() -> _memberService.update(notFoundExample)),
        is(instanceOf(NotFoundException.class)));

    // create one to test update
    Date born = java.sql.Date.valueOf(LocalDate.of(2000, 1, 31));
    long newId = _memberRepo.save(buildMember("a", "v", born, "12345")).getId();

    // test updating single property
    MemberDto updateFields = new MemberDto();
    updateFields.setId(newId);
    updateFields.setFirstName("new fn");

    _memberService.update(updateFields);// only fn must be updated

    Member dbMember = _memberRepo.findOne(newId);
    assertEquals("new fn", dbMember.getFirstName());
    assertEquals("v", dbMember.getLastName()); // only one field changed others remain intact
    assertEquals("12345", dbMember.getPostalCode());
    assertEquals(born, dbMember.getBirthDate());
  }
  
  /*
  * delete a no further used member
  */
  @Test
  public void test_deleteMember() {
    Date born = java.sql.Date.valueOf(LocalDate.of(2000, 1, 31));
    long newId = _memberRepo.save(buildMember("a", "v", born, "12345")).getId();
    
    //make sure it exists
    assertNotNull(_memberRepo.findOne(newId));
    
    _memberService.delete(newId);

    // does not exist anymore
    assertNull(_memberRepo.findOne(newId));
    
    // and now throws notfound
    assertThat(TestUtils.expectThrows(() -> _memberService.delete(newId)),
        is(instanceOf(NotFoundException.class)));
  }
}
