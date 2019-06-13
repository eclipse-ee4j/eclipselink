package org.eclipse.persistence.internal.oxm.util;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.function.BiFunction;

import org.eclipse.persistence.internal.oxm.Namespace;

import static org.eclipse.persistence.internal.oxm.util.VectorUtils.emptyVector;
import static org.eclipse.persistence.internal.oxm.util.VectorUtils.unmodifiableVector;

public class NamespaceResolverStorage extends LinkedHashMap<String, String> {
    private static final long serialVersionUID = -4697397620139076774L;
    private transient Vector namespaces = emptyVector();
    private transient boolean modified = false;

    public NamespaceResolverStorage() {
        super();
    }

    public NamespaceResolverStorage(int initialCapacity) {
        super(initialCapacity);
    }

    private void readObject(java.io.ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        namespaces = emptyVector();
        modified = true;
    }

    @Override
    public String put(String key, String value) {
        String response = super.put(key, value);
        setModified();
        return response;
    }

    private void setModified() {
        setModified(true);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        super.putAll(m);
        setModified();
    }

    @Override
    public String remove(Object key) {
        if (containsKey(key)) {
            setModified();
        }

        return super.remove(key);
    }

    @Override
    public String putIfAbsent(String key, String value) {
        String response = super.putIfAbsent(key, value);
        setModified();
        return response;
    }

    @Override
    public boolean remove(Object key, Object value) {
        boolean response = super.remove(key, value);
        if (response) {
            setModified();
        }
        return response;
    }

    @Override
    public boolean replace(String key, String oldValue, String newValue) {
        boolean response = super.replace(key, oldValue, newValue);
        if (response) {
            setModified(response);
        }
        return response;
    }

    @Override
    public String replace(String key, String value) {
        String response = super.replace(key, value);
        setModified();
        return response;
    }

    public Vector getNamespaces() {
        if (isModified()) {
            namespaces = buildNamespacesUnmodifiable();
            setModified(false);
        }
        return namespaces;
    }

    public void setNamespaces(Vector namespaces) {
        super.clear();
        for (Namespace namespace : (Vector<Namespace>) namespaces) {
            if ((namespace.getPrefix() != null) && (namespace.getNamespaceURI() != null)) {
                super.put(namespace.getPrefix(), namespace.getNamespaceURI());
            }
        }
        setModified();
    }

    private boolean isModified() {
        return modified;
    }

    private void setModified(boolean modified) {
        this.modified = modified;
    }

    private Vector buildNamespacesUnmodifiable() {
        Vector names = new Vector(size());
        for (Map.Entry<String, String> entry : entrySet()) {
            Namespace namespace = new Namespace(entry.getKey(), entry.getValue());
            names.addElement(namespace);
        }
        return unmodifiableVector(names);
    }

    /**
     * Unmodifiable set of keys
     */
    @Override
    public Set<String> keySet() {
        return Collections.unmodifiableSet(super.keySet());
    }

    /**
     * Unmodifiable collection of values
     */
    @Override
    public Collection<String> values() {
        return Collections.unmodifiableCollection(super.values());
    }

    /**
     * Unmodifiable set of entries
     */
    @Override
    public Set<Map.Entry<String, String>> entrySet() {
        return Collections.unmodifiableSet(super.entrySet());
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super String, ? extends String> function) {
        super.replaceAll(function);
        setModified();
    }
}
