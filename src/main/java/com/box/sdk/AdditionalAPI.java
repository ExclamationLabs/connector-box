package com.box.sdk;

import java.net.URL;
import java.util.Iterator;

/**
 * @author Hiroyuki Wada
 */
public class AdditionalAPI {

    /**
     * Box SDK for Java doesn't have getAllGroupByName with fields currently.
     *
     * @param api
     * @param name
     * @param fields
     * @return
     */
    public static Iterable<BoxGroup.Info> getAllGroupsByName(final BoxAPIConnection api, String name, String... fields) {
        final QueryStringBuilder builder = new QueryStringBuilder();
        if (name != null && !name.trim().isEmpty()) {
            builder.appendParam("name", name);
            if (fields.length > 0) {
                builder.appendParam("fields", fields);
            }
            return new Iterable<BoxGroup.Info>() {
                public Iterator<BoxGroup.Info> iterator() {
                    URL url = BoxGroup.GROUPS_URL_TEMPLATE.buildWithQuery(api.getBaseURL(), builder.toString(), new Object[0]);
                    return new BoxGroupIterator(api, url);
                }
            };
        } else {
            throw new BoxAPIException("Searching groups by name requires a non NULL or non empty name");
        }
    }
}
