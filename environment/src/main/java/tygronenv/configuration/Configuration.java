package tygronenv.configuration;

import java.util.Map;
import java.util.Map.Entry;

import eis.eis2java.exception.NoTranslatorException;
import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Translator;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.data.engine.item.Stakeholder.Type;
import nl.tytech.data.engine.item.Strategy;

/**
 * The environment configuration as specified in the init param
 *
 */
public class Configuration {
	private Type stakeholder = null;
	private String project = null;
	private Integer slot = null;
	private String domain = null;

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
				setStakeholder(translator.translate2Java(entry.getValue(), Stakeholder.Type.class));
				break;
			case PROJECT:
				setProject(translator.translate2Java(entry.getValue(), String.class));
				break;
			case SLOT:
				setSlot(translator.translate2Java(entry.getValue(), Integer.class));
				break;
            case DOMAIN:
                setDomain(translator.translate2Java(entry.getValue(), String.class));
                break;
			default:
				break;
			}
		}
	}

	private void checkSanity() {
		if (project == null) {
			throw new IllegalStateException("Invalid configuration: map is mandatory");
		}
	}

	public void setStakeholder(Stakeholder.Type holder) {
		if (holder == null)
			throw new IllegalStateException("stakeholder must be provided");
		stakeholder = holder;
	}

	public Stakeholder.Type getStakeholder() {
		return stakeholder;
	}

	/**
	 * Set the project name
	 * 
	 * @param project
	 *            name of the project
	 */
	public void setProject(String project) {
		if (project == null)
			throw new IllegalStateException("map must be provided");
		this.project = project;
	}

	/**
	 * 
	 * @return the project name to use
	 */
	public String getProject() {
		return project;
	}

	public void setSlot(int slot) {
		if (slot < 0)
			throw new IllegalArgumentException("slot must be >0 or not provided at all.");
		this.slot = slot;
	}

	public Integer getSlot() {
		return slot;
	}

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }
}
