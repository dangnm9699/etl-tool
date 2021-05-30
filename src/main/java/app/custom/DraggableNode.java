package app.custom;

import app.model.ConfigData;

public interface DraggableNode {
    void setupConfigHandler();

    ConfigData getConfigData();
}
