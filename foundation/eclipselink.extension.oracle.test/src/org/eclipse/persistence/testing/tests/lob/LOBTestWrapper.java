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
package org.eclipse.persistence.testing.tests.lob;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.platform.database.oracle.Oracle8Platform;

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

    protected void setup() throws Throwable {
        DatabasePlatform platform = getSession().getPlatform(); 
        if (!platform.isOracle()) {
            throw new TestWarningException("This test case works on Oracle only");
        }
        if(platform instanceof Oracle8Platform) {
            Oracle8Platform platform8 = (Oracle8Platform)platform;
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

    public void reset() throws Throwable {
        super.reset();
        DatabasePlatform platform = getSession().getPlatform(); 
        if(!shouldUseLocatorForLOBWrite) {
            platform.setUsesStringBinding(usesStringBindingOriginal);
        }
        if(platform instanceof Oracle8Platform) {
            Oracle8Platform platform8 = (Oracle8Platform)platform;
            if(shouldSetUseLocatorForLOBWriteIntoPlatform) {
                platform8.setShouldUseLocatorForLOBWrite(shouldUseLocatorForLOBWriteOriginal);
            }
        }
    }
}
