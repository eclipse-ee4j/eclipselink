/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
    protected boolean ociOnlyMode;
    protected DatabasePlatform platformOriginal;
    protected boolean shouldUseLocatorForLOBWrite;
    protected boolean shouldUseLocatorForLOBWriteOriginal;
    protected Oracle8Platform platform8;
    protected boolean usesStreamForBindingOriginal;
    protected boolean usesByteArrayBindingOriginal;
    protected boolean usesStringBindingOriginal;

    public LOBTestWrapper(AutoVerifyTestCase test) {
        super(test);
        this.shouldUseLocatorForLOBWrite = true;
    }

    public LOBTestWrapper(AutoVerifyTestCase test, boolean ociOnlyMode) {
        super(test);
        this.ociOnlyMode = ociOnlyMode;
        setName(getName() + " OCI only mode: doesn't use LOBLocator, uses ByteArrayBinding, StreamsForBinding and StringBinding");
    }

    protected Oracle8Platform getOracle8Platform() throws ClassCastException {
        return (Oracle8Platform)getSession().getPlatform();
    }

    protected void setup() throws Throwable {
        if (!getSession().getPlatform().isOracle()) {
            throw new TestWarningException("This test case works on Oracle only");
        }
        try {
            platform8 = getOracle8Platform();
        } catch (ClassCastException ex) {
            if (!ociOnlyMode) {
                DatabasePlatform platform = getSession().getPlatform();
                try {
                    getSession().getLogin().usePlatform(new Oracle8Platform());
                    getDatabaseSession().logout();
                    getDatabaseSession().login();
                    platform8 = getOracle8Platform();
                    platformOriginal = platform;
                } catch (Exception ex2) {
                    throw new TestWarningException("Test with shouldUseLocatorForLOBWrite=true requires Oracle8Platform or higher");
                }
            }
        }
        if (platform8 != null) {
            shouldUseLocatorForLOBWriteOriginal = platform8.shouldUseLocatorForLOBWrite();
            shouldUseLocatorForLOBWrite = !ociOnlyMode;
            if (shouldUseLocatorForLOBWrite != shouldUseLocatorForLOBWriteOriginal) {
                platform8.setShouldUseLocatorForLOBWrite(shouldUseLocatorForLOBWrite);
            }
        }

        if (ociOnlyMode) {
            usesByteArrayBindingOriginal = getSession().getPlatform().usesByteArrayBinding();
            getSession().getPlatform().setUsesByteArrayBinding(true);
            usesStringBindingOriginal = getSession().getPlatform().usesStringBinding();
            getSession().getPlatform().setUsesStringBinding(true);
            usesStreamForBindingOriginal = getSession().getPlatform().usesStreamsForBinding();
            getSession().getPlatform().setUsesStreamsForBinding(true);
        }
        super.setup();
    }

    public void reset() throws Throwable {
        super.reset();
        if (ociOnlyMode) {
            getSession().getPlatform().setUsesByteArrayBinding(usesByteArrayBindingOriginal);
            getSession().getPlatform().setUsesStringBinding(usesStringBindingOriginal);
            getSession().getPlatform().setUsesStreamsForBinding(usesStreamForBindingOriginal);
        }
        if (platform8 != null) {
            if (shouldUseLocatorForLOBWrite != shouldUseLocatorForLOBWriteOriginal) {
                getOracle8Platform().setShouldUseLocatorForLOBWrite(shouldUseLocatorForLOBWriteOriginal);
            }
            if (platformOriginal != null) {
                getSession().getLogin().usePlatform(platformOriginal);
                getDatabaseSession().logout();
                getDatabaseSession().login();
                platformOriginal = null;
            }
            platform8 = null;
        }
    }
}
