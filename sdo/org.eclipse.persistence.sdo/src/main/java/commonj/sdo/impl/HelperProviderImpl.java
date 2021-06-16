/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package commonj.sdo.impl;

import org.eclipse.persistence.sdo.SDOResolvable;
import org.eclipse.persistence.sdo.helper.SDOCopyHelper;
import org.eclipse.persistence.sdo.helper.SDODataFactory;
import org.eclipse.persistence.sdo.helper.SDODataHelper;
import org.eclipse.persistence.sdo.helper.SDOEqualityHelper;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.sdo.helper.delegates.SDODataFactoryDelegator;
import org.eclipse.persistence.sdo.helper.delegates.SDOTypeHelperDelegator;
import org.eclipse.persistence.sdo.helper.delegates.SDOXMLHelperDelegator;
import org.eclipse.persistence.sdo.helper.delegates.SDOXSDHelperDelegator;
import commonj.sdo.helper.CopyHelper;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.DataHelper;
import commonj.sdo.helper.EqualityHelper;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;

public class HelperProviderImpl extends HelperProvider {

    private static final SDOXMLHelper xmlHelper = new SDOXMLHelperDelegator();
    private static final SDOTypeHelper typeHelper = new SDOTypeHelperDelegator();
    private static final SDOXSDHelper xsdHelper = new SDOXSDHelperDelegator();
    private static final SDODataFactory dataFactory = new SDODataFactoryDelegator();
    private static final SDODataHelper dataHelper = new SDODataHelper();
    private static final SDOCopyHelper copyHelper = new SDOCopyHelper();
    private static final SDOEqualityHelper equalityHelper = new SDOEqualityHelper();

    public HelperProviderImpl() {
        super();
    }

    @Override
    public CopyHelper copyHelper() {
        return copyHelper;
    }

    @Override
    public DataFactory dataFactory() {
        return dataFactory;
    }

    @Override
    public DataHelper dataHelper() {
        return dataHelper;
    }

    @Override
    public EqualityHelper equalityHelper() {
        return equalityHelper;
    }

    @Override
    public TypeHelper typeHelper() {
        return typeHelper;
    }

    @Override
    public XMLHelper xmlHelper() {
        return xmlHelper;
    }

    @Override
    public XSDHelper xsdHelper() {
        return xsdHelper;
    }

    /**
     * This class handles resolving objects from a deserialized stream for
     * Reading
     */
    @Override
    public ExternalizableDelegator.Resolvable resolvable() {
        return new SDOResolvable(HelperProvider.getDefaultContext());
    }

    /**
     * This class handles custom serialization of target objects for
     * Writing
     */
    @Override
    public ExternalizableDelegator.Resolvable resolvable(Object target) {
        return new SDOResolvable(target, HelperProvider.getDefaultContext());
    }
}
