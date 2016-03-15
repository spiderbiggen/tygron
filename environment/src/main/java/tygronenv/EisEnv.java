package tygronenv;

import java.util.LinkedList;
import java.util.Map;

import eis.EIDefaultImpl;
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

	private ServerConnection serverConnection;
	private TygronEntity entity;

	/**
	 * General initialization: translators,
	 */
	public EisEnv() {
		installTranslators();
	}

	@Override
	protected LinkedList<Percept> getAllPerceptsFromEntity(String entity)
			throws PerceiveException, NoEnvironmentException {
		return null;
	}

	@Override
	protected boolean isSupportedByEnvironment(Action action) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isSupportedByType(Action action, String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isSupportedByEntity(Action action, String entity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected Percept performEntityAction(String entity, Action action) throws ActException {

		return null;
	}

	@Override
	public void init(Map<String, Parameter> parameters) throws ManagementException {
		super.init(parameters);
		Configuration config;
		try {
			config = new Configuration(parameters);
		} catch (Exception e) {
			throw new ManagementException("Problem with init parameters", e);
		}
		serverConnection = new ServerConnection(config);
		setState(EnvironmentState.RUNNING);

		entity = new TygronEntity(config.getStakeholder(), serverConnection.getSession().getTeamSlot());
		try {
			notifyNewEntity("entity");
		} catch (EntityException e) {
			throw new ManagementException("failed to register entity", e);
		}
	}

	@Override
	public void kill() throws ManagementException {
		super.kill();
		if (entity != null) {
			entity.close();
			entity = null;
		}
		serverConnection.disconnect();
	};

	@Override
	public boolean isStateTransitionValid(EnvironmentState oldState, EnvironmentState newState) {
		return true;
	}

	/************************* SUPPORT FUNCTIONS ****************************/

	Java2Parameter<?>[] j2p = new Java2Parameter<?>[] {};
	Parameter2Java<?>[] p2j = new Parameter2Java<?>[] { new ParamEnum2J(), new HashMap2J(), new Stakeholder2J() };

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
