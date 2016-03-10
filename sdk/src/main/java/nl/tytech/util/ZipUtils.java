/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import nl.tytech.util.logger.TLogger;

/**
 * ZipUtils
 * <p>
 * ZipUtils can compress and decompress objects into an byte[]
 * <p>
 *
 * @author Maxim Knepfle
 */
public class ZipUtils {

    /**
     * Level for compression, between 0 and 9
     */
    public final static int COMPRESSION_LEVEL = 6;

    // private static boolean useLZMA = false;

    /**
     * Tweaked optimal size for speed in buffer
     */
    private static final int BUFFER_SIZE = 8 * 1024;

    public static void addToZip(ZipOutputStream zos, String rootDirectoryName, String fileName) throws IOException {

        /**
         * Get the files for the given directory
         */
        byte[] buf = new byte[BUFFER_SIZE];
        File d = new File(rootDirectoryName + fileName);

        if (d.isDirectory()) {
            for (File file : d.listFiles()) {
                if (file.isDirectory()) {
                    addToZip(zos, rootDirectoryName, fileName + file.getName() + File.separator);
                    continue;
                }

                FileInputStream fis = new FileInputStream(file.getAbsolutePath());
                ZipEntry entry = new ZipEntry(fileName + file.getName());
                zos.putNextEntry(entry);
                int len;
                while ((len = fis.read(buf)) > 0) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
        } else {

            FileInputStream fis = new FileInputStream(d.getAbsolutePath());
            ZipEntry entry = new ZipEntry(d.getName());
            zos.putNextEntry(entry);
            int len;
            while ((len = fis.read(buf)) > 0) {
                zos.write(buf, 0, len);
            }
            zos.closeEntry();
            fis.close();
        }
    }

    /**
     * Compress object into byte[] using GZIP.
     * @param data
     * @return
     */
    public static byte[] compressObject(Object data) {
        if (data == null) {
            return null;
        }

        try {
            ByteArrayOutputStream fos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(fos) {
                {
                    def.setLevel(COMPRESSION_LEVEL);
                }
            });
            oos.writeObject(data);
            oos.flush();
            oos.close();
            fos.close();
            return fos.toByteArray();
        } catch (Exception e) {
            TLogger.exception(e);
        }
        return null;
    }

    public static void copy(InputStream input, OutputStream output) throws IOException {
        int bytesRead;
        byte[] data = new byte[BUFFER_SIZE];
        while ((bytesRead = input.read(data)) != -1) {
            output.write(data, 0, bytesRead);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T decompressObject(InputStream inputStream) {

        if (inputStream == null) {
            return null;
        }

        try {
            ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(inputStream));
            Object result = ois.readObject();
            ois.close();
            inputStream.close();
            return (T) result;
        } catch (Exception e) {
            TLogger.exception(e);
        }
        return null;
    }

    public static byte[] fromByteStream(InputStream inputStream, boolean compressed) {

        if (inputStream == null) {
            return null;
        }

        try {
            ByteArrayOutputStream fos = new ByteArrayOutputStream();
            InputStream stream;
            if (compressed) {
                stream = new GZIPInputStream(inputStream);
            } else {
                stream = inputStream;
            }

            int nRead;
            byte[] data = new byte[BUFFER_SIZE];
            while ((nRead = stream.read(data, 0, data.length)) != -1) {
                fos.write(data, 0, nRead);
            }
            fos.close();
            stream.close();
            return fos.toByteArray();
        } catch (Exception e) {
            TLogger.exception(e);
        }
        return null;
    }

    public static void toByteStream(InputStream inputStream, OutputStream outputStream, boolean compressed) {

        if (outputStream == null || inputStream == null) {
            return;
        }

        try {
            OutputStream stream;
            if (compressed) {
                stream = new GZIPOutputStream(outputStream) {
                    {
                        def.setLevel(COMPRESSION_LEVEL);
                    }
                };
            } else {
                stream = outputStream;
            }

            int nRead;
            byte[] data = new byte[BUFFER_SIZE];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                stream.write(data, 0, nRead);
            }
            stream.flush();
            stream.close();
            inputStream.close();
        } catch (Exception e) {
            TLogger.exception(e);
        }
    }
}
