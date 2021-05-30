package app.model;

public class SortBy {
    private final String column;
    private final SortOrder order;

    public SortBy(String column, SortOrder order) {
        this.column = column;
        this.order = order;
    }

    public String getColumn() {
        return column;
    }

    public SortOrder getOrder() {
        return order;
    }
}
