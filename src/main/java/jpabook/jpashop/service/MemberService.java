package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //스프링에서 제공하는 어노테이션으로 쓰기가 아닌 경우 readOnly를 붙인다.
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    //RequiredArgsConstructor에서 만들어준다.
    /*
    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }*/

    //회원가입
    @Transactional // 메소드 위에 붙인 트랜잭션이 클래스 위의 트랜잭션보다 우선권을 가진다.
    public Long join(Member member){
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체조회
    @Transactional
    public List<Member> findMember(){
        return memberRepository.findAll();
    }

    //회원 조회
    @Transactional
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }
}
