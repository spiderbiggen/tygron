/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.util;

import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import nl.tytech.util.RestUtils.BadRequestType;
import nl.tytech.util.RestUtils.ErrorMessage;
import nl.tytech.util.logger.TLogger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

/**
 * Manager class to do all kinds of basic REST calls to web servers.
 * @author Maxim Knepfle
 *
 */
public class RestManager {

    /**
     * Server query format e.g. ?f=JSON
     */
    public enum Format {

        /**
         * HTML formatted data
         */
        HTML(MediaType.TEXT_HTML),

        /**
         * Plain XML, return as String
         */
        XML(MediaType.APPLICATION_XML),

        /**
         * Plain text JSON
         */
        JSON(MediaType.APPLICATION_JSON),

        /**
         * Typed text JSON
         */
        TJSON(MediaType.APPLICATION_JSON),

        /**
         * Binary zipped JSON
         */
        ZIPJSON(MediaType.APPLICATION_OCTET_STREAM),

        /**
         * Binary zipped Typed JSON
         */
        ZIPTJSON(MediaType.APPLICATION_OCTET_STREAM),

        /**
         * Zipped Assets binary data
         */
        ZIPBINARY(MediaType.APPLICATION_OCTET_STREAM);

        public final static String QUERY = "f";

        private final String mediaType;

        private Format(String mediaType) {
            this.mediaType = mediaType;
        }

        /**
         * Associated media type
         */
        public String getMediaType() {
            return mediaType;
        }

        public boolean isPrettified() {
            return this == TJSON || this == JSON || this == HTML;
        }

        public boolean isTyped() {
            return this == TJSON || this == ZIPTJSON;
        }
    }

    /**
     * Default supported http methods.
     *
     */
    private enum HttpMethod {
        GET, POST, PUT, DELETE;
    }

    public static class ResponseException extends Exception {

        private static final long serialVersionUID = -2216980807605465675L;

        public static ResponseException create(int statusCode, String message) {

            try {
                // try this first, otherwise use fallback
                ErrorMessage error = RestUtils.readJsonString(Format.JSON, message, ErrorMessage.class);
                if (error != null) {
                    return new ResponseException(statusCode, error);
                }
            } catch (Exception e) {
            }

            return new ResponseException(statusCode, message);
        }

        private BadRequestType type = null;

        private int statusCode;

        public ResponseException(BadRequestType type, String message) {
            super(message);
            this.statusCode = Response.Status.BAD_REQUEST.getStatusCode();
            this.type = type;
        }

        public ResponseException(int statusCode, ErrorMessage error) {
            super(error.message);
            this.statusCode = statusCode;
            this.type = error.type;
        }

        public ResponseException(int statusCode, String message) {
            super(message);
            this.statusCode = statusCode;
            try {
                this.type = BadRequestType.valueOf(message);
            } catch (Exception e) {
            }
        }

        public BadRequestType getExceptionType() {
            return type;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }

    private static class SingletonHolder {
        private static final RestManager INSTANCE = new RestManager();
    }

    private final static String ENCODING = "UTF-8";

    /**
     * Default timeout to connect
     */
    public final static int DEFAULT_CONNECT_TIMOUT = 60 * 1000;

    /**
     * Default timeout to read the contents
     */
    public final static int DEFAULT_READ_TIMOUT = 5 * 60 * 1000;

    public static void delete(String serverURL, String restPath) throws ResponseException {
        SingletonHolder.INSTANCE._action(serverURL, restPath, null, null, null, Format.JSON, HttpMethod.DELETE);
    }

    public static <T> T get(String serverURL, String restPath, Class<T> responseClass) throws ResponseException {
        return get(serverURL, restPath, null, responseClass);
    }

    public static <T> T get(String serverURL, String restPath, String[] params, Class<T> responseClass) throws ResponseException {
        return get(serverURL, restPath, params, responseClass, Format.JSON);
    }

    public static <T> T get(String serverURL, String restPath, String[] params, Class<T> responseClass, Format format)
            throws ResponseException {
        return SingletonHolder.INSTANCE._action(serverURL, restPath, params, null, responseClass, format, HttpMethod.GET);
    }

    public static URL getWebTargetURL(String serverURL, String path, String[] params) {
        return SingletonHolder.INSTANCE._getWebTargetURL(serverURL, path, params);
    }

    public static <T> T jsonPut(String serverURL, String restPath, Object content) throws ResponseException {
        return SingletonHolder.INSTANCE._action(serverURL, restPath, null, content, null, Format.JSON, HttpMethod.PUT);
    }

    public static <T> T post(String serverURL, String restPath, Object content) throws ResponseException {
        return post(serverURL, restPath, null, content);
    }

    public static <T> T post(String serverURL, String restPath, String[] params, Object content) throws ResponseException {
        return post(serverURL, restPath, params, content, null);
    }

    public static <T> T post(String serverURL, String restPath, String[] params, Object content, Class<T> responseClass)
            throws ResponseException {
        return post(serverURL, restPath, params, content, responseClass, Format.JSON);
    }

    public static <T> T post(String serverURL, String restPath, String[] params, Object content, Class<T> responseClass, Format format)
            throws ResponseException {
        return SingletonHolder.INSTANCE._action(serverURL, restPath, params, content, responseClass, format, HttpMethod.POST);
    }

    public static <T> T postForm(String serverURL, String restPath, String[] params, String[] content, Class<T> responseClass, Format format)
            throws ResponseException {
        return SingletonHolder.INSTANCE._postForm(serverURL, restPath, params, content, responseClass, format);
    }

    public static void setHeaders(String serverURL, MultivaluedMap<String, Object> headers) {
        SingletonHolder.INSTANCE._setHeaders(serverURL, headers);
    }

    private final Client client;

    private Map<String, WebTarget> targetMap = new HashMap<>();

    private Map<String, MultivaluedMap<String, Object>> headerMap = new HashMap<>();

    private RestManager() {

        final ClientConfig clientConfig = new ClientConfig();
        // by default timeout is infinity! Set to our defaults
        clientConfig.property(ClientProperties.CONNECT_TIMEOUT, DEFAULT_CONNECT_TIMOUT);
        clientConfig.property(ClientProperties.READ_TIMEOUT, DEFAULT_READ_TIMOUT);
        client = ClientBuilder.newClient(clientConfig);
    }

    private final <T> T _action(String serverURL, String path, String[] queryParams, Object content, Class<T> responseClass, Format format,
            HttpMethod method) throws ResponseException {

        if (format == null || format == Format.HTML) {
            throw new ResponseException(BadRequestType.INVALID_JSON_CONTENT, "Invalid query fomat: " + format);
        }

        WebTarget target = getWebTarget(serverURL, path, queryParams);

        // set format in query
        target = target.queryParam(Format.QUERY, format.name());
        Builder builder = target.request(format.getMediaType());
        builder = builder.headers(this.getHeaders(serverURL));

        Response response = null;
        if (method == HttpMethod.GET) {
            response = builder.get();
        } else if (method == HttpMethod.DELETE) {
            response = builder.delete();
        } else {
            String jsonContent;
            try {
                jsonContent = RestUtils.writeJsonString(format, content);
            } catch (Exception e) {
                throw new ResponseException(BadRequestType.INVALID_JSON_CONTENT, "Invalid JSON input: " + e.getMessage());
            }
            if (method == HttpMethod.POST) {
                response = builder.post(Entity.json(jsonContent));
            } else if (method == HttpMethod.PUT) {
                response = builder.put(Entity.json(jsonContent));
            }
        }

        try {
            int status = response.getStatus();
            if (status == Response.Status.OK.getStatusCode() || status == Response.Status.CREATED.getStatusCode()) {
                if (responseClass != null) {
                    return RestUtils.readResponse(response, responseClass, format);
                } else {
                    return null;
                }
            } else if (status == Response.Status.NO_CONTENT.getStatusCode()) {
                return null;
            } else if (status == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
                throw new ResponseException(BadRequestType.INTERNAL_SERVER_ERROR, response.readEntity(String.class));
            } else {
                throw ResponseException.create(status, response.readEntity(String.class));
            }
        } finally {
            if (responseClass != GZIPInputStream.class) {
                response.close();
            }
        }
    }

    private final URL _getWebTargetURL(String serverURL, String path, String[] params) {

        try {
            WebTarget target = this.getWebTarget(serverURL, path, params);
            return target.getUri().toURL();
        } catch (Exception e) {
            TLogger.exception(e);
        }
        return null;
    }

    private final <T> T _postForm(String serverURL, String path, String[] params, String[] content, Class<T> responseClass, Format format)
            throws ResponseException {

        if (format != Format.JSON && format != Format.TJSON) {
            throw new ResponseException(BadRequestType.INVALID_JSON_CONTENT, "Invalid query fomat: " + format);
        }

        WebTarget webTarget = getWebTarget(serverURL, path, params);
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
        formData.add(Format.QUERY, format.name());

        if (content != null) {
            for (int i = 0; i < content.length; i += 2) {
                formData.add(content[i], content[i + 1]);
            }
        }

        Builder builder = webTarget.request(format.getMediaType());
        builder = builder.headers(this.getHeaders(serverURL));

        Response postResponse = builder.post(Entity.form(formData));

        try {
            int status = postResponse.getStatus();
            if (status == Response.Status.OK.getStatusCode() || status == Response.Status.CREATED.getStatusCode()) {
                if (responseClass != null) {
                    return RestUtils.readResponse(postResponse, responseClass, format);
                } else {
                    return null;
                }
            } else if (status == Response.Status.NO_CONTENT.getStatusCode()) {
                return null;
            } else {
                throw new ResponseException(status, postResponse.readEntity(String.class));
            }
        } finally {
            postResponse.close();
        }
    }

    private final synchronized void _setHeaders(String serverURL, final MultivaluedMap<String, Object> headers) {

        if (headers == null) {
            TLogger.severe("Invalid headers for server: " + serverURL);
            return;
        }
        // add user agent data
        headers.putSingle(HttpHeaders.USER_AGENT, Engine.USER_AGENT);

        Map<String, MultivaluedMap<String, Object>> newHeaderMap = new HashMap<>(headerMap);
        newHeaderMap.put(serverURL, headers);

        // replace old map in 1 call.
        this.headerMap = newHeaderMap;
    }

    private final MultivaluedMap<String, Object> getHeaders(String serverURL) {

        // always have basic headers
        if (!headerMap.containsKey(serverURL)) {
            this._setHeaders(serverURL, new MultivaluedHashMap<>());
        }
        return headerMap.get(serverURL);
    }

    private final synchronized WebTarget getWebTarget(String serverURL) {

        if (!targetMap.containsKey(serverURL)) {
            TLogger.info("Creating new JSON/REST WebTarget: " + serverURL);
            targetMap.put(serverURL, client.target(serverURL));
        }
        return targetMap.get(serverURL);
    }

    private final WebTarget getWebTarget(String serverURL, String path, String[] params) {

        WebTarget target = null;
        try {
            WebTarget webTarget = getWebTarget(serverURL);
            target = webTarget.path(path);
            if (params != null) {
                for (int i = 0; i < params.length; i += 2) {

                    /**
                     * Skip empty params
                     */
                    if (!StringUtils.containsData(params[i])) {
                        continue;
                    }
                    /**
                     * Remove spaces and make it url encoded
                     */
                    String dirtyParam = StringUtils.internalTrim(params[i + 1]);
                    String safeParam = URLEncoder.encode(dirtyParam, ENCODING);
                    target = target.queryParam(params[i], safeParam);
                }
            }
        } catch (Exception e) {
            TLogger.exception(e);
        }
        return target;
    }
}
