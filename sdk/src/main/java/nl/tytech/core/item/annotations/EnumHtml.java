/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.item.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * EnumHtml
 * <p>
 * This interface defines that an enum contains constants whose values can be HTML.
 * <p>
 * The annotation is given a list of ordinals (Integers).
 * <p>
 * 
 * 
 * @author Marijn
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumHtml {
    int[] ordinal();
}
