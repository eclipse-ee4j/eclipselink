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
package org.eclipse.persistence.testing.tests.validation;

import java.util.Vector;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;


/**
 * Bug 3214053
 * Ensure ConversionException serialize correctly.
 */
public class ExceptionSerializationTestCase extends AutoVerifyTestCase {

    protected ConversionException conversionException = null;

    public ExceptionSerializationTestCase() {
        setDescription("Ensure ConversionException and CacheSynchronizationException serialize correctly.");
    }

    public void setup() {
        conversionException = ConversionException.couldNotBeConverted(new Object(), String.class);
        Exception exception = new Exception();
        Vector errors = new Vector();
        errors.addElement(errors);
        UnitOfWorkChangeSet changeSet = new UnitOfWorkChangeSet();
    }

    public void test() {
        java.io.ByteArrayOutputStream byteOut = new java.io.ByteArrayOutputStream();
        try {
            java.io.ObjectOutputStream objectOut = new java.io.ObjectOutputStream(byteOut);
            objectOut.writeObject(conversionException);
            objectOut.flush();
        } catch (java.io.IOException exception) {
            throw new TestErrorException("Exception while serializing exceptions: " + exception.toString(), exception);
        }
        byte[] data = byteOut.toByteArray();

        java.io.ByteArrayInputStream byteIn = new java.io.ByteArrayInputStream(data);
        try {
            java.io.ObjectInputStream objectIn = new java.io.ObjectInputStream(byteIn);
            conversionException = (ConversionException)objectIn.readObject();
        } catch (Exception exception) {
            throw new TestErrorException("Exception while serializing exceptions: " + exception.toString(), exception);
        }
    }

    public void verify() {
        if (conversionException.getSourceObject() != null) {
            throw new TestErrorException("Source Object was sent by serialization and it should not be.");
        }
    }
}

