package org.skit;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.skit.model.Category;
import org.skit.model.Item;
import org.skit.model.Type;
import org.skit.repository.ItemRepository;
import org.skit.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.skit.model.exceptions.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class ItemServiceTests {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @AfterEach
    void tearDown() {
        itemRepository.deleteAll();
    }

    @Test
    void testListAll() {
        List<Item> items = Arrays.asList(
                MockedItems.lostPhone,
                MockedItems.lostWallet,
                MockedItems.foundWallet
        );

        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});

        items.forEach(item -> itemService.create(
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDateIssueCreated(),
                item.getLocation(),
                item.getType(),
                mockImageFile
        ));

        Pageable pageable = PageRequest.of(0, 10);
        assertEquals(3, itemService.listAll().size(), "Expected 2 lost items.");
    }

    @Test
    void testListItems() {
        List<Item> items = Arrays.asList(
                MockedItems.lostPhone,
                MockedItems.lostWallet,
                MockedItems.foundWallet
        );

        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});

        items.forEach(item -> itemService.create(
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDateIssueCreated(),
                item.getLocation(),
                item.getType(),
                mockImageFile
        ));

        Pageable pageable = PageRequest.of(0, 10);
        assertEquals(2, itemService.getAllLostItems(pageable).getTotalElements(), "Expected 2 lost items.");
        assertEquals(1, itemService.getAllFoundItems(pageable).getTotalElements(), "Expected 1 found item.");
    }

    @Test
    void testFindById() {
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});
        Item item = MockedItems.lostPhone;
        item = itemService.create(
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDateIssueCreated(),
                item.getLocation(),
                item.getType(),
                mockImageFile
        );
        Item retrievedItem = itemService.findById(item.getId());
        assertEquals(item.getId().toString(), retrievedItem.getId().toString());
    }

    @Test
    void testFindByIdNonExistentItem() {
        Long nonExistentId = 999L;
        assertThrows(NullPointerException.class, () -> itemService.findById(nonExistentId));
    }

    @Test
    void testCreateItem() throws IOException {
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});

        Item newItem = MockedItems.lostPhone;

        Item createdItem = itemService.create(
                newItem.getName(),
                newItem.getCategory(),
                newItem.getDescription(),
                newItem.getDateIssueCreated(),
                newItem.getLocation(),
                newItem.getType(),
                mockImageFile
        );

        assertNotNull(createdItem, "Created item should not be null");
    }

    @Test
    void testCreateItemEmptyName() throws IOException {
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});

        Item newItem = MockedItems.lostPhone;

        assertThrows(InvalidItemException.class, () -> {
            itemService.create(
                    "",
                    newItem.getCategory(),
                    newItem.getDescription(),
                    newItem.getDateIssueCreated(),
                    newItem.getLocation(),
                    newItem.getType(),
                    mockImageFile);
        });
    }

    @Test
    void testCreateItemEmptyDescription() throws IOException {
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});

        Item newItem = MockedItems.lostPhone;

        assertThrows(InvalidItemException.class, () -> {
            itemService.create(
                    newItem.getName(),
                    newItem.getCategory(),
                    "",
                    newItem.getDateIssueCreated(),
                    newItem.getLocation(),
                    newItem.getType(),
                    mockImageFile);
        });
    }

    @Test
    void testCreateItemEmptyLocation() throws IOException {
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});

        Item newItem = MockedItems.lostPhone;

        assertThrows(InvalidItemException.class, () -> {
            itemService.create(
                    newItem.getName(),
                    newItem.getCategory(),
                    newItem.getDescription(),
                    newItem.getDateIssueCreated(),
                    "",
                    newItem.getType(),
                    mockImageFile);
        });

    }

    @Test
    void testCreateInvalidItemNameNull() {
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});
        Item item = MockedItems.lostPhone;
        assertThrows(InvalidItemException.class, () -> {
            itemService.create(
                    null,
                    item.getCategory(),
                    item.getDescription(),
                    item.getDateIssueCreated(),
                    item.getLocation(),
                    item.getType(),
                    mockImageFile);
        });
    }

    @Test
    void testCreateInvalidItemCategoryNull() {
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});
        Item item = MockedItems.lostPhone;
        assertThrows(InvalidItemException.class, () -> {
            itemService.create(
                    item.getName(),
                    null,
                    item.getDescription(),
                    item.getDateIssueCreated(),
                    item.getLocation(),
                    item.getType(),
                    mockImageFile);
        });
    }

    @Test
    void testCreateInvalidItemDescriptionNull() {
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});
        Item item = MockedItems.lostPhone;
        assertThrows(InvalidItemException.class, () -> {
            itemService.create(
                    item.getName(),
                    item.getCategory(),
                    null,
                    item.getDateIssueCreated(),
                    item.getLocation(),
                    item.getType(),
                    mockImageFile);
        });
    }

    @Test
    void testCreateInvalidItemDateIssueCreatedNull() {
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});
        Item item = MockedItems.lostPhone;
        assertThrows(InvalidItemException.class, () -> {
            itemService.create(
                    item.getName(),
                    item.getCategory(),
                    item.getDescription(),
                    null,
                    item.getLocation(),
                    item.getType(),
                    mockImageFile);
        });
    }

    @Test
    void testCreateInvalidItemDateIssueCreatedAfterNow() {
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});
        Item item = MockedItems.lostPhone;
        assertThrows(InvalidItemException.class, () -> {
            itemService.create(
                    item.getName(),
                    item.getCategory(),
                    item.getDescription(),
                    LocalDate.now().plusDays(5),
                    item.getLocation(),
                    item.getType(),
                    mockImageFile);
        });
    }

    @Test
    void testCreateInvalidItemLocationNull() {
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});
        Item item = MockedItems.lostPhone;
        assertThrows(InvalidItemException.class, () -> {
            itemService.create(
                    item.getName(),
                    item.getCategory(),
                    item.getDescription(),
                    item.getDateIssueCreated(),
                    null,
                    item.getType(),
                    mockImageFile);
        });
    }

    @Test
    void testCreateInvalidItemTypeNull() {
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});
        Item item = MockedItems.lostPhone;
        assertThrows(InvalidItemException.class, () -> {
            itemService.create(
                    item.getName(),
                    item.getCategory(),
                    item.getDescription(),
                    item.getDateIssueCreated(),
                    item.getLocation(),
                    null,
                    mockImageFile);
        });
    }

    @Test
    void testUpdateItem() {
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});
        Item item = MockedItems.lostPhone;
        item = itemService.create(
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDateIssueCreated(),
                item.getLocation(),
                item.getType(),
                mockImageFile
        );

        String updatedName = "Updated Lost Phone";
        itemService.update(item.getId(), updatedName, item.getCategory(), item.getDescription(), item.getDateIssueCreated(), item.getLocation(), item.getType(), mockImageFile, false);

        Item updatedItem = itemService.findById(item.getId());
        assertEquals(updatedName, updatedItem.getName());
    }

    @Test
    void testUpdateDeletedItem() {
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});
        Item item = MockedItems.foundPhone;
        item = itemService.create(
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDateIssueCreated(),
                item.getLocation(),
                item.getType(),
                mockImageFile
        );

        itemService.delete(item.getId());

        Item finalItem = item;
        assertThrows(NullPointerException.class, () -> {
            itemService.update(finalItem.getId(), "Updated Item", finalItem.getCategory(), finalItem.getDescription(), finalItem.getDateIssueCreated(), finalItem.getLocation(), finalItem.getType(), mockImageFile, false);
        });
    }

    @Test
    void testDeleteItem() {
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});
        Item item = MockedItems.lostPhone;
        item = itemService.create(
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDateIssueCreated(),
                item.getLocation(),
                item.getType(),
                mockImageFile
        );

        itemService.delete(item.getId());

        Long itemId = item.getId();
        assertThrows(RuntimeException.class, () -> itemService.findById(itemId));
    }

    @Test
    void testDeleteNonExistentItem() {
        Long nonExistentId = 999L;
        assertThrows(RuntimeException.class, () -> itemService.delete(nonExistentId));
    }

    @Test
    void testGetAllLostItems() {
        List<Item> items = Arrays.asList(
                MockedItems.lostPhone,
                MockedItems.lostWallet,
                MockedItems.foundWallet
        );

        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});

        items.forEach(item -> itemService.create(
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDateIssueCreated(),
                item.getLocation(),
                item.getType(),
                mockImageFile
        ));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> result = itemService.getAllLostItems(pageable);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(item -> item.getType() == Type.LOST));
    }

    @Test
    void testGetAllFoundItems() {
        List<Item> items = Arrays.asList(
                MockedItems.lostPhone,
                MockedItems.foundWallet,
                MockedItems.foundWallet
        );

        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});

        items.forEach(item -> itemService.create(
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDateIssueCreated(),
                item.getLocation(),
                item.getType(),
                mockImageFile
        ));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> result = itemService.getAllFoundItems(pageable);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(item -> item.getType() == Type.FOUND));
    }

    @Test
    void testFilterByNameOnly() {
        Item item = MockedItems.lostPhone;
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});
        itemService.create(
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDateIssueCreated(),
                item.getLocation(),
                item.getType(),
                mockImageFile
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> result = itemService.filter("Phone", null, null, null, null, null, false, pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testFilterByCategoryOnly() {
        List<Item> items = Arrays.asList(
                MockedItems.lostPhone,
                MockedItems.lostWallet
        );

        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});

        items.forEach(item -> itemService.create(
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDateIssueCreated(),
                item.getLocation(),
                item.getType(),
                mockImageFile
        ));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemService.filter(null, Category.ACCESSORIES, null, null, null, null, false, pageable);

        AssertionsForClassTypes.assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testFilterByLocationOnly() {
        List<Item> items = Arrays.asList(
                MockedItems.lostPhone,
                MockedItems.lostWallet
        );

        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});

        items.forEach(item -> itemService.create(
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDateIssueCreated(),
                item.getLocation(),
                item.getType(),
                mockImageFile
        ));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemService.filter(null, null, "Location A", null, null, null, false, pageable);

        AssertionsForClassTypes.assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testFilterByDescriptionOnly() {
        List<Item> items = Arrays.asList(
                MockedItems.lostPhone,
                MockedItems.foundWallet
        );
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});

        items.forEach(item -> itemService.create(
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDateIssueCreated(),
                item.getLocation(),
                item.getType(),
                mockImageFile
        ));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemService.filter(null, null, null, "lost", null, null, false, pageable);

        AssertionsForClassTypes.assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testFilterByDateOnly() {
        List<Item> items = Arrays.asList(
                MockedItems.lostPhone,
                MockedItems.foundWallet,
                MockedItems.foundPhone
        );
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            itemService.create(
                    item.getName(),
                    item.getCategory(),
                    item.getDescription(),
                    item.getDateIssueCreated().minusDays(i + 3), // Adjusted for each item
                    item.getLocation(),
                    item.getType(),
                    mockImageFile
            );
        }

        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemService.filter(null, null, null, null, LocalDate.now().minusDays(4), null, false, pageable);

        AssertionsForClassTypes.assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void testFilterByTypeFoundOnly() {
        List<Item> items = Arrays.asList(
                MockedItems.lostPhone,
                MockedItems.foundWallet
        );
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});

        items.forEach(item -> itemService.create(
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDateIssueCreated(),
                item.getLocation(),
                item.getType(),
                mockImageFile
        ));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemService.filter(null, null, null, null, null, Type.FOUND, false, pageable);

        AssertionsForClassTypes.assertThat(result.getTotalElements()).isEqualTo(1);
    }
    @Test
    void testFilterByTypeLostOnly() {
        List<Item> items = Arrays.asList(
                MockedItems.lostPhone,
                MockedItems.foundWallet
        );
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});

        items.forEach(item -> itemService.create(
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDateIssueCreated(),
                item.getLocation(),
                item.getType(),
                mockImageFile
        ));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemService.filter(null, null, null, null, null, Type.LOST, false, pageable);

        AssertionsForClassTypes.assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testFilterByHandover() {
        List<Item> items = Arrays.asList(
                MockedItems.lostPhone,
                MockedItems.foundWallet,
                MockedItems.foundPhone
        );
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});

        Item item1 = itemService.create(
                items.get(0).getName(),
                items.get(0).getCategory(),
                items.get(0).getDescription(),
                items.get(0).getDateIssueCreated(),
                items.get(0).getLocation(),
                items.get(0).getType(),
                mockImageFile
        );

        itemService.create(
                items.get(1).getName(),
                items.get(1).getCategory(),
                items.get(1).getDescription(),
                items.get(1).getDateIssueCreated(),
                items.get(1).getLocation(),
                items.get(1).getType(),
                mockImageFile
        );
        itemService.create(
                items.get(2).getName(),
                items.get(2).getCategory(),
                items.get(2).getDescription(),
                items.get(2).getDateIssueCreated(),
                items.get(2).getLocation(),
                items.get(2).getType(),
                mockImageFile
        );

        itemService.update(
                item1.getId(),
                item1.getName(),
                item1.getCategory(),
                item1.getDescription(),
                item1.getDateIssueCreated(),
                item1.getLocation(),
                item1.getType(),
                mockImageFile,
                true
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> resultHandoverTrue = itemService.filter(null, null, null, null, null, null, true, pageable);
        AssertionsForClassTypes.assertThat(resultHandoverTrue.getTotalElements()).isEqualTo(1);

        pageable = PageRequest.of(0, 10);
        Page<Item> resultHandoverFalse = itemService.filter(null, null, null, null, null, null, false, pageable);
        AssertionsForClassTypes.assertThat(resultHandoverFalse.getTotalElements()).isEqualTo(2);
    }

    @Test
    void testFilterWithNameAndCategory() {
        List<Item> items = Arrays.asList(
                MockedItems.lostPhone,
                MockedItems.lostWallet
        );
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});

        items.forEach(item -> itemService.create(
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDateIssueCreated(),
                item.getLocation(),
                item.getType(),
                mockImageFile
        ));


        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemService.filter("Phone", Category.MOBILE_PHONES, null, null, null, null, false, pageable);

        AssertionsForClassTypes.assertThat(result.getTotalElements()).isEqualTo(1);
        AssertionsForClassTypes.assertThat(result.getContent().get(0).getName()).isEqualTo("Lost Phone");
    }

    @Test
    void testFilterWithNameAndLocation() {
        List<Item> items = Arrays.asList(
                MockedItems.lostPhone,
                MockedItems.lostWallet
        );
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});

        items.forEach(item -> itemService.create(
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDateIssueCreated(),
                item.getLocation(),
                item.getType(),
                mockImageFile
        ));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemService.filter("Wallet", null, "Location B", null, null, null, false, pageable);

        AssertionsForClassTypes.assertThat(result.getTotalElements()).isEqualTo(1);
        AssertionsForClassTypes.assertThat(result.getContent().get(0).getName()).isEqualTo("Lost Wallet");
    }

    @Test
    void testFilterWithNameCategoryAndDate() {
        List<Item> items = Arrays.asList(
                MockedItems.lostPhone,
                MockedItems.foundWallet
        );
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});
        items.get(0).setDateIssueCreated(LocalDate.now().minusDays(5));
        items.get(1).setDateIssueCreated(LocalDate.now().minusDays(10));
        items.forEach(item -> itemService.create(
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDateIssueCreated(),
                item.getLocation(),
                item.getType(),
                mockImageFile
        ));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemService.filter("Phone", Category.MOBILE_PHONES, null, null, LocalDate.now().minusDays(6), null, false, pageable);

        AssertionsForClassTypes.assertThat(result.getTotalElements()).isEqualTo(1);
        AssertionsForClassTypes.assertThat(result.getContent().get(0).getName()).isEqualTo("Lost Phone");
    }

    @Test
    void testFilterWithCategoryAndType() {
        List<Item> items = Arrays.asList(
                MockedItems.lostPhone,
                MockedItems.foundWallet
        );
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});

        items.forEach(item -> itemService.create(
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDateIssueCreated(),
                item.getLocation(),
                item.getType(),
                mockImageFile
        ));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemService.filter(null, Category.ACCESSORIES, null, null, null, Type.FOUND, false, pageable);

        AssertionsForClassTypes.assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testFilterWithMultipleCombinedFilters() {
        List<Item> items = Arrays.asList(
                MockedItems.lostPhone,
                MockedItems.foundPhone
        );
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});
        items.forEach(item -> itemService.create(
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDateIssueCreated(),
                item.getLocation(),
                item.getType(),
                mockImageFile
        ));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemService.filter("Phone", Category.MOBILE_PHONES, "Location A", null, null, Type.LOST, false, pageable);

        AssertionsForClassTypes.assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testFilterWithNoResults() {
        List<Item> items = Arrays.asList(
                MockedItems.lostPhone,
                MockedItems.foundWallet
        );
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});

        items.forEach(item -> itemService.create(
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDateIssueCreated(),
                item.getLocation(),
                item.getType(),
                mockImageFile
        ));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> result = itemService.filter("Non-existent", null, null, null, null, null, false, pageable);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFilterEmptyParameters() {
        List<Item> items = Arrays.asList(
                MockedItems.lostPhone,
                MockedItems.foundWallet,
                MockedItems.lostPhone
        );
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});

        items.forEach(item -> itemService.create(
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDateIssueCreated(),
                item.getLocation(),
                item.getType(),
                mockImageFile
        ));
        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> result = itemService.filter(null, null, null, null, null, null, false, pageable);
        assertEquals(3, result.getTotalElements());
    }

    @Test
    void testFilterWithEmptyValues() {
        List<Item> items = Arrays.asList(
                MockedItems.lostPhone,
                MockedItems.foundWallet,
                MockedItems.lostWallet
        );
        MultipartFile mockImageFile = new MockMultipartFile("image", new byte[]{});

        items.forEach(item -> itemService.create(
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                item.getDateIssueCreated(),
                item.getLocation(),
                item.getType(),
                mockImageFile
        ));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemService.filter("", null, "", "", null, null, true, pageable);

        AssertionsForClassTypes.assertThat(result.getTotalElements()).isEqualTo(0);
    }
}