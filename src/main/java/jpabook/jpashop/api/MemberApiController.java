package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        /**
         * @JsonIgnore를 통해 출력 시 Member 클래스의 주문 정보를 제외할 수 있다.
         */
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    /**
     * API를 설계할 떄 위처럼 엔티티 자체를 외부로 노출시키는 것은 여러가지 문제점이 있다.(엔티티가 변경되면 API 스펙이 변경되어야 함)
     * 때문에 꼭 아래처럼 DTO를 따르 만들어 이를 반환하는 것이 좋다.
     */
    public Result membersV2() {
        List<Member> members = memberService.findMembers();
        List<MemberDTO> collect = members.stream()
                .map(m -> new MemberDTO(m.getName()))
                .collect(Collectors.toList());

        // Entity가 아닌 Result를 반환할 경우 반환값을 다루기 용이하기 때문에 추가적으로 원하는 데이터를 반환할 수 있다.(count)
        return new Result(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDTO {
        private String name;
    }

    @PostMapping("/api/v1/members")
    /**
     * @Valid : Member를 검증한다.
     * 예를 들어 Member클래스의 private String name; 위에 @NotEmpty를 넣으면 널을 허용하지 않는다.
     */
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    /**
     * API를 설계할 떄 saveMemberV1처럼 엔티티 자체를 파라미터로 받는 것또한 문제의 여지가 있다.
     * 때문에 아래처럼 DTO를 따르 만들어 파라미터로 받는 것이 좋다.
     * 아래와 같이 작성하면 API를 호출할 때 어떤 파라미터가 필요한지 CraeteMemberRequest를 보고 한 눈에 알 수 있다.
     */
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CraeteMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());
        Member member = memberService.findOne(id);

        return new UpdateMemberResponse(member.getId(), member.getName());
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor // @AllArgsConstructor : Entity에선 문제가 될 여지가 있어 지양하나 DTO에서는 사용해도 된다.
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class CraeteMemberRequest {
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
