/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.EditOptions;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.TargetDescription;
import nl.tytech.locale.unit.UnitType;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * Indicator that belongs a the achievements of one specific stakeholder.
 * @author Maxim
 *
 */
public class PersonalIndicator extends Indicator {

    public enum PersonalIndicatorType implements TypeInterface {
        HOUSING(EditOptions.GREEN, TColor.RED, null, true),

        FINANCE(EditOptions.GREEN, TColor.WHITE, new TargetDescription[] { new TargetDescription(
                "Amount of money that the Stakeholder needs to earn.", 1000, -Double.MAX_VALUE, Double.MAX_VALUE, UnitType.NONE) }, true),

        @Deprecated
        PIPE_NETWORK_CONNECTED(EditOptions.HEAT, TColor.RED, new TargetDescription[] { new TargetDescription("Amount of connected units.",
                1000, -Double.MAX_VALUE, Double.MAX_VALUE, UnitType.NONE) }, true),

        @Deprecated
        PIPE_NETWORK_POLLUTION(EditOptions.HEAT, TColor.RED, new TargetDescription[] { new TargetDescription("Reduction of e.g. CO2.).", 1,
                0, Double.MAX_VALUE, UnitType.NONE) }, true),

        @Deprecated
        PIPE_NETWORK_FINANCE(EditOptions.HEAT, TColor.RED, new TargetDescription[] { new TargetDescription(
                "Efficiency of a pipe transport network (Amount X delivered/cost).", 1, 0, Double.MAX_VALUE, UnitType.NONE) }, true),

        ;

        private static PersonalIndicatorType[] ACTIVE_TYPES;

        public static PersonalIndicatorType[] getActiveValues() {
            if (ACTIVE_TYPES != null) {
                return ACTIVE_TYPES;
            }
            List<PersonalIndicatorType> types = new ArrayList<PersonalIndicator.PersonalIndicatorType>();
            for (PersonalIndicatorType type : PersonalIndicatorType.values()) {
                Deprecated depAnno = ObjectUtils.getEnumAnnotation(type, Deprecated.class);
                if (depAnno == null) {
                    types.add(type);
                }
            }
            ACTIVE_TYPES = types.toArray(new PersonalIndicatorType[0]);
            return ACTIVE_TYPES;
        }

        public static PersonalIndicatorType[] getActiveValues(EditOptions userZone) {

            List<PersonalIndicatorType> types = new ArrayList<>();
            for (PersonalIndicatorType type : getActiveValues()) {
                if (type.editZone.ordinal() <= userZone.ordinal()) {
                    types.add(type);
                }
            }
            return types.toArray(new PersonalIndicatorType[0]);
        }

        private EditOptions editZone;

        private TargetDescription[] targetDescriptions;

        private boolean isSingleInstance;

        private TColor color;

        private PersonalIndicatorType(EditOptions editZone, TColor color, TargetDescription[] targets, boolean isSingleInstance) {
            this.editZone = editZone;
            this.color = color;
            this.targetDescriptions = targets;
            this.isSingleInstance = isSingleInstance;
        }

        @Override
        public TColor getDefaultColor() {
            return color;
        }

        @Override
        public double[] getDefaultTargetsCopy() {

            double[] targets = new double[getTargetDescriptions().length];
            for (int i = 0; i < targets.length; i++) {
                targets[i] = targetDescriptions[i].getDefaultValue();
            }
            return targets;
        }

        @Override
        public String getHumanString() {
            return StringUtils.capitalizeWithSpacedUnderScores(this.name());
        }

        @Override
        public TargetDescription[] getTargetDescriptions() {
            if (this == HOUSING && targetDescriptions == null) {
                this.targetDescriptions = new TargetDescription[Category.values().length];
                for (Category cat : Category.values()) {
                    if (cat.isHousing()) {
                        targetDescriptions[cat.ordinal()] = new TargetDescription("Amount of houses that need to be build for type: ", 0,
                                -Double.MAX_VALUE, Double.MAX_VALUE, UnitType.NONE);
                    } else {
                        targetDescriptions[cat.ordinal()] = new TargetDescription("Amount of floorspace that needs to be build for type: ",
                                0, -Double.MAX_VALUE, Double.MAX_VALUE, UnitType.SURFACE);
                    }
                    targetDescriptions[cat.ordinal()].addROTermAsDescriptionAddition(cat.getLocalisedTerm());
                }
            }
            return targetDescriptions;
        }

        @Override
        public boolean isGlobal() {
            return false;
        }

        @Override
        public boolean isSingleInstance() {
            return isSingleInstance;
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = 8232308446109929627L;

    public static String getStrippedIndicatorName(PersonalIndicator indicator) {
        String name = indicator.getName();
        Stakeholder stakeholder = indicator.getStakeholder();
        if (stakeholder != null) {
            name = name.replace(stakeholder.getName(), StringUtils.EMPTY);
        }
        return name;
    }

    @XMLValue
    private PersonalIndicatorType type = null;

    @XMLValue
    @ItemIDField("STAKEHOLDERS")
    private Integer stakeholderID = Item.NONE;

    public final Stakeholder getStakeholder() {
        return this.getItem(MapLink.STAKEHOLDERS, getStakeholderID());
    }

    public final Integer getStakeholderID() {
        return this.stakeholderID;
    }

    @Override
    public TypeInterface getType() {
        return this.type;
    }

    public void setStakeholderID(Integer stakeholderID) {
        this.stakeholderID = stakeholderID;
    }

    public void setType(PersonalIndicatorType type) {
        this.type = type;
    }

    @Override
    public String validated(boolean startNewGame) {
        if (this.getStakeholder() == null) {
            return "Failed to load Indicator: " + this.getName() + " it should have a valid stakeholder linked.";
        }
        return super.validated(startNewGame);
    }
}
