package org.eclipse.persistence.internal.sessions.factories;

import org.eclipse.persistence.oxm.NamespaceResolver;

public class NamespaceResolverWithPrefixes extends NamespaceResolver {

    protected String primaryPrefix = null;
    protected String secondaryPrefix = null;

    public void putPrimary(String ns1, String primaryNamespace) {
        this.primaryPrefix = ns1;
        put(ns1, primaryNamespace);
    }
    public String getPrimaryPrefix() {
        return primaryPrefix;
    }
    public void putSecondary(String ns1, String secondaryNamespace) {
        this.secondaryPrefix = ns1;
        put(ns1, secondaryNamespace);
    }
    public String getSecondaryPrefix() {
        return secondaryPrefix;
    }
}
