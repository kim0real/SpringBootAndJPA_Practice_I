package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue // PK 자동생성(기본 시퀀스)
    @Column(name = "member_id") // 컬럼명
    private Long id;
    private String name;

    @Embedded
    private Address address;

    @JsonIgnore
    @OneToMany(mappedBy = "member") // 연관관계의 주인인 Order 엔티티의 member에 의해 매핑된다.(읽기 전용)
    /**
     * 컬렉션을 필드에서 바로 초기화하자.
     * - Null 문제에서 안전하다.
     * - 하이버네이트는 엔티티를 영속화할 때 컬렉션을 감싸서 하이버네이트에서 제공하는 내장 컬렉션으로 변경한다.
     * 만약 임의의 메서드에서 컬렉션을 잘못 생성하면 하이버네이트 내부 메커니즘에 문제가 생길 수 있다.
     * 따라서 필드레벨에서 초기화하는 것이 안전하고 코드도 간결하다.
     */
    private List<Order> orders = new ArrayList<>();
}