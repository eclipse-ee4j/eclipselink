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
package org.eclipse.persistence.testing.tests.isolatedsession;

import org.eclipse.persistence.config.ExclusiveConnectionMode;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.testing.framework.*;

public class IsolatedSessionTestModel extends TestModel {
    public IsolatedSessionTestModel() {
        setDescription("This model tests the Isoalted Session Support and verifies its compatability with other TopLink features.");
    }

    public IsolatedSessionTestModel(boolean isSRG) {
        this();
        this.isSRG = isSRG;
    }

    public void addRequiredSystems() {
        if (!(getSession().getPlatform() instanceof OraclePlatform)) {
            throw new TestWarningException("This model is intended for Oracle databases only.");
        }
        addRequiredSystem(new IsolatedSessionSystem());
    }

    public void addTests() {
        addTest(new VerifyIsolationTest(ExclusiveConnectionMode.Transactional));
        addTest(new VerifyIsolationTest(ExclusiveConnectionMode.Isolated));
        addTest(new VerifyIsolationTest(ExclusiveConnectionMode.Always));
        addTest(new VerifyExclusiveConnectionTest(ExclusiveConnectionMode.Always, false));
        addTest(new VerifyExclusiveConnectionTest(ExclusiveConnectionMode.Always, true));
        addTest(new VerifyExclusiveConnectionTest(ExclusiveConnectionMode.Isolated, true));
        addTest(new ExceptionOnIsolatedReference());
        addTest(new VPDSupportTest());
        addTest(new IsolatedQueryTest());
        addTest(new ExclusiveConnectionClosedExceptionTest());
        addTest(new NoRowsUpdatedTest());
        addTest(new ProjectXMLTest());
        addTest(new VerifyQueryCacheIsolated(false));
        addTest(new CheckAccessorUsageForIsolatedClass());
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.

    public void addSRGTests() {
    }

    public void reset() {
        //need to remove this system so that other tests do not create Isolated Sessions
        getExecutor().removeConfigureSystem(new IsolatedSessionSystem());

    }
}
