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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.annotations.IdValidation;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestProblemException;


/**
 * This test is in response to a support question.  There was a problem with
 * The stability of the cache when an object was registered with the Unit Of
 * Work as a new object.
 * 
 * Test extended for bug Bug 300556 - Can't configure descriptor to make sequence override existing negative pk values.
 * Test can run with different IdValidations: 
 *   IdValidation.NULL: Allow 0 or negative primary key (now not supported by default).
 *   IdValidation.ZERO: Allow negative primary key (default).
 *   IdValidation.NEGATIVE: Allow only positive primary key (now not supported by default).
 * keepSequencing set to true indicates that sequence values should be assigned to the newly created objects:
 *   if shouldAlwaysOverrideExistingValue is true then all existing pk values should be overridden by sequencing,
 *   otherwise only not allowed pk values should be overridden.
 */

public class RegisterNewObjectInIdentityMapNoSeqTest extends AutoVerifyTestCase {
    public String sequenceNumberName;
    public DatabaseField sequenceNumberField;
    boolean keepSequencing;
    boolean shouldAlwaysOverrideExistingValue;
    IdValidation idValidation;
    boolean zeroFailed;
    boolean negativeFailed;
    boolean zeroOverridden;
    boolean negativeOverridden;
    IdValidation idValidationOriginal;
    boolean shouldAlwaysOverrideExistingValueOriginal;

    public RegisterNewObjectInIdentityMapNoSeqTest(IdValidation idValidation, boolean keepSequencing, boolean shouldAlwaysOverrideExistingValue) {
        if(idValidation != IdValidation.NULL && idValidation != IdValidation.ZERO && idValidation != IdValidation.NEGATIVE) {
            throw new TestProblemException(idValidation + " is not supported.");
        }
        this.idValidation = idValidation;
        this.keepSequencing = keepSequencing;
        if(keepSequencing) {
            this.shouldAlwaysOverrideExistingValue = shouldAlwaysOverrideExistingValue;
        }
        setDescription("This test verifies the the UOW cache when registering a new object with a primitive primary key");
        setName(getName() + getNameSuffix());
    }
    
    String getNameSuffix() {
        return " " + idValidation + (keepSequencing ? " Sequencing" + (shouldAlwaysOverrideExistingValue ? " always overrides" : ""): "");
    }

    public void reset() {
        ClassDescriptor descriptor = getSession().getClassDescriptor(Weather.class);
        descriptor.setIdValidation(idValidationOriginal);
        if(keepSequencing) {
            descriptor.getSequence().setShouldAlwaysOverrideExistingValue(shouldAlwaysOverrideExistingValueOriginal);
        } else {
            descriptor.setSequenceNumberField(this.sequenceNumberField);
            descriptor.setSequenceNumberName(this.sequenceNumberName);
        }
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        zeroFailed = false;
        negativeFailed = false;
        zeroOverridden = false;
        negativeOverridden = false;
        
        getAbstractSession().beginTransaction();
        ClassDescriptor descriptor = getSession().getClassDescriptor(Weather.class);
        idValidationOriginal = descriptor.getIdValidation(); 
        descriptor.setIdValidation(idValidation);
        if(keepSequencing) {
            if(!shouldAlwaysOverrideExistingValue) {
                if(descriptor.getSequence().shouldAcquireValueAfterInsert()) {
                    throw new TestProblemException("Cannot run test with keepSequencing==true and alwayOverrideExistingValueOriginal==false with Identity sequence: it should always override");
                }
            }
            shouldAlwaysOverrideExistingValueOriginal = descriptor.getSequence().shouldAlwaysOverrideExistingValue();
            descriptor.getSequence().setShouldAlwaysOverrideExistingValue(shouldAlwaysOverrideExistingValue);
        } else {
            sequenceNumberField = descriptor.getSequenceNumberField();
            descriptor.setSequenceNumberField(null);
            sequenceNumberName = descriptor.getSequenceNumberName();
            descriptor.setSequenceNumberName(null);
        }
    }

    public void test() {
        Session session = getSession();
        UnitOfWork uow = session.acquireUnitOfWork();
        uow.setShouldNewObjectsBeCached(true);
        
        Weather weather = new Weather();
        weather.setStormPattern("Something really bad");
        weather.id = 0;
        ReadObjectQuery query = new ReadObjectQuery(weather);
        query.checkCacheOnly();
        Weather weatherClone = (Weather)uow.registerObject(weather);
        weather = (Weather)uow.executeQuery(query);
        zeroFailed = weather == null;
        
        Weather weatherNeg = new Weather();
        weatherNeg.setStormPattern("Something really bad below zero");
        weatherNeg.id = -1;
        ReadObjectQuery queryNeg = new ReadObjectQuery(weatherNeg);
        queryNeg.checkCacheOnly();
        Weather weatherNegClone = (Weather)uow.registerObject(weatherNeg);
        weatherNeg = (Weather)uow.executeQuery(queryNeg);
        negativeFailed = weatherNeg == null;

        if(keepSequencing) {
            uow.assignSequenceNumbers();
            zeroOverridden = weatherClone.id != 0;
            negativeOverridden = weatherNegClone.id != -1;
        }
    }
    
    public void verify() {
        String errorMsg = "";
        boolean zeroFailedExpected = false;
        boolean negativeFailedExpected = false;
        boolean zeroOverriddenExpected = shouldAlwaysOverrideExistingValue;
        boolean negativeOverriddenExpected = shouldAlwaysOverrideExistingValue;
        if(idValidation == IdValidation.NULL) {
            // nothing to do
        } else if(idValidation == IdValidation.ZERO) {
            zeroFailedExpected = true;
            negativeFailedExpected = false;
            if(keepSequencing) {
                zeroOverriddenExpected = true;
                negativeOverriddenExpected = shouldAlwaysOverrideExistingValue;
            }
        } else if(idValidation == IdValidation.NEGATIVE) {
            zeroFailedExpected = true;
            negativeFailedExpected = true;
            if(keepSequencing) {
                zeroOverriddenExpected = true;
                negativeOverriddenExpected = true;
            }
        }
        if(zeroFailed != zeroFailedExpected) {
            errorMsg += " zeroFailed = " + zeroFailed + ";";
        }
        if(negativeFailed != negativeFailedExpected) {
            errorMsg += " negativeFailed = " + negativeFailed + ";";
        }
        if(keepSequencing) {
            if(zeroOverridden != zeroOverriddenExpected) {
                errorMsg += " zeroOverridden = " + zeroOverridden + ";";
            }
            if(negativeOverridden != negativeOverriddenExpected) {
                errorMsg += " negativeOverridden = " + negativeOverridden + ";";
            }
        }
        if (errorMsg.length() > 0) {
            errorMsg = getNameSuffix() + ": Unexpected results: " + errorMsg;
            throw new TestErrorException(errorMsg);
        }
    }
}
