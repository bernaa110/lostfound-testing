package org.skit.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Base64;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Item {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String description;

    private LocalDate dateIssueCreated;

    private String location;

    @Enumerated(EnumType.STRING)
    private Type type;

    private byte[] image;

    private boolean handover;


    public Item(String name, Category category, String description, LocalDate dateIssueCreated, String location, Type type, byte[] image) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.dateIssueCreated = dateIssueCreated;
        this.location = location;
        this.type = type;
        this.image = image;
        this.handover = false;
    }
    public String getBase64Image() {
        return this.image != null ? Base64.getEncoder().encodeToString(this.image) : null;
    }

}