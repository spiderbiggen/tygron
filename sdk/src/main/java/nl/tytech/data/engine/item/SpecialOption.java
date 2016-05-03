/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import java.util.ArrayList;
import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.EnumOrderedItem;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;
import nl.tytech.data.engine.other.Action;
import nl.tytech.util.StringUtils;

/**
 * SpecialOption
 *
 * SpecialOption's enum wrapped in an item.
 *
 * @author Maxim Knepfle
 */
public class SpecialOption extends EnumOrderedItem<SpecialOption.Type> implements Action {

    public enum Group {

        LAND("category_buyland.png", ClientTerms.BUY_LAND),

        SCULPTING("category_waterway.png", ClientTerms.LANDSCAPE),

        FINANCE("category_finance.png", ClientTerms.FINANCE),

        DEMOLISH("category_bulldozer.png", ClientTerms.DEMOLISH),

        INBOX("category_communication.png", ClientTerms.INBOX);

        private String iconName;
        private ClientTerms clientWord;

        private Group(String iconName, ClientTerms clientWord) {
            this.iconName = iconName;
            this.clientWord = clientWord;
        }

        public String getIconName() {
            return iconName;
        }

        public ClientTerms getLocalisedTerm() {
            return clientWord;
        }
    }

    public enum Type {

        BUY_LAND(Group.LAND, true),

        SELL_LAND(Group.LAND, true),

        DEMOLISH(Group.DEMOLISH),

        DEMOLISH_VACANT(Group.DEMOLISH),

        REVERT(Group.DEMOLISH),

        DEMOLISH_UNDERGROUND(Group.DEMOLISH),

        RAISE_LAND(Group.SCULPTING),

        SHOW_BUDGETPANEL(Group.FINANCE, true, false),

        SHOW_MEASUREPROPOSAL_PANEL(Group.FINANCE, false, false),

        SHOW_MONEY_TRANSFER_PANEL(Group.FINANCE, true, false),

        SHOW_SUBSIDY_PANEL(Group.FINANCE, true, false),

        INBOX(Group.INBOX, true, false),

        SHOW_LOAN_PANEL(Group.FINANCE, true, false),

        LOWER_LAND(Group.SCULPTING);

        private boolean buildable = true;
        private boolean requiresSelection = true;
        private boolean showSpecialPanel = false;
        private Group group;

        private Type() {

        }

        private Type(Group group) {
            this.group = group;
        }

        private Type(Group group, boolean showSpecialPanel) {
            this.group = group;
            this.showSpecialPanel = showSpecialPanel;
        }

        private Type(Group group, boolean buildable, boolean requiresSelection) {
            this.group = group;
            this.buildable = buildable;
            this.requiresSelection = requiresSelection;
            this.showSpecialPanel = true;
        }

        public Group getGroup() {
            return group;
        }

        public boolean isBuildable() {
            return buildable;
        }

        /**
         * When true this special option demolishes something
         * @return
         */
        public boolean isDemolisher() {
            return this.getGroup() == Group.DEMOLISH;
        }

        public boolean isSelectionRequired() {
            return requiresSelection;
        }

        public boolean isShowSpecialPanel() {
            return showSpecialPanel;
        }

        public boolean isTypeOf(Action option) {
            if (option instanceof SpecialOption) {
                SpecialOption special = (SpecialOption) option;
                return special.getType() == this;
            }
            return false;
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = 7100716881313651171L;

    public static final String GUI_IMAGES_ACTION_ICONS = "Gui/Images/Actions/Icons/";

    @XMLValue
    private String name = StringUtils.EMPTY;

    @XMLValue
    private String description = StringUtils.EMPTY;

    @XMLValue
    @ListOfClass(Stakeholder.Type.class)
    private ArrayList<Stakeholder.Type> defaults = new ArrayList<>();

    @AssetDirectory(GUI_IMAGES_ACTION_ICONS)
    @XMLValue
    private String imageName = DEFAULT_IMAGE;

    @Override
    public double getConstructionTimeInMonths() {
        if (this.getType().isDemolisher()) {
            return Function.DEFAULT_DEMOLISH_TIME_IN_MONTHS;
        }
        return 0;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Type[] getEnumValues() {
        return Type.values();
    }

    public Group getGroup() {
        return this.getType().getGroup();
    }

    @Override
    public String getImageLocation() {
        if (!StringUtils.containsData(imageName)) {
            return StringUtils.EMPTY;
        }
        return GUI_IMAGES_ACTION_ICONS + imageName;
    }

    @Override
    public MapLink getMapLink() {
        return MapLink.SPECIAL_OPTIONS;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isBuildable() {

        // Maxim: Special special case for my friend the Proposal
        if (this.getType() == Type.SHOW_MEASUREPROPOSAL_PANEL) {
            Setting measureProposalSetting = this.getItem(MapLink.SETTINGS, Setting.Type.SHOW_MEASURE_PROPOSAL);
            return measureProposalSetting.getBooleanValue();
        }
        return this.getType().isBuildable();
    }

    public boolean isDefaultOption(Stakeholder stakeholder) {
        return defaults.contains(stakeholder.getType());
    }

    @Override
    public boolean isFixedLocation() {
        return false;
    }

    public boolean isSelectionRequired() {
        return this.getType().isSelectionRequired();
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
