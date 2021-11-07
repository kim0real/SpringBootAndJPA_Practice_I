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

    // 회원가입
    @Transactional // 메소드 위에 붙인 트랜잭션이 클래스 위의 트랜잭션보다 우선권을 가진다.
    public Long join(Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체조회
    @Transactional
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 회원 조회
    @Transactional
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    @Transactional
    /**
     * update 메서드에서 Member를 리턴하는 것은 커맨드와 쿼리를 동시에 쓰는 것이 되는데
     * 커맨드와 쿼리를 분리한다는 강사님의 개발 정책 상 그렇게 하는 것보다는
     * void나 리턴하는 것이 바람직하다.(필요에 따라 id를 리턴할 때도 있다.)
     */
    public void update(Long id, String name){
        Member member = memberRepository.findOne(id);
        member.setName(name); // 변경감지를 통한 update
    }

}
