/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.util.concurrent;

import java.util.ArrayList;
import java.util.List;

/**
 * Returns a cleared local list when get() is called for each thread. *
 * 
 * @author Maxim Knepfle
 */
public class LocalThreadList<T> extends ThreadLocal<List<T>> {

    @Override
    public List<T> get() {
        List<T> list = super.get();
        list.clear();
        return list;
    }

    @Override
    public List<T> initialValue() {
        return new ArrayList<>();
    }
}
