package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
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

    // v1은 엔티티를 반환하는 방식이며 이를 JsonIgnore와 hibernate5 Module을 통하여 에러를 막는 방법을 사용하였으나 이는 생략

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2(){
        /**
         * N +1문제 발생(첫 번째 쿼리의 결과로 N번만큼 쿼리가 추가 실행 되는 것 - 성능 문제) -> 1 + 회원N + 배송N
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
         * 패치 조인을 통해 LAZY를 무시하고 프록시가 아닌 실데이터를 한번에 가져와 쿼리의 중복 실행을 막는다.
         */
        return orderRepository.findAllWithMemberDelivery().stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
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
