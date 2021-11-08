package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable // 내장타입
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    /**
     * JPA에서는 Entity 클래스에 기본생성자를 필요로 한다.
     * @Embeddable 또는 @Entity는 기본생성자를 Public 또는 Protected로 해야하는데 그나마 안전한 Protected을 사용한다.
     * 이러한 제약을 두는 이유는 JPA 구현 라이브러리가 객체를 생성할 때 리플랙션이나 프록시같은 기술을 사용하기 위함이다.
     * @NoArgsConstructor(access = AccessLevel.PROTECTED)로 써도 된다.
     */
    protected Address() {}

    // 값 타입은 변경 불가능하게 설계해야하므로 생성자를 추가하여 모두 초기화하여 변경불가능한 클래스로 만든다.
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
