/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.net.serializable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import nl.tytech.util.logger.TLogger;

/**
 *
 * ArrayList with a version number that can be updated.
 *
 * @author Maxim Knepfle
 */
public class AssetList implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8413816431256469466L;

    private int version = 0;
    private String[] checksums = new String[0];
    private String[] paths = new String[0];

    private transient Map<String, String> localMap = null;

    public AssetList() {

    }

    public AssetList(int version, String[] paths, String[] checksums) {

        if (paths.length == checksums.length) {
            this.paths = paths;
            this.checksums = checksums;
            this.version = version;
        } else {
            TLogger.severe("Invalid checksum asset list!");
        }
    }

    public void addAsset(String path, String checksum) {

        String[] oldPaths = paths;
        paths = new String[oldPaths.length + 1];
        System.arraycopy(oldPaths, 0, paths, 0, oldPaths.length);
        paths[paths.length - 1] = path;

        String[] oldChecksums = checksums;
        checksums = new String[oldChecksums.length + 1];
        System.arraycopy(oldChecksums, 0, checksums, 0, oldChecksums.length);
        checksums[checksums.length - 1] = checksum;

        version++;
    }

    /**
     * Convert object to map
     * @return
     */
    public Map<String, String> createLocalMap() {

        if (localMap == null) {
            localMap = new HashMap<>();
            for (int i = 0; i < paths.length; i++) {
                localMap.put(paths[i], checksums[i]);
            }
        }
        return localMap;
    }

    public String[] getChecksums() {
        return checksums;
    }

    public String[] getPaths() {
        return paths;
    }

    public int getVersion() {
        return version;
    }

    public void setChecksums(String[] checksums) {
        this.checksums = checksums;
    }

    public void setPaths(String[] paths) {
        this.paths = paths;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
