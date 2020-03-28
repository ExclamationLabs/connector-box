package com.exclamationlabs.connid.box;

import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.Uid;

public class BoxFilter {
    public final Uid uid;
    public final Name name;

    private BoxFilter(Uid uid) {
        this.uid = uid;
        this.name = null;
    }

    private BoxFilter(Name name) {
        this.uid = null;
        this.name = name;
    }

    public static BoxFilter By(Uid uid) {
        return new BoxFilter(uid);
    }

    public static BoxFilter By(Name name) {
        return new BoxFilter(name);
    }

    public boolean isByUid() {
        return uid != null;
    }

    public boolean isByName() {
        return name != null;
    }
}
