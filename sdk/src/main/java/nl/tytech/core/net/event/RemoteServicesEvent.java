/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.event;

import nl.tytech.core.event.Event;
import nl.tytech.core.net.serializable.User.AccessLevel;

/**
 *
 * @author Maxim Knepfle
 */
public class RemoteServicesEvent extends Event {

    public interface ServiceEventType extends EventTypeEnum {

        public AccessLevel getAccessLevel();

    }

    /**
     *
     */
    private static final long serialVersionUID = 4815797996076386179L;

    private String clientName = null;

    public RemoteServicesEvent(String clientName, EventTypeEnum type, Object[] arguments) {

        super(type, arguments);
        this.clientName = clientName;
    }

    public AccessLevel getAccessLevel() {
        return ((ServiceEventType) this.getType()).getAccessLevel();
    }

    public String getClientName() {
        return clientName;
    }

}
