/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.client.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import nl.tytech.core.client.concurrent.SliceManager;
import nl.tytech.core.client.event.OnEventThread.EventThread;
import nl.tytech.core.client.net.SlotConnection.ComEvent;
import nl.tytech.core.client.net.Status;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.event.EventInterceptor;
import nl.tytech.core.event.EventListenerInterface;
import nl.tytech.core.event.EventValidationUtils;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.core.util.SettingsManager.RunMode;
import nl.tytech.data.core.item.CodedEvent;
import nl.tytech.data.core.item.EnumOrderedItem;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.StringUtils;
import nl.tytech.util.concurrent.LocalThreadList;
import nl.tytech.util.logger.TLogger;
import org.jdesktop.swingx.event.WeakEventListenerList2;
import com.sun.javafx.application.PlatformImpl;

/**
 * This class is effectively a centralised ActionEvent redispatcher. It also catches and distributes all communication events.
 * @version 1.0 Jun 1, 2007 <br>
 * @author Jeroen Warmerdam & Maxim Knepfle
 */
public class EventManager {

    public enum ItemManipulationEventType implements EventTypeEnum {
        /**
         * List with the ID's of all deleted items. The first content (ServerUpdateEventType specific Enum) defines in which mapLink list
         * the items are deleted.
         */
        DELETE_ITEMS(MapLink.class, ArrayList.class);

        private List<Class<?>> classes;

        private ItemManipulationEventType(Class<?>... classes) {
            this.classes = Arrays.asList(classes);
        }

        @Override
        public boolean canBePredefined() {
            return false;
        }

        @Override
        public List<Class<?>> getClasses() {
            return classes;
        }

        @Override
        public Class<?> getResponseClass() {
            return null;
        }

        @Override
        public boolean isServerSide() {
            return false;
        }

    }

    private static class SingletonHolder {
        private static EventManager INSTANCE = new EventManager();

    }

    public interface TaskExecuter {
        public <V> Future<V> enqueue(Callable<V> callable);
    }

    private final static Class<?>[] enhumClasses = new Class[] { Event.class, Enum.class };

    private final static Class<?>[] idClasses = new Class[] { Event.class, Integer.class };

    public static TaskExecuter OPENGL_EVENT_EXECUTER = new TaskExecuter() {

        @Override
        public <V> Future<V> enqueue(Callable<V> callable) {
            TLogger.info("No OpenGL task executer is defined, Skip callable!");
            return null;
        }
    };

    public static void addEnumListener(EventIDListenerInterface listener, EventTypeEnum type, Enum<?> id) {
        SingletonHolder.INSTANCE._addEnumListener(listener, type, id);
    }

    public static void addEventInterceptor(EventTypeEnum type, EventInterceptor interceptor) {
        SingletonHolder.INSTANCE._addEventInterceptor(type, interceptor);
    }

    public static void addIDListener(EventIDListenerInterface listener, EventTypeEnum type, Integer id) {
        SingletonHolder.INSTANCE._addIDListener(listener, type, id);
    }

    /**
     * Static wrapper for getInstance().addEventListener();
     *
     * @param listener
     * @param type
     */
    public static void addListener(EventListenerInterface listener, Class<? extends EventTypeEnum> clazz) {
        SingletonHolder.INSTANCE._addEventClassListener(listener, clazz);
    }

    /**
     * Static wrapper for getInstance().addEventListeners();
     *
     * @param listener
     * @param type
     */
    public static void addListener(EventListenerInterface listener, EventTypeEnum... types) {
        SingletonHolder.INSTANCE._addEventListeners(false, listener, types);
    }

    public static void addPriorityListener(EventListenerInterface listener, EventTypeEnum... types) {
        SingletonHolder.INSTANCE._addEventListeners(true, listener, types);
    }

    public static void execFX(Runnable task) {

        if (PlatformImpl.isFxApplicationThread()) {
            task.run();
        } else {
            PlatformImpl.runLater(task);
        }
    }

    public static void fire(final EventTypeEnum type, final Object source, final Object... contents) {
        SingletonHolder.INSTANCE.fireEvent(new Event(type, contents));
    }

    public static void fire(final int connectionID, final EventTypeEnum type, final Object source, final Object... contents) {
        SingletonHolder.INSTANCE.fireEvent(new SlotEvent(connectionID, type, contents));
    }

    public static void fire(List<CodedEvent> events) {
        SingletonHolder.INSTANCE.fireCodedEvents(Item.NONE, events);
    }

    public static Integer getActiveConnectionID() {
        return SingletonHolder.INSTANCE._getActiveConnectionID();
    }

    public static Network.SessionType getActiveSessionType() {
        return SingletonHolder.INSTANCE._getActiveSessionType();
    }

    public static Status getActiveStatus() {
        return SingletonHolder.INSTANCE._getActiveStatus();
    }

    public static <I extends EnumOrderedItem<G>, G extends Enum<G>> I getItem(Integer connectionID, MapLink mapLink, G id) {
        return SingletonHolder.INSTANCE._getItem(connectionID, mapLink, id);
    }

    public static <I extends Item> I getItem(Integer connectionID, MapLink mapLink, Integer id) {
        return SingletonHolder.INSTANCE._getItem(connectionID, mapLink, id);
    }

    public static <I extends EnumOrderedItem<G>, G extends Enum<G>> I getItem(MapLink mapLink, G id) {
        return SingletonHolder.INSTANCE._getItem(mapLink, id);
    }

    public static <I extends Item> I getItem(MapLink mapLink, Integer id) {
        return SingletonHolder.INSTANCE._getItem(mapLink, id);
    }

    public static <I extends Item> ItemMap<I> getItemMap(Integer connectionID, MapLink mapLink) {
        return SingletonHolder.INSTANCE._getItemMap(connectionID, mapLink);
    }

    public static <I extends Item> ItemMap<I> getItemMap(MapLink mapLink) {
        return SingletonHolder.INSTANCE._getItemMap(mapLink);
    }

    public static long getSimTimeMillis() {
        return getSimTimeMillis(getActiveConnectionID());
    }

    public static long getSimTimeMillis(Integer connectionID) {
        Status status = SingletonHolder.INSTANCE._getStatus(connectionID);
        if (status != null) {
            return status.getSimTimeMillis();
        }
        return -1;
    }

    public static boolean isFirstUpdateFinished() {
        Status status = SingletonHolder.INSTANCE._getActiveStatus();
        if (status != null) {
            return status.isFirstUpdateFinished();
        }
        return false;
    }

    /**
     * Check if event listener is still part of the list
     */
    private final static <T extends EventListener> boolean isValid(WeakEventListenerList2 list, Class<T> classz, Object listener) {

        Object[] listenersThread = list.getListeners(classz);
        for (Object listenerThread : listenersThread) {
            if (listenerThread == listener) {
                return true;
            }
        }
        return false;
    }

    public static void removeAllListeners(Object potentialListener) {
        if (potentialListener instanceof EventListenerInterface) {
            removeListener((EventListenerInterface) potentialListener);
        }
        if (potentialListener instanceof EventIDListenerInterface) {
            removeIDListener((EventIDListenerInterface) potentialListener);
        }
    }

    public static void removeIDListener(EventIDListenerInterface listener) {
        SingletonHolder.INSTANCE._removeEventIDListener(listener);
    }

    public static void removeIDListener(EventIDListenerInterface listener, EventTypeEnum type) {
        SingletonHolder.INSTANCE._removeEventIDListener(listener, type);
    }

    public static void removeListener(EventListenerInterface listener) {
        SingletonHolder.INSTANCE._removeEventListener(listener);
    }

    public static void removeListener(EventListenerInterface listener, EventTypeEnum... type) {
        SingletonHolder.INSTANCE._removeEventListeners(listener, type);
    }

    public static <T> T request(final EventTypeEnum eventType, Object... args) {
        return SingletonHolder.INSTANCE.<T> _requestInner(eventType, args);
    }

    /**
     * Reset the eventmanager
     */
    public static void reset() {
        SingletonHolder.INSTANCE.clean();
        SingletonHolder.INSTANCE = new EventManager();
    }

    public static void setActiveConnectionID(Integer connectionID) {
        if (connectionID != null) {
            SingletonHolder.INSTANCE._setActiveConnection(connectionID);
        }
    }

    public static void setStatus(final Integer connectionID, final Status status) {
        SingletonHolder.INSTANCE._setStatus(connectionID, status);
    }

    /**
     * Push the runnable to the correct Thread
     * @param threadType
     * @param runnable
     */
    private static void threadPusher(EventThread threadType, final Runnable runnable) {

        switch (threadType) {
            case CALLER:
                TLogger.severe("Execute me direct, no need to push!");
                runnable.run();
                return;
            case PARALLEL:
                SliceManager.exec(runnable);
                return;
            case JAVAFX:
                execFX(runnable);
                return;
            case LONG:
                SliceManager.execLongRunner(runnable);
                return;
            case OPENGL:
                OPENGL_EVENT_EXECUTER.enqueue(() -> {
                    runnable.run();
                    return null;
                });
                return;
        }
    }

    private final Map<Class<?>, EventThread> eventIDThreads = new HashMap<>();

    private final Map<Class<?>, EventThread> eventEnumThreads = new HashMap<>();

    private final Map<Integer, WeakEventListenerList2> listUpdateEventListenerList = new HashMap<Integer, WeakEventListenerList2>();

    private final Map<EventTypeEnum, EventInterceptor> interceptionList = new HashMap<Event.EventTypeEnum, EventInterceptor>();

    private final Map<EventTypeEnum, Map<Integer, WeakEventListenerList2>> idEventList = new HashMap<Event.EventTypeEnum, Map<Integer, WeakEventListenerList2>>();

    private final Map<EventTypeEnum, Map<Enum<?>, WeakEventListenerList2>> enumEventList = new HashMap<Event.EventTypeEnum, Map<Enum<?>, WeakEventListenerList2>>();

    private final HashMap<Integer, Status> statusMap = new HashMap<Integer, Status>();

    private final LocalThreadList<Entry<Enum<?>, WeakEventListenerList2>> tempEnumList = new LocalThreadList<>();

    private final LocalThreadList<Entry<Integer, WeakEventListenerList2>> tempIDList = new LocalThreadList<>();

    private final Map<Class<?>, EventThread> eventThreads = new HashMap<>();

    private final Map<EventTypeEnum, WeakEventListenerList2> listMap = new HashMap<>();

    private Integer activeConnectionID = Item.NONE;

    private EventManager() {
    }

    private void _addEnumListener(EventIDListenerInterface listener, EventTypeEnum type, Enum<?> id) {

        synchronized (enumEventList) {
            Map<Enum<?>, WeakEventListenerList2> map = enumEventList.get(type);
            if (map == null) {
                map = new HashMap<Enum<?>, WeakEventListenerList2>();
            }

            WeakEventListenerList2 list = map.get(id);
            if (list == null) {
                list = new WeakEventListenerList2();
            }
            list.add(EventIDListenerInterface.class, listener);
            map.put(id, list);
            enumEventList.put(type, map);
        }
    }

    private void _addEventClassListener(EventListenerInterface listener, Class<? extends EventTypeEnum> clazz) {
        this._addEventListeners(false, listener, clazz.getEnumConstants());
    }

    private void _addEventInterceptor(EventTypeEnum type, EventInterceptor interceptor) {
        this.interceptionList.put(type, interceptor);
    }

    private void _addEventListeners(boolean priority, EventListenerInterface listener, EventTypeEnum... types) {
        for (EventTypeEnum type : types) {
            addEventListener(priority, listener, type);
        }
    }

    private void _addIDListener(EventIDListenerInterface listener, EventTypeEnum type, Integer id) {

        synchronized (idEventList) {
            Map<Integer, WeakEventListenerList2> map = idEventList.get(type);
            if (map == null) {
                map = new HashMap<Integer, WeakEventListenerList2>();
            }

            WeakEventListenerList2 list = map.get(id);
            if (list == null) {
                list = new WeakEventListenerList2();
            }
            list.add(EventIDListenerInterface.class, listener);
            map.put(id, list);
            idEventList.put(type, map);
        }
    }

    private Integer _getActiveConnectionID() {
        return this.activeConnectionID;
    }

    private Network.SessionType _getActiveSessionType() {
        Status activeStatus = _getActiveStatus();
        if (activeStatus == null) {
            TLogger.severe("Trying to access SessionType, however no status is available, please connect first.");
            return null;
        }
        return activeStatus.getSessionType();
    }

    private Status _getActiveStatus() {
        return this._getStatus(this.activeConnectionID);
    }

    private <I extends EnumOrderedItem<G>, G extends Enum<G>> I _getItem(Integer connectionID, MapLink mapLink, G id) {
        ItemMap<I> items = _getItemMap(connectionID, mapLink);
        if (items == null) {
            return null;
        }
        return items.get(id);
    }

    private <I extends Item> I _getItem(Integer connectionID, MapLink mapLink, Integer id) {
        ItemMap<I> items = _getItemMap(connectionID, mapLink);
        if (items == null || id == null) {
            return null;
        }
        return items.get(id);
    }

    private <I extends EnumOrderedItem<G>, G extends Enum<G>> I _getItem(MapLink mapLink, G id) {
        return _getItem(this.activeConnectionID, mapLink, id);
    }

    private <I extends Item> I _getItem(MapLink mapLink, Integer id) {
        return _getItem(this.activeConnectionID, mapLink, id);
    }

    private <I extends Item> ItemMap<I> _getItemMap(Integer connectionID, MapLink mapLink) {
        if (!this.statusMap.containsKey(connectionID)) {
            return null;
        }
        return statusMap.get(connectionID).getMap(mapLink);
    }

    private <I extends Item> ItemMap<I> _getItemMap(MapLink mapLink) {
        return _getItemMap(this.activeConnectionID, mapLink);
    }

    private Status _getStatus(Integer connectionID) {
        return this.statusMap.get(connectionID);
    }

    private void _removeEventIDListener(EventIDListenerInterface listener) {

        synchronized (enumEventList) {
            for (Map<Enum<?>, WeakEventListenerList2> map : enumEventList.values()) {
                for (WeakEventListenerList2 list : map.values()) {
                    list.remove(EventIDListenerInterface.class, listener);
                }
            }
        }
        synchronized (idEventList) {
            for (Map<Integer, WeakEventListenerList2> map : idEventList.values()) {
                for (WeakEventListenerList2 list : map.values()) {
                    list.remove(EventIDListenerInterface.class, listener);
                }
            }
        }
    }

    private void _removeEventIDListener(EventIDListenerInterface listener, EventTypeEnum type) {

        synchronized (enumEventList) {
            Map<Enum<?>, WeakEventListenerList2> enummap = enumEventList.get(type);
            if (enummap != null) {
                for (WeakEventListenerList2 list : enummap.values()) {
                    list.remove(EventIDListenerInterface.class, listener);
                }
            }
        }
        synchronized (idEventList) {
            Map<Integer, WeakEventListenerList2> idmap = idEventList.get(type);
            if (idmap != null) {
                for (WeakEventListenerList2 list : idmap.values()) {
                    list.remove(EventIDListenerInterface.class, listener);
                }
            }
        }
    }

    /**
     * Remove a generic event listener
     *
     * @param listener
     */
    private void _removeEventListener(EventListenerInterface listener) {

        synchronized (listMap) {
            // remove also from all type-only listeners
            for (EventTypeEnum type : new ArrayList<EventTypeEnum>(listMap.keySet())) {
                _removeEventListener(listener, type);
            }
        }
    }

    /**
     * Remove an event listener for this specific type if it has been added in the past
     *
     * @param listener
     * @param type
     */
    private void _removeEventListener(EventListenerInterface listener, EventTypeEnum type) {

        synchronized (listMap) {
            // Don't do anything if the list doesn't exist at all
            if (!listMap.containsKey(type)) {
                TLogger.warning("Attempting to remove listener from non existing type");
                return;
            }

            // Call the list if it has not been called yet
            WeakEventListenerList2 list = listMap.get(type);
            list.remove(EventListenerInterface.class, listener);
        }
    }

    /**
     * Remove an event listener for multiple types at once
     *
     * @param listener
     * @param types
     */
    private void _removeEventListeners(EventListenerInterface listener, EventTypeEnum... types) {
        for (EventTypeEnum type : types) {
            _removeEventListener(listener, type);
        }
    }

    private <T> T _requestInner(final EventTypeEnum eventType, Object... args) {
        Event wrapper = new Event(eventType, args);
        wrapper.setRequestEvent(true);
        fireToNormalEvent(wrapper);
        return wrapper.getRequestLoad();
    }

    private void _setActiveConnection(Integer connectionID) {
        if (!this.activeConnectionID.equals(connectionID)) {
            TLogger.info("Changing active connection id from: " + this.activeConnectionID + " to " + connectionID);
            this.activeConnectionID = new Integer(connectionID);

            Status status = this.statusMap.get(this.activeConnectionID);

            EventManager.fire(ComEvent.MAPLINKS_INITIALIZED, this, status.getSessionType(), status.getProjectName(), status.getAppType());
        }

    }

    private void _setStatus(final Integer connectionID, final Status status) {
        this.statusMap.put(connectionID, status);

        // when active connection is no set jet, use this one.
        if (Item.NONE.equals(this.activeConnectionID)) {
            activeConnectionID = connectionID;
        }
    }

    /**
     * Add an event listener for this specific type
     *
     * @param listener
     * @param type
     */
    private void addEventListener(boolean priority, EventListenerInterface listener, EventTypeEnum type) {

        if (type.isServerSide()) {
            if (SettingsManager.getRunMode() == RunMode.RELEASE) {
                TLogger.severe("Event " + type.getClass().getSimpleName() + " " + type
                        + " is a server side event, the client should not listen to this!");
            } else {
                Thread.dumpStack();
                TLogger.showstopper("Event " + type.getClass().getSimpleName() + " " + type
                        + " is a server side event, the client should not listen to this!");
            }
            return;
        }

        synchronized (listMap) {
            // Create the listenerlist if it does not already exist
            if (!listMap.containsKey(type)) {
                listMap.put(type, new WeakEventListenerList2());
            }
            WeakEventListenerList2 list = listMap.get(type);

            // prio means put me first on the list
            if (priority) {
                WeakEventListenerList2 newList = new WeakEventListenerList2();
                newList.add(EventListenerInterface.class, listener);
                EventListenerInterface[] listeners = list.getListeners(EventListenerInterface.class);
                for (Object old : listeners) {
                    newList.add(EventListenerInterface.class, (EventListenerInterface) old);
                }
                listMap.put(type, newList);
            } else {
                list.add(EventListenerInterface.class, listener);
            }
        }
    }

    /**
     * Cleans the content of the eventmanager
     */

    private void clean() {

        this.listMap.clear();
        this.listUpdateEventListenerList.clear();
        this.statusMap.clear();
        this.activeConnectionID = Item.NONE;
    }

    private final void fireCodedEvents(Integer stakeholderID, List<CodedEvent> events) {

        if (events == null) {
            return;
        }

        try {
            for (CodedEvent codedEvent : events) {
                List<Object> event = codedEvent.getParameters();

                if (event.size() > 0) {
                    EventTypeEnum ete = null;
                    if (event.get(0) instanceof EventTypeEnum) {
                        ete = (EventTypeEnum) event.get(0);

                    } else if (event.get(0) instanceof String) {
                        String[] classAndValue = ((String) event.get(0)).split(StringUtils.WHITESPACE);
                        Class<?> c = Class.forName(classAndValue[0]);
                        for (Object value : c.getEnumConstants()) {
                            if (value.toString().equals(classAndValue[1])) {
                                ete = (EventTypeEnum) value;
                                break;
                            }
                        }
                    } else {
                        TLogger.severe("Missing Event Type.");
                        continue;
                    }

                    if (ete != null) {
                        Object[] contents = new Object[event.size() - 1];
                        for (int i = 1; i < event.size(); i++) {
                            contents[i - 1] = event.get(i);
                        }

                        if (!Item.NONE.equals(stakeholderID)) {
                            contents[0] = stakeholderID;
                        }

                        EventManager.fire(ete, this, contents);
                    }
                }
            }
        } catch (Exception exp) {
            TLogger.exception(exp);
        }
    }

    private void fireEvent(final Event event) {

        if (event.getType().isServerSide()) {
            if (SettingsManager.getRunMode() == RunMode.RELEASE) {
                TLogger.severe("Cannot fire server event: " + event.getClass().getSimpleName() + "." + event.getType()
                        + ", use CommunicationManager.fireServerEvent() instead");
            } else {
                TLogger.showstopper("Cannot fire server event: " + event.getClass().getSimpleName() + "." + event.getType()
                        + ", use CommunicationManager.fireServerEvent() instead");
            }
        }

        if (!EventValidationUtils.validateEvent(event)) {
            if (SettingsManager.getRunMode() != RunMode.RELEASE) {
                TLogger.showstopper("Event failure, see error messages above!");
            }
            return;
        }

        EventInterceptor interceptor = interceptionList.get(event.getType());
        if (interceptor != null && interceptor.interceptEvent(event)) {
            TLogger.info("Intercepting " + event.getType());
            return;
        }

        fireToNormalEvent(event);
        fireToIDEvent(event);
        fireToEnumEvent(event);
    }

    private void fireToEnumEvent(Event event) {

        // Do enum stuff
        Map<Enum<?>, WeakEventListenerList2> enumMap = enumEventList.get(event.getType());
        if (enumMap == null) {
            return;
        }
        List<Entry<Enum<?>, WeakEventListenerList2>> temp = tempEnumList.get();
        temp.addAll(enumMap.entrySet());

        ItemMap<Item> allItems = event.<ItemMap<Item>> getContent(0);
        Collection<Item> updatedItems = event.<Collection<Item>> getContent(1);
        for (Entry<Enum<?>, WeakEventListenerList2> entry : temp) {
            Enum id = entry.getKey();
            Item item = allItems.get(id);
            if (updatedItems.contains(item)) {
                EventIDListenerInterface[] listeners = entry.getValue().getListeners(EventIDListenerInterface.class);
                for (final EventIDListenerInterface listener : listeners) {
                    EventThread threadType = getEventThread(listener, true);
                    if (threadType == EventThread.CALLER) {
                        listener.notifyEnumListener(event, id);
                    } else {
                        threadPusher(threadType, () -> {
                            if (isValid(entry.getValue(), EventIDListenerInterface.class, listener)) {
                                listener.notifyEnumListener(event, id);
                            }
                        });
                    }
                }
                // Do not return here,might be more enums in this list e.g. in settings!
            }
        }
    }

    private void fireToIDEvent(Event event) {

        Map<Integer, WeakEventListenerList2> idMap = idEventList.get(event.getType());
        if (idMap == null) {
            return;
        }
        List<Entry<Integer, WeakEventListenerList2>> temp = tempIDList.get();
        temp.addAll(idMap.entrySet());

        ItemMap<Item> allItems = event.<ItemMap<Item>> getContent(0);
        Collection<Item> updatedItems = event.<Collection<Item>> getContent(1);
        for (Entry<Integer, WeakEventListenerList2> entry : temp) {
            Integer id = entry.getKey();
            Item item = allItems.get(id);
            if (updatedItems.contains(item)) {
                EventIDListenerInterface[] listeners = entry.getValue().getListeners(EventIDListenerInterface.class);
                for (final EventIDListenerInterface listener : listeners) {
                    EventThread threadType = getEventThread(listener, false);
                    if (threadType == EventThread.CALLER) {
                        listener.notifyIDListener(event, id);
                    } else {
                        threadPusher(threadType, () -> {
                            if (isValid(entry.getValue(), EventIDListenerInterface.class, listener)) {
                                listener.notifyIDListener(event, id);
                            }
                        });
                    }
                }
                // Do not return here,might be more enums in this list e.g. in settings!
            }
        }
    }

    private void fireToNormalEvent(final Event event) {

        // Now, check if we need to throw it to any specific listeners too
        WeakEventListenerList2 list = listMap.get(event.getType());
        if (list == null) {
            return;
        }
        // Fire here too.
        EventListenerInterface[] listeners = list.getListeners(EventListenerInterface.class);
        for (EventListenerInterface listener : listeners) {
            try {
                EventThread threadType = getEventThread(listener);
                if (event.isRequestEvent() || threadType == EventThread.CALLER) {
                    listener.notifyListener(event);
                } else {
                    threadPusher(threadType, () -> {
                        if (isValid(list, EventListenerInterface.class, listener)) {
                            listener.notifyListener(event);
                        }
                    });
                }
            } catch (Exception exp) {
                TLogger.exception(exp);
            }
        }
    }

    private EventThread getEventThread(EventIDListenerInterface listener, boolean enhum) {

        Class<?> classz = listener.getClass();
        Map<Class<?>, EventThread> map = enhum ? this.eventEnumThreads : this.eventIDThreads;

        if (map.containsKey(classz)) {
            return map.get(classz);
        }

        // unknow, lets find out
        try {
            nl.tytech.core.client.event.OnEventThread.EventThread eventThread = EventThread.PARALLEL;
            Method method = classz.getMethod(enhum ? "notifyEnumListener" : "notifyIDListener", enhum ? enhumClasses : idClasses);
            if (method.isAnnotationPresent(OnEventThread.class)) {
                eventThread = EventThread.valueOf(method.getAnnotation(OnEventThread.class).value());
            }
            map.put(classz, eventThread);
            return eventThread;
        } catch (Exception e) {
            TLogger.exception(e);
            return null;
        }
    }

    private EventThread getEventThread(EventListenerInterface listener) {

        Class<?> classz = listener.getClass();
        if (eventThreads.containsKey(classz)) {
            return eventThreads.get(classz);
        }

        // unknow, lets find out

        try {
            EventThread eventThread = EventThread.PARALLEL;
            Method method = classz.getMethod("notifyListener", Event.class);
            if (method.isAnnotationPresent(OnEventThread.class)) {
                eventThread = EventThread.valueOf(method.getAnnotation(OnEventThread.class).value());
            }
            eventThreads.put(classz, eventThread);
            return eventThread;
        } catch (Exception e) {
            TLogger.exception(e);
            return null;
        }
    }

}
