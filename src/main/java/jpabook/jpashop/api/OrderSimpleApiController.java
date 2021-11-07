package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    // v1은 엔티티를 반환하는 방식이며 이를 JsonIgnore와 hibernate5 Module을 통하여 에러를 막는 방법을 사용하였으나 이는 생략

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2(){
        /**
         * v2 - N+1문제 발생(첫 번째 쿼리의 결과로 N번만큼 쿼리가 추가 실행 되는 것 - 성능 문제) -> 1 + 회원N + 배송N
         * v3에서 패치조인을 통하여 N+1문제를 해결해보자
         */
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;

        /* return orderRepository.findAllByString(new OrderSearch()).stream()
                .map(SimpleOrderDto::new)
                .collect(Collectors.toList()); */
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3() {
        /**
         * 패치 조인을 통해 지연로딩(LAZY)을 막고 프록시가 아닌 실데이터를 한번에 가져와 쿼리의 중복 실행을 막는다. - 성능 최적화
         */
        return orderRepository.findAllWithMemberDelivery().stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
    }

    @GetMapping("/api/v4/simple-orders")
    /**
     * 쿼리에서 new명령어를 사용하여 JPA에서 DTO로 바로 조회 - 레파지토리 재사용성이 떨어지며 V3과 우열을 가릴 순 없다.
     *
     * 우선 엔티티를 DTO로 변환하는 방식을 선택하면 되며 필요 시 패치조인으로 성능 최적화를 하면 대부분 이슈는 해결된다.(v2, v3)
     * 그래도 어떠한 문제가 생기는 경우가 있을 수 있는데 그럴 때 DTO로 직접 조회하는 방법을 택한다.(v4)
     * 그럼에도 안될 경우 최후의 방법으로 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template를 사용하여 SQL을 직접 사용한다.
     */
    public List<OrderSimpleQueryDto> orderV4(){
        return orderSimpleQueryRepository.findOrderDtos();
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화
        }

    }
}
