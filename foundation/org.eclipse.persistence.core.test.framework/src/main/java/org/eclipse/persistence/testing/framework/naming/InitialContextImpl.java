/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.framework.naming;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import java.util.Hashtable;

public class InitialContextImpl implements Context {
    Hashtable<?, ?> env;

    // Single global namespace
    static Hashtable<String, Object> stringNamespace = new Hashtable<>();
    static Hashtable<Name, Object> namespace = new Hashtable<>();

    public InitialContextImpl() {
    }

    public InitialContextImpl(Hashtable<?, ?> env) {
        this.env = env;
    }

    public Object internalLookup(Object name) {
        // Check the String namespace first
        Object obj = stringNamespace.get(name);

        // If not found then check the real namespace
        if (obj == null) {
            obj = namespace.get(name);
        }

        // If still not found then it just isn't there
        if (obj == null) {
            return null;
        }

        return obj;
    }

    /*************************************/
    /***** Supported Context API *****/
    /*************************************/
    @Override
    public Object lookup(String name) throws NamingException {
        Object obj = internalLookup(name);
        if (obj == null) {
            throw new NameNotFoundException(name);
        }
        return obj;
    }

    @Override
    public Object lookup(Name name) throws NamingException {
        Object obj = internalLookup(name);
        if (obj == null) {
            throw new NameNotFoundException(name.toString());
        }
        return obj;
    }

    @Override
    public void bind(String name, Object obj) throws NamingException {
        if (internalLookup(name) != null) {
            throw new NameAlreadyBoundException(name);
        }
        rebind(name, obj);
    }

    @Override
    public void bind(Name name, Object obj) throws NamingException {
        if (internalLookup(name) != null) {
            throw new NameAlreadyBoundException(name.toString());
        }
        rebind(name, obj);
    }

    @Override
    public void rebind(String name, Object obj) throws NamingException {
        stringNamespace.put(name, obj);
        // Bind as a Name as well
        rebind(new CompositeName(name), obj);
    }

    @Override
    public void rebind(Name name, Object obj) {
        namespace.put(name, obj);
    }

    @Override
    public Hashtable<?, ?> getEnvironment() {
        return env;
    }

    @Override
    public void close() {
    }

    /*************************************/
    /***** Not supported Context API *****/
    /*************************************/
    @Override
    public void unbind(Name name) {
        namespace.remove(name);
    }

    @Override
    public void unbind(String name) throws NamingException {
        stringNamespace.remove(name);
        // Bind as a Name as well
        unbind(new CompositeName(name));
    }

    @Override
    public void rename(Name oldName, Name newName) {
    }

    @Override
    public void rename(String oldName, String newName) {
    }

    @Override
    public NamingEnumeration<NameClassPair> list(Name name) {
        return null;
    }

    @Override
    public NamingEnumeration<NameClassPair> list(String name) {
        return null;
    }

    @Override
    public NamingEnumeration<Binding> listBindings(Name name) {
        return null;
    }

    @Override
    public NamingEnumeration<Binding> listBindings(String name) {
        return null;
    }

    @Override
    public void destroySubcontext(Name name) {
    }

    @Override
    public void destroySubcontext(String name) {
    }

    @Override
    public Context createSubcontext(Name name) {
        return null;
    }

    @Override
    public Context createSubcontext(String name) {
        return null;
    }

    @Override
    public Object lookupLink(Name name) {
        return null;
    }

    @Override
    public Object lookupLink(String name) {
        return null;
    }

    @Override
    public NameParser getNameParser(Name name) {
        return null;
    }

    @Override
    public NameParser getNameParser(String name) {
        return null;
    }

    @Override
    public Name composeName(Name name, Name prefix) {
        return null;
    }

    @Override
    public String composeName(String name, String prefix) {
        return null;
    }

    @Override
    public Object addToEnvironment(String propName, Object propVal) {
        return null;
    }

    @Override
    public Object removeFromEnvironment(String propName) {
        return null;
    }

    @Override
    public String getNameInNamespace() {
        return null;
    }
}
