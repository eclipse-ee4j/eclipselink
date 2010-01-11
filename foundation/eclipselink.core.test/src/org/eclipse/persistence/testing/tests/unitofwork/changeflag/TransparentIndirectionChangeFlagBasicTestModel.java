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
package org.eclipse.persistence.testing.tests.unitofwork.changeflag;

import org.eclipse.persistence.descriptors.changetracking.AttributeChangeTrackingPolicy;
import org.eclipse.persistence.descriptors.changetracking.ObjectChangePolicy;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.models.relationshipmaintenance.FieldOffice;
import org.eclipse.persistence.testing.models.relationshipmaintenance.RelationshipsSystem;


/**
 * This model tests reading/writing/deleting through using the employee demo.
 * This model is set up to test the use of change tracking policy.  It uses
 * the employee demo test framework to ensure everything works as it did before when 
 * the new change policy is used.  It also makes use of several of the tests from the UnitOfWork
 * model to ensure that the actual updates function correctly.
 * @author Tom Ware
 */
public class TransparentIndirectionChangeFlagBasicTestModel extends TestModel {

    protected ObjectChangePolicy fieldOfficeChangePolicy;

    public TransparentIndirectionChangeFlagBasicTestModel() {
        setDescription("This model tests reading/writing/deleting using the IndirectList with change tracking.");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new RelationshipsSystem());
    }

    public void addTests() {
        addTest(new TransparentIndirectionTest());
        addTest(new TransparentIndirectionAddOverflowBehaviourTest());
        addTest(new TransparentIndirectionAddRemoveTest());
        addTest(new TransparentIndirectionResumeAddTest());
    }

    public void setup() {
        // Save change policies for the all employee demo class in order to restore them at reset time.
        fieldOfficeChangePolicy = getSession().getDescriptor(FieldOffice.class).getObjectChangePolicy();
        getSession().getDescriptor(FieldOffice.class).setObjectChangePolicy(new AttributeChangeTrackingPolicy());
    }

    public void reset() {
        // restore old change policies.
        getSession().getDescriptor(FieldOffice.class).setObjectChangePolicy(fieldOfficeChangePolicy);
    }

}
