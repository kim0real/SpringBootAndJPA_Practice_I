package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class MemberServiceTest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
    // persist는 기본적으로 insert문이 Rollback되나 @Rollback(false)를 추가하면 실제로 insert된다.
    // 또는 then절에 em.flush();를 추가해줘도 된다.
    @Rollback(false)
    public void 회원가입() {
        //given
        Member member = new Member();
        member.setName("Zero");

        //when
        Long savedId = memberService.join(member);

        //then
        //Assertions.assertThat(savedId).isEqualTo(member);
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test
    //@Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() {
        //given
        Member member1 = new Member();
        member1.setName("Elon");
        Member member2 = new Member();
        member2.setName("Elon");

        //when
        memberService.join(member1);
        try {
            memberService.join(member2); //예외가 발생해야한다.
        } catch (IllegalStateException e) {
            return;
        }

        //then
        Assertions.fail("예외가 발생해야 한다.");
    }
}