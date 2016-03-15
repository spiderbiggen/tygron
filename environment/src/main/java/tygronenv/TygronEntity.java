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

/**
 * the 'participant' - a single stakeholder connection. Handles events coming in
 * for this stakeholder
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

	/**
	 * 
	 * @param stakeholder
	 * @param slotID
	 *            the slot ID of the team.
	 */
	public TygronEntity(Stakeholder.Type stakeholdertype, Integer slotID) {
		getSlotConnection(slotID);
		Stakeholder stakeholder = getStakeholder(stakeholdertype);
		if (stakeholder == null) {
			throw new IllegalArgumentException("Stakeholder of type " + stakeholdertype + " is not available");
		}
		slotConnection.fireServerEvent(true, ParticipantEventType.STAKEHOLDER_SELECT, stakeholder.getID(),
				joinedConfirm.client.getClientToken());
	}

	/**
	 * Select given stakeholder.
	 * 
	 * @param intendedStakeHolder
	 *            the stakeholder to use. Or null if any stakeholder is ok.
	 * @return stakeholder, or null if requested type is not available.
	 */
	private Stakeholder getStakeholder(Stakeholder.Type intendedStakeHolder) {
		try {
			Thread.sleep(1000);// HACK wait till cache updated.
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ItemMap<Stakeholder> stakeholders = EventManager.getItemMap(MapLink.STAKEHOLDERS);
		if (intendedStakeHolder == null) {
			// pick the first
			return stakeholders.toList(0).get(0);
		}

		for (Stakeholder holder : stakeholders) {
			if (holder.getType().equals(intendedStakeHolder)) {
				return holder;
			}
		}
		return null;
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

		eventHandler = new EntityEventHandler();
	}

	/**
	 * Close server connection.
	 */
	public void close() {
		eventHandler.stop();
		eventHandler = null;
		slotConnection.disconnect(false);
		slotConnection = null;
	}
}
