package tygronenv;

/**
 * Translates a init parameter type into a ParamEnum
 *
 */
public enum ParamEnum {
	STAKEHOLDER("stakeholder"), MAP("map"), SLOT("slot");

	private String param;

	private ParamEnum(String name) {
		this.param = name;
	}

	public String getParam() {
		return param;
	}
}
