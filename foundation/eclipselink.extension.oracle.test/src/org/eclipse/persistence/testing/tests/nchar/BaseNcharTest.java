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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.nchar;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.platform.database.oracle.Oracle9Platform;

public abstract class BaseNcharTest extends TestCase {
    protected DatabasePlatform platformOriginal;
    protected CharNchar object;
    protected CharNchar controlObject;
    protected boolean usesStringBindingOriginal;    

    protected Oracle9Platform getOracle9Platform() throws ClassCastException {
        return (Oracle9Platform)getSession().getPlatform();
    }

    protected void setup() {
        if (!getSession().getPlatform().isOracle()) {
            throw new TestWarningException("This test case works on Oracle only");
        }
        try {
            getOracle9Platform();
        } catch (ClassCastException ex) {
            DatabasePlatform platform = getSession().getPlatform();
            try {
                getSession().getLogin().usePlatform(new org.eclipse.persistence.platform.database.oracle.Oracle9Platform());
                getDatabaseSession().logout();
                getDatabaseSession().login();
                platformOriginal = platform;
                getOracle9Platform();
            } catch (Exception ex2) {
                throw new TestWarningException("This test case works with Oracle9 platform or higher");
            }
        }
        DatabasePlatform platform = getSession().getPlatform();
        if(!platform.shouldBindAllParameters()) {
            // without binding string binding must be enabled to so that a big String for NCLOB is still bound.
            usesStringBindingOriginal = platform.usesStringBinding();
            platform.setUsesStringBinding(true);
        }
    }

    protected void verify() {
        if (object == null) {
            throw new TestException("object is missing");
        }
        if (controlObject == null) {
            throw new TestProblemException("control object is missing");
        }
        if (object.getChar() != null) {
            if (!object.getChar().equals(controlObject.getChar())) {
                throw new TestException("wrong CHAR: " + object.getChar() + "; should be: " + controlObject.getChar());
            }
        } else {
            if (controlObject.getChar() != null) {
                throw new TestException("wrong CHAR: NULL  should be: " + controlObject.getChar());
            }
        }
        if (object.getNchar() != null) {
            if (!object.getNchar().equals(controlObject.getNchar())) {
                throw new TestException("wrong NCHAR: " + charCode(object.getNchar().charValue()) + "; should be: " + charCode(controlObject.getNchar().charValue()));
            }
        } else {
            if (controlObject.getNchar() != null) {
                throw new TestException("wrong NCHAR: NULL  should be: " + charCode(controlObject.getNchar().charValue()));
            }
        }
        if (object.getStr() != null) {
            if (object.getStr().length() != controlObject.getStr().length()) {
                throw new TestException("wrong VARCHAR2, length: " + object.getStr().length() + "; should be: " + controlObject.getStr().length());
            }
            for (int i = 0; i < object.getStr().length(); i++) {
                if (object.getStr().charAt(i) != controlObject.getStr().charAt(i)) {
                    throw new TestException("wrong VARCHAR2, char # " + i + " : " + object.getStr().charAt(i) + "; should be: " + controlObject.getStr().charAt(i));
                }
            }
        } else {
            if (controlObject.getStr() != null) {
                throw new TestException("wrong VARCHAR2: NULL  should be: " + controlObject.getStr().charAt(0) + "...");
            }
        }
        if (object.getNstr() != null) {
            if (object.getNstr().length() != controlObject.getNstr().length()) {
                throw new TestException("wrong NVARCHAR2, length: " + object.getNstr().length() + "; should be: " + controlObject.getNstr().length());
            }
            for (int i = 0; i < object.getNstr().length(); i++) {
                if (object.getNstr().charAt(i) != controlObject.getNstr().charAt(i)) {
                    throw new TestException("wrong NVARCHAR2, char # " + i + " : " + charCode(object.getNstr().charAt(i)) + "; should be: " + charCode(controlObject.getNstr().charAt(i)));
                }
            }
        } else {
            if (controlObject.getNstr() != null) {
                throw new TestException("wrong NVARCHAR2: NULL  should be: " + charCode(controlObject.getNstr().charAt(0)) + "...");
            }
        }
        if (object.getClob() != null) {
            if (object.getClob().length != controlObject.getClob().length) {
                throw new TestException("wrong CLOB, length: " + object.getClob().length + "; should be: " + controlObject.getClob().length);
            }
            for (int i = 0; i < object.getClob().length; i++) {
                if (object.getClob()[i] != controlObject.getClob()[i]) {
                    throw new TestException("wrong CLOB, char # " + i + " : " + object.getClob()[i] + "; should be: " + controlObject.getClob()[i]);
                }
            }
        } else {
            if (controlObject.getClob() != null) {
                throw new TestException("wrong CLOB: NULL  should be: " + controlObject.getClob()[0] + "...");
            }
        }
        if (object.getNclob() != null) {
            if (object.getNclob().length != controlObject.getNclob().length) {
                throw new TestException("wrong NCLOB, length: " + object.getNclob().length + "; should be: " + controlObject.getNclob().length);
            }
            for (int i = 0; i < object.getNclob().length; i++) {
                if (object.getNclob()[i] != controlObject.getNclob()[i]) {
                    throw new TestException("wrong NCLOB, char # " + i + " : " + charCode(object.getNclob()[i]) + "; should be: " + charCode(controlObject.getNclob()[i]));
                }
            }
        } else {
            if (controlObject.getNclob() != null) {
                throw new TestException("wrong NCLOB: NULL  should be: " + charCode(controlObject.getNclob()[0]) + "...");
            }
        }
        if (object.getClob2() != null) {
            if (object.getClob2().length != controlObject.getClob2().length) {
                throw new TestException("wrong CLOB2, length: " + object.getClob2().length + "; should be: " + controlObject.getClob2().length);
            }
            for (int i = 0; i < object.getClob().length; i++) {
                if (object.getClob2()[i] != controlObject.getClob2()[i]) {
                    throw new TestException("wrong CLOB2, char # " + i + " : " + object.getClob2()[i] + "; should be: " + controlObject.getClob2()[i]);
                }
            }
        } else {
            if (controlObject.getClob2() != null) {
                throw new TestException("wrong CLOB2: NULL  should be: " + controlObject.getClob2()[0] + "...");
            }
        }
        if (object.getNclob2() != null) {
            if (object.getNclob2().length != controlObject.getNclob2().length) {
                throw new TestException("wrong NCLOB2, length: " + object.getNclob2().length + "; should be: " + controlObject.getNclob2().length);
            }
            for (int i = 0; i < object.getNclob2().length; i++) {
                if (object.getNclob2()[i] != controlObject.getNclob2()[i]) {
                    throw new TestException("wrong NCLOB2, char # " + i + " : " + charCode(object.getNclob2()[i]) + "; should be: " + charCode(controlObject.getNclob2()[i]));
                }
            }
        } else {
            if (controlObject.getNclob2() != null) {
                throw new TestException("wrong NCLOB2: NULL  should be: " + charCode(controlObject.getNclob2()[0]) + "...");
            }
        }
    }

    public void reset() {
        if (object != null) {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            uow.deleteObject(object);
            uow.commit();

            object = null;
        }
        controlObject = null;
        DatabasePlatform platform = getSession().getPlatform();
        if(!platform.shouldBindAllParameters()) {            
            // restore original value
            platform.setUsesStringBinding(usesStringBindingOriginal);
        }
        if (platformOriginal != null) {
            getSession().getLogin().usePlatform(platformOriginal);
            getDatabaseSession().logout();
            getDatabaseSession().login();
            platformOriginal = null;
        }
    }

    protected String charCode(char ch) {
        String temp = Integer.toHexString(ch).toUpperCase();
        while (temp.length() != 4) {
            temp = "0" + temp;
        }
        return "\\u" + temp;
    }
}
