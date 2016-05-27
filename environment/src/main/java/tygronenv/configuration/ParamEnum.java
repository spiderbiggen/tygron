package tygronenv.configuration;

/**
 * Translates a init parameter type into a ParamEnum
 *
 */
public enum ParamEnum {
	STAKEHOLDERS("stakeholders"), PROJECT("project"), SLOT("slot"), DOMAIN("domain");
	private String param;

	private ParamEnum(String name) {
		this.param = name;
	}

	public String getParam() {
		return param;
	}
}
