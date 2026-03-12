/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.expressions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.ReadObjectTest;
import org.eclipse.persistence.testing.framework.TestWarningException;

/**
 * Test expressions for reading objects.
 */
public class ReadObjectExpressionTest extends ReadObjectTest {
    Expression expression;
    /** The class of the target objects to be read from the database. */
    private Class<?> referenceClass;
    private List<Class<? extends DatabasePlatform>> supportedPlatforms;
    private List<Class<? extends DatabasePlatform>> unsupportedPlatforms;

    public ReadObjectExpressionTest(Object theOriginalObject, Expression theExpression) {
        originalObject = theOriginalObject;
        expression = theExpression;
        if (theOriginalObject != null) {
            referenceClass = theOriginalObject.getClass();
        }
    }

    public ReadObjectExpressionTest(Class<?> theReferenceClass, Expression theExpression) {
        referenceClass = theReferenceClass;
        expression = theExpression;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression theExpression) {
        expression = theExpression;
    }

    @Override
    protected void setup() {
        if (!isPlatformSupported(getSession().getLogin().getPlatform())) {
            throw new TestWarningException("(" + getName() + ") This expression is not supported on this platform.");
        }

        // Access and DB2 do not support UPPER and LOWER
        if (getQuery() == null) {
            setQuery(new ReadObjectQuery());
            getQuery().setReferenceClass(referenceClass);
            getQuery().setSelectionCriteria(getExpression());
        }
    }

    public void addSupportedPlatform(Class<? extends DatabasePlatform> platform) {
        if (supportedPlatforms == null) {
            supportedPlatforms = new ArrayList<>();
        }
        supportedPlatforms.add(platform);
    }

    public void addUnsupportedPlatform(Class<? extends DatabasePlatform> platform) {
        if (unsupportedPlatforms == null) {
            unsupportedPlatforms = new ArrayList<>();
        }
        unsupportedPlatforms.add(platform);
    }

    private boolean isPlatformSupported(DatabasePlatform platform) {
        boolean supported = false;
        boolean notSupported = false;
        if ((unsupportedPlatforms == null) && (supportedPlatforms == null)) {
            return true;
        }
        if (supportedPlatforms != null) {
            for (Class<? extends DatabasePlatform> supportedPlatform : supportedPlatforms) {
                Class<? extends DatabasePlatform> platformClass = supportedPlatform;
                if (platformClass.isInstance(platform)) {
                    supported = true;
                }
            }
        } else {
            supported = true;
        }
        if (unsupportedPlatforms != null) {
            for (Class<? extends DatabasePlatform> unsupportedPlatform : unsupportedPlatforms) {
                Class<? extends DatabasePlatform> platformClass = unsupportedPlatform;
                if (platformClass.isInstance(platform)) {
                    notSupported = true;
                }
            }
        }
        return supported && (!notSupported);
    }
}
