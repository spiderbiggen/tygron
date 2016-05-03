/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.core.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.StringUtils;

/**
 * Word
 * <p>
 * This class keeps track of a translated word.
 * <p>
 *
 *
 * @author Maxim Knepfle
 */

public abstract class Word<E extends Enum<E>> extends EnumOrderedItem<E> {

    public interface Terms {

    }

    /**
	 *
	 */
    private static final long serialVersionUID = -4333909239136691264L;

    @XMLValue
    private String translation = StringUtils.EMPTY;

    public Word() {

    }

    @Override
    public String getDescription() {
        return toString() + ": " + this.getTranslation();
    }

    /**
     * @return the translation
     */
    public final String getTranslation() {
        return this.translation;
    }
}
