/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.engine.other.Action;
import nl.tytech.data.engine.serializable.CalculationSpaceType;
import nl.tytech.data.engine.serializable.TimeState;
import nl.tytech.data.engine.serializable.UpgradePair;
import nl.tytech.util.StringUtils;

/**
 * UpgradeType
 * <p>
 * This class keeps track of the upgrades per model.
 * </p>
 * @author Alexander Hofstede & Maxim Knepfle, Frank Baars
 */
public class UpgradeType extends UniqueNamedItem implements Action {

    public enum Type {
        MAKE_VACANT, REVERT_VACANT, UPGRADE
    }

    private static final long serialVersionUID = -3902779613875731026L;

    @XMLValue
    private Type type = Type.UPGRADE;

    @XMLValue
    private boolean deprecated = false;

    @XMLValue
    private String description = StringUtils.EMPTY;

    @XMLValue
    private double costsPerM2 = 0;

    @XMLValue
    @AssetDirectory(GUI_IMAGES_ACTIONS)
    private String imageName = DEFAULT_IMAGE;

    @XMLValue
    @ListOfClass(UpgradePair.class)
    private ArrayList<UpgradePair> pairs = new ArrayList<>();

    @XMLValue
    private double buildingTimeInMonths = 0;

    @XMLValue
    private boolean mustOwn = false;

    @XMLValue
    private boolean zoningPermitRequired = true;

    @XMLValue
    private CalculationSpaceType calculationType = CalculationSpaceType.SURFACE_SPACE;

    public void addUpgradePair(UpgradePair pair) {
        if (containsSourceFunction(pair.getSourceFunctionID())) {
            return;
        }
        pairs.add(pair);
    }

    public boolean containsSourceFunction(Integer sourceFunctionID) {
        for (UpgradePair aPair : pairs) {
            if (aPair.getSourceFunctionID().equals(sourceFunctionID)) {
                return true;
            }
        }
        return false;
    }

    public CalculationSpaceType getCalculationType() {
        return calculationType;
    }

    @Override
    public double getConstructionTimeInMonths() {
        return buildingTimeInMonths;
    }

    public double getCosts(double sizeM2, double floorSize) {
        if (calculationType == CalculationSpaceType.SURFACE_SPACE) {
            return getCostsM2() * sizeM2;
        } else {
            return getCostsM2() * sizeM2 * floorSize;
        }
    }

    public double getCostsM2() {
        return costsPerM2;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getImageLocation() {

        if (!StringUtils.containsData(imageName)) {
            return StringUtils.EMPTY;
        }
        return GUI_IMAGES_ACTIONS + imageName;
    }

    public String getImageName() {
        return imageName;
    }

    @Override
    public MapLink getMapLink() {
        return MapLink.UPGRADE_TYPES;
    }

    public List<UpgradePair> getPairs() {
        return pairs;
    }

    public Function getTargetFunction(final Integer sourceFunctionID) {

        return this.getItem(MapLink.FUNCTIONS, this.getTargetFunctionID(sourceFunctionID));
    }

    public Integer getTargetFunctionID(final Integer sourceFunctionID) {

        for (UpgradePair pair : pairs) {
            if (pair.getSourceFunctionID().equals(sourceFunctionID)) {
                return pair.getTargetFunctionID();
            }
        }
        return Item.NONE;
    }

    public Type getType() {
        return type;
    }

    public UpgradePair getUpgradePairForSourceID(Integer sourceFunctionID) {
        for (UpgradePair somePair : getPairs()) {
            if (somePair.getSourceFunctionID().equals(sourceFunctionID)) {
                return somePair;
            }
        }
        return null;
    }

    @Override
    public boolean isBuildable() {
        return true;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    @Override
    public boolean isFixedLocation() {
        return false;
    }

    /**
     * When true this upgrade changes the zoning and the building can be sold again
     * @return
     */
    public boolean isFunctionCategoryChange() {

        for (UpgradePair pair : pairs) {
            Function source = this.getItem(MapLink.FUNCTIONS, pair.getSourceFunctionID());
            Function target = this.getItem(MapLink.FUNCTIONS, pair.getTargetFunctionID());

            if (source.getCategories().size() != target.getCategories().size()
                    || !source.getCategories().containsAll(target.getCategories())) {
                return true;
            }
        }
        return false;
    }

    public boolean isMustOwn() {
        return mustOwn;
    }

    public boolean isUpgradable(Integer stakeholderID, Building building) {
        if (building.getTimeState() != TimeState.READY) {
            return false;
        }
        if (isMustOwn() && !building.getOwnerID().equals(stakeholderID)) {
            return false;
        }
        return !Item.NONE.equals(this.getTargetFunctionID(building.getFunctionID()));
    }

    public boolean isZoningPermitRequired() {
        return zoningPermitRequired;
    }

    public void removeUpgradePair(UpgradePair pair) {
        pairs.remove(pair);
    }

    public void setCalculationType(CalculationSpaceType calculationType) {
        this.calculationType = calculationType;
    }

    public void setConstructionTimeInMonths(double constructionTimeInMonths) {
        this.buildingTimeInMonths = constructionTimeInMonths;
    }

    public void setCostsM2(double costsPerM2) {
        this.costsPerM2 = costsPerM2;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageName(String name) {
        this.imageName = name;
    }

    public void setMustOwn(boolean mustOwn) {
        this.mustOwn = mustOwn;
    }

    public void setZoningPermitRequired(boolean zonePermitRequired) {
        this.zoningPermitRequired = zonePermitRequired;
    }

    @Override
    public String toString() {
        return getName();
    }
}
