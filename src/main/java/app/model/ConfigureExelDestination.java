package app.model;

import java.util.ArrayList;
import java.util.List;

public class ConfigureExelDestination {
    private String path;
    private Boolean hasLabel;
    private String table;
    private List<MappingSelect> mappingSelects;
    private List<Mapping> mappings;
    private List<String> input;

    public ConfigureExelDestination() {
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Boolean getHasLabel() {
        return this.hasLabel;
    }

    public void setHasLabel(Boolean hasLabel) {
        this.hasLabel = hasLabel;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<Mapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<Mapping> mappings) {
        this.mappings = mappings;
    }

    public List<String> getInput() {
        return input;
    }

    public void setInput(List<String> input) {
        this.input = input;
    }

    public int getSelectedInputCount() {
        int count = 0;
        for (Mapping mapping : mappings) {
            if (!mapping.getInput().equals("ignore this")) count++;
        }
        return count;
    }


    public List<MappingSelect> getMappingSelects() {
        return mappingSelects;
    }

    public void setMappingSelects(List<MappingSelect> mappingSelects) {
        this.mappingSelects = mappingSelects;
    }

    public List<String> getSelectedInputs() {
        List<String> selected = new ArrayList<>();
        if (mappings != null) {
            for (Mapping mapping : mappings) {
                if (!mapping.getInput().equals("ignore this")) selected.add(mapping.getInput());
            }
        }
        return selected;
    }

    public ConfigData getConfigData() {
        return ConfigData.getInstanceFromMappingList(mappings);
    }


}
