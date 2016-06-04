package contextvh.configuration;

/**
 * Translates a init parameter type into a ContextParamEnum
 */
public enum ContextParamEnum {
    STAKEHOLDERS("stakeholders"), PROJECT("project"), SLOT("slot"), DOMAIN("domain");
    private String param;

    private ContextParamEnum(String name) {
        this.param = name;
    }

    public String getParam() {
        return param;
    }
}
