/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpars.test;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.persistence.jpars.test.server.ServerEmployeeTestV2;
import org.eclipse.persistence.jpars.test.service.MarshalUnmarshalTestV2;
import org.eclipse.persistence.jpars.test.util.RestUtils;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

public class VersionedTestSuite extends Suite {
    private static String[] v2TestClasses = { ServerEmployeeTestV2.class.getName(), MarshalUnmarshalTestV2.class.getName() };

    public VersionedTestSuite(Class<?> clazz, RunnerBuilder builder) throws InitializationError
    {
        super(clazz, builder);
        try {
            filter(new Filter() {
                @Override
                public boolean shouldRun(Description description) {
                    try {
                        URI serverUri = RestUtils.getServerURI();
                        if ((serverUri != null) && (description != null)) {
                            if (serverUri.toASCIIString().contains("v2.0")) {
                                if (isVersionTwoTestClass(description.getClassName())) {
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                if (isVersionTwoTestClass(description.getClassName())) {
                                    return false;
                                } else {
                                    return true;
                                }
                            }
                        }
                        return false;
                    } catch (URISyntaxException ex) {
                        ex.printStackTrace();
                        return false;
                    }
                }

                private boolean isVersionTwoTestClass(String className) {
                    if (className != null) {
                        for (int i = 0; i < v2TestClasses.length; i++) {
                            String v2Class = v2TestClasses[i];
                            if (v2Class.equals(className)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }

                @Override
                public String describe() {
                    return "";
                }
            });
        } catch (NoTestsRemainException ex) {
            ex.printStackTrace();
        }
    }
}