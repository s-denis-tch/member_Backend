package org.tc.demo.services;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tc.demo.dto.MemberDto;
import org.tc.demo.model.Member;
import org.tc.demo.repo.MemberRepository;
import org.tc.demo.utils.NotFoundException;
import org.tc.demo.utils.ObjectValidator;

@Service
public class MemberService {
  @Autowired 
  private MemberRepository _memberRepo;
  
  private ModelMapper _modelMapper;
  
  public MemberService() {
    _modelMapper = new ModelMapper();
    _modelMapper.createTypeMap(MemberDto.class, Member.class, "NotNullOnly")
      .setPropertyCondition(Conditions.isNotNull());
  }
  
  /**
   * Gets a DTO for specified ID, or throws {@link NotFoundException}
   * @param id
   * @return member dto
   */
  public MemberDto getMember(Long id) {
    Member member = ObjectValidator.checkFound(_memberRepo.findOne(id), 
        String.format("Member with id=%d is not found", id));
    return _modelMapper.map(member, MemberDto.class);
  }

  /**
   * @return list of all existing members
   */
  public List<MemberDto> listAll() {
    List<MemberDto> result = new ArrayList<>();
    
    for (Member member : _memberRepo.findAll()) {
      result.add(_modelMapper.map(member, MemberDto.class));
    }
    
    return result;
  }
  
  /**
   * Creates new instance from fields passed in DTO
   * @param dto
   * @return
   */ 
  @Transactional
  public MemberDto createNew(MemberDto dto) {
    Member member = _modelMapper.map(dto, Member.class);
    member = _memberRepo.save(member);
    return _modelMapper.map(member, MemberDto.class);
  }

  /**
   * Updates non-null params. ID is mandatory.
   * @param dto
   * @return
   */
  public void update(MemberDto dto) {
   Member member = ObjectValidator.checkFound(_memberRepo.findOne(dto.getId()), 
        String.format("Member with id=%d is not found", dto.getId()));
    // copy fields but id
    _modelMapper.map(dto, member, "NotNullOnly");
    _memberRepo.save(member);
  }
  
  public void delete(long memberId) {
    if (_memberRepo.deleteMember(memberId) == 0) {
      throw new NotFoundException(String.format("Member with id=%d is not found", memberId));
    }
  }
}
