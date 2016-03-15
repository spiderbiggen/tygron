package tygronenv;

import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.client.net.ServicesManager;
import nl.tytech.core.client.net.SlotConnection;
import nl.tytech.core.net.Network.AppType;
import nl.tytech.core.net.event.IOServiceEventType;
import nl.tytech.core.net.serializable.JoinReply;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.data.engine.event.ParticipantEventType;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.util.logger.TLogger;

/**
 * the 'participant' , but close to the EIS level.
 * 
 * @author W.Pasman
 *
 */
public class TygronEntity {

	private SlotConnection slotConnection = null;
	private JoinReply joinedConfirm; // the confirmation from the server that we joined.

	/**
	 * 
	 * @param stakeholder
	 * @param slotID
	 *            the slot ID of the team.
	 */
	public TygronEntity(Stakeholder stakeholder, Integer slotID) {
		getSlotConnection(slotID);
		selectStakeholder(stakeholder);
	}

	private void selectStakeholder(Stakeholder intendedStakeHolder) {

		int stakeholderID = 0;
		ItemMap<Stakeholder> stakeholders = EventManager.getItemMap(MapLink.STAKEHOLDERS);
		for (Stakeholder stakeholder : stakeholders) {
			stakeholderID = stakeholder.getID();
			TLogger.info("Selecting first stakeholder: " + stakeholder.getName() + " to play!");
			break;
		}
		slotConnection.fireServerEvent(true, ParticipantEventType.STAKEHOLDER_SELECT, stakeholderID,
				joinedConfirm.client.getClientToken());
	}

	private void getSlotConnection(Integer slotID) {
		joinedConfirm = ServicesManager.fireServiceEvent(IOServiceEventType.JOIN_SESSION, slotID, AppType.PARTICIPANT);
		if (joinedConfirm == null) {
			throw new IllegalStateException("Failed to join session " + slotID + " as participant");
		}

		slotConnection = new SlotConnection();
		slotConnection.initSettings(AppType.PARTICIPANT, SettingsManager.getServerIP(), slotID, joinedConfirm.serverToken,
				joinedConfirm.client.getClientToken());

		if (!slotConnection.connect()) {
			throw new IllegalStateException("Failed to connect slotConnection");
		}

		// FIXME add event handler to the slot
		// add event handler to receive updates on
		// eventHandler = new ExampleEventHandler();
	}

	/**
	 * Close server connection.
	 */
	public void close() {
		slotConnection.disconnect(false);
		slotConnection = null;
	}
}
