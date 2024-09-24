package org.skit.service;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import org.skit.model.Category;
import org.skit.model.Item;
import org.skit.model.Type;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;


public interface ItemService {

    List<Item> listAll();

    Item findById(Long id);

    Item create(String name, Category category, String description, LocalDate dateIssueCreated, String location, Type type, MultipartFile file);

    Item update(Long id, String name, Category category, String description, LocalDate dateIssueCreated, String location, Type type, MultipartFile file, boolean handover);

    Item delete(Long id);

    Page<Item> getAllLostItems(Pageable pageable);

    Page<Item> getAllFoundItems(Pageable pageable);

    Page<Item> filter(String name, Category category, String location, String description, LocalDate dateIssueCreated, Type type, boolean handover, Pageable pageable);

}
