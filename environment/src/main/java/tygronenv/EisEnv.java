package tygronenv;

import java.util.LinkedList;
import java.util.Map;

import eis.EIDefaultImpl;
import eis.eis2java.translation.Java2Parameter;
import eis.eis2java.translation.Parameter2Java;
import eis.eis2java.translation.Translator;
import eis.exceptions.ActException;
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
import tygronenv.translators.J2Category;
import tygronenv.translators.J2ClientItemMap;
import tygronenv.translators.J2Setting;
import tygronenv.translators.J2Stakeholder;
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

	private static final String ENTITY = "entity";
	private ServerConnection serverConnection = null;
	private TygronEntity entity = null;

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
			serverConnection = new ServerConnection(config);
			setState(EnvironmentState.RUNNING);

			PerceptPipe pipe = new PerceptPipe() {
				@Override
				public void push(Percept percept) {
					System.out.println("percept:" + percept);
					try {
						notifyAgentsViaEntity(percept, ENTITY);
					} catch (Throwable e) {
						// catch any bug in the agent.
						System.out.println("Agent percept handler throws a bug into the environment!");
						e.printStackTrace();
					}
				}
			};

			// construct the entity first, as the constructor TygronEntity will
			// start writing to the pipe. Notice that we are ready to handle
			// getAllPercepts since that returns empty list anyway.
			addEntity(ENTITY);

			entity = new TygronEntity(config.getStakeholder(), serverConnection.getSession().getTeamSlot(), pipe);

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

	/************************* SUPPORT FUNCTIONS ****************************/

	Java2Parameter<?>[] j2p = new Java2Parameter<?>[] { new J2ClientItemMap(), new J2Stakeholder(), new J2Setting(),
			new J2BaseFunction(), new J2Category() };
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
