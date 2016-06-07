package contextvh;

import contextvh.connection.ContextServerConnection;
import contextvh.translators.J2ActionLog;
import contextvh.translators.ContextJ2Building;
import contextvh.translators.J2Indicator;
import contextvh.translators.ContextJ2Stakeholder;
import contextvh.translators.J2UpgradePair;
import contextvh.translators.J2UpgradeType;
import contextvh.translators.ContextJ2Zone;
import eis.EIDefaultImpl;
import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.eis2java.translation.Parameter2Java;
import eis.eis2java.translation.Translator;
import eis.exceptions.ActException;
import eis.exceptions.EntityException;
import eis.exceptions.ManagementException;
import eis.exceptions.NoEnvironmentException;
import eis.exceptions.PerceiveException;
import eis.iilang.Action;
import eis.iilang.EnvironmentState;
import eis.iilang.Parameter;
import eis.iilang.Percept;
import tygronenv.EntityListener;
import tygronenv.TygronEntity;
import tygronenv.configuration.Configuration;
import tygronenv.translators.HashMap2J;
import tygronenv.translators.J2ActionMenu;
import tygronenv.translators.J2Answer;
import tygronenv.translators.J2Category;
import tygronenv.translators.J2ClientItemMap;
import tygronenv.translators.J2Function;
import tygronenv.translators.J2Land;
import tygronenv.translators.J2MultiPolygon;
import tygronenv.translators.J2PopupData;
import tygronenv.translators.J2Setting;
import tygronenv.translators.J2TimeState;
import tygronenv.translators.MultiPolygon2J;
import tygronenv.translators.ParamEnum2J;
import tygronenv.translators.Stakeholder2J;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * @author Stefan Breetveld
 */
public class ContextEnv extends EIDefaultImpl implements EntityListener {

    private ContextServerConnection serverConnection = null;
    private Map<String, TygronEntity> entities = new HashMap<>();
    private Java2Parameter<?>[] j2p = new Java2Parameter<?>[] {new J2ClientItemMap(), new ContextJ2Stakeholder(),
            new J2Setting(), new J2Function(), new J2Category(), new ContextJ2Building(), new J2TimeState(),
            new J2ActionLog(), new J2ActionMenu(), new ContextJ2Zone(), new J2Land(), new J2MultiPolygon(),
            new J2PopupData(), new J2Answer(), new J2Indicator(), new J2UpgradeType(), new J2UpgradePair()};
    private Parameter2Java<?>[] p2j = new Parameter2Java<?>[]{new ParamEnum2J(),
            new HashMap2J(), new Stakeholder2J(), new MultiPolygon2J()};

    /**
     * General initialization: translators.
     */
    public ContextEnv() {
        installTranslators();
    }

    /**
     *
     * @param e
     * @return
     * @throws PerceiveException
     * @throws NoEnvironmentException
     */
    @Override
    protected LinkedList<Percept> getAllPerceptsFromEntity(final String e)
            throws PerceiveException, NoEnvironmentException {
        return getEntity(e).getPercepts();
    }

    /**
     * Retrieve the entity assigned to the given entity.
     *
     * @param entityName the name of the entity
     * @return the entity with name {@code entityName}
     * @throws IllegalArgumentException thrown when an entity with the given name doesn't exist
     */
    private TygronEntity getEntity(final String entityName) throws IllegalArgumentException {
        String entity = entityName.toUpperCase();
        if (!entities.containsKey(entity)) {
            throw new IllegalArgumentException("Unknown entity " + entity + ". Have:" + entities.keySet());
        }
        return entities.get(entity);
    }

    @Override
    protected boolean isSupportedByEnvironment(final Action action) {
        return true;
    }

    @Override
    protected boolean isSupportedByType(final Action action, final String type) {
        return isSupportedByEnvironment(action); // ignore type.
    }

    @Override
    protected boolean isSupportedByEntity(final Action action, final String entity) {
        return getEntity(entity).isSupported(action);
    }

    /**
     * Perform action for a given entity, possibly return a {@link Percept}.
     *
     * @param entity the entity to perform this {@link Action}.
     * @param action the {@link Action} to perform.
     * @return returns a {@link Percept} if the {@link Action} returns one.
     * @throws ActException thrown if the action failed to execute
     */
    @Override
    protected Percept performEntityAction(final String entity,
                                          final Action action) throws ActException {
        try {
            getEntity(entity).performAction(action);
        } catch (TranslationException | IllegalArgumentException e1) {
            throw new ActException("Failed to execute action " + action, e1);
        }
        return null;
    }

    /**
     * Initializes a server connection for the current configuration.
     *
     * @param parameters {@link Parameter}s gotten from the used main goal file.
     * @throws ManagementException
     */
    @Override
    public void init(final Map<String, Parameter> parameters) throws ManagementException {
        super.init(parameters);
        Configuration config;
        try {
            config = new Configuration(parameters);
        } catch (TranslationException e) {
            throw new ManagementException("problem with the init settings", e);
        }
        serverConnection = new ContextServerConnection(config);
        setState(EnvironmentState.RUNNING);

        for (String st : config.getStakeholders()) {
            String stakeholder = st.toUpperCase();
            TygronEntity entity = createNewEntity(this, stakeholder, serverConnection.getSession().getTeamSlot());
            // These will report themselves to EIS when they are ready.
            entities.put(stakeholder, entity);
        }

    }

    /**
     * Factory method. Creates new entity. The entity should announce itself to
     * GOAL, but only when it is ready to handle getPercepts.
     *
     * @param listener    Listener supplying updates
     * @param stakeholder name of the stakeholder
     * @param slot        slot connection ID that this entity connects to
     * @return new entity
     */
    public TygronEntity createNewEntity(final EntityListener listener, final String stakeholder, final Integer slot) {
        return new ContextEntity(listener, stakeholder, slot);
    }

    @Override
    public void kill() throws ManagementException {
        super.kill();
        for (TygronEntity entity : entities.values()) {
            entity.close();
        }
        entities = new HashMap<>();
        if (serverConnection != null) {
            serverConnection.disconnect();
            serverConnection = null;
        }
    }

    @Override
    public boolean isStateTransitionValid(final EnvironmentState oldState, final EnvironmentState newState) {
        return true;
    }

    /**
     * Entity with given name is ready for use. Report to EIS
     *
     * @param entity the identifier of the entity
     */
    @Override
    public void entityReady(final String entity) {
        try {
            addEntity(entity, "stakeholder");
        } catch (EntityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Installs the required EIS2Java translators.
     */
    private void installTranslators() {
        Translator translatorfactory = Translator.getInstance();

        for (Java2Parameter<?> translator : j2p) {
            translatorfactory.registerJava2ParameterTranslator(translator);
        }
        for (Parameter2Java<?> translator : p2j) {
            translatorfactory.registerParameter2JavaTranslator(translator);
        }
    }
}
