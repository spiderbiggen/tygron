/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.util;

import nl.tytech.data.engine.item.Building.Detail;
import nl.tytech.data.engine.item.Function;
import nl.tytech.data.engine.item.UpgradeType;
import nl.tytech.data.engine.other.ValueItem;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.CategoryValue;
import nl.tytech.data.engine.serializable.FunctionValue;

/**
 *
 * @author Frank Baars
 *
 */
public class DetailUtils {

    public final static double getBuildingDetailForM2(ValueItem valueItem, double buildingSizeM2, double floors, Detail detail,
            boolean isVacant) {

        double floorSizeM2 = buildingSizeM2 * floors;

        switch (detail) {
            case PARKING_LOTS:
                return floorSizeM2 * valueItem.getValue(CategoryValue.PARKING_LOTS_PER_M2);

            case PARKING_LOTS_DEMAND:
                return floorSizeM2 * valueItem.getValue(CategoryValue.PARKING_LOTS_DEMAND_PER_M2);

            case CONSTRUCTION_COST:
                return floorSizeM2 * valueItem.getValue(CategoryValue.CONSTRUCTION_COST_M2);

            case DEMOLISH_COST:
                double costs = valueItem.getValue(CategoryValue.DEMOLISH_COST_M2);
                if (!isVacant) {
                    // also include buyout costs when not vacant.
                    costs = costs + valueItem.getValue(CategoryValue.BUYOUT_COST_M2);
                }
                return floorSizeM2 * costs;

            case BUYOUT_COST:
                return floorSizeM2 * valueItem.getValue(CategoryValue.BUYOUT_COST_M2);

            case SELL_PRICE:
                return floorSizeM2 * valueItem.getValue(CategoryValue.SELL_PRICE_M2);

            case WATER_STORAGE_INNOVATIVE_M3:
                return buildingSizeM2 * valueItem.getValue(FunctionValue.WATER_STORAGE_M2);

            case SELLABLE_FLOORSPACE_M2:
                return floorSizeM2;

            case GREEN_M2:
                return buildingSizeM2 * valueItem.getValue(FunctionValue.GREEN_M2);

            case WATER_STORAGE_TRADITIONAL_M3:
                // building is always innovative
                return 0;

            case NUMBER_OF_HOUSES:
                double houses = 0;
                for (Category cat : valueItem.getCategories()) {
                    if (cat.isHousing()) {
                        double unitSizeM2 = valueItem.getValue(cat, CategoryValue.UNIT_SIZE_M2);
                        houses += (floorSizeM2 * valueItem.getCategoryPercentage(cat)) / unitSizeM2;
                    }
                }
                return houses;
            default:
                return 0;
        }
    }

    public static double getBuyoutUpgradedBuildingDetailForM2(Function function, double surfaceArea, double floors, Detail detail,
            boolean isVacant) {
        switch (detail) {
            case DEMOLISH_COST:
            case CONSTRUCTION_COST:
            case SELL_PRICE:
                return 0;
            default:
                return getBuildingDetailForM2(function, surfaceArea, floors, detail, isVacant);

        }
    }

    public static final double getCategoryUnits(ValueItem item, Category cat, double surfaceArea, double floors) {

        /**
         * NOTE: only houses are counted per unit size, other all go for 1 as unit size
         */
        double unitSizeM2 = cat.isHousing() ? item.getValue(cat, CategoryValue.UNIT_SIZE_M2) : 1;
        double floorSizeM2 = surfaceArea * floors;
        double catFloorSizeM2 = (floorSizeM2 * item.getCategoryPercentage(cat));
        return catFloorSizeM2 / unitSizeM2;
    }

    public final static double getConstructedBuildingDetailForM2(Function function, double surfaceArea, double floors, Detail detail,
            boolean isVacant) {
        switch (detail) {
            case DEMOLISH_COST:
            case BUYOUT_COST:
                return 0;
            default:
                return getBuildingDetailForM2(function, surfaceArea, floors, detail, isVacant);

        }
    }

    public static double getDefaultUpgradedFromBuildingDetailForM2(Function function, double surfaceArea, double floors, Detail detail,
            boolean isVacant) {
        switch (detail) {
            case DEMOLISH_COST:
            case BUYOUT_COST:
            case CONSTRUCTION_COST:
            case SELL_PRICE:
                return 0;
            default:
                return getBuildingDetailForM2(function, surfaceArea, floors, detail, isVacant);

        }
    }

    public static double getDefaultUpgradedToBuildingDetailForM2(Function function, double surfaceArea, double floors, Detail detail,
            boolean isVacant) {
        switch (detail) {
            case DEMOLISH_COST:
            case BUYOUT_COST:
            case CONSTRUCTION_COST:
            case SELL_PRICE:
            case SELLABLE_FLOORSPACE_M2:
                return 0;
            default:
                return getBuildingDetailForM2(function, surfaceArea, floors, detail, isVacant);

        }
    }

    public final static double getDemolishedBuildingDetailForM2(Function function, double surfaceArea, double floors, Detail detail,
            boolean isVacant) {
        switch (detail) {
            case CONSTRUCTION_COST:
            case SELL_PRICE:
                return 0;
            default:
                return getBuildingDetailForM2(function, surfaceArea, floors, detail, isVacant);

        }
    }

    public static double getSoldUpgradedBuildingDetailForM2(Function function, double surfaceArea, double floors, Detail detail,
            boolean isVacant) {
        switch (detail) {
            case DEMOLISH_COST:
            case BUYOUT_COST:
            case CONSTRUCTION_COST:
                return 0;
            default:
                return getBuildingDetailForM2(function, surfaceArea, floors, detail, isVacant);

        }
    }

    public static double getUpgradeDetailForM2(UpgradeType upgrade, double surfaceArea, double floors, Detail detail) {
        switch (detail) {
            case CONSTRUCTION_COST:
                return upgrade.getCosts(surfaceArea, floors);
            default:
                return 0;
        }
    }
}
