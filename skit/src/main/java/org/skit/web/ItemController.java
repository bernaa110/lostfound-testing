package org.skit.web;


import org.skit.model.Category;
import org.skit.model.Item;
import org.skit.model.Type;
import org.skit.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Controller
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping(value = {"/lost"})
    public String showListLost(@RequestParam(required = false, defaultValue = "1") int pageNum,
                               @RequestParam(required = false, defaultValue = "10") int size,
                               @RequestParam(required = false) String name,
                               @RequestParam(required = false) Category category,
                               @RequestParam(required = false) String location,
                               @RequestParam(required = false) String description,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateIssueCreated,
                               @RequestParam(required = false) boolean handover,
                               Pageable pageable,
                               Model model) {

        Page<Item> items = this.itemService.getAllLostItems(PageRequest.of(pageNum - 1, size));
        if (name != null || category != null || location != null || description != null || dateIssueCreated != null) {
            items = this.itemService.filter(name, category, location, description, dateIssueCreated, Type.LOST, handover, PageRequest.of(pageNum - 1, size));
        }

        model.addAttribute("items", items);
        model.addAttribute("category", Category.values());
        model.addAttribute("type", Type.values());
        model.addAttribute("size", size);
        return "lost";
    }

    @GetMapping(value = {"/found"})
    public String showListFound(@RequestParam(required = false, defaultValue = "1") int pageNum,
                                @RequestParam(required = false, defaultValue = "10") int size,
                                @RequestParam(required = false) String name,
                                @RequestParam(required = false) Category category,
                                @RequestParam(required = false) String location,
                                @RequestParam(required = false) String description,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateIssueCreated,
                                @RequestParam(required = false) boolean handover,
                                Pageable pageable,
                                Model model) {

        Page<Item> items = this.itemService.getAllFoundItems(PageRequest.of(pageNum - 1, size));

        if (name != null || category != null || location != null || description != null || dateIssueCreated != null) {
            items = this.itemService.filter(name, category, location, description, dateIssueCreated, Type.FOUND, handover,PageRequest.of(pageNum - 1, size));
        }

        model.addAttribute("items", items);
        model.addAttribute("category", Category.values());
        model.addAttribute("type", Type.values());
        model.addAttribute("size", size);
        return "found";
    }

    @GetMapping("/item/add")
    public String showAdd(Model model) {
        model.addAttribute("item", new Item());
        model.addAttribute("category", Category.values());
        model.addAttribute("types", Type.values());
        return "form";
    }


    @GetMapping("/item/{id}/edit")
    public String showEdit(@PathVariable Long id,
                           Model model) {
        Item item = itemService.findById(id);
        if (item == null) {
            model.addAttribute("errorMessage", "Item not found");
            return "error";
        }
        model.addAttribute("item", item);
        model.addAttribute("category", Category.values());
        model.addAttribute("types", Type.values());
        return "form";
    }

    @GetMapping("/item/{id}/details")
    public String showDetails(@PathVariable Long id,
                              Model model) {
        Item item = itemService.findById(id);
        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
        }
        model.addAttribute("item", item);
        model.addAttribute("category", Category.values());
        model.addAttribute("types", Type.values());
        return "details";
    }

    @PostMapping("/item")
    public String create(@RequestParam String name,
                         @RequestParam Category category,
                         @RequestParam String description,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateIssueCreated,
                         @RequestParam String location,
                         @RequestParam Type type,
                         @RequestParam(required = false) MultipartFile imageFile
    ) {
        this.itemService.create(name, category, description, dateIssueCreated, location, type, imageFile);
        if (type == Type.LOST) {
            return "redirect:/lost";
        } else
            return "redirect:/found";
    }


    @PostMapping("/item/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String name,
                         @RequestParam Category category,
                         @RequestParam String description,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateIssueCreated,
                         @RequestParam String location,
                         @RequestParam Type type,
                         @RequestParam(required = false) MultipartFile imageFile,
                         @RequestParam(required = false) boolean handover
    ) {
        this.itemService.update(id, name, category, description, dateIssueCreated, location, type, imageFile, handover);
        if (type == Type.LOST) {
            return "redirect:/lost";
        } else
            return "redirect:/found";
    }

    @PostMapping("/item/{id}/delete")
    public String delete(@PathVariable Long id) {
        Type type = itemService.findById(id).getType();
        this.itemService.delete(id);
        if (type == Type.LOST) {
            return "redirect:/lost";
        } else
            return "redirect:/found";
    }

}


