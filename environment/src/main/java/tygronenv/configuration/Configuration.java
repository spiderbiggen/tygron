package tygronenv.configuration;

import java.util.Map;
import java.util.Map.Entry;

import eis.eis2java.exception.NoTranslatorException;
import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Translator;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import nl.tytech.data.engine.item.Stakeholder;

/**
 * The environment configuration as specified in the init param
 *
 */
public class Configuration {
	private Stakeholder stakeholder = null;
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
		parseParameters(parameters);
		checkSanity();
	}

	/**
	 * Parse the parameters and put them in our fields
	 * 
	 * @param parameters
	 *            the init parameters
	 * @throws TranslationException
	 * @throws NoTranslatorException
	 */
	private void parseParameters(Map<String, Parameter> parameters) throws TranslationException, NoTranslatorException {
		Translator translator = Translator.getInstance();
		for (Entry<String, Parameter> entry : parameters.entrySet()) {
			ParamEnum param = translator.translate2Java(new Identifier(entry.getKey()), ParamEnum.class);
			switch (param) {
			case STAKEHOLDER:
				setStakeholder(translator.translate2Java(entry.getValue(), Stakeholder.class));
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
	}

	private void checkSanity() {
		if (map == null) {
			throw new IllegalStateException("Invalid configuration: map is mandatory");
		}
	}

	public void setStakeholder(Stakeholder holder) {
		if (holder == null)
			throw new IllegalStateException("stakeholder must be provided");
		stakeholder = holder;
	}

	public Stakeholder getStakeholder() {
		return stakeholder;
	}

	public void setMap(String map) {
		if (map == null)
			throw new IllegalStateException("map must be provided");
		this.map = map;
	}

	public String getMap() {
		return map;
	}

	public void setSlot(int slot) {
		if (slot < 0)
			throw new IllegalArgumentException("slot must be >0 or not provided at all.");
		this.slot = slot;
	}

	public Integer getSlot() {
		return slot;
	}

}
