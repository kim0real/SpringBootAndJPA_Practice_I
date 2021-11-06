package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Service
@Transactional(readOnly = true) //스프링에서 제공하는 어노테이션으로 쓰기가 아닌 경우 readOnly를 붙인다.
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    /*영속성 컨테이너가 관리하므로 따로 업데이트 로직없이 Setter만 있어도 알아서 변경감지하여 flush한다.
      Merge는 변경을 원하지않는 컬럼까지 업데이트하기 때문에 위험하므로 이 방법으로 Update하여야 한다.
      물론 아래처럼 Setter를 남발하지 않고 의미있는 메소드를 통해 매핑해야한다.
     */
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        Item findItem = itemRepository.findOne(itemId);
        findItem.setPrice(price);
        findItem.setName(name);
        findItem.setStockQuantity(stockQuantity);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long id) {
        return itemRepository.findOne(id);
    }
}
