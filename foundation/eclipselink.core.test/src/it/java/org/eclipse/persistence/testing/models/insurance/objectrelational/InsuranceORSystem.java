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
package org.eclipse.persistence.testing.models.insurance.objectrelational;

import java.util.*;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.insurance.*;

public class InsuranceORSystem extends TestSystem {
    /**
     * Return the descriptor required for this system.
     */
    public Vector buildDescriptors() {
        return InsuranceProject.getAllDescriptors();
    }

    /**
     * Default method to create/recreate the database.
     * First drop constraints ignoring errors if tables did not exist.
     * Second recreate the tables.
     * Third add the constraints back.
     */
    public void createTables(DatabaseSession session) {
        InsuranceProject project = new InsuranceProject();
        SchemaManager schemaManager = new SchemaManager(session);

        if (!SchemaManager.FAST_TABLE_CREATOR) {
            // Must remove dependecies before creation
            try {
                session.executeQuery(new DataModifyQuery("drop table PolicyHolders cascade constraints"));
            } catch (DatabaseException exception) {
            }
            try {
                session.executeQuery(new DataModifyQuery("drop table Policies cascade constraints"));
            } catch (DatabaseException exception) {
            }
            try {
                session.executeQuery(new DataModifyQuery("drop table Claims cascade constraints"));
            } catch (DatabaseException exception) {
            }
            try {
                session.executeQuery(new DataModifyQuery("drop type Claim_type force"));
            } catch (DatabaseException exception) {
            }
            try {
                session.executeQuery(new DataModifyQuery("drop type Claims_type force"));
            } catch (DatabaseException exception) {
            }
            try {
                session.executeQuery(new DataModifyQuery("drop type Policy_type force"));
            } catch (DatabaseException exception) {
            }
            try {
                session.executeQuery(new DataModifyQuery("drop type Policies_type force"));
            } catch (DatabaseException exception) {
            }
            try {
                session.executeQuery(new DataModifyQuery("drop type PolicyHolder_type force"));
            } catch (DatabaseException exception) {
            }
            try {
                session.executeQuery(new DataModifyQuery("drop type Phone_type force"));
            } catch (DatabaseException exception) {
            }
            try {
                session.executeQuery(new DataModifyQuery("drop type PhoneList_type force"));
            } catch (DatabaseException exception) {
            }
            try {
                session.executeQuery(new DataModifyQuery("drop type Address_type force"));
            } catch (DatabaseException exception) {
            }
            try {
                session.executeQuery(new DataModifyQuery("drop type NameList_type force"));
            } catch (DatabaseException exception) {
            }

            // Create tables
            for (Enumeration typesEnum = project.getTypes().elements(); typesEnum.hasMoreElements(); ) {
                schemaManager.replaceObject((DatabaseObjectDefinition)typesEnum.nextElement());
            }
        }

        for (Enumeration typesEnum = project.getTables().elements(); typesEnum.hasMoreElements(); ) {
            schemaManager.replaceObject((DatabaseObjectDefinition)typesEnum.nextElement());
        }
    }

    /**
     * Populate the database with example instance of the domain using session.
     */
    public void populate(DatabaseSession session) throws DatabaseException {
        UnitOfWork unitOfWork = session.acquireUnitOfWork();
        PolicyHolder example = null;

        example = PolicyHolder.example1();
        PopulationManager.getDefaultManager().registerObject(example, "example1");
        unitOfWork.registerObject(example);

        example = PolicyHolder.example2();
        PopulationManager.getDefaultManager().registerObject(example, "example2");
        unitOfWork.registerObject(example);

        example = PolicyHolder.example3();
        PopulationManager.getDefaultManager().registerObject(example, "example3");
        unitOfWork.registerObject(example);

        example = PolicyHolder.example4();
        PopulationManager.getDefaultManager().registerObject(example, "example4");
        unitOfWork.registerObject(example);

        unitOfWork.commit();
    }
}
