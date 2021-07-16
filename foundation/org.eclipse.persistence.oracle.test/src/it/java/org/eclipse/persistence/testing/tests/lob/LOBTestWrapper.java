/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.lob;

import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.framework.TestWrapper;

public class LOBTestWrapper extends TestWrapper {
    protected boolean shouldSetUseLocatorForLOBWriteIntoPlatform;
    protected boolean shouldUseLocatorForLOBWrite;
    protected boolean shouldUseLocatorForLOBWriteOriginal;
    protected boolean usesStringBindingOriginal;

    public LOBTestWrapper(AutoVerifyTestCase test) {
        super(test);
        this.shouldSetUseLocatorForLOBWriteIntoPlatform = false;
    }

    public LOBTestWrapper(AutoVerifyTestCase test, boolean shouldUseLocatorForLOBWrite) {
        super(test);
        this.shouldUseLocatorForLOBWrite = shouldUseLocatorForLOBWrite;
        this.shouldSetUseLocatorForLOBWriteIntoPlatform = true;
        setName(getName() + " platform forced to "+(shouldUseLocatorForLOBWrite ? "use" : "NOT use")+" lob locators)");
    }

    @Override
    protected void setup() throws Throwable {
        DatabasePlatform platform = getSession().getPlatform();
        if (!platform.isOracle()) {
            throw new TestWarningException("This test case works on Oracle only");
        }
        if(platform instanceof OraclePlatform) {
            OraclePlatform platform8 = (OraclePlatform)platform;
            shouldUseLocatorForLOBWriteOriginal = platform8.shouldUseLocatorForLOBWrite();
            if(shouldSetUseLocatorForLOBWriteIntoPlatform) {
                platform8.setShouldUseLocatorForLOBWrite(shouldUseLocatorForLOBWrite);
            } else {
                // otherwise don't change the flag.
                this.shouldUseLocatorForLOBWrite = shouldUseLocatorForLOBWriteOriginal;
            }
        } else {
            // can't use locators if it's not Oracle8Platform or higher.
            if(shouldSetUseLocatorForLOBWriteIntoPlatform && shouldUseLocatorForLOBWrite) {
                throw new TestProblemException("Can't call platform.setShouldUseLocatorForLobWrite(true) - it's not Oracle8Platform");
            }
            this.shouldUseLocatorForLOBWrite = false;
        }

        if(!shouldUseLocatorForLOBWrite) {
            usesStringBindingOriginal = platform.usesStringBinding();
            platform.setUsesStringBinding(true);
        }
        super.setup();
    }

    @Override
    public void reset() throws Throwable {
        super.reset();
        DatabasePlatform platform = getSession().getPlatform();
        if(!shouldUseLocatorForLOBWrite) {
            platform.setUsesStringBinding(usesStringBindingOriginal);
        }
        if(platform instanceof OraclePlatform) {
            OraclePlatform platform8 = (OraclePlatform)platform;
            if(shouldSetUseLocatorForLOBWriteIntoPlatform) {
                platform8.setShouldUseLocatorForLOBWrite(shouldUseLocatorForLOBWriteOriginal);
            }
        }
    }
}
