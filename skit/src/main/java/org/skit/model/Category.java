package org.skit.model;

public enum Category {
    MOBILE_PHONES("Мобилни телефони"),
    LAPTOPS("Лаптопи"),
    TABLETS("Таблети"),
    CAMERAS("Фотоапарати"),
    HEADPHONES("Слушалки"),
    SMARTWATCHES("Смарт часовници"),
    CHARGERS("Полначи"),
    USB_DRIVES("USB уреди"),
    KEYS("Клучеви"),
    WALLETS("Паричници"),
    GLASSES("Очила"),
    WATCHES("Рачни часовници"),
    JEWELRY("Накит"),
    ACCESSORIES("Додатоци"),
    CLOTHING("Облека"),
    BAGS("Торби"),
    DOCUMENTS("Документи"),
    BOOK("Книги"),
    OTHER("Друго");

    private final String categoryName;

    Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
