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
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Hashtable;

/**
 * dummy Context that returns a named DataSource
 */
public class TestContext implements Context {
    private DataSource dataSource;
    private Name name;

    public TestContext(Name name, DataSource dataSource) {
        this.bind(name, dataSource);
    }

    public TestContext(String name, DataSource dataSource) throws NamingException {
        this(new CompositeName(name), dataSource);
    }

    @Override
    public Object addToEnvironment(String arg1, Object arg2) {
        return null;
    }

    @Override
    public void bind(String name, Object object) throws NamingException {
        this.bind(new CompositeName(name), object);
    }

    @Override
    public void bind(Name name, Object object) {
        this.name = name;
        this.dataSource = (DataSource)object;
    }

    @Override
    public void close() {
    }

    @Override
    public String composeName(String arg1, String arg2) {
        return null;
    }

    @Override
    public Name composeName(Name arg1, Name arg2) {
        return null;
    }

    @Override
    public Context createSubcontext(String arg1) {
        return null;
    }

    @Override
    public Context createSubcontext(Name arg1) {
        return null;
    }

    @Override
    public void destroySubcontext(String arg1) {
    }

    @Override
    public void destroySubcontext(Name arg1) {
    }

    @Override
    public Hashtable<?, ?> getEnvironment() {
        return null;
    }

    @Override
    public String getNameInNamespace() {
        return null;
    }

    @Override
    public NameParser getNameParser(String arg1) {
        return null;
    }

    @Override
    public NameParser getNameParser(Name arg1) {
        return null;
    }

    @Override
    public NamingEnumeration<NameClassPair> list(String arg1) {
        return null;
    }

    @Override
    public NamingEnumeration<NameClassPair> list(Name arg1) {
        return null;
    }

    @Override
    public NamingEnumeration<Binding> listBindings(String arg1) {
        return null;
    }

    @Override
    public NamingEnumeration<Binding> listBindings(Name arg1) {
        return null;
    }

    @Override
    public Object lookup(String name) throws NamingException {
        return this.lookup(new CompositeName(name));
    }

    @Override
    public Object lookup(Name name) {
        if (name.equals(this.name)) {
            return dataSource;
        } else {
            return null;
        }
    }

    @Override
    public Object lookupLink(String arg1) {
        return null;
    }

    @Override
    public Object lookupLink(Name arg1) {
        return null;
    }

    @Override
    public void rebind(String arg1, Object arg2) {
    }

    @Override
    public void rebind(Name arg1, Object arg2) {
    }

    @Override
    public Object removeFromEnvironment(String arg1) {
        return null;
    }

    @Override
    public void rename(String arg1, String arg2) {
    }

    @Override
    public void rename(Name arg1, Name arg2) {
    }

    @Override
    public void unbind(String arg1) {
    }

    @Override
    public void unbind(Name arg1) {
    }
}
