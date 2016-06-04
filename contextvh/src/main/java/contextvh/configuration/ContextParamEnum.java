package contextvh.configuration;

/**
 * Translates a init parameter type into a ContextParamEnum.
 */
public enum ContextParamEnum {
    STAKEHOLDERS("stakeholders"), PROJECT("project"), SLOT("slot"), DOMAIN("domain");
    private String param;

    /**
     * Set the string to identify this parameter from the GOAL file.
     * @param name the identifying String
     */
    ContextParamEnum(final String name) {
        this.param = name;
    }

    /**
     * @return the String that identifies this parameter.
     */
    public String getParam() {
        return param;
    }
}
