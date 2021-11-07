package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

//Spring Data JPA 맛보기 - 기존 MemberRepository는 MemberRepositoryOld로 변경
public interface MemberRepository extends JpaRepository<Member, Long> {
    /**
     * 아래처럼 선언만 해도 findBy'Name'을 캐치해
     * select m from Member m where m.name = :name 의 쿼리를 만들어준다.
     */
    List<Member> findByName(String name);
}
