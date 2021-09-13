package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());

        return "items/createItemForm";
    }

    @PostMapping("items/new")
    public String create(BookForm form){
        Book book = new Book();
        /* Setter로 지정해주는 것보다는 생성 메소드를 사용하는 것이 바람직하다.
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());
         */
        book = book.createBook(form);
        itemService.saveItem(book);

        return "redirect:/items";
    }

    @GetMapping("/items")
    public String selectItems(Model model){
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);

        return "items/itemList";
    }

    @GetMapping("items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model){
        Book item = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);

        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit")
    public String updateItem(BookForm form, @PathVariable Long itemId){
        Book book = new Book();

        //book = book.createBook(form);
        //book.setId(form.getId());
        //itemService.saveItem(book);

        //itemService.updateItem(form.getId(), form.getName(), form.getPrice(), form.getStockQuantity());
        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity()); // Spring에서 PathVariable을 통해 id값을 받는 것을 선호한다.

        return "redirect:/items";
    }
}
