package org.skit.service.impl;

import org.skit.model.Category;
import org.skit.model.Item;
import org.skit.model.Type;
import org.skit.model.exceptions.InvalidItemException;
import org.skit.repository.ItemRepository;
import org.skit.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public List<Item> listAll() {
        return this.itemRepository.findAll();
    }

    @Override
    public Item findById(Long id) {
        return this.itemRepository.findById(id).orElseThrow(null);
    }

    @Override
    public Item create(String name, Category category, String description, LocalDate dateIssueCreated, String location, Type type, MultipartFile imageFile) {
        if (name == null || name.isEmpty()) {
            throw new InvalidItemException();
        }
        if (category == null) {
            throw new InvalidItemException();
        }
        if (description == null || description.isEmpty()) {
            throw new InvalidItemException();
        }
        if (dateIssueCreated == null || dateIssueCreated.isAfter(LocalDate.now())) {
            throw new InvalidItemException();
        }
        if (location == null || location.isEmpty()) {
            throw new InvalidItemException();
        }
        if (type == null) {
            throw new InvalidItemException();
        }

        byte[] image;
        try {
            image = imageFile.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + e.getMessage());
        }

        return this.itemRepository.save(new Item(name, category, description, dateIssueCreated, location, type, image));

    }

    @Override
    public Item update(Long id, String name, Category category, String description, LocalDate dateIssueCreated, String location, Type type, MultipartFile imageFile, boolean handover) {
        Item item = this.itemRepository.findById(id).orElseThrow(null);
        if (!imageFile.isEmpty()) {
            byte[] image;
            try {
                image = imageFile.getBytes();
                item.setImage(image);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read file: " + e.getMessage());
            }
        }

        item.setName(name);
        item.setCategory(category);
        item.setDescription(description);
        item.setDateIssueCreated(dateIssueCreated);
        item.setLocation(location);
        item.setType(type);
        item.setHandover(handover);
        return this.itemRepository.save(item);
    }

    @Override
    public Item delete(Long id) {
        Item item = findById(id);
        this.itemRepository.delete(item);
        return item;
    }

    @Override
    public Page<Item> getAllLostItems(Pageable pageable) {
        return itemRepository.findByTypeAndHandoverIsFalse(Type.LOST, pageable);
    }

    @Override
    public Page<Item> getAllFoundItems(Pageable pageable) {
        return itemRepository.findByTypeAndHandoverIsFalse(Type.FOUND, pageable);
    }

    @Override
    public Page<Item> filter(String name, Category category, String location, String description, LocalDate dateIssueCreated, Type type, boolean handover, Pageable pageable) {
        List<Item> items = itemRepository.findAll();

        if (name != null && !name.isEmpty()) {
            items = items.stream()
                    .filter(item -> item.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (category != null) {
            items = items.stream()
                    .filter(item -> item.getCategory() == category)
                    .collect(Collectors.toList());
        }

        if (location != null && !location.isEmpty()) {
            items = items.stream()
                    .filter(item -> item.getLocation().toLowerCase().contains(location.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (description != null && !description.isEmpty()) {
            items = items.stream()
                    .filter(item -> item.getDescription().toLowerCase().contains(description.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (dateIssueCreated != null) {
            items = items.stream()
                    .filter(item -> item.getDateIssueCreated().isAfter(dateIssueCreated) || item.getDateIssueCreated().isEqual(dateIssueCreated))
                    .collect(Collectors.toList());
        }

        if (type != null) {
            items = items.stream()
                    .filter(item -> item.getType() == type)
                    .collect(Collectors.toList());
        }

        if (handover) {
            items = items.stream()
                    .filter(Item::isHandover)
                    .collect(Collectors.toList());
        } else {
            items = items.stream()
                    .filter(item -> !item.isHandover())
                    .collect(Collectors.toList());
        }

        int total = items.size();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), total);
        List<Item> pagedItems = items.subList(start, end);

        return new PageImpl<>(pagedItems, pageable, total);
    }


}

