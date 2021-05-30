package app.model;

import java.util.ArrayList;
import java.util.List;

public class ConfigureDBSource {
    private MetadataDB metadata;
    private String table;
    private List<MappingSelect> mappingSelects;

    public ConfigureDBSource() {
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

    public List<MappingSelect> getMappingSelects() {
        return mappingSelects;
    }

    public void setMappingSelects(List<MappingSelect> mappingSelects) {
        this.mappingSelects = mappingSelects;
    }

    public List<String> getOriginalExternals() {
        List<String> externals = new ArrayList<>();
        for (MappingSelect mappingSelect : mappingSelects) {
            externals.add(mappingSelect.getExternal());
        }
        return externals;
    }

    public List<String> getSelectedExternals() {
        List<String> externals = new ArrayList<>();
        for (MappingSelect mappingSelect : this.mappingSelects) {
            if (mappingSelect.isSelected()) externals.add(mappingSelect.getExternal());
        }
        return externals;
    }

    public List<String> getSelectedOutputs() {
        List<String> externals = new ArrayList<>();
        for (MappingSelect mappingSelect : this.mappingSelects) {
            if (mappingSelect.isSelected()) externals.add(mappingSelect.getOutput());
        }
        return externals;
    }

    public ConfigData getConfigData() {
        return ConfigData.getInstanceFromMappingSelectList(mappingSelects);
    }
}
