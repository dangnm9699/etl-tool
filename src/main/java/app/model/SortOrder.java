package app.model;

public enum SortOrder {
    Ascending("ASC"),
    Descending("DES");

    private final String name;

    SortOrder(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
