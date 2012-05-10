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
package org.eclipse.persistence.testing.tests.readonly;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.readonly.HollywoodAgent;
import org.eclipse.persistence.testing.models.readonly.Actor;
import org.eclipse.persistence.testing.models.readonly.ReadOnlyHollywoodAgent;
import org.eclipse.persistence.testing.models.readonly.Charity;
import org.eclipse.persistence.testing.models.readonly.ReadOnlyCharity;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;

/**
 * Test added for bug 3013948.  Since one can modify oneToOne or manyToMany
 * references to read only objects without changing those objects, this is
 * something we should fully support.
 * 
 * Bug 3013948 fixes the read only case of CR 1360.  The original description
 * is:
 * 
 * Description:  There was an issue that loss of identity could cause a problem with deletes from collection mappings. 
 * Consider an object model where an Employee contains a privately owned vector of PhoneNumbers.  Also, assume that the IdentityMap for PhoneNumber is a CacheIdentityMap of size 10.  Now, imagine that 10 Employees, each with 3 PhoneNumbers are read in.  Because the identityMap is of a fixed size, only 10 of the phone number will be in the cache.  Since PhoneNumbers are privately owned, deleting them out of the vector of the Employee will delete them from the database.  However, in the above scenario, if the PhoneNumber is not longer in the identityMap, the delete would occur on the database, but not in the original. 
 *
 * Also, if this collection were not a Vector but a Hashtable, there would be a NullPointer exception when the merge to the original object was attempted.. 
 * 
 * Note: The above scenario is generally one that you should avoid if possible.  Loss of identity may also cause other unexpected behavior.  In general it is better to use an identity map that will maintain identity for the life of you domain objects. 
 * 
 * Symptom:Either a nullpointer exeption on merge, or an incorrect merge due to loss of identity.
 * 
 */
public class WriteableMappingReadOnlyClassTest extends TransactionalTestCase {
    protected UnitOfWork uow;
    protected Actor actorClone;

    public WriteableMappingReadOnlyClassTest() {
        super();
    }

    protected void setup() {
        super.setup();
        // These two lines are needed due to problems with ReadOnlyClassAccessingTestCase.
        getSession().getDescriptor(ReadOnlyCharity.class).setShouldBeReadOnly(true);
        getSession().getDescriptor(ReadOnlyHollywoodAgent.class).setShouldBeReadOnly(true);
    }

    protected void test() {
        Vector charities = getSession().readAllObjects(Charity.class);
        HollywoodAgent hollywoodAgent = (HollywoodAgent)getSession().readObject(HollywoodAgent.class);

        // Now create a new Actor, and assign it read only versions of the previous.
        uow = getSession().acquireUnitOfWork();
        Actor actor = new Actor();

        for (Enumeration enumtr = charities.elements(); enumtr.hasMoreElements();) {
            Charity charity = (Charity)enumtr.nextElement();
            ReadOnlyCharity readOnlyCharity = new ReadOnlyCharity();
            readOnlyCharity.id = charity.id;
            readOnlyCharity.setName(charity.getName());
            actor.addCharity(readOnlyCharity);
        }
        ReadOnlyHollywoodAgent readOnlyHollywoodAgent = new ReadOnlyHollywoodAgent();
        readOnlyHollywoodAgent.id = hollywoodAgent.id;
        readOnlyHollywoodAgent.setName(hollywoodAgent.getName());
        actor.setHollywoodAgent(readOnlyHollywoodAgent);

        setActorClone((Actor)uow.registerObject(actor));
        uow.commit();

        // Prior to 3013948 the merge would have failed and the actor in the
        // global cache would have all their read only references replaced by nulls.
    }

    protected void verify() {
        try {
            uow = getSession().acquireUnitOfWork();
            Actor actorClone = getActorClone();
            actorClone.getCharities().removeAllElements();
            actorClone.setHollywoodAgent(null);

            Actor original = (Actor)uow.readObject(actorClone);
            Actor sessionOriginal = (Actor)getSession().readObject(actorClone);
            actorClone = (Actor)uow.deepMergeClone(actorClone);

            uow.commit();
        } catch (Exception e) {
            throw new TestErrorException("The original commit corrupted the session cache.", e);
        }
        Actor verification = (Actor)getSession().readObject(actorClone);
        if ((verification.getHollywoodAgent() != null) || (verification.getCharities().size() != 0)) {
            throw new TestErrorException("An exception was not thrown but the updates did not work.");
        }
    }

    public void reset() {
        super.reset();
    }

    protected Actor getActorClone() {
        return actorClone;
    }

    protected void setActorClone(Actor actorClone) {
        this.actorClone = actorClone;
    }
}
