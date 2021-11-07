package jpabook.jpashop.service.query;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
public class OrderDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemDto> orderItems;
    public OrderDto(Order o) {
        orderId = o.getId();
        name = o.getMember().getName();
        orderDate = o.getOrderDate();
        address = o.getDelivery().getAddress();
        orderStatus = o.getStatus();
            /*
            이렇게 작성을 하게 되면 엔티티를 DTO로 완전히 변환했다고 할 수 없다. 여전히 엔티티에 의존하는 것이므로
            o.getOrderItems().stream().forEach(e -> e.getItem().getName());
            orderItems = o.getOrderItems(); */
        orderItems = o.getOrderItems().stream()
                .map(orderItem -> new OrderItemDto(orderItem))
                .collect(toList());
    }
}
