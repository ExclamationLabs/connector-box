package com.exclamationlabs.connid.box.testutil;

import com.box.sdk.BoxAPIRequest;
import com.box.sdk.BoxAPIResponse;
import com.box.sdk.BoxAPIResponseException;
import com.box.sdk.BoxJSONResponse;
import com.eclipsesource.json.JsonObject;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test utilities.
 *
 * @author Hiroyuki Wada
 */
public class TestUtils {

    public static JsonObject toJsonObject(BoxAPIRequest request) {
        try {
            request.getBody().reset();
            return JsonObject.readFrom(new BufferedReader(new InputStreamReader(request.getBody(), "UTF-8")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getJsonAttr(BoxAPIRequest request, String attrName) {
        JsonObject json = toJsonObject(request);
        if (json.get(attrName) == null) {
            return null;
        }
        return json.get(attrName).asString();
    }

    public static JsonObject getJsonObject(BoxAPIRequest request, String attrName) {
        JsonObject json = toJsonObject(request);
        if (json.get(attrName) == null) {
            return null;
        }
        return json.get(attrName).asObject();
    }

    public static BoxAPIResponse created(String path) {
        return new BoxJSONResponse(201, new TreeMap(String.CASE_INSENSITIVE_ORDER), readJSONFile(path));
    }

    public static BoxAPIResponse ok(String path) {
        return new BoxJSONResponse(200, new TreeMap(String.CASE_INSENSITIVE_ORDER), readJSONFile(path));
    }

    public static BoxAPIResponse noContent() {
        return new BoxAPIResponse(204, new TreeMap(String.CASE_INSENSITIVE_ORDER));
    }

    public static JsonObject readJSONFile(String path) {
        InputStream in = MockBoxAPIHelper.class.getResourceAsStream("/" + path);
        if (in == null) {
            fail(path + " is not found.");
        }
        try {
            JsonObject json = JsonObject.readFrom(new BufferedReader(new InputStreamReader(in, "UTF-8")));
            return json;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String enc(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String dec(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> parseQuery(BoxAPIRequest request) {
        Map<String, String> map = new HashMap<>();
        String[] params = request.getUrl().getQuery().split("&");
        for (String param : params) {
            String[] kv = param.split("=");
            map.put(dec(kv[0]), dec(kv[1]));
        }
        return map;
    }

    public static Set<String> parseFields(String fields) {
        String[] keys = fields.split(",");
        return Arrays.stream(keys).collect(Collectors.toSet());
    }

    public static Set<String> mergeFields(String[]... fields) {
        return Stream.of(fields)
                .flatMap(Arrays::stream)
                .map(s -> s.split("\\.")[0])
                .collect(Collectors.toSet());
    }

    public static BoxAPIResponseException notFound() {
        MockBoxJSONResponse response = new MockBoxJSONResponse(404,
                JsonObject.readFrom("{\"code\":\"not_found\"}"));
        // TODO: set real API message
        BoxAPIResponseException e = new BoxAPIResponseException("not_found", response);
        return e;
    }

    public static BoxAPIResponseException conflict() {
        MockBoxJSONResponse response = new MockBoxJSONResponse(409,
                JsonObject.readFrom("{\"code\":\"conflict\"}"));
        // TODO: set real API message
        BoxAPIResponseException e = new BoxAPIResponseException("A resource with this value already exists", response);
        return e;
    }

    public static BoxAPIResponseException userLoginAlreadyUsed() {
        MockBoxJSONResponse response = new MockBoxJSONResponse(409,
                JsonObject.readFrom("{\"code\":\"user_login_already_used\"}"));
        // TODO: set real API message
        BoxAPIResponseException e = new BoxAPIResponseException("User with the specified login already exists", response);
        return e;
    }

    public static BoxAPIResponseException internalServerError() {
        MockBoxJSONResponse response = new MockBoxJSONResponse(500,
                JsonObject.readFrom("{\"code\":\"internal_server_error\"}"));
        BoxAPIResponseException e = new BoxAPIResponseException("Internal Server Error", response);
        return e;
    }

    static class MockBoxJSONResponse extends BoxAPIResponse {
        private final JsonObject body;

        MockBoxJSONResponse(int code, JsonObject body) {
            super(code, new TreeMap(String.CASE_INSENSITIVE_ORDER));
            this.body = body;
        }

        @Override
        protected String bodyToString() {
            return body.toString();
        }
    }
}
