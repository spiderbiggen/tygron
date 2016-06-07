/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.core.item;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.net.Lord;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.core.structure.ItemNamespace;
import nl.tytech.core.structure.ItemNamespace.Filter;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 * Item
 * <p>
 * This class is an abstract class for items sent through the network. All items are versioned, serializable and cloneable. They also have
 * an ID. The ID is the location of the item in a list.
 * <p>
 *
 *
 * @author Maxim Knepfle
 */
public abstract class Item implements Serializable, Comparable<Item> {

    /**
     * Item with an ID larger or equal to this one are game specific.
     */
    public final static int SPECIFIC_START_ID = 1000000;

    /**
     *
     */
    private static final long serialVersionUID = 1769699526703175911L;

    public static final int X = 0;

    public static final int Y = 1;

    public static final int Z = 2;

    /**
     * NONE is used for multiple application.
     */
    public final static Integer NONE = -1;

    /**
     * The items ID, also its location number in a list.
     */
    private Integer id = NONE;

    /**
     * The lord is the master containing all lists. With the Lord an items can ask for another item in another list.
     */
    private transient Lord lord = null;

    /**
     * Version number in the game. Each time the items is changed the items gets a new version number.
     */
    private int version = 0;

    @Override
    public int compareTo(Item other) {

        // when null value or no toString, return same 0.
        if (other == null || this.toString() == null || other.toString() == null) {
            return 0;
        }
        // compare based on the toString
        return this.toString().compareTo(other.toString());
    }

    @Override
    public boolean equals(Object obj) {

        // same object
        if (this == obj) {
            return true;
        }

        // null's and other classes
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Item other = (Item) obj;

        // check for id
        if (!this.getID().equals(other.getID())) {
            return false;
        }
        // nothing more, must be equal
        return true;
    }

    /**
     * Get description of the item. Override when required!
     *
     * @return description
     */
    public String getDescription() {
        return this.toString();
    }

    public final <I extends EnumOrderedItem<T>, T extends Enum<T>> List<I> getEnumItems(final MapLink mapLink,
            final Collection<T> requestEnums) {

        ItemMap<I> itemMap = this.getMap(mapLink);
        if (itemMap == null) {
            return null;
        }
        return itemMap.getEnumItems(requestEnums);
    }

    /**
     * Return item ID
     *
     * @return the ID
     */
    public final Integer getID() {

        return id;
    }

    /**
     * Using the lord an item can get an item from another list. Note that you must cast the Item to the desired object. Using getList this
     * can be avoided.
     *
     * @param controlType List type
     * @param id Item ID.
     * @return The requested Item.
     */
    public final <I extends EnumOrderedItem<G>, G extends Enum<G>> I getItem(MapLink eventType, final G type) {

        if (type == null) {
            // okay!
            return null;
        }
        // requested map
        ItemMap<I> requestMap = this.getMap(eventType);
        if (requestMap == null) {
            return null;
        }
        // return the item
        return requestMap.get(type);
    }

    /**
     * Using the lord an item can get an item from another list. Note that you must cast the Item to the desired object. Using getList this
     * can be avoided.
     *
     * @param controlType List type
     * @param id Item ID.
     * @return The requested Item.
     */

    public final <I extends Item> I getItem(MapLink mapLink, final Integer id) {
        if (id == null || NONE.equals(id)) {
            // okay!
            return null;
        }
        // requested map
        ItemMap<I> requestMap = this.getMap(mapLink);
        if (requestMap == null) {
            return null;
        }
        // return the item
        return requestMap.get(id);
    }

    public final <I extends UniqueNamedItem> I getItem(MapLink mapLink, final String uniqueName) {
        if (!StringUtils.containsData(uniqueName)) {
            return null;
        }
        // requested map
        ItemMap<I> requestMap = this.getMap(mapLink);
        if (requestMap == null) {
            return null;
        }
        for (I item : requestMap.values()) {
            if (item instanceof UniqueNamedItem && uniqueName.equals(item.getName())) {
                return item;
            }
        }
        return null;
    }

    /**
     * Using the lord get a list of items for the given ID's.
     *
     * @param <C>
     * @param <I>
     * @param controlType
     * @param requestIDs
     * @return
     */
    public final <I extends Item> List<I> getItems(final MapLink mapLink, final Iterable<Integer> requestIDs) {
        ItemMap<I> itemMap = this.getMap(mapLink);
        if (itemMap == null) {
            return null;
        }
        return itemMap.getItems(requestIDs);
    }

    public Lord getLord() {
        return lord;
    }

    /**
     * Using the lord you can get another item map
     *
     * @param controlClass List type
     * @param id Item ID.
     * @return The requested ItemMap.
     */
    public final <I extends Item> ItemMap<I> getMap(final MapLink controlClass) {

        if (lord == null) {
            // if the lord is not set we had some problem constructing this item
            TLogger.severe(this.getClass().getSimpleName() + "(" + id + ") from " + controlClass.name() + " has no lord set.");
            return null;
        }
        return lord.getMap(controlClass);
    }

    /**
     * Returns a sorted list of the items that go before itemID
     *
     * E.g. get all previous game levels (based on there sorted order)
     * @param mapLink
     * @param itemID
     * @return
     */
    public final <I extends Item> Collection<I> getPreviousSortedItems(final MapLink mapLink, final Integer itemID) {

        Item item = this.getItem(mapLink, itemID);
        if (item == null) {
            return new ArrayList<>();
        }

        ItemMap<I> allItems = this.getMap(mapLink);
        List<I> sortedItems = new ArrayList<>(allItems.values());

        /**
         * Reverse sort items
         */
        Collections.sort(sortedItems);
        Collections.reverse(sortedItems);

        /**
         * Get my index +1 (so i am not included myself)
         */
        int startIndex = sortedItems.indexOf(item) + 1;
        return sortedItems.subList(startIndex, sortedItems.size());
    }

    /**
     * Return the version of the item.
     *
     * @return the Version
     */
    public final int getVersion() {

        return version;
    }

    public final <C extends Word<E>, E extends Enum<E>> String getWord(final MapLink mapLink, E term, Object... args) {

        Word<E> word = this.getItem(mapLink, term);
        if (word == null) {
            TLogger.severe("Missing " + Word.class.getSimpleName() + " for " + mapLink + " " + term + ".");
            return null;
        }
        return StringUtils.formatEnumString(word.getTranslation(), term, args);
    }

    @Override
    public int hashCode() {

        // the id is always unique?
        return this.getID();
    }

    /**
     * Reset the version and id of the item.
     */
    public void reset() {
        this.version = NONE;
        this.id = NONE;
    }

    /**
     * Set item ID.
     *
     * @param id the new ID
     */
    public final void setId(final Integer id) {

        this.id = id;
    }

    /**
     * Set a new lord on the otherside of the network.
     *
     * @param argLord The new Lord.
     */
    public void setLord(final Lord argLord) {

        lord = argLord;
    }

    /**
     * Set item version.
     *
     * @param version the new version
     */
    public final void setVersion(final int version) {

        if (version <= this.version) {
            TLogger.severe("Version of item " + toString() + ", ID: " + this.getID() + " is set back from " + this.version + " to "
                    + version + ".");
        }
        this.version = version;
    }

    public final void syncInternalVersion() {
        this.updateInternalVersion(this.getVersion());
    }

    @Override
    public abstract String toString();

    /**
     * Override this method if you want to keep a multiple versions in the item.
     */
    protected void updateInternalVersion(int version) {

    }

    /**
     * Check for correct asset names, not strange signs are allowed.
     */
    private String validAssetName(Field field) {

        AssetDirectory assetField = field.getAnnotation(AssetDirectory.class);
        String result = StringUtils.EMPTY;
        if (assetField == null) {
            return result;
        }

        try {
            field.setAccessible(true);
            Object value = field.get(this);
            if (value instanceof String && !StringUtils.validFilename(value.toString().toLowerCase(), null)) {

                result += "\nInvalid asset name in field: " + field.getName() + " [" + value + "] of item "
                        + this.getClass().getSimpleName() + " " + this.getID() + ".";
            }
        } catch (Exception e) {
            TLogger.exception(e);
            result += "\n" + e.toString();
        }
        return result;
    }

    /**
     * Default validate method for the item, should be overridden in subclasses.
     *
     * @return ValidationType
     */
    public String validated(boolean startSession) {
        return StringUtils.EMPTY;
    }

    /**
     * Checks if this item has incorrect links to other items and or valid asset names.
     * @return
     */
    public String validFields() {

        // TODO: Maxim; also do this check for sub item object e.g. IndicatorScore.
        String result = StringUtils.EMPTY;
        for (Field field : ItemNamespace.getFields(this.getClass(), Filter.ALL)) {
            result += validIDFieldLinkage(field);
            result += validAssetName(field);
            result += validListValues(field);
        }
        return result;
    }

    private String validIDFieldLinkage(Field field) {
        ItemIDField itemIDField = field.getAnnotation(ItemIDField.class);

        String result = StringUtils.EMPTY;
        if (itemIDField == null) {
            return result;
        }
        MapLink controlType = MapLink.valueOf(itemIDField.value());

        try {
            field.setAccessible(true);
            Object value = field.get(this);
            if (value instanceof Integer) {
                result += validIntegerLink(field, controlType, value);

            } else if (value instanceof List) {
                @SuppressWarnings("unchecked")
                List<Integer> intListValue = (List<Integer>) value;
                for (Integer intValue : intListValue) {
                    result += validIntegerLink(field, controlType, intValue);
                }
            } else if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<Integer, ?> map = (Map<Integer, ?>) value;
                Iterable<Integer> idKeys = map.keySet();
                for (Integer intValue : idKeys) {
                    result += validIntegerLink(field, controlType, intValue);
                }
            }
        } catch (Exception e) {
            TLogger.exception(e);
            result += "\n" + e.toString();
        }
        return result;
    }

    protected String validIntegerLink(Field field, MapLink mapLink, Object value) {

        String result = StringUtils.EMPTY;
        Integer intValue = (Integer) value;
        if (Item.NONE.equals(intValue)) {
            return result;
        }

        Item item = this.getItem(mapLink, intValue);
        if (item == null) {
            if (field == null) {
                result += "\nFailed linkage in [" + intValue + "] of item " + this.getClass().getSimpleName() + " " + this.getID()
                        + " for " + mapLink + ".";
            } else {
                result += "\nFailed linkage in field: " + field.getName() + " [" + intValue + "] of item "
                        + this.getClass().getSimpleName() + " " + this.getID() + ".";
            }
            return result;
        }
        return result;
    }

    private String validListValues(Field field) {
        ListOfClass listOfClass = field.getAnnotation(ListOfClass.class);

        String result = StringUtils.EMPTY;
        if (listOfClass == null) {
            return result;
        }
        Class<?> listClass = listOfClass.value();

        try {
            field.setAccessible(true);
            Object value = field.get(this);
            if (value != null) {
                List<?> list = (List<?>) value;
                for (Object object : list) {
                    if (!object.getClass().isAssignableFrom(listClass)) {
                        result += "Invalid object detected in List: " + field.getName();
                    }
                }
            }
        } catch (Exception e) {
            TLogger.exception(e);
            result += "\n" + e.toString();
        }
        return result;
    }
}
