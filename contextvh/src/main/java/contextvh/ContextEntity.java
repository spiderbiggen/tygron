package contextvh;

import nl.tytech.core.client.net.TSlotConnection;
import tygronenv.EntityListener;

/**
 * Created on 4-6-2016.
 *
 * @author Stefan Breetveld.
 */
public class ContextEntity extends tygronenv.TygronEntityImpl {

    /**
     * Create new Tygron entity. It will report to env when the entity is ready
     * to run. This happens when initial percepts have been prepared and the
     * name matches one of the actual stakeholder names
     *
     * @param env                 the environment to report back to.
     * @param intendedStakeholder the intended stakeholder name. If null, any name is ok.
     * @param slotID
     */
    public ContextEntity(EntityListener env, String intendedStakeholder, Integer slotID) {
        super(env, intendedStakeholder, slotID);
    }

    @Override
    public tygronenv.EntityEventHandler createEntityEventhandler(TSlotConnection slotConnection) {
        return new ContextEntityEventHandler(this, slotConnection.getConnectionID(), this);
    }
}
