package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import jpabook.jpashop.service.query.OrderDto;
import jpabook.jpashop.service.query.OrderQueryService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiContoller {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        /**
         * 이 또한 엔티티를 외부에 바로 노출하므로 사용하진 않는다.
         * 바로 엔티티를 조회하여 리턴할 경우 에러가 나기 때문에
         * Hibernate5Module을 사용하며 Order를 바라보는 엔티티에서 @JsonIgnore를 추가한다.
         */
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o ->  o.getItem().getName());
        }

        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> orderV2() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = all.stream()
                .map(OrderDto::new)
                .collect(toList());

        return collect;
    }

    private final OrderQueryService orderQueryService;
    @GetMapping("/api/v3/orders")
    public List<OrderDto> orderV3() {
        /**
         * 패치 조인을 사용하여 N+1 문제를 방지한다.
         *
         * OneToMany 패치 조인(Order, OrderItems)을 사용할 경우 페이징이 불가능하다.(JPA는 OrderItems를 기준을 잡기 때문에)
         * 하이버네이트는 경고 로그를 남기면서 모든 데이터를 DB에서 읽어오고
         * 메모리에서 페이징해버린다.(매우 위험하다.)
         * v3.1에서 이러한 한계를 돌파해본다.
         *
         * 또한 패치조인은 컬렉션 둘 이상일 때 사용하면 안된다.
         */
        return orderQueryService.orderV3();
    }

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> orderV3_1(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit)
            {
        /**
         * 페이징 + 컬렉션 반환을 함께 하려면 어떻게 해야할까?
         * 1. XToOne 관계를 모두 패치조인한다. ToOne관계는 Row수를 증가시키지 않으므로 페이지에 영향을 주지 않는다.
         *    즉 v3에서 문제가 되었던 Order, orderItems의 패치 조인은 제외한다.
         * 2. 컬렉션(=ToMany)은 지연 로딩으로 조회한다.(패치조인 사용 X)
         * 3. 지연 로딩의 성능 최적화를 위해 hibernate.default_batch_fetch_size, @BatchSize를 적용한다.
         * hibernate.default_batch_fetch_size : 글로벌 설정(applcation.yml)
         * @BatchSize : 개별 최적화로써 패치 사이즈를 클래스에서 사용할 때(보통 글로벌 설정으로 하면 된다.)
         * 이 옵션을 사용하면 컬렉션이나, 프록시 객체를 한번에 설정한 size만큼 IN 쿼리로 조회한다.
         *
         * 즉 ToOne 관계는 패치조인해도 페이징에 영향을 주지 않으므로 사용하여 쿼리 수를 줄이고
         * 나머지는 hibernate.default_batch_fetch_size으로 최적화한다.(사이즈 100~1000 추천)
         */
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        List<OrderDto> result = orders.stream()
                .map(OrderDto::new)
                .collect(toList());

        return result;
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> orderV4() {
        /**
         * JPA에서 DTO 직접 조회
         * 코드가 단순하여 특정 몇 건만 조회할 경우 이 방식을 사용해도 성능이 우수하다.
         */
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> orderV5() {
        /**
         * JPA에서 DTO 직접 조회 - 컬렉션 조회 최적화
         * 일대다 관계인 컬렉션은 IN절을 활용해서 메모리에 미리 조회해 최적화
         * 다수의 건을 조회할 경우에는 v4보다 100배 이상 성능이 우수할 수도 있으므로 이 방법을 사용하자.
         */
        return orderQueryRepository.findAllByDto_optimization();
    }

    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> orderV6() {
        /**
         * JPA에서 DTO 직접 조회 - 플랫 데이터 최적화
         * JOIN결과를 그대로 조회 후 어플리케이션에서 원하는 모양으로 직접 변환
         * 쿼리는 한번만 나가도록 했지만 중복 데이터가 나오므로 V5보다 느릴 수도 있다. -> 페이징이 불가능하다.
         * 쿼리를 줄인다고 무조건적으로 좋은 것은 아니기도 하고 실무에서 선택하긴 힘든 방식이다.
         */
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }

}
