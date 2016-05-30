package tygronenv;

import java.util.LinkedList;
import java.util.List;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Translator;
import eis.exceptions.EntityException;
import eis.iilang.Action;
import eis.iilang.Parameter;
import eis.iilang.Percept;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.client.net.ServicesManager;
import nl.tytech.core.client.net.SlotConnection;
import nl.tytech.core.net.Network.AppType;
import nl.tytech.core.net.event.IOServiceEventType;
import nl.tytech.core.net.serializable.JoinReply;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.data.engine.event.LogicEventType;
import nl.tytech.data.engine.event.ParticipantEventType;
import nl.tytech.data.engine.item.Stakeholder;
import tygronenv.actions.ActionContainer;
import tygronenv.actions.CustomAction;

/**
 * the 'participant' - a single stakeholder connection. Handles events coming in
 * for this stakeholder.
 * 
 * A TygronEntity becomes an EIS entity only when the initial percepts have come
 * in, and there is a stakeholder with the {@link #intendedStakeholderName}.
 * 
 * @author W.Pasman
 *
 */
public class TygronEntity {

	private SlotConnection slotConnection = null;
	/**
	 * the confirmation from the server that we joined.
	 */
	private JoinReply joinedConfirm;
	private EntityEventHandler eventHandler;
	private Stakeholder stakeholder;
	private EisEnv environment;
	/**
	 * The name that this entity should represent. At construction time we do
	 * not yet know if such a stakeholder actually exists in the project.
	 */
	private String intendedStakeholderName;

	private final static Translator translator = Translator.getInstance();
	
	private ActionContainer customActions = new ActionContainer();

	/**
	 * Create new Tygron entity. It will report to env when the entity is ready
	 * to run. This happens when initial percepts have been prepared and the
	 * name matches one of the actual stakeholder names
	 * 
	 * @param env
	 *            the environment to report back to.
	 * @param intendedStakeholder
	 *            the intended stakeholder name. If null, any name is ok.
	 * @param slotID
	 *            the slot ID of the team.
	 */
	public TygronEntity(EisEnv env, String intendedStakeholder, Integer slotID) {
		if (env == null) {
			throw new NullPointerException("env=null");
		}
		this.environment = env;
		this.intendedStakeholderName = intendedStakeholder;
		eventHandler = new EntityEventHandler(this);
		getSlotConnection(slotID);
	}

	/**
	 * called when the entity has received initial percepts.
	 * 
	 * @param entity
	 * @throws EntityException
	 */
	public void notifyReady(String entity) throws EntityException {
		slotConnection.fireServerEvent(true, ParticipantEventType.STAKEHOLDER_SELECT, stakeholder.getID(),
				joinedConfirm.client.getClientToken());
		slotConnection.fireServerEvent(true, LogicEventType.SETTINGS_ALLOW_INTERACTION, true);

		environment.entityReady(stakeholder.getName().toUpperCase());
	}

	/**
	 * Connect with the stakeholder that has the
	 * {@link #intendedStakeholderName}.
	 * 
	 * @throws EntityException
	 *             when this entity can not be connected, eg when the requested
	 *             stakeholder name does not exist.
	 * 
	 */
	public void connectStakeholder() throws EntityException {

		ItemMap<Stakeholder> stakeholders = EventManager.getItemMap(MapLink.STAKEHOLDERS);

		// safety check
		if (stakeholders.size() == 0) {
			throw new EntityException("The project does not contain any stakeholders");
		}

		if (intendedStakeholderName == null) {
			// pick the first
			stakeholder = stakeholders.toList(0).get(0);
		}

		for (Stakeholder holder : stakeholders) {
			if (intendedStakeholderName.equalsIgnoreCase(holder.getName())) {
				stakeholder = holder;
				return;
			}
		}
		String names = stakeholders.get(0).getName();
		for (Stakeholder holder : stakeholders) {
			names = names + "," + holder.getName();
		}
		throw new EntityException(
				"Stakeholder with name " + intendedStakeholderName + " is not available. Available are:" + names);
	}
	
	/**
	 * Get the current stakeholder.
	 * @return The {@link Stakeholder} you are in the game.
	 */
	public Stakeholder getStakeholder() {
		return stakeholder;
	}
	
	/**
	 * Join the existing session on the given slot.
	 * 
	 * @param slotID
	 *            the slot where our team is on.
	 */
	private void getSlotConnection(Integer slotID) {
		joinedConfirm = ServicesManager.fireServiceEvent(IOServiceEventType.JOIN_SESSION, slotID, AppType.PARTICIPANT);
		if (joinedConfirm == null) {
			throw new IllegalStateException("Failed to join session " + slotID + " as participant");
		}

		slotConnection = new SlotConnection();
		slotConnection.initSettings(AppType.PARTICIPANT, SettingsManager.getServerIP(), slotID,
				joinedConfirm.serverToken, joinedConfirm.client.getClientToken());

		if (!slotConnection.connect()) {
			throw new IllegalStateException("Failed to connect slotConnection");
		}

	}

	/**
	 * Close server connection.
	 */
	public void close() {
		eventHandler.stop();
		eventHandler = null;
		if (slotConnection != null) {
			slotConnection.disconnect(false);
			slotConnection = null;
		}
	}

	public LinkedList<Percept> getPercepts() {
		LinkedList<Percept> allPercepts = new LinkedList<Percept>();
		for (List<Percept> percepts : eventHandler.getPercepts().values()) {
			allPercepts.addAll(percepts);
		}
		return allPercepts;
	}

	/**
	 * Perform given action
	 * 
	 * @param action
	 *            action of the form action(p1,...). Action must be a
	 *            {@link ParticipantEventType}. The arguments are those as
	 *            specified in {@link ParticipantEventType}, except that the
	 *            obligatory first argument "Stakeholder ID" is automatically
	 *            filled in so you can leave that out and start with parameter
	 *            2.
	 * @throws TranslationException
	 *             if a parameter can not be translated.
	 */
	public void performAction(Action action) throws TranslationException {
		/**
		 * Action is of the form 'BUILDING_PLAN_CONSTRUCTION'(p1,p2,p3...).
		 * 
		 * We must call something like this: <code>
		Integer newBuildingID = slotConnection.fireServerEvent(true, ParticipantEventType.BUILDING_PLAN_CONSTRUCTION,
				stakeholderID, functionID, floors, roadMultiPolygon);
				</code>
		 */
		String actionName = action.getName().toLowerCase();
		CustomAction customAction = customActions.get(actionName);
		
		if (customAction != null) {
			customAction.call(action.getParameters());
		} else {
			ParticipantEventType type = getActionType(actionName);
			if (type == null) {
				throw new TranslationException("unknown action " + actionName);
			}

			Object[] arguments = translateParameters(action, stakeholder.getID());

			// call. We ignore the return value.
			slotConnection.fireServerEvent(true, type, arguments);
		}
	}

	/**
	 * Translate parameters into object[] for tygron call.
	 * 
	 * @param action
	 *            the action to translate.
	 * @return the translated object[], ready to use for the call to
	 *         {@link SlotConnection#fireServerEvent(boolean, nl.tytech.core.event.Event.EventTypeEnum, Object...)}
	 * @throws TranslationException
	 *             if there is something wrong with action type or parameters.
	 */
	public static Object[] translateParameters(Action action, int stakeholderID) throws TranslationException {
		ParticipantEventType type = getActionType(action.getName());

		LinkedList<Parameter> parameters = action.getParameters();

		// convert the arguments to the required type
		List<Class<?>> argtypes = type.getClasses();
		if (argtypes.size() - 1 != parameters.size()) {
			throw new TranslationException("Action " + type + " takes " + (argtypes.size() - 1)
					+ " parameters but received " + parameters.size());
		}

		Object[] arguments = new Object[argtypes.size()];

		// the first arg is always stakeholder ID.
		arguments[0] = stakeholderID;
		// the rest needs conversion
		for (int n = 0; n < parameters.size(); n++) {
			arguments[n + 1] = translator.translate2Java(parameters.get(n), argtypes.get(n + 1));
		}
		return arguments;
	}

	/**
	 * Find the action associated with given name.
	 * 
	 * @param actionName
	 *            the action as string
	 * @return the {@link ParticipantEventType} action.
	 */
	public static ParticipantEventType getActionType(String actionName) throws TranslationException {
		try {
			return ParticipantEventType.valueOf(actionName.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new TranslationException("unknown action " + actionName);
		}
	}

}
