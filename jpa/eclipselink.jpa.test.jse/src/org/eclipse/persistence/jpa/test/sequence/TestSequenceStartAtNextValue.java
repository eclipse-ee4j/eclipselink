/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2017 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     09/14/2017-2.6.0 Will Dazey
//       - 522312: Add support for sequence generation to start forward from nextval
package org.eclipse.persistence.jpa.test.sequence;

import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.sequence.model.SequenceEntity;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.sequencing.QuerySequence;
import org.eclipse.persistence.sequencing.Sequence;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * The purpose of this test is to test that when Sequencing defaultSeqenceAtNextValue is set,
 * the ID values generated from the sequence will start at the Next Val value and not -size.
 */
@RunWith(EmfRunner.class)
public class TestSequenceStartAtNextValue {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { SequenceEntity.class, }, properties = { 
            @Property(name = "eclipselink.sequencing.start-sequence-at-nextval", value = "true") })
    private EntityManagerFactory emf;

    @Test
    public void testStartSequenceAtNextval() {
        EntityManagerImpl em = emf.createEntityManager().unwrap(EntityManagerImpl.class);

        Sequence seq = em.getServerSession().getDescriptor(SequenceEntity.class).getSequence();
        if (seq instanceof QuerySequence) {
            if(((DatasourcePlatform)seq.getDatasourcePlatform()).supportsSequenceObjects()) {
                ValueReadQuery q = ((DatasourcePlatform)seq.getDatasourcePlatform()).buildSelectQueryForSequenceObject(seq.getQualified(seq.getName()), seq.getPreallocationSize());
                if(q != null) {
                    //Execute NextVal or whatever query moves the sequence forward size
                    //This gives us a baseline to compare to
                    int nextVal = ((Number)em.getServerSession().executeQuery(q)).intValue();
                    //Calling this next should prompt another query for NextVal, returning the next ID value 
                    int seqNum = em.getServerSession().getNextSequenceNumberValue(SequenceEntity.class).intValue();
                    //Expect (nextVal + size), not (nextVal + size) - size
                    Assert.assertEquals("Expected " + seqNum + " == " + nextVal + " + " + seq.getPreallocationSize() + "(Size)", nextVal + seq.getPreallocationSize(), seqNum);
                } else {
                   Assert.fail((DatasourcePlatform)seq.getDatasourcePlatform() + " does support SequenceObjects, but SelectQuery is null.");
                }
            } else {
                System.out.println((DatasourcePlatform)seq.getDatasourcePlatform() + " does not support SequenceObjects. Test is not valid.");
            }
        } else {
            Assert.fail("Expected sequence for " + SequenceEntity.class + " to be of type QuerySequence, but was " + seq);
        }

        em.close();
    }
}
