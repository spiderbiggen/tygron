package tygronenv;

import java.util.LinkedList;
import java.util.Map;

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
import tygronenv.translators.J2BaseFunction;
import tygronenv.translators.J2Building;
import tygronenv.translators.J2Category;
import tygronenv.translators.J2ClientItemMap;
import tygronenv.translators.J2Indicator;
import tygronenv.translators.J2Setting;
import tygronenv.translators.J2Stakeholder;
import tygronenv.translators.J2TimeState;
import tygronenv.translators.MultiPolygon2J;
import tygronenv.translators.ParamEnum2J;
import tygronenv.translators.Stakeholder2J;

/**
 * Implements the Tygron EIS adapter
 * 
 * @author W.Pasman
 *
 */
@SuppressWarnings("serial")
public class EisEnv extends EIDefaultImpl {

	private ServerConnection serverConnection = null;
	private TygronEntity entity = null;

	/**
	 * General initialization: translators,
	 */
	public EisEnv() {
		installTranslators();
	}

	@Override
	protected LinkedList<Percept> getAllPerceptsFromEntity(String e) throws PerceiveException, NoEnvironmentException {
		return entity.getPercepts();
	}

	@Override
	protected boolean isSupportedByEnvironment(Action action) {
		try {
			TygronEntity.getActionType(action.getName());
			TygronEntity.translateParameters(action, 0);
		} catch (TranslationException e) {
			return false;
		}

		return true;
	}

	@Override
	protected boolean isSupportedByType(Action action, String type) {
		return isSupportedByEnvironment(action); // ignore type.
	}

	@Override
	protected boolean isSupportedByEntity(Action action, String entity) {
		return isSupportedByEnvironment(action); // ignore entity.
	}

	@Override
	protected Percept performEntityAction(String e, Action action) throws ActException {
		try {
			entity.performAction(action);
		} catch (TranslationException | IllegalArgumentException e1) {
			throw new ActException("Failed to execute action " + action, e1);
		}
		return null;
	}

	@Override
	public void init(Map<String, Parameter> parameters) throws ManagementException {
		super.init(parameters);
		Configuration config;
		try {
			config = new Configuration(parameters);
			serverConnection = new ServerConnection(config);
			setState(EnvironmentState.RUNNING);

			entity = new TygronEntity(this, config.getStakeholder(), serverConnection.getSession().getTeamSlot());
			// entity will register itself with EIS

		} catch (Exception e) {
			throw new ManagementException("Problem with initialization of environment", e);
		}
	}

	@Override
	public void kill() throws ManagementException {
		super.kill();
		if (entity != null) {
			entity.close();
			entity = null;
		}
		if (serverConnection != null) {
			serverConnection.disconnect();
			serverConnection = null;
		}
	};

	// FIXME reset #3844

	@Override
	public boolean isStateTransitionValid(EnvironmentState oldState, EnvironmentState newState) {
		return true;
	}

	/**
	 * Entity with given name is ready for use. Report to EIS
	 * 
	 * @param entity
	 *            the identifier of the entity
	 * @throws EntityException
	 */
	public void entityReady(String entity) throws EntityException {
		addEntity(entity, "stakeholder");
	}

	/************************* SUPPORT FUNCTIONS ****************************/

	Java2Parameter<?>[] j2p = new Java2Parameter<?>[] { new J2ClientItemMap(), new J2Stakeholder(), new J2Setting(),
			new J2BaseFunction(), new J2Category(), new J2Building(), new J2TimeState(),
			new J2Indicator(), new J2Zone()};
	Parameter2Java<?>[] p2j = new Parameter2Java<?>[] { new ParamEnum2J(), new HashMap2J(), new Stakeholder2J(),
			new MultiPolygon2J() };

	/**
	 * Installs the required EIS2Java translators
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
