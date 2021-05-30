package app.model;

public class Mapping {
    private String external;
    private String output;

    public Mapping(String external, String output) {
        this.external = external;
        this.output = output;
    }

    public String getExternal() {
        return external;
    }

    public void setExternal(String external) {
        this.external = external;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
