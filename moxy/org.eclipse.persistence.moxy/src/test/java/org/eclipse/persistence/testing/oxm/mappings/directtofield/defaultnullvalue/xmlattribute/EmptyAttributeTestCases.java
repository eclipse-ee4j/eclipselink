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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.xmlattribute;

import junit.textui.TestRunner;

import org.eclipse.persistence.testing.oxm.OXTestCase.Metadata;
import org.eclipse.persistence.testing.oxm.OXTestCase.Platform;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.Employee;

public class EmptyAttributeTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/defaultnullvalue/xmlattribute/EmptyAttribute.xml";
    private final static String XML_RESOURCE_WRITE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/defaultnullvalue/xmlattribute/EmptyAttributeWrite.xml";

    public EmptyAttributeTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        // Round-trip does not occur because we are marshalling as absent node by default when the nullValue == "" in the non-deploymentXML
           if(!(platform==Platform.DOM && metadata==Metadata.XML_ECLIPSELINK)) {
            setControlDocument(XML_RESOURCE_WRITE);
        }
        setProject(new DefaultNullValueAttributeProject());
    }

   protected Object getControlObject() {
         Employee employee = new Employee();
         // We currently have different behavior when using XMLProjectReader
          if(platform==Platform.DOM && metadata==Metadata.XML_ECLIPSELINK) {
             employee.setID(DefaultNullValueAttributeProject.CONTROL_ID);
             // See bug#209398 - current behavior for deployment xml is to not preserve the nullValue of ""
             //employee.setFirstName(DefaultNullValueAttributeProject.CONTROL_FIRSTNAME);
         } else {
             employee.setID(DefaultNullValueAttributeProject.CONTROL_ID);
             // See bug#209398 - current behavior for deployment xml is to not preserve the nullValue of ""
             employee.setFirstName(DefaultNullValueAttributeProject.CONTROL_FIRSTNAME);
         }

         return employee;
       }

   public Object getWriteControlObject() {
        Employee employee = new Employee();
        // We currently have different behavior when using XMLProjectReader
          if(platform==Platform.DOM && metadata==Metadata.XML_ECLIPSELINK) {
            employee.setID(DefaultNullValueAttributeProject.CONTROL_ID);
            // See bug#209398 - current behavior for deployment xml is to not preserve the nullValue of ""
            employee.setFirstName(DefaultNullValueAttributeProject.CONTROL_FIRSTNAME);
        } else {
            employee.setID(DefaultNullValueAttributeProject.CONTROL_ID);
            // See bug#209398 - current behavior for deployment xml is to not preserve the nullValue of ""
            employee.setFirstName(DefaultNullValueAttributeProject.CONTROL_FIRSTNAME);
        }

        return employee;
      }

     public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.xmlattribute.EmptyAttributeTestCases" };
        TestRunner.main(arguments);
    }
}
