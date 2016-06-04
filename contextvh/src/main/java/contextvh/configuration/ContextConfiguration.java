package contextvh.configuration;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Translator;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * The environment configuration as specified in the init param.
 */
public class ContextConfiguration {
    private final Translator translator = Translator.getInstance();
    private Set<String> stakeholders = new HashSet<>();
    private String project = null;
    private Integer slot = null;
    private String domain = null;

    /**
     * Construct configuration directly from set of Parameters as in the MAS
     * init.
     *
     * @param parameters the Map<String,Parameter> coming from init
     * @throws TranslationException {@link TranslationException}
     */
    public ContextConfiguration(final Map<String, Parameter> parameters) throws TranslationException {
        parseParameters(parameters);
        checkSanity();
    }

    /**
     * Parse the parameters and put them in our fields.
     *
     * @param parameters the init parameters
     * @throws TranslationException {@link TranslationException}
     */
    private void parseParameters(final Map<String, Parameter> parameters) throws TranslationException {
        for (Entry<String, Parameter> entry : parameters.entrySet()) {
            ContextParamEnum param = translator.translate2Java(new Identifier(entry.getKey()), ContextParamEnum.class);
            switch (param) {
                case STAKEHOLDERS:
                    System.out.println(entry.getValue());
                    setStakeholders(paramlist2Set(entry.getValue()));
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

    /**
     * Translate param list to set. We cant use a standard translator: #3919.
     *
     * @param param The parameter list to convert
     * @return Set of Parameters as Strings
     * @throws TranslationException thrown when {@code param} is not an
     *         instanceof {@link ParameterList}
     */
    private Set<String> paramlist2Set(final Parameter param) throws TranslationException {
        if (!(param instanceof ParameterList)) {
            throw new TranslationException("Expected ParameterList but got " + param);
        }
        Set<String> set = new HashSet<String>();
        for (Parameter p : ((ParameterList) param)) {
            set.add(translator.translate2Java(p, String.class));
        }
        return set;
    }

    /**
     *
     */
    private void checkSanity() {
        if (project == null) {
            throw new IllegalStateException("Invalid configuration: map is mandatory");
        }
    }

    /**
     * @return Set of strings. Each string the name (not type) of a requested
     * stakeholders.
     */
    public Set<String> getStakeholders() {
        return stakeholders;
    }

    /**
     * Set the new stakeholders.
     *
     * @param newstakeholders set of strings, each string being the name (not the type) of a
     *                        stakeholder
     */
    public void setStakeholders(final Set<String> newstakeholders) {
        if (newstakeholders == null) {
            throw new IllegalStateException("stakeholders must be provided");
        }
        this.stakeholders = newstakeholders;
    }

    /**
     * @return the project name to use
     */
    public String getProject() {
        return project;
    }

    /**
     * Set the project name.
     *
     * @param projectName name of the project
     */
    public void setProject(final String projectName) {
        if (projectName == null) {
            throw new IllegalStateException("map must be provided");
        }
        this.project = projectName;
    }

    /**
     * @return the Slot number to use.
     */
    public Integer getSlot() {
        return slot;
    }

    /**
     * Set the slot number.
     *
     * @param slotNum number of the slot.
     */
    public void setSlot(final int slotNum) {
        if (slotNum < 0) {
            throw new IllegalArgumentException("slot must be > 0 or not provided at all.");
        }
        this.slot = slotNum;
    }

    /**
     * @return The domain name to connect to.
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets the domain name.
     * @param domainName name of the domain
     */
    public void setDomain(final String domainName) {
        this.domain = domainName;
    }
}
