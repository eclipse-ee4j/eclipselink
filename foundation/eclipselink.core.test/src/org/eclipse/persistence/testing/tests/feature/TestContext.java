/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.feature;

import java.util.*;
import javax.naming.*;
import javax.sql.*;

/**
 * dummy Context that returns a named DataSource
 */
public class TestContext implements Context {
    private DataSource dataSource;
    private Name name;

    public TestContext(Name name, DataSource dataSource) throws NamingException {
        this.bind(name, dataSource);
    }

    public TestContext(String name, DataSource dataSource) throws InvalidNameException, NamingException {
        this(new CompositeName(name), dataSource);
    }

    public Object addToEnvironment(String arg1, Object arg2) throws NamingException {
        return null;
    }

    public void bind(String name, Object object) throws NamingException {
        this.bind(new CompositeName(name), object);
    }

    public void bind(Name name, Object object) throws NamingException {
        this.name = name;
        this.dataSource = (DataSource)object;
    }

    public void close() throws NamingException {
    }

    public String composeName(String arg1, String arg2) throws NamingException {
        return null;
    }

    public Name composeName(Name arg1, Name arg2) throws NamingException {
        return null;
    }

    public Context createSubcontext(String arg1) throws NamingException {
        return null;
    }

    public Context createSubcontext(Name arg1) throws NamingException {
        return null;
    }

    public void destroySubcontext(String arg1) throws NamingException {
    }

    public void destroySubcontext(Name arg1) throws NamingException {
    }

    public Hashtable getEnvironment() throws NamingException {
        return null;
    }

    public String getNameInNamespace() throws NamingException {
        return null;
    }

    public NameParser getNameParser(String arg1) throws NamingException {
        return null;
    }

    public NameParser getNameParser(Name arg1) throws NamingException {
        return null;
    }

    public NamingEnumeration list(String arg1) throws NamingException {
        return null;
    }

    public NamingEnumeration list(Name arg1) throws NamingException {
        return null;
    }

    public NamingEnumeration listBindings(String arg1) throws NamingException {
        return null;
    }

    public NamingEnumeration listBindings(Name arg1) throws NamingException {
        return null;
    }

    public Object lookup(String name) throws NamingException {
        return this.lookup(new CompositeName(name));
    }

    public Object lookup(Name name) throws NamingException {
        if (name.equals(this.name)) {
            return dataSource;
        } else {
            return null;
        }
    }

    public Object lookupLink(String arg1) throws NamingException {
        return null;
    }

    public Object lookupLink(Name arg1) throws NamingException {
        return null;
    }

    public void rebind(String arg1, Object arg2) throws NamingException {
    }

    public void rebind(Name arg1, Object arg2) throws NamingException {
    }

    public Object removeFromEnvironment(String arg1) throws NamingException {
        return null;
    }

    public void rename(String arg1, String arg2) throws NamingException {
    }

    public void rename(Name arg1, Name arg2) throws NamingException {
    }

    public void unbind(String arg1) throws NamingException {
    }

    public void unbind(Name arg1) throws NamingException {
    }
}
