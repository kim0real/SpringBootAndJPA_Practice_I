package jpabook.jpashop;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(false) //테스트에서는 insert 후 자동 롤백이 되므로 @Rollback을 통해 막아줄 수 있다.
    public void testMember(){
        //give
        Member member = new Member();
        member.setUsername("memberA");

        //when
        memberRepository.save(member);
        Member findMember = memberRepository.find(member.getId());

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        // 같은 트랜잭션, 즉 같은 영속성을 가진 컨테이너안에서 같은 아이디를 가진다면 동일한 엔티티로 식별한다.
        Assertions.assertThat(findMember).isEqualTo(member);
        System.out.println(findMember == member);
    }
}