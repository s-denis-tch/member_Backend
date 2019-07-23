package org.tc.demo.testutils;

import java.util.Date;

import org.tc.demo.dto.MemberDto;
import org.tc.demo.model.Member;

public class TestObjectBuilder {

  /**
   * Utility method
   * 
   * @param firstName
   * @param lastName
   * @param birthDate
   * @param postalCode
   * @return
   */
  public static Member buildMember(String firstName, String lastName, Date birthDate, String postalCode) {
    Member bo = new Member();
    bo.setFirstName(firstName);
    bo.setLastName(lastName);
    bo.setBirthDate(birthDate);
    bo.setPostalCode(postalCode);
    return bo;
  }

  /**
   * Utility method
   * 
   * @param firstName
   * @param lastName
   * @param birthDate
   * @param postalCode
   * @return
   */
  public static MemberDto buildMemberDto(String firstName, String lastName, Date birthDate, String postalCode) {
    return buildMemberDto(0, firstName, lastName, birthDate, postalCode);
  }

  public static MemberDto buildMemberDto(long id, String firstName, String lastName, Date birthDate,
      String postalCode) {
    MemberDto dto = new MemberDto();
    dto.setId(id);
    dto.setFirstName(firstName);
    dto.setLastName(lastName);
    dto.setBirthDate(birthDate);
    dto.setPostalCode(postalCode);
    return dto;
  }
}
