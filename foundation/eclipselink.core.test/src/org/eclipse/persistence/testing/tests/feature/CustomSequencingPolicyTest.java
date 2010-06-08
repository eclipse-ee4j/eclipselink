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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.sequencing.SeqTestClass2;
import org.eclipse.persistence.testing.models.sequencing.SampleStringSequencingPolicy;
import org.eclipse.persistence.sequencing.Sequence;

public class CustomSequencingPolicyTest extends AutoVerifyTestCase {
    protected SeqTestClass2 obj1;
    protected SeqTestClass2 obj2;
    protected SeqTestClass2 obj3;
    protected SeqTestClass2 obj4;
    protected Sequence originalSequence;

    public CustomSequencingPolicyTest() {
        super();
        setDescription("Test custom sequencing policy, which produces sequences of type String");
    }

    protected void setup() {
        originalSequence = getSession().getLogin().getPlatform().getDefaultSequence();
        getSession().getPlatform().setDefaultSequence(new SampleStringSequencingPolicy(originalSequence.getName(), "", originalSequence.getPreallocationSize()));
        ((DatabaseSession)getSession()).getSequencingControl().resetSequencing();
    }

    protected void test() {
        obj1 = new SeqTestClass2();
        obj2 = new SeqTestClass2();
        obj3 = new SeqTestClass2();
        obj4 = new SeqTestClass2();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(obj1);
        SeqTestClass2 clone2 = (SeqTestClass2)uow.registerObject(obj2);
        clone2.setPkey(null);
        clone2.setTest1("");
        SeqTestClass2 clone3 = (SeqTestClass2)uow.registerObject(obj3);
        clone3.setPkey("a1");
        clone3.setTest1("a1");
        SeqTestClass2 clone4 = (SeqTestClass2)uow.registerObject(obj4);
        clone4.setPkey("zzzzzzzzzz");
        clone4.setTest1("zzzzzzzzzz");
        uow.commit();
    }

    protected void verify() throws Exception {
        if (obj1.getPkey() == null) {
            throw (new TestErrorException("Has not assigned seq string to an object with no pk"));
        }
        if (obj2.getPkey().equals("")) {
            throw (new TestErrorException("Has not overridden an empty string"));
        }
        if (obj3.getPkey().equals("1")) {
            throw (new TestErrorException("Has overridden a valid PK"));
        }
        if (!obj4.getPkey().equals("zzzzzzzzzz")) {
            throw (new TestErrorException("Has overridden a valid PK"));
        }
    }

    public void reset() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteObject(obj1);
        uow.deleteObject(obj2);
        uow.deleteObject(obj3);
        uow.deleteObject(obj4);
        uow.commit();
        obj1 = null;
        obj2 = null;
        obj3 = null;
        obj4 = null;
        getSession().getPlatform().setDefaultSequence(originalSequence);
        ((DatabaseSession)getSession()).getSequencingControl().resetSequencing();
    }
}
