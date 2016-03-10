/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.other;

import java.util.Collection;
import nl.tytech.data.engine.item.Function.Value;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.CategoryValue;

/**
 * ValueItem
 * <p>
 * Item containing function values
 * <p>
 *
 *
 * @author Maxim Knepfle
 */
public interface ValueItem {

    public Collection<Category> getCategories();

    public double getCategoryPercentage(Category cat);

    public double getValue(Category cat, CategoryValue val);

    public double getValue(Value key);

}
