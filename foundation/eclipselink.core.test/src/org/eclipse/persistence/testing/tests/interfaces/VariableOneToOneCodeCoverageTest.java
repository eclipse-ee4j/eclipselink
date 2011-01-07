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
package org.eclipse.persistence.testing.tests.interfaces;

import java.util.Vector;
import java.util.Enumeration;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.Association;
import org.eclipse.persistence.mappings.TypedAssociation;
import org.eclipse.persistence.mappings.VariableOneToOneMapping;
import org.eclipse.persistence.testing.models.interfaces.*;

public class VariableOneToOneCodeCoverageTest extends TestCase {
    VariableOneToOneMapping mapping;
    private String testFailures;

    public void setup() {
        mapping = new VariableOneToOneMapping();
        testFailures = "";
    }

    public void verify() {
        if (testFailures.length() > 0) {
            throw new TestErrorException("Tests failures from VariableOneToOneMapping: " + testFailures);
        }
    }

    public void test() {
        runTests();
    }

    public void runTests() {
        /**************************************************************************/
        mapping.addClassIndicator(Employee.class, null);

        if (mapping.getClassIndicatorAssociations().isEmpty()) {
            testFailures += "addClassIndicator = null did not add a null wrapper type indicator";
        } else {
            Enumeration e = mapping.getClassIndicatorAssociations().elements();

            while (e.hasMoreElements()) {
                TypedAssociation association = (TypedAssociation)e.nextElement();

                if (association.getKey() == Employee.class) {
                    if (!(association.getValue() instanceof Helper)) {
                        testFailures += "addClassIndicator = null, type indicator does not = Helper class";
                    }
                }
            }
        }

        /**************************************************************************/
        Vector vectorIn = new Vector();
        vectorIn.add(new Association(Actor.class, new String("ASHLEY JUDD")));
        vectorIn.add(new Association(Secretary.class, new String("DARTH VADER")));
        vectorIn.add(new Association(Broadcastor.class, new String("RED KELLY")));

        mapping.setClassIndicatorAssociations(vectorIn);

        Vector vectorOut = mapping.getClassIndicatorAssociations();

        if (vectorOut.size() != 3) {
            testFailures += "setClassIndicatorAssociations - the set failed";
        } else {
            int foundCount = 0;

            for (int i = 0; i < vectorOut.size(); i++) {
                Association ass = (Association)vectorOut.elementAt(i);

                if (ass.getKey() == Actor.class.getName() && ass.getValue().equals("ASHLEY JUDD")) {
                    foundCount++;
                }
                if (ass.getKey() == Secretary.class.getName() && ass.getValue().equals("DARTH VADER")) {
                    foundCount++;
                }
                if (ass.getKey() == Broadcastor.class.getName() && ass.getValue().equals("RED KELLY")) {
                    foundCount++;
                }
            }

            if (foundCount != 3) {
                testFailures += "setClassIndicatorAssociations - association values not found";
            }
        }

        /**************************************************************************/
        Association assoc = new Association(new String("key"), new String("value"));
        Vector in = new Vector();
        in.add(assoc);
        mapping.setSourceToTargetQueryKeyFieldAssociations(in);
        Vector out = mapping.getSourceToTargetQueryKeyFieldAssociations();

        if (out.size() != 1) {
            testFailures += "setSourceToTargetQueryFieldAssociations - the set failed";
        } else {
            Association a = (Association)out.elementAt(0);

            if (!(a.getKey().equals("key") && a.getValue().equals("value"))) {
                testFailures += "setSourceToTargetQueryFieldAssociations - value in the set failed";
            }
        }

        /**************************************************************************/
        Vector foreignKeyNames = new Vector();
        foreignKeyNames.add("fkey1");
        foreignKeyNames.add("fkey2");
        foreignKeyNames.add("fkey3");
        mapping.setForeignKeyFieldNames(foreignKeyNames);

        Vector fieldNames = mapping.getForeignKeyFieldNames();

        if (!(mapping.getForeignKeyFieldNames().contains("fkey1"))) {
            testFailures += "addForeignQueryKeyName - fkey1";
        }

        if (!(mapping.getForeignKeyFieldNames().contains("fkey2"))) {
            testFailures += "addForeignQueryKeyName - fkey2";
        }

        if (!(mapping.getForeignKeyFieldNames().contains("fkey3"))) {
            testFailures += "addForeignQueryKeyName - fkey3";
        }

        /**************************************************************************/
        mapping.setTypeFieldName("doesNotExist");

        if (!mapping.getTypeFieldName().equals("doesNotExist")) {
            testFailures += "setTypeFieldName failed";
        }

        /**************************************************************************/
        // tests done through the wrapper
        /**************************************************************************/

        /**************************************************************************/
        VariableOneToOneMapping2 wrappedMapping = new VariableOneToOneMapping2();
        wrappedMapping.setTypeField(null);

        if (wrappedMapping.getTypeFieldName() != null) {
            testFailures += "setTypeField - set to null failed";
        }

        /**************************************************************************/
        wrappedMapping.addClassIndicator(Employee.class, null);

        if (wrappedMapping.getTypeForImplementor(Employee.class) != null) {
            testFailures += "getTypeForImplementor failed";
        }

        if (wrappedMapping.getImplementorForType(null, getSession()) != Employee.class) {
            testFailures += "getImplementorForType failed";
        }
    }
}

class VariableOneToOneMapping2 extends VariableOneToOneMapping {
    public void setTypeField(DatabaseField typeField) {
        super.setTypeField(typeField);
    }

    public Object getTypeForImplementor(Class implementor) {
        return super.getTypeForImplementor(implementor);
    }

    public Object getImplementorForType(Object type, Session session) {
        return super.getImplementorForType(type, (AbstractSession)session);
    }
}
