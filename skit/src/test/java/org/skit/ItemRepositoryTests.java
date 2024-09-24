package org.skit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skit.model.Category;
import org.skit.model.Item;
import org.skit.model.Type;
import org.skit.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class ItemRepositoryTests {

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
    }

    @Test
    void testFindByTypeLostAndHandoverIsFalse() {
        Item item1 = MockedItems.lostPhone;
        Item item2 = MockedItems.lostWallet;
        Item item3 = MockedItems.foundWallet;
        item2.setHandover(true);

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        Pageable pageable = PageRequest.of(0, 10);
        var result = itemRepository.findByTypeAndHandoverIsFalse(Type.LOST, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testFindByTypeFoundAndHandoverIsFalse() {
        Item item1 = MockedItems.lostPhone;
        Item item2 = MockedItems.foundPhone;
        Item item3 = MockedItems.foundWallet;
        item3.setHandover(true);

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        Pageable pageable = PageRequest.of(0, 10);
        var result = itemRepository.findByTypeAndHandoverIsFalse(Type.FOUND, pageable);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testFindByTypeAndCategoryAndHandoverIsFalse() {
        List<Item> itemsByCategory = MockedItems.itemsByCategory();
        itemRepository.saveAll(itemsByCategory);
        for (Category category : Category.values()) {
            List<Item> lostItems = itemRepository.findByTypeAndCategoryAndHandoverIsFalse(Type.LOST, category);
            List<Item> foundItems = itemRepository.findByTypeAndCategoryAndHandoverIsFalse(Type.FOUND, category);

            assertThat(lostItems.size()).isEqualTo(1);
            assertThat(foundItems.size()).isEqualTo(1);
        }
    }

}

