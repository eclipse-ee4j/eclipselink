/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/ 
package commonj.sdo.impl;

import org.eclipse.persistence.sdo.SDOResolvable;
import org.eclipse.persistence.sdo.helper.SDOCopyHelper;
import org.eclipse.persistence.sdo.helper.SDODataFactory;
import org.eclipse.persistence.sdo.helper.SDODataHelper;
import org.eclipse.persistence.sdo.helper.SDOEqualityHelper;
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

public class SDOHelperProvider extends HelperProvider {
    // a list Helper will be maintained here for convenience.
    private static final TypeHelper typeHelper = new SDOTypeHelperDelegator();
    private static final XSDHelper xsdHelper = new SDOXSDHelperDelegator();
    private static final XMLHelper xmlHelper = new SDOXMLHelperDelegator();
    private static final DataFactory dataFactory = new SDODataFactory();
    private static final DataHelper dataHelper = new SDODataHelper();
    private static final CopyHelper copyHelper = new SDOCopyHelper();
    private static final EqualityHelper equalityHelper = new SDOEqualityHelper();

    public SDOHelperProvider() {
    }

    public CopyHelper copyHelper() {
        return copyHelper;
    }

    public DataFactory dataFactory() {
        return dataFactory;
    }

    public DataHelper dataHelper() {
        return dataHelper;
    }

    public EqualityHelper equalityHelper() {
        return equalityHelper;
    }

    /**
     * Act as a factory for producing TypeHelper.
     * @return    generated TypeHelper.
     */
    public TypeHelper typeHelper() {
        return typeHelper;
    }

    public XMLHelper xmlHelper() {
        return xmlHelper;
    }

    /**
     * Act as a factory for producing XSDHelper.
     * @return    generated XSDHelper.
     */
    public XSDHelper xsdHelper() {
        return xsdHelper;
    }

    /**
     * This class handles resolving objects from a deserialized stream for
     * Reading
     */
    public ExternalizableDelegator.Resolvable resolvable() {
        return new SDOResolvable(HelperProvider.getDefaultContext());
    }

    /**
     * This class handles custom serialization of target objects for
     * Writing
     */
    public ExternalizableDelegator.Resolvable resolvable(Object target) {
        return new SDOResolvable(target, HelperProvider.getDefaultContext());
    }
}
