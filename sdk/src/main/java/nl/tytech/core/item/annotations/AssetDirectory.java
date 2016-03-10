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
 * AssetDirectory
 * <p>
 * AssetDirectory defines the asset directory for this String field containing the file's name.
 * <p>
 * 
 * 
 * @author Maxim Knepfle
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface AssetDirectory {

    public enum Type {
        IMAGE, VIDEO, MODEL, SOUND
    };

    boolean allowEmpty() default false;

    Type type() default Type.IMAGE;

    /**
     * Returns the default Asset Directory
     * 
     * @return
     */
    String value() default StringUtils.EMPTY;
}
