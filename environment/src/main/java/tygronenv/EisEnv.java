package tygronenv;

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
import tygronenv.configuration.Configuration;
import tygronenv.connection.ServerConnection;
import tygronenv.translators.HashMap2J;
import tygronenv.translators.J2ActionLog;
import tygronenv.translators.J2ActionMenu;
import tygronenv.translators.J2Answer;
import tygronenv.translators.J2Building;
import tygronenv.translators.J2Category;
import tygronenv.translators.J2ClientItemMap;
import tygronenv.translators.J2Function;
import tygronenv.translators.J2Indicator;
import tygronenv.translators.J2Land;
import tygronenv.translators.J2MultiPolygon;
import tygronenv.translators.J2PopupData;
import tygronenv.translators.J2Setting;
import tygronenv.translators.J2Stakeholder;
import tygronenv.translators.J2TimeState;
import tygronenv.translators.J2UpgradePair;
import tygronenv.translators.J2UpgradeType;
import tygronenv.translators.J2Zone;
import tygronenv.translators.MultiPolygon2J;
import tygronenv.translators.ParamEnum2J;
import tygronenv.translators.Stakeholder2J;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Implements the Tygron EIS adapter.
 *
 * @author W.Pasman
 */
@SuppressWarnings("serial")
public class EisEnv extends EIDefaultImpl {

	private ServerConnection serverConnection = null;

	/**
	 * Map with {@link TygronEntity}s. The key String is equal to
	 * {@link TygronEntity#getName}.
	 */
	private Map<String, TygronEntity> entities = new HashMap<String, TygronEntity>();

	/**
	 * General initialization: translators.
	 */
	public EisEnv() {
		installTranslators();
	}

	@Override
	protected LinkedList<Percept> getAllPerceptsFromEntity(final String e)
			throws PerceiveException, NoEnvironmentException {
		return getEntity(e).getPercepts();
	}

	/**
	 * Returns an entity with the requested name.
	 *
	 * @param e the name of the entity.
	 * @return the requested entity.
	 */
	private TygronEntity getEntity(final String e) {
		String entity = e.toUpperCase();
		if (!entities.containsKey(entity)) {
			throw new IllegalArgumentException("Unknown entity " + entity + ". Have:" + entities.keySet());
		}
		return entities.get(entity);
	}

	@Override
	protected boolean isSupportedByEnvironment(final Action action) {
		try {
			TygronEntity.getActionType(action.getName());
			TygronEntity.translateParameters(action, 0);
		} catch (TranslationException e) {
			return false;
		}

		return true;
	}

	@Override
	protected boolean isSupportedByType(final Action action, final String type) {
		return isSupportedByEnvironment(action); // ignore type.
	}

	@Override
	protected boolean isSupportedByEntity(final Action action, final String entity) {
		return isSupportedByEnvironment(action); // ignore entity.
	}

	@Override
	protected Percept performEntityAction(final String e, final Action action) throws ActException {
		try {
			getEntity(e).performAction(action);
		} catch (TranslationException | IllegalArgumentException e1) {
			throw new ActException("Failed to execute action " + action, e1);
		}
		return null;
	}

	@Override
	public void init(final Map<String, Parameter> parameters) throws ManagementException {
		super.init(parameters);
		Configuration config;
		try {
			config = new Configuration(parameters);
		} catch (TranslationException e) {
			throw new ManagementException("problem with the init settings", e);
		}
		serverConnection = new ServerConnection(config);
		setState(EnvironmentState.RUNNING);

		for (String st : config.getStakeholders()) {
			String stakeholder = st.toUpperCase();
			TygronEntity entity = new TygronEntity(this, stakeholder, serverConnection.getSession().getTeamSlot());
			// These will report themselves to EIS when they are ready.
			entities.put(stakeholder, entity);
		}

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
	 * @throws EntityException Exception when we can't find a the correct stakeholder.
	 */
	public void entityReady(final String entity) throws EntityException {
		addEntity(entity, "stakeholder");
	}

	/*************************
	 * SUPPORT FUNCTIONS.
	 ****************************/

	private Java2Parameter<?>[] j2p = new Java2Parameter<?>[]{new J2ClientItemMap(), new J2Stakeholder(),
	    new J2Setting(), new J2Function(), new J2Category(), new J2Building(), new J2TimeState(),
	    new J2ActionLog(), new J2ActionMenu(), new J2Zone(), new J2Land(), new J2MultiPolygon(),
	    new J2PopupData(), new J2Answer(), new J2Indicator(), new J2UpgradeType(), new J2UpgradePair()};

	private Parameter2Java<?>[] p2j = new Parameter2Java<?>[]{new ParamEnum2J(),
	  new HashMap2J(), new Stakeholder2J(), new MultiPolygon2J()};

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
