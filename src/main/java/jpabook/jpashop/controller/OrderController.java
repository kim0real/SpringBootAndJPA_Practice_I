package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")
    public String createForm(Model model){
        List<Member> members = memberService.findMember();
        List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    @PostMapping("/order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count){
        System.out.println("parameter:" + memberId);
        System.out.println("parameter:" + itemId);
        System.out.println("parameter:" + count);
        orderService.order(memberId, itemId, count);

        return "redirect:/orders";
    }

    //https://heydoit.tistory.com/7 - Parameter로 받을 때 ModelAttribute, RequestParam은 생략할 순 있으나 적는 것이 바람직하다.
    @GetMapping(value = "/orders")
    public String orderList(@ModelAttribute("orderSearch") OrderSearch
                                    orderSearch, Model model) {
        List<Order> orders = orderService.findOrders(orderSearch);
        orders.forEach((o) -> {
            System.out.println(":::::::");
            System.out.println(o.getTotalPrice());
            System.out.println(o.getDelivery());
            System.out.println(o.getId());
            System.out.println(o.getOrderDate());
            System.out.println(o.getMember());
            System.out.println(o.getOrderItems());
            System.out.println(o.getStatus());
        });
        model.addAttribute("orders", orders);
        return "order/orderList";
    }
}
