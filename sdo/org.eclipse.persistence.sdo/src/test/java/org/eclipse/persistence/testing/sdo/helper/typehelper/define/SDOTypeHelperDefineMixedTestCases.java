/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.helper.typehelper.define;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class SDOTypeHelperDefineMixedTestCases extends SDOTestCase {
    public SDOTypeHelperDefineMixedTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.typehelper.define.SDOTypeHelperDefineMixedTestCases" };
        TestRunner.main(arguments);
    }

    public void testDefineNotSequencedNotOpen() {
        DataObject dataObject = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)dataObject.getType().getProperty("uri");
        dataObject.set(prop, "http://example.com/customer");
        dataObject.set("name", "rootType");
        dataObject.set("sequenced", false);
        dataObject.set("open", false);

        Type theType = typeHelper.define(dataObject);

        assertFalse(theType.isOpen());
        assertFalse(theType.isSequenced());

        assertFalse(xsdHelper.isMixed(theType));
    }

    public void testDefineSequencedOpen() {
        DataObject dataObject = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)dataObject.getType().getProperty("uri");
        dataObject.set(prop, "http://example.com/customer");
        dataObject.set("name", "rootType");
        dataObject.set("sequenced", true);
        dataObject.set("open", true);

        Type theType = typeHelper.define(dataObject);

        assertTrue(theType.isOpen());
        assertTrue(theType.isSequenced());

        assertTrue(xsdHelper.isMixed(theType));
    }

    public void testDefineSequencedNotOpen() {
        DataObject dataObject = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)dataObject.getType().getProperty("uri");
        dataObject.set(prop, "http://example.com/customer");
        dataObject.set("name", "rootType");
        dataObject.set("sequenced", true);
        dataObject.set("open", false);

        Type theType = typeHelper.define(dataObject);

        assertFalse(theType.isOpen());
        assertTrue(theType.isSequenced());

        assertTrue(xsdHelper.isMixed(theType));
    }

    public void testDefineNotSequencedOpen() {
        DataObject dataObject = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)dataObject.getType().getProperty("uri");
        dataObject.set(prop, "http://example.com/customer");
        dataObject.set("name", "rootType");
        dataObject.set("sequenced", false);
        dataObject.set("open", true);

        Type theType = typeHelper.define(dataObject);

        assertTrue(theType.isOpen());
        assertFalse(theType.isSequenced());

        assertFalse(xsdHelper.isMixed(theType));
    }
}
