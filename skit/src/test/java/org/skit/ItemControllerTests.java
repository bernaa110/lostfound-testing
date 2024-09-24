package org.skit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.skit.model.Category;
import org.skit.model.Item;
import org.skit.model.Type;
import org.skit.service.ItemService;
import org.skit.web.ItemController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ItemController.class)
public class ItemControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Mock
    private MultipartFile mockFile;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShowListFound() throws Exception {
        Page<Item> items = new PageImpl<>(Collections.singletonList(MockedItems.foundWallet));

        when(itemService.getAllFoundItems(any(PageRequest.class))).thenReturn(items);

        mockMvc.perform(get("/found"))
                .andExpect(status().isOk())
                .andExpect(view().name("found"))
                .andExpect(model().attributeExists("items", "category", "type"))
                .andExpect(model().attribute("size", 10));
    }

    @Test
    public void testShowListLost() throws Exception {
        Page<Item> items = new PageImpl<>(Collections.singletonList(MockedItems.lostPhone));

        when(itemService.getAllLostItems(any(PageRequest.class))).thenReturn(items);

        mockMvc.perform(get("/lost"))
                .andExpect(status().isOk())
                .andExpect(view().name("lost"))
                .andExpect(model().attributeExists("items", "category", "type"))
                .andExpect(model().attribute("size", 10));
    }

    @Test
    public void testShowListLostWithFilters() throws Exception {
        String name = "Lost Wallet";
        Category category = Category.OTHER;
        String location = "Park";
        String description = "Found in the park.";
        LocalDate dateIssueCreated = LocalDate.now();
        boolean handover = false;
        int pageNum = 1;
        int size = 10;

        Page<Item> filteredItems = new PageImpl<>(Collections.singletonList(MockedItems.lostWallet));

        when(itemService.filter(eq(name), eq(category), eq(location), eq(description), eq(dateIssueCreated), eq(Type.LOST), eq(handover), any(Pageable.class)))
                .thenReturn(filteredItems);

        mockMvc.perform(get("/lost")
                        .param("pageNum", String.valueOf(pageNum))
                        .param("size", String.valueOf(size))
                        .param("name", name)
                        .param("category", category.name())
                        .param("location", location)
                        .param("description", description)
                        .param("dateIssueCreated", dateIssueCreated.toString())
                        .param("handover", String.valueOf(handover)))
                .andExpect(status().isOk())
                .andExpect(view().name("lost"))
                .andExpect(model().attributeExists("items", "category", "type"))
                .andExpect(model().attribute("items", filteredItems))
                .andExpect(model().attribute("size", size));

        verify(itemService).filter(name, category, location, description, dateIssueCreated, Type.LOST, handover, PageRequest.of(pageNum - 1, size));
    }


    @Test
    public void testShowListFoundWithFilters() throws Exception {
        String name = "Lost Wallet";
        Category category = Category.OTHER;
        String location = "Park";
        String description = "Found in the park.";
        LocalDate dateIssueCreated = LocalDate.now();
        boolean handover = false;
        int pageNum = 1;
        int size = 10;

        Page<Item> filteredItems = new PageImpl<>(Collections.singletonList(MockedItems.foundWallet));

        when(itemService.filter(eq(name), eq(category), eq(location), eq(description), eq(dateIssueCreated), eq(Type.FOUND), eq(handover), any(Pageable.class)))
                .thenReturn(filteredItems);

        mockMvc.perform(get("/found")
                        .param("pageNum", String.valueOf(pageNum))
                        .param("size", String.valueOf(size))
                        .param("name", name)
                        .param("category", category.name())
                        .param("location", location)
                        .param("description", description)
                        .param("dateIssueCreated", dateIssueCreated.toString())
                        .param("handover", String.valueOf(handover)))
                .andExpect(status().isOk())
                .andExpect(view().name("found"))
                .andExpect(model().attributeExists("items", "category", "type"))
                .andExpect(model().attribute("items", filteredItems))
                .andExpect(model().attribute("size", size));

        verify(itemService).filter(name, category, location, description, dateIssueCreated, Type.FOUND, handover, PageRequest.of(pageNum - 1, size));
    }

    @Test
    public void testShowListLostWithCategoryFilter() throws Exception {
        Category category = Category.OTHER;
        int pageNum = 1;
        int size = 10;

        Page<Item> filteredItems = new PageImpl<>(Collections.singletonList(MockedItems.lostWallet));

        when(itemService.filter(eq(null), eq(category), eq(null), eq(null), eq(null), eq(Type.LOST), eq(false), any(Pageable.class)))
                .thenReturn(filteredItems);

        mockMvc.perform(get("/lost")
                        .param("pageNum", String.valueOf(pageNum))
                        .param("size", String.valueOf(size))
                        .param("category", category.name()))
                .andExpect(status().isOk())
                .andExpect(view().name("lost"))
                .andExpect(model().attributeExists("items", "category", "type"))
                .andExpect(model().attribute("items", filteredItems))
                .andExpect(model().attribute("size", size));

        verify(itemService).filter(null, category, null, null, null, Type.LOST, false, PageRequest.of(pageNum - 1, size));
    }

    @Test
    public void testShowListLostWithLocationFilter() throws Exception {
        String location = "Park";
        int pageNum = 1;
        int size = 10;

        Page<Item> filteredItems = new PageImpl<>(Collections.singletonList(MockedItems.lostWallet));

        when(itemService.filter(eq(null), eq(null), eq(location), eq(null), eq(null), eq(Type.LOST), eq(false), any(Pageable.class)))
                .thenReturn(filteredItems);

        mockMvc.perform(get("/lost")
                        .param("pageNum", String.valueOf(pageNum))
                        .param("size", String.valueOf(size))
                        .param("location", location))
                .andExpect(status().isOk())
                .andExpect(view().name("lost"))
                .andExpect(model().attributeExists("items", "category", "type"))
                .andExpect(model().attribute("items", filteredItems))
                .andExpect(model().attribute("size", size));

        verify(itemService).filter(null, null, location, null, null, Type.LOST, false, PageRequest.of(pageNum - 1, size));
    }

    @Test
    public void testShowListLostWithDescriptionFilter() throws Exception {
        String description = "Found in the park.";
        int pageNum = 1;
        int size = 10;

        Page<Item> filteredItems = new PageImpl<>(Collections.singletonList(MockedItems.lostWallet));

        when(itemService.filter(eq(null), eq(null), eq(null), eq(description), eq(null), eq(Type.LOST), eq(false), any(Pageable.class)))
                .thenReturn(filteredItems);

        mockMvc.perform(get("/lost")
                        .param("pageNum", String.valueOf(pageNum))
                        .param("size", String.valueOf(size))
                        .param("description", description))
                .andExpect(status().isOk())
                .andExpect(view().name("lost"))
                .andExpect(model().attributeExists("items", "category", "type"))
                .andExpect(model().attribute("items", filteredItems))
                .andExpect(model().attribute("size", size));

        verify(itemService).filter(null, null, null, description, null, Type.LOST, false, PageRequest.of(pageNum - 1, size));
    }

    @Test
    public void testShowListLostWithDateIssueCreatedFilter() throws Exception {
        LocalDate dateIssueCreated = LocalDate.now();
        int pageNum = 1;
        int size = 10;

        Page<Item> filteredItems = new PageImpl<>(Collections.singletonList(MockedItems.lostWallet));

        when(itemService.filter(eq(null), eq(null), eq(null), eq(null), eq(dateIssueCreated), eq(Type.LOST), eq(false), any(Pageable.class)))
                .thenReturn(filteredItems);

        mockMvc.perform(get("/lost")
                        .param("pageNum", String.valueOf(pageNum))
                        .param("size", String.valueOf(size))
                        .param("dateIssueCreated", dateIssueCreated.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("lost"))
                .andExpect(model().attributeExists("items", "category", "type"))
                .andExpect(model().attribute("items", filteredItems))
                .andExpect(model().attribute("size", size));

        verify(itemService).filter(null, null, null, null, dateIssueCreated, Type.LOST, false, PageRequest.of(pageNum - 1, size));
    }

    @Test
    public void testShowListFoundWithCategoryFilter() throws Exception {
        Category category = Category.OTHER;
        int pageNum = 1;
        int size = 10;

        Page<Item> filteredItems = new PageImpl<>(Collections.singletonList(MockedItems.foundWallet));

        when(itemService.filter(eq(null), eq(category), eq(null), eq(null), eq(null), eq(Type.FOUND), eq(false), any(Pageable.class)))
                .thenReturn(filteredItems);

        mockMvc.perform(get("/found")
                        .param("pageNum", String.valueOf(pageNum))
                        .param("size", String.valueOf(size))
                        .param("category", category.name()))
                .andExpect(status().isOk())
                .andExpect(view().name("found"))
                .andExpect(model().attributeExists("items", "category", "type"))
                .andExpect(model().attribute("items", filteredItems))
                .andExpect(model().attribute("size", size));

        verify(itemService).filter(null, category, null, null, null, Type.FOUND, false, PageRequest.of(pageNum - 1, size));
    }

    @Test
    public void testShowListFoundWithLocationFilter() throws Exception {
        String location = "Park";
        int pageNum = 1;
        int size = 10;

        Page<Item> filteredItems = new PageImpl<>(Collections.singletonList(MockedItems.foundWallet));

        when(itemService.filter(eq(null), eq(null), eq(location), eq(null), eq(null), eq(Type.FOUND), eq(false), any(Pageable.class)))
                .thenReturn(filteredItems);

        mockMvc.perform(get("/found")
                        .param("pageNum", String.valueOf(pageNum))
                        .param("size", String.valueOf(size))
                        .param("location", location))
                .andExpect(status().isOk())
                .andExpect(view().name("found"))
                .andExpect(model().attributeExists("items", "category", "type"))
                .andExpect(model().attribute("items", filteredItems))
                .andExpect(model().attribute("size", size));

        verify(itemService).filter(null, null, location, null, null, Type.FOUND, false, PageRequest.of(pageNum - 1, size));
    }

    @Test
    public void testShowListFoundWithDescriptionFilter() throws Exception {
        String description = "Found in the park.";
        int pageNum = 1;
        int size = 10;

        Page<Item> filteredItems = new PageImpl<>(Collections.singletonList(MockedItems.foundWallet));

        when(itemService.filter(eq(null), eq(null), eq(null), eq(description), eq(null), eq(Type.FOUND), eq(false), any(Pageable.class)))
                .thenReturn(filteredItems);

        mockMvc.perform(get("/found")
                        .param("pageNum", String.valueOf(pageNum))
                        .param("size", String.valueOf(size))
                        .param("description", description))
                .andExpect(status().isOk())
                .andExpect(view().name("found"))
                .andExpect(model().attributeExists("items", "category", "type"))
                .andExpect(model().attribute("items", filteredItems))
                .andExpect(model().attribute("size", size));

        verify(itemService).filter(null, null, null, description, null, Type.FOUND, false, PageRequest.of(pageNum - 1, size));
    }

    @Test
    public void testShowListFoundWithDateIssueCreatedFilter() throws Exception {
        LocalDate dateIssueCreated = LocalDate.now();
        int pageNum = 1;
        int size = 10;

        Page<Item> filteredItems = new PageImpl<>(Collections.singletonList(MockedItems.foundWallet));

        when(itemService.filter(eq(null), eq(null), eq(null), eq(null), eq(dateIssueCreated), eq(Type.FOUND), eq(false), any(Pageable.class)))
                .thenReturn(filteredItems);

        mockMvc.perform(get("/found")
                        .param("pageNum", String.valueOf(pageNum))
                        .param("size", String.valueOf(size))
                        .param("dateIssueCreated", dateIssueCreated.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("found"))
                .andExpect(model().attributeExists("items", "category", "type"))
                .andExpect(model().attribute("items", filteredItems))
                .andExpect(model().attribute("size", size));

        verify(itemService).filter(null, null, null, null, dateIssueCreated, Type.FOUND, false, PageRequest.of(pageNum - 1, size));
    }

    @Test
    public void testShowListFoundWithMultipleFilters() throws Exception {
        String name = "Found Wallet";
        Category category = Category.OTHER;
        String location = "Park";
        LocalDate dateIssueCreated = LocalDate.now();
        boolean handover = false;
        int pageNum = 1;
        int size = 10;

        Page<Item> filteredItems = new PageImpl<>(Collections.singletonList(MockedItems.foundWallet));

        when(itemService.filter(eq(name), eq(category), eq(location), eq(null), eq(dateIssueCreated), eq(Type.FOUND), eq(handover), any(Pageable.class)))
                .thenReturn(filteredItems);

        mockMvc.perform(get("/found")
                        .param("pageNum", String.valueOf(pageNum))
                        .param("size", String.valueOf(size))
                        .param("name", name)
                        .param("category", category.name())
                        .param("location", location)
                        .param("dateIssueCreated", dateIssueCreated.toString())
                        .param("handover", String.valueOf(handover)))
                .andExpect(status().isOk())
                .andExpect(view().name("found"))
                .andExpect(model().attributeExists("items", "category", "type"))
                .andExpect(model().attribute("items", filteredItems))
                .andExpect(model().attribute("size", size));

        verify(itemService).filter(name, category, location, null, dateIssueCreated, Type.FOUND, handover, PageRequest.of(pageNum - 1, size));
    }

    @Test
    public void testShowListLostWithMultipleFilters() throws Exception {
        String name = "Lost Wallet";
        Category category = Category.OTHER;
        String location = "Park";
        LocalDate dateIssueCreated = LocalDate.now();
        boolean handover = false;
        int pageNum = 1;
        int size = 10;

        Page<Item> filteredItems = new PageImpl<>(Collections.singletonList(MockedItems.lostWallet));

        when(itemService.filter(eq(name), eq(category), eq(location), eq(null), eq(dateIssueCreated), eq(Type.LOST), eq(handover), any(Pageable.class)))
                .thenReturn(filteredItems);

        mockMvc.perform(get("/lost")
                        .param("pageNum", String.valueOf(pageNum))
                        .param("size", String.valueOf(size))
                        .param("name", name)
                        .param("category", category.name())
                        .param("location", location)
                        .param("dateIssueCreated", dateIssueCreated.toString())
                        .param("handover", String.valueOf(handover)))
                .andExpect(status().isOk())
                .andExpect(view().name("lost"))
                .andExpect(model().attributeExists("items", "category", "type"))
                .andExpect(model().attribute("items", filteredItems))
                .andExpect(model().attribute("size", size));

        verify(itemService).filter(name, category, location, null, dateIssueCreated, Type.LOST, handover, PageRequest.of(pageNum - 1, size));
    }


    @Test
    public void testShowFoundItemDetails() throws Exception {
        Item item = MockedItems.foundWallet;

        when(itemService.findById(anyLong())).thenReturn(item);

        mockMvc.perform(get("/item/1/details"))
                .andExpect(status().isOk())
                .andExpect(view().name("details"))
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attribute("item", item));
    }

    @Test
    public void testShowLostItemDetails() throws Exception {
        Item item = MockedItems.lostPhone;

        when(itemService.findById(anyLong())).thenReturn(item);

        mockMvc.perform(get("/item/2/details"))
                .andExpect(status().isOk())
                .andExpect(view().name("details"))
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attribute("item", item));
    }

    @Test
    public void testShowAddForm() throws Exception {
        mockMvc.perform(get("/item/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("form"))
                .andExpect(model().attributeExists("item", "category", "types"));
    }

    @Test
    public void testShowEditForm() throws Exception {
        Item item = MockedItems.lostPhone;

        when(itemService.findById(anyLong())).thenReturn(item);

        mockMvc.perform(get("/item/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("form"))
                .andExpect(model().attributeExists("item", "category", "types"));
    }

    @Test
    public void testFindItemByIdNotFound() throws Exception {
        Long id = 1L;
        when(itemService.findById(id)).thenReturn(null);

        mockMvc.perform(get("/item/{id}/edit", id))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Item not found"));
    }

    @Test
    public void testCreateItemLost() throws Exception {
        mockMvc.perform(post("/item")
                        .param("name", "Lost Phone")
                        .param("category", Category.MOBILE_PHONES.toString())
                        .param("description", "Lost phone description")
                        .param("dateIssueCreated", LocalDate.now().toString())
                        .param("location", "Another Location")
                        .param("type", Type.LOST.toString())
                        .flashAttr("imageFile", mockFile))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/lost"));
    }

    @Test
    public void testCreateItemFound() throws Exception {
        mockMvc.perform(post("/item")
                        .param("name", "Lost Phone")
                        .param("category", Category.MOBILE_PHONES.toString())
                        .param("description", "Lost phone description")
                        .param("dateIssueCreated", LocalDate.now().toString())
                        .param("location", "Another Location")
                        .param("type", Type.FOUND.toString())
                        .flashAttr("imageFile", mockFile))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/found"));
    }

    @Test
    public void testUpdateItemLost() throws Exception {
        mockMvc.perform(post("/item/1")
                        .param("name", "Lost Phone")
                        .param("category", Category.MOBILE_PHONES.toString())
                        .param("description", "Lost phone description")
                        .param("dateIssueCreated", LocalDate.now().toString())
                        .param("location", "Another Location")
                        .param("type", Type.LOST.toString())
                        .flashAttr("imageFile", mockFile)
                        .param("handover", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/lost"));
    }

    @Test
    public void testUpdateItemFound() throws Exception {
        mockMvc.perform(post("/item/1")
                        .param("name", "Lost Phone")
                        .param("category", Category.MOBILE_PHONES.toString())
                        .param("description", "Lost phone description")
                        .param("dateIssueCreated", LocalDate.now().toString())
                        .param("location", "Another Location")
                        .param("type", Type.FOUND.toString())
                        .flashAttr("imageFile", mockFile)
                        .param("handover", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/found"));
    }


    @Test
    public void testDeleteItemLost() throws Exception {
        Item item = MockedItems.lostPhone;

        when(itemService.findById(anyLong())).thenReturn(item);

        mockMvc.perform(post("/item/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/lost"));
    }

    @Test
    public void testDeleteItemFound() throws Exception {
        Item item = MockedItems.foundPhone;

        when(itemService.findById(anyLong())).thenReturn(item);

        mockMvc.perform(post("/item/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/found"));
    }

    @Test
    public void testItemNotFound() throws Exception {
        when(itemService.findById(anyLong())).thenReturn(null);

        mockMvc.perform(get("/item/999/details"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateItemValidationNameRequired() throws Exception {
        Item mockedItem = MockedItems.lostPhone;
        mockMvc.perform(post("/item")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("category", mockedItem.getCategory().toString())
                        .param("description", mockedItem.getDescription())
                        .param("dateIssueCreated", mockedItem.getDateIssueCreated().toString())
                        .param("location", mockedItem.getLocation())
                        .param("type", mockedItem.getType().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertNotNull(result.getResolvedException());
                    String message = result.getResolvedException().getMessage();
                    assertTrue(message.contains("name"));
                    assertTrue(message.contains("Required request parameter"));
                });
        verify(itemService, never()).create(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    public void testCreateItemValidationCategoryRequired() throws Exception {
        Item mockedItem = MockedItems.lostPhone;
        mockMvc.perform(post("/item")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", mockedItem.getName())
                        .param("description", mockedItem.getDescription())
                        .param("dateIssueCreated", mockedItem.getDateIssueCreated().toString())
                        .param("location", mockedItem.getLocation())
                        .param("type", mockedItem.getType().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertNotNull(result.getResolvedException());
                    String message = result.getResolvedException().getMessage();
                    assertTrue(message.contains("category"));
                    assertTrue(message.contains("Required request parameter"));
                });
        verify(itemService, never()).create(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    public void testCreateItemValidationDescriptionRequired() throws Exception {
        Item mockedItem = MockedItems.lostPhone;
        mockMvc.perform(post("/item")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", mockedItem.getName())
                        .param("category", mockedItem.getCategory().toString())
                        .param("dateIssueCreated", mockedItem.getDateIssueCreated().toString())
                        .param("location", mockedItem.getLocation())
                        .param("type", mockedItem.getType().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertNotNull(result.getResolvedException());
                    String message = result.getResolvedException().getMessage();
                    assertTrue(message.contains("description"));
                    assertTrue(message.contains("Required request parameter"));
                });
        verify(itemService, never()).create(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    public void testCreateItemValidationDateRequired() throws Exception {
        Item mockedItem = MockedItems.lostPhone;
        mockMvc.perform(post("/item")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", mockedItem.getName())
                        .param("category", mockedItem.getCategory().toString())
                        .param("description", mockedItem.getDescription())
                        .param("location", mockedItem.getLocation())
                        .param("type", mockedItem.getType().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertNotNull(result.getResolvedException());
                    String message = result.getResolvedException().getMessage();
                    assertTrue(message.contains("dateIssueCreated"));
                    assertTrue(message.contains("Required request parameter"));
                });
        verify(itemService, never()).create(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    public void testCreateItemValidationLocationRequired() throws Exception {
        Item mockedItem = MockedItems.lostPhone;
        mockMvc.perform(post("/item")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", mockedItem.getName())
                        .param("category", mockedItem.getCategory().toString())
                        .param("description", mockedItem.getDescription())
                        .param("dateIssueCreated", mockedItem.getDateIssueCreated().toString())
                        .param("type", mockedItem.getType().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertNotNull(result.getResolvedException());
                    String message = result.getResolvedException().getMessage();
                    assertTrue(message.contains("location"));
                    assertTrue(message.contains("Required request parameter"));
                });
        verify(itemService, never()).create(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    public void testCreateItemValidationTypeRequired() throws Exception {
        Item mockedItem = MockedItems.lostPhone;
        mockMvc.perform(post("/item")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", mockedItem.getName())
                        .param("category", mockedItem.getCategory().toString())
                        .param("description", mockedItem.getDescription())
                        .param("dateIssueCreated", mockedItem.getDateIssueCreated().toString())
                        .param("location", mockedItem.getLocation()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertNotNull(result.getResolvedException());
                    String message = result.getResolvedException().getMessage();
                    assertTrue(message.contains("type"));
                    assertTrue(message.contains("Required request parameter"));
                });
        verify(itemService, never()).create(any(), any(), any(), any(), any(), any(), any());
    }
}



