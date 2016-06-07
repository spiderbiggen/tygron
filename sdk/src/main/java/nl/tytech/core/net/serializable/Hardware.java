/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.serializable;

import java.io.Serializable;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.StringUtils;

/**
 * Hardware defintion
 *
 * @author Maxim Knepfle
 *
 */
public class Hardware implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -45482385613618096L;

    private String cpu = StringUtils.EMPTY;

    private String video = StringUtils.EMPTY;

    private String mem = StringUtils.EMPTY;

    private String mac = StringUtils.EMPTY;

    private String name = StringUtils.EMPTY;

    @Deprecated
    private boolean suggest64Bit = false;

    private long lastLogin = Item.NONE;

    public String getCpu() {
        return cpu;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public String getMac() {
        return mac;
    }

    public String getMem() {
        return mem;
    }

    public String getName() {
        return name;
    }

    public String getVideo() {
        return video;
    }

    @Deprecated
    public boolean isSuggest64Bit() {
        return suggest64Bit;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setMem(String mem) {
        this.mem = mem;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Deprecated
    public void setSuggest64Bit(boolean suggest64Bit) {
        this.suggest64Bit = suggest64Bit;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    @Override
    public String toString() {
        return this.name + " (" + this.mac + "):\n" + this.cpu + "\n" + this.video + "\n" + this.mem + "\nLast used (by any user): "
                + StringUtils.dateToHumanString(this.lastLogin, true);
    }
}
