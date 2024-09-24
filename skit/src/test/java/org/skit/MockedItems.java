package org.skit;

import org.skit.model.Category;
import org.skit.model.Item;
import org.skit.model.Type;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class MockedItems {

    static Item lostPhone = new Item("Lost Phone",
            Category.MOBILE_PHONES,
            "A lost phone.",
            LocalDate.now(),
            "Location A",
            Type.LOST,
            new byte[]{}
    );

    static Item foundPhone = new Item("Found Phone",
            Category.MOBILE_PHONES,
            "A found phone.",
            LocalDate.now(),
            "Location A",
            Type.FOUND,
            new byte[]{}
    );

    static Item lostWallet = new Item("Lost Wallet",
            Category.ACCESSORIES,
            "A lost wallet.",
            LocalDate.now(),
            "Location B",
            Type.LOST,
            new byte[]{}
    );

    static Item foundWallet = new Item("Found Wallet",
            Category.ACCESSORIES,
            "A found wallet.",
            LocalDate.now(),
            "Location B",
            Type.FOUND,
            new byte[]{}
    );

    static List<Item> itemsByCategory() {
        return Arrays.stream(Category.values())
                .flatMap(category -> Stream.of(
                        new Item(
                                "Lost " + category.getCategoryName(),
                                category,
                                "A lost " + category.getCategoryName().toLowerCase() + ".",
                                LocalDate.now(),
                                "Test Location",
                                Type.LOST,
                                new byte[]{}),
                        new Item(
                                "Found " + category.getCategoryName(),
                                category,
                                "A found " + category.getCategoryName().toLowerCase() + ".",
                                LocalDate.now(),
                                "Test Location",
                                Type.FOUND,
                                new byte[]{})
                ))
                .collect(Collectors.toList());
    }
}

