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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.IntegrityException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.sessions.Project;


/**
 * Test descriptor validation of invalid contructors.
 */
public class ConstructorTest extends ExceptionTest {
    protected EclipseLinkException secondExpectedException;
    protected EclipseLinkException thirdExpectedException;

    public void setup() {
        this.expectedException = DescriptorException.illegalAccessWhileConstructorInstantiation(null, null);
        this.secondExpectedException = DescriptorException.noSuchMethodWhileConstructorInstantiation(null, null);
        this.thirdExpectedException = DescriptorException.noSuchMethodWhileInitializingInstantiationPolicy(null, null, null);
    }

    public void test() {
        org.eclipse.persistence.sessions.DatabaseSession testSession = null;
        try {
            Project project = new ConstructorProject();
            project.setLogin(getSession().getLogin());
            testSession = project.createDatabaseSession();
            testSession.dontLogMessages();
            testSession.login();
        } catch (org.eclipse.persistence.exceptions.EclipseLinkException exception) {
            caughtException = exception;

        } catch (Exception e) {
            throw new org.eclipse.persistence.testing.framework.TestWarningException("A non-TopLink exception is caught.");
        } finally {
            if(testSession != null) {
                if(testSession.isConnected()) {
                    testSession.logout();
                }
            }
        }
    }

    protected void verify() {
        if (caughtException == null) {
            throwError("The proper exception was not thrown:" + org.eclipse.persistence.internal.helper.Helper.cr() + "caught exception was null! " + org.eclipse.persistence.internal.helper.Helper.cr() + org.eclipse.persistence.internal.helper.Helper.cr() + "[EXPECTING] " + expectedException + " or " + secondExpectedException);
        }

        if (caughtException instanceof IntegrityException) {
            boolean illegalAccessConstructorInit = false;
            boolean noSuchMethodConstructorInit = false;
            boolean noSuchMethodInstantiationInit = false;

            IntegrityChecker ic = new IntegrityChecker();
            ic = ((IntegrityException)caughtException).getIntegrityChecker();
            java.util.Vector exceptionList = ic.getCaughtExceptions();

            for (int i = 0; i < exceptionList.size(); i++) {
                if (exceptionList.elementAt(i) instanceof DescriptorException) {
                    if (((DescriptorException)(exceptionList.elementAt(i))).getErrorCode() == DescriptorException.ILLEGAL_ACCESS_WHILE_CONSTRUCTOR_INSTANTIATION) {
                        illegalAccessConstructorInit = true;
                    }

                    if (((DescriptorException)(exceptionList.elementAt(i))).getErrorCode() == DescriptorException.NO_SUCH_METHOD_WHILE_CONSTRUCTOR_INSTANTIATION) {
                        noSuchMethodConstructorInit = true;
                    }

                    if (((DescriptorException)(exceptionList.elementAt(i))).getErrorCode() == DescriptorException.NO_SUCH_METHOD_WHILE_INITIALIZING_INSTANTIATION_POLICY) {
                        noSuchMethodInstantiationInit = true;
                    }
                }
            }

            String cr = org.eclipse.persistence.internal.helper.Helper.cr();

            if (!illegalAccessConstructorInit && !noSuchMethodConstructorInit && !noSuchMethodInstantiationInit) {
                throw new org.eclipse.persistence.testing.framework.TestErrorException("The proper exception was not thrown:" + cr + "[CAUGHT] " + caughtException + cr + cr + "[EXPECTING] " + expectedException.getMessage() + cr + " or " + secondExpectedException.getMessage() + cr + " or " + thirdExpectedException.getMessage());
            }
        }
    }
}
