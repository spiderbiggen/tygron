package tygronenv;

import java.util.Map;
import java.util.Map.Entry;

import eis.eis2java.exception.NoTranslatorException;
import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Translator;
import eis.exceptions.ManagementException;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;

/**
 * The configuration as specified in the MAS init param
 *
 */
public class Configuration {
	private Integer stakeholder = null;
	private String map = null;
	private Integer slot = null;

	/**
	 * Construct configuration directly from set of Parameters as in the MAS
	 * init.
	 * 
	 * @param parameters
	 *            the Map<String,Parameter> coming from init
	 * @throws NoTranslatorException
	 * @throws TranslationException
	 */
	public Configuration(Map<String, Parameter> parameters) throws NoTranslatorException, TranslationException {
		Translator translator = Translator.getInstance();
		for (Entry<String, Parameter> entry : parameters.entrySet()) {
			ParamEnum param = translator.translate2Java(new Identifier(entry.getKey()), ParamEnum.class);
			switch (param) {
			case STAKEHOLDER:
				setStakeholder(translator.translate2Java(entry.getValue(), Integer.class));
				break;
			case MAP:
				setMap(translator.translate2Java(entry.getValue(), String.class));
				break;
			case SLOT:
				setSlot(translator.translate2Java(entry.getValue(), Integer.class));
				break;
			default:
				break;
			}
		}
		checkSanity();
	}

	private void checkSanity() {
		if (map == null)
			throw new IllegalStateException("map is not provided");
		if (stakeholder == null)
			throw new IllegalStateException("stakeholder is not provided");

	}

	/**
	 * @param parameters
	 *            the EIS init params
	 * @return {@link Configuration}
	 * @throws ManagementException
	 */
	public Configuration makeConfiguration(Map<String, Parameter> parameters) throws ManagementException {
		Configuration configuration;
		try {
			ParameterList parameterMap = new ParameterList();
			for (Entry<String, Parameter> entry : parameters.entrySet()) {
				parameterMap.add(new ParameterList(new Identifier(entry.getKey()), entry.getValue()));
			}
			configuration = Translator.getInstance().translate2Java(parameterMap, Configuration.class);
		} catch (TranslationException e) {
			throw new ManagementException("Invalid parameters", e);
		}
		return configuration;
	}

	public void setStakeholder(int stakeholderParametersList) {
		stakeholder = stakeholderParametersList;
	}

	public int getStakeholder() {
		return stakeholder;
	}

	public void setMap(String map) {
		this.map = map;
	}

	public String getMap() {
		return map;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public int getSlot() {
		return slot;
	}

}
