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
package org.eclipse.persistence.testing.tests.mapping;

import java.math.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.mapping.Baby;
import org.eclipse.persistence.testing.models.mapping.BabyMonitor;
import org.eclipse.persistence.testing.models.mapping.Crib;

/**
 * Bug 3142898 - Ensure joining works to multiple levels
 */
public class TwoLevelJoinedAttributeTest extends AutoVerifyTestCase {
    protected int cribMonitorUseJoining;
    protected int babyCribUseJoining;
    protected BigDecimal babyId = null;
    protected Baby baby = null;

    public TwoLevelJoinedAttributeTest() {
        setDescription("Ensure objects that use joining to two levels execute join queries properly.");
    }

    public void setup() {
        beginTransaction();
        // populate the database
        UnitOfWork uow = getSession().acquireUnitOfWork();

        Baby populationBaby = new Baby();
        populationBaby.setName("Bobby");

        BabyMonitor monitor = new BabyMonitor();
        monitor.setBrandName("Nokia");
        monitor.setBaby(populationBaby);

        Crib crib = new Crib();
        crib.setColor("yellow");
        crib.setBabyMonitor(monitor);
        crib.setBaby(populationBaby);

        monitor.setCrib(crib);

        populationBaby.setBabyMonitor(monitor);
        populationBaby.setCrib(crib);

        uow.registerObject(populationBaby);

        uow.commit();

        babyId = populationBaby.getId();

        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        // set joining behavior so baby is joined to crib and crib is joined to baby monitor
        ClassDescriptor descriptor = getSession().getClassDescriptor(Baby.class);
        babyCribUseJoining = ((OneToOneMapping)descriptor.getMappingForAttributeName("crib")).getJoinFetch();
        ((OneToOneMapping)descriptor.getMappingForAttributeName("crib")).useInnerJoinFetch();
        descriptor.reInitializeJoinedAttributes();
        descriptor = getSession().getClassDescriptor(Crib.class);
        cribMonitorUseJoining = ((OneToOneMapping)descriptor.getMappingForAttributeName("babyMonitor")).getJoinFetch();
        ((OneToOneMapping)descriptor.getMappingForAttributeName("babyMonitor")).useInnerJoinFetch();
        descriptor.reInitializeJoinedAttributes();
    }

    public void test() {
        // read a baby
        ExpressionBuilder babys = new ExpressionBuilder();
        Expression expression = babys.get("id").equal(babyId);
        baby = (Baby)getSession().readObject(Baby.class, expression);
    }

    public void verify() {
        // Since we are using joining, all attributes should be properly populated.
        if ((baby.getCrib() == null) || (baby.getCrib().getBabyMonitor() == null)) {
            throw new TestErrorException("TopLink did not properly process multiple levels of joining within " + " descriptors.");
        }
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        ClassDescriptor descriptor = getSession().getClassDescriptor(Baby.class);
        ((OneToOneMapping)descriptor.getMappingForAttributeName("crib")).setJoinFetch(babyCribUseJoining);
        descriptor.reInitializeJoinedAttributes();
        descriptor = getSession().getClassDescriptor(Crib.class);
        ((OneToOneMapping)descriptor.getMappingForAttributeName("babyMonitor")).setJoinFetch(cribMonitorUseJoining);
        descriptor.reInitializeJoinedAttributes();
    }
}
