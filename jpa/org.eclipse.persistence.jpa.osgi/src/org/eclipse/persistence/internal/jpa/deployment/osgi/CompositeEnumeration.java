/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors: 
 *     ssmith
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.deployment.osgi;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * CompositeEnumeration is, as the name implies, a Composite of Enumerations.
 * It provides a way to iterate across a set of Enumerations as if they are
 * a single Enumeration.  The order of the elements returned reflects the order
 * of the Enumerations in the Vector passed to the constructor.
 * 
 * @author shsmith
 *
 * @see org.eclipse.persistence.internal.jpa.deployment.osgi.CompositeClassLoader
 * 
 * @param <T>
 */
public class CompositeEnumeration<T> implements Enumeration<T> {
    
    private Enumeration<T> currentEnumeration;
    private Iterator<Enumeration<T>> enumerationIterator;
    
    public CompositeEnumeration(List<Enumeration<T>> enumerations) {
        this.enumerationIterator = enumerations.iterator();
        if (this.enumerationIterator.hasNext()) {
            this.currentEnumeration = this.enumerationIterator.next();
        } else {
            this.currentEnumeration = new NullObjectEnumeration();
        }
    }

    public boolean hasMoreElements() {
        boolean hasMoreElements = this.currentEnumeration.hasMoreElements();
        if (hasMoreElements) {
            return true;
        } else {
            if (this.enumerationIterator.hasNext()) {
                this.currentEnumeration = this.enumerationIterator.next();
                return this.hasMoreElements();
            } else {
                return false;
            }
        }
    }

    public T nextElement() {
        return this.currentEnumeration.nextElement();
    }

    private final class NullObjectEnumeration implements Enumeration<T> {
        public boolean hasMoreElements() {
            return false;
        }

        public T nextElement() {
            throw new NoSuchElementException();
        }
    }


}
