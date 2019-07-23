package org.tc.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.tc.demo.model.Member;

@Repository
@Transactional
public interface MemberRepository extends JpaRepository<Member, Long> {
  @Modifying
  @Query("delete from Member where id = ?1")
  int deleteMember(long id);
}
