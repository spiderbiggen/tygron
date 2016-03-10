/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import nl.tytech.util.RestManager.Format;
import nl.tytech.util.RestManager.ResponseException;
import nl.tytech.util.logger.TLogger;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Util function to correctly format json, xml stuff for REST calls, also added special serializer to handle JTS classes like polygons
 * @author Maxim Knepfle
 *
 */

public class RestUtils {

    public static enum BadRequestType {

        INVALID_SERVER_TOKEN,

        SERVER_REBOOT,

        INSUFFICIENT_RIGHTS,

        CLIENT_RELEASED,

        INVALID_JSON_CONTENT,

        INTERNAL_SERVER_ERROR,

        NO_SESSION_IN_SLOT,

        INVALID_REQUEST,

        INVALID_FORMAT,

        LOGIC_ERROR,

        OTHER;
    }

    public final static class ErrorMessage implements SkipObfuscation {

        public BadRequestType type;

        public String message;

    }

    public static JsonFactory FACTORY = new JsonFactory();

    @SuppressWarnings("unchecked")
    public static <T> T readByteStream(InputStream inputStream, Class<T> responseClass) throws Exception {

        if (responseClass != byte[].class) {
            throw new Exception("Zipped Binary can only handle byte[].class as response class. Not: " + responseClass);
        }
        return (T) ZipUtils.fromByteStream(inputStream, true);
    }

    public static <T> T readJsonFile(Format format, File file, Class<T> responseClass) throws JsonParseException, JsonMappingException,
            IOException {
        return JsonMapper.getMapper(format).readValue(file.toURI().toURL(), responseClass);
    }

    public static <T> T readJsonStream(Format format, InputStream inputStream, Class<T> responseClass) throws JsonParseException,
            JsonMappingException, IOException {

        if (inputStream.available() == 0) {
            inputStream.close();
            return null;
        }

        return JsonMapper.getMapper(format).readValue(inputStream, responseClass);
    }

    public static <T> T readJsonString(Format format, String inputString, Class<T> responseClass) throws JsonParseException,
            JsonMappingException, IOException {
        return JsonMapper.getMapper(format).readValue(inputString, responseClass);
    }

    public static <T> T readResponse(Response response, Class<T> responseClass, Format format) throws ResponseException {

        try {
            // get the data streamed
            InputStream inputStream = response.readEntity(InputStream.class);
            switch (format) {
                case XML:
                    return readXML(inputStream, responseClass);
                case JSON:
                case TJSON:
                    return readJsonStream(format, inputStream, responseClass);
                case ZIPJSON:
                case ZIPTJSON:
                    return readZipJsonStream(format, inputStream, responseClass);
                case ZIPBINARY:
                    return readByteStream(inputStream, responseClass);
                default:
                    throw new UnsupportedOperationException("Format: " + format + " is not implemented!");
            }
        } catch (Exception e) {
            throw new ResponseException(BadRequestType.INVALID_JSON_CONTENT, "Invalid JSON response: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T readXML(InputStream inputStream, Class<T> responseClass) throws Exception {

        if (responseClass != Element.class) {
            throw new Exception("XML can only handle Element.class as response class. Not: " + responseClass);
        }

        final SAXBuilder builder = new SAXBuilder();
        final Element rootElement = builder.build(inputStream).getRootElement();
        inputStream.close();
        // XML is always Element class
        return (T) rootElement;
    }

    private static <T> T readZipJsonStream(Format format, InputStream inputStream, Class<T> responseClass) throws IOException {

        if (inputStream.available() == 0) {
            inputStream.close();
            return null;
        }

        GZIPInputStream ois = new GZIPInputStream(inputStream);
        T resultObject = JsonMapper.getMapper(format).readValue(ois, responseClass);
        ois.close();
        inputStream.close();
        return resultObject;
    }

    public static void throwWebException(BadRequestType type) throws WebApplicationException {
        throwWebException(type, StringUtils.capitalizeWithSpacedUnderScores(type.name().toLowerCase()));
    }

    public static void throwWebException(BadRequestType type, String message) throws WebApplicationException {
        throwWebException(Status.BAD_REQUEST, type, message);
    }

    public static void throwWebException(Status status, BadRequestType type, String message) throws WebApplicationException {

        String reply = type.toString();
        try {
            ErrorMessage error = new ErrorMessage();
            error.type = type;
            error.message = message;
            reply = (String) RestUtils.writeObject(error, Format.JSON);
        } catch (Exception e) {
            TLogger.exception(e);
        }

        Response response = Response.status(status).entity(reply).type(MediaType.APPLICATION_JSON).build();
        throw new WebApplicationException(response);
    }

    public static String writeJsonString(Format format, Object result) throws IOException {
        return JsonMapper.getMapper(format).writeValueAsString(result);
    }

    private static byte[] writeJsonZipBytes(Format format, Object result) throws IOException {

        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        GZIPOutputStream goos = new GZIPOutputStream(fos) {
            {
                def.setLevel(ZipUtils.COMPRESSION_LEVEL);
            }
        };
        JsonMapper.getMapper(format).writeValue(goos, result);
        goos.flush();
        goos.close();
        fos.close();
        return fos.toByteArray();
    }

    public static Object writeObject(Object object, Format format) throws IOException, WebApplicationException {

        switch (format) {
            case HTML:
                return object;
            case JSON:
            case TJSON:
                return writeJsonString(format, object);
            case ZIPJSON:
            case ZIPTJSON:
                return writeJsonZipBytes(format, object);
            case ZIPBINARY:
                return ZipUtils.compressObject(object);
            default:
                throwWebException(BadRequestType.INVALID_REQUEST);
                return null;
        }
    }
}
