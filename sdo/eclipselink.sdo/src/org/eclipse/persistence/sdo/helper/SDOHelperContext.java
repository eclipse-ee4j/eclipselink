/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  

/**
 * INTERNAL:
 * <b>Purpose:</b>
 * <ul><li>This class is a reference implementation of an instantiable {@link Sequence commonj.sdo.HelperContext}.</li>
 * </ul>
 * <b>Responsibilities:</b>
 * <ul>
 * <li>Provide access to a instances of helper objects.</li>
 * </ul>
 * <p/>
 * Use this class in place of the default HelperProvider.DefaultHelperContext implementation.
 * <br/>
 *
 * @since Oracle TopLink 11.1.1.0.0
 */
package org.eclipse.persistence.sdo.helper;

import org.eclipse.persistence.sdo.SDOResolvable;
import org.eclipse.persistence.sdo.helper.delegates.SDOTypeHelperDelegate;
import org.eclipse.persistence.sdo.helper.delegates.SDOXMLHelperDelegate;
import org.eclipse.persistence.sdo.helper.delegates.SDOXSDHelperDelegate;
import commonj.sdo.helper.CopyHelper;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.DataHelper;
import commonj.sdo.helper.EqualityHelper;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;
import commonj.sdo.impl.ExternalizableDelegator;

public class SDOHelperContext implements HelperContext {
    private final CopyHelper copyHelper = new SDOCopyHelper(this);
    private final DataFactory dataFactory = new SDODataFactory(this);
    private final DataHelper dataHelper = new SDODataHelper(this);
    private final EqualityHelper equalityHelper = new SDOEqualityHelper(this);
    private final TypeHelper typeHelper = new SDOTypeHelperDelegate(this);
    private final XMLHelper xmlHelper = new SDOXMLHelperDelegate(this);
    private final XSDHelper xsdHelper = new SDOXSDHelperDelegate(this);

    public SDOHelperContext() {
        //AbstractSessionLog.getLog().log(AbstractSessionLog.FINEST, "{0} creating new instance of {1}", //
        //		new Object[] {getClass().getName(), this}, false);
    }

    public void reset() {
        ((SDOTypeHelper)getTypeHelper()).reset();
        ((SDOXMLHelper)getXMLHelper()).reset();
        ((SDOXSDHelper)getXSDHelper()).reset();
    }

    public CopyHelper getCopyHelper() {
        return copyHelper;
    }

    public DataFactory getDataFactory() {
        return dataFactory;
    }

    public DataHelper getDataHelper() {
        return dataHelper;
    }

    public EqualityHelper getEqualityHelper() {
        return equalityHelper;
    }

    public TypeHelper getTypeHelper() {
        return typeHelper;
    }

    public XMLHelper getXMLHelper() {
        return xmlHelper;
    }

    public XSDHelper getXSDHelper() {
        return xsdHelper;
    }

    public ExternalizableDelegator.Resolvable createResolvable() {
        return new SDOResolvable(this);
    }

    public ExternalizableDelegator.Resolvable createResolvable(Object target) {
        return new SDOResolvable(target, this);
    }
}