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
package org.eclipse.persistence.testing.models.insurance;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.tools.schemaframework.*;

/**
 * <b>Purpose</b>: To define system behavior.
 * <p><b>Responsibilities</b>:    <ul>
 * <li> Login and return an initialize database session.
 * <li> Create and populate the database.
 * </ul>
 */
public class InsuranceSystem extends TestSystem {

    /**
     * Recreate the insurance database.
     */
    public void createTables(DatabaseSession session) {
        if (!SchemaManager.FAST_TABLE_CREATOR) {
            try {
                session.executeNonSelectingCall(new SQLCall("drop table CHILDNAM"));
                session.executeNonSelectingCall(new SQLCall("drop table INS_ADDR"));
                session.executeNonSelectingCall(new SQLCall("drop table INS_PHONE"));
                session.executeNonSelectingCall(new SQLCall("drop table VHCL_POL"));
                session.executeNonSelectingCall(new SQLCall("drop table VHCL_CLM"));
                session.executeNonSelectingCall(new SQLCall("drop table CLAIM"));
                session.executeNonSelectingCall(new SQLCall("drop table POLICY"));
                session.executeNonSelectingCall(new SQLCall("drop table HOLDER"));
            } catch (Exception e) {
            }
        }
        new InsuranceTableCreator().replaceTables(session);
    }

    /**
     * Modify the PHONE descriptor so that it knows it's an aggregate collection
     */
    public static void modifyPhoneDescriptor(ClassDescriptor descriptor) {
        descriptor.descriptorIsAggregateCollection();
    }

    /**
     * Modify the POLICYHOLDER descriptor so that it contains an aggregate collection mappping for 'phones'
     */
    public static void modifyPolicyHolderDescriptor(ClassDescriptor descriptor) {
        org.eclipse.persistence.mappings.AggregateCollectionMapping aggregatecollectionmapping = new org.eclipse.persistence.mappings.AggregateCollectionMapping();
        aggregatecollectionmapping.setAttributeName("phones");
        aggregatecollectionmapping.setIsReadOnly(false);
        aggregatecollectionmapping.setUsesIndirection(false);
        aggregatecollectionmapping.setGetMethodName("getPhones");
        aggregatecollectionmapping.setSetMethodName("setPhones");
        aggregatecollectionmapping.setIsPrivateOwned(true);
        aggregatecollectionmapping.setReferenceClass(Phone.class);
        aggregatecollectionmapping.addTargetForeignKeyFieldName("INS_PHONE.HOLDER_SSN", "HOLDER.SSN");
        descriptor.addMapping(aggregatecollectionmapping);
    }

    /**
     * Populate the database with example instance of the domain using session.
     */
    public void populate(DatabaseSession session) throws DatabaseException {
        UnitOfWork unitOfWork = session.acquireUnitOfWork();
        PolicyHolder example;

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

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new InsuranceProject();
        }

        session.addDescriptors(project);
    }
}
