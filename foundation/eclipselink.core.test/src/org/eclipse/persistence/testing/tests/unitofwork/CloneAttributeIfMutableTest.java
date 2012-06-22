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
 *     dminsky - initial API and implementation
 ******************************************************************************/ 
package org.eclipse.persistence.testing.tests.unitofwork;

import java.util.Vector;

import junit.framework.Assert;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * EL Bug 252047 - Mutable attributes are not cloned when isMutable is enabled on a Direct Mapping
 * Regression test to ensure that when an AbstractDirectMapping subclass is set as mutable
 * (setMutable(false)), an attribute of type byte[], java.util.Date or java.util.Calendar (and
 * their subclasses) is cloned before being returned. This will occur as a result of creating 
 * a clone of the mapped object which references the mutable attribute.
 * @author dminsky
 */
public class CloneAttributeIfMutableTest extends TestCase {

    protected MutableAttributeObject original;
    protected MutableAttributeObject clone;

    public CloneAttributeIfMutableTest() {
        super();
        setDescription("Test that cloning an object also clones its attributes if their mappings are set as mutable");
    }
    
    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        original = MutableAttributeObject.example1();
        clone = (MutableAttributeObject) uow.registerObject(original);
        uow.release();
    }
    
    public void verify() {
        Vector errors = new Vector();
        // extra check to pre-verify that: clone != original
        try {
            Assert.assertNotSame("1. original == clone", 
                original, 
                clone);
        } catch (Throwable t) {
            errors.add(t);
        }
        
        // byte[]
        try {
            Assert.assertNotSame("2. original.getByteArray() == clone.getByteArray()", 
                original.getByteArray(), 
                clone.getByteArray());
        } catch (Throwable t) {
            errors.add(t);
        }
        
        // java.util.Calendar
        try {
            Assert.assertNotSame("3. original.getUtilCalendar() == clone.getUtilCalendar()", 
                original.getUtilCalendar(), 
                clone.getUtilCalendar());
        } catch (Throwable t) {
            errors.add(t);
        }
            
        // java.util.Date
        try {
            Assert.assertNotSame("4. original.getUtilDate() == clone.getUtilDate()", 
                original.getUtilDate(), 
                clone.getUtilDate());
        } catch (Throwable t) {
            errors.add(t);
        }
        
        // java.sql.Date
        try {
            Assert.assertNotSame("5. original.getSqlDate() == clone.getSqlDate()", 
                original.getSqlDate(), 
                clone.getSqlDate());
        } catch (Throwable t) {
            errors.add(t);
        }
        
        // java.sql.Time
        try {
            Assert.assertNotSame("6. original.getSqlTime() == clone.getSqlTime()", 
                original.getSqlTime(), 
                clone.getSqlTime());
        } catch (Throwable t) {
            errors.add(t);
        }
        
        // java.sql.Timestamp
        try {
            Assert.assertNotSame("7. original.getSqlTimestamp() == clone.getSqlTimestamp()", 
                original.getSqlTimestamp(), 
                clone.getSqlTimestamp());
        } catch (Throwable t) {
            errors.add(t);
        }
        
        try {
            // Custom java.util.Date subclass
            Assert.assertNotSame("8. original.getDateSubclass() == clone.getDateSubclass()", 
                original.getDateSubclass(), 
                clone.getDateSubclass());
        } catch (Throwable t) {
            errors.add(t);
        }
        
        // error handling for all failed assertions
        if (errors.isEmpty() == false) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("The following assertions failed:");
            buffer.append(Helper.cr());
            for (int i = 0; i < errors.size(); i++) {
                Throwable t = (Throwable)errors.get(i);
                buffer.append(String.valueOf(t));
                buffer.append(Helper.cr());
            }
            throw new TestErrorException(buffer.toString());
        }
    }
    
}
