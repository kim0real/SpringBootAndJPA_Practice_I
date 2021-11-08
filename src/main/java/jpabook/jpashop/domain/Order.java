package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // 테이블명을 ORDER로 명명할 수 없으므로 바꿔준다.
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    // 연관관계 주인
    private Member member; // 주문 회원

    /**
     * cascade = CascadeType.ALL : orderItems에 데이터를 넣고 order를 저장하면 orderItems도 함께 저장된다.
     * 기존에는 orderItems를 persist하고 order를 persist하였으나 그럴 필요가 없어진다.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery; // 배송정보

    private LocalDateTime orderDate; // 주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [ORDER, CANCEL]

    /**
     * 연관관계 메서드 : 핵심적으로 제어하는 쪽에 추가한다.
     * 기존
     * Member member = new Member();
     * Order order = new Order();
     * member.getOrders().add(order);
     * order.setMember(member);
     *
     * 연관관계 메서드 setMember 추가 후
     * Member member = new Member();
     * Order order = new Order();
     * order.setMember(memember);
     */
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    // 생성 메서드
    public static Order createOrder(Member member, Delivery delivery,
                                    OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    // 비즈니스 로직
    /**
     * 주문 취소
     *
     * 비지니스 로직이 서비스 계층에 있는 것이 아니고 서비스 계층은 단순히 엔티티에 필요한 요청을 위힘하는 역할을 한다.
     * 이처럼 엔티티가 비지니스 로직을 가지고 객체 지향의 특성을 적극 활용하는 것을 도메인 모델 패턴이라고 하며
     * 엔티티에는 비지니스 로직이 거의 없고 비지니스 로직을 서비스 단에서 처리하는 것을 트랜잭션 스크립트 패턴이라고 한다.
     * 어떤 것이 더 좋다고 할 수는 없지만 JPA 프로젝트에서는 도메인 모델 패턴을 대부분 사용한다.
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }
        this.setStatus(OrderStatus.CANCLE);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    // 조회 로직
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        /* int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice; */

        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }
}