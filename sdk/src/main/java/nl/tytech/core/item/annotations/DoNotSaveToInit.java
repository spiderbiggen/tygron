/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.item.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import nl.tytech.util.StringUtils;

/**
 * DoNotSaveToInit
 * <p>
 * This interface defines that a field is updated by the game editor at runtime, e.g. indicator scores. These values are not saved to the
 * INIT xml files.
 * <p>
 * 
 * 
 * @author Maxim Knepfle
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface DoNotSaveToInit {

    /**
     * When given a value this is the ID's of the items that are runtime changed, others are ignored
     * 
     * @return
     */
    String value() default StringUtils.EMPTY;

}
