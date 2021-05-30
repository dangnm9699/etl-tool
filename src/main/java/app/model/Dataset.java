package app.model;

import java.util.List;

public class Dataset {
    private List<List<Object>> data;

    public Dataset() {
    }

    public List<List<Object>> getData() {
        return data;
    }

    public void setData(List<List<Object>> data) {
        this.data = data;
    }
}
