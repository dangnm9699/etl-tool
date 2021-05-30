package app.model;

import java.util.ArrayList;
import java.util.List;

public class ConfigureDBDestination {
    private MetadataDB metadata;
    private String table;
    private List<Mapping> mappings;
    private List<String> input;

    public ConfigureDBDestination() {
    }

    public MetadataDB getMetadata() {
        return metadata;
    }

    public void setMetadata(MetadataDB metadata) {
        this.metadata = metadata;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
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
