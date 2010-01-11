/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.framework.naming;

import java.util.Hashtable;
import javax.naming.*;

public class InitialContextImpl implements Context {
    Hashtable env;

    // Single global namespace
    static Hashtable stringNamespace = new Hashtable();
    static Hashtable namespace = new Hashtable();

    public InitialContextImpl() {
    }

    public InitialContextImpl(Hashtable env) {
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
    public Object lookup(String name) throws NamingException {
        Object obj = internalLookup(name);
        if (obj == null) {
            throw new NameNotFoundException(name);
        }
        return obj;
    }

    public Object lookup(Name name) throws NamingException {
        Object obj = internalLookup(name);
        if (obj == null) {
            throw new NameNotFoundException(name.toString());
        }
        return obj;
    }

    public void bind(String name, Object obj) throws NamingException {
        if (internalLookup(name) != null) {
            throw new NameAlreadyBoundException(name);
        }
        rebind(name, obj);
    }

    public void bind(Name name, Object obj) throws NamingException {
        if (internalLookup(name) != null) {
            throw new NameAlreadyBoundException(name.toString());
        }
        rebind(name, obj);
    }

    public void rebind(String name, Object obj) throws NamingException {
        stringNamespace.put(name, obj);
        // Bind as a Name as well
        rebind(new CompositeName(name), obj);
    }

    public void rebind(Name name, Object obj) throws NamingException {
        namespace.put(name, obj);
    }

    public Hashtable getEnvironment() throws NamingException {
        return env;
    }

    public void close() throws NamingException {
    }

    /*************************************/
    /***** Not supported Context API *****/
    /*************************************/
    public void unbind(Name name) throws NamingException {
        namespace.remove(name);
    }

    public void unbind(String name) throws NamingException {
        stringNamespace.remove(name);
        // Bind as a Name as well
        unbind(new CompositeName(name));
    }

    public void rename(Name oldName, Name newName) throws NamingException {
    }

    public void rename(String oldName, String newName) throws NamingException {
    }

    public NamingEnumeration list(Name name) throws NamingException {
        return null;
    }

    public NamingEnumeration list(String name) throws NamingException {
        return null;
    }

    public NamingEnumeration listBindings(Name name) throws NamingException {
        return null;
    }

    public NamingEnumeration listBindings(String name) throws NamingException {
        return null;
    }

    public void destroySubcontext(Name name) throws NamingException {
    }

    public void destroySubcontext(String name) throws NamingException {
    }

    public Context createSubcontext(Name name) throws NamingException {
        return null;
    }

    public Context createSubcontext(String name) throws NamingException {
        return null;
    }

    public Object lookupLink(Name name) throws NamingException {
        return null;
    }

    public Object lookupLink(String name) throws NamingException {
        return null;
    }

    public NameParser getNameParser(Name name) throws NamingException {
        return null;
    }

    public NameParser getNameParser(String name) throws NamingException {
        return null;
    }

    public Name composeName(Name name, Name prefix) throws NamingException {
        return null;
    }

    public String composeName(String name, String prefix) throws NamingException {
        return null;
    }

    public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        return null;
    }

    public Object removeFromEnvironment(String propName) throws NamingException {
        return null;
    }

    public String getNameInNamespace() throws NamingException {
        return null;
    }
}
