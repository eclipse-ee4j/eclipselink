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
 *     dmccann - September 15/2009 - 1.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.sdo;

import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sdo.helper.SDOCopyHelper;
import org.eclipse.persistence.sdo.helper.SDODataFactory;
import org.eclipse.persistence.sdo.helper.SDODataHelper;
import org.eclipse.persistence.sdo.helper.SDOEqualityHelper;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.sdo.types.SDOChangeSummaryType;
import org.eclipse.persistence.sdo.types.SDODataObjectType;
import org.eclipse.persistence.sdo.types.SDODataType;
import org.eclipse.persistence.sdo.types.SDOObjectType;
import org.eclipse.persistence.sdo.types.SDOOpenSequencedType;
import org.eclipse.persistence.sdo.types.SDOPropertyType;
import org.eclipse.persistence.sdo.types.SDOTypeType;
import org.eclipse.persistence.sdo.types.SDOWrapperType;
import org.eclipse.persistence.sdo.types.SDOXMLHelperLoadOptionsType;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.CopyHelper;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.DataHelper;
import commonj.sdo.helper.EqualityHelper;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;
import commonj.sdo.impl.HelperProvider;

/**
 * This class provides a mechanism to obtain the EclipseLink implementation of various SDO runtime
 * classes based on a given SDO class/interface. This is useful for accessing extended EclipseLink
 * features. Using this helper class will alleviate the need for consumers of EclipseLink SDO to
 * perform casts where their code makes use of the standard SDO API. In addition, a given SDO
 * class/interface can be unwrapped based on a user-specified class. This will allow access - in
 * certain cases - to a given SDO implementation class' underlying class(es). For example, an
 * XMLHelper could be unwrapped resulting in an EclipseLink SDOXMLHelper or its underlying
 * XMLContext.
 */
public class SDOHelper {

    /**
     * Return the EclipseLink implementation of Type. The given Type is assumed to be an instance of
     * {@link org.eclipse.persistence.sdo.SDOType}. If not, an exception will be thrown.
     * 
     * @param type
     * @return
     * @throws IllegalArgumentException
     */
    public static SDOType getType(Type type) throws IllegalArgumentException {
        try {
            return (SDOType) type;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("sdo_helper_invalid_type", new Object[] { type.getClass() }));
        }
    }

    /**
     * Return the EclipseLink implementation of Property. The given Property is assumed to be an
     * instance of {@link org.eclipse.persistence.sdo.SDOProperty}. If not, an exception will be
     * thrown.
     * 
     * @param property
     * @return
     * @throws IllegalArgumentException
     */
    public static SDOProperty getProperty(Property property) throws IllegalArgumentException {
        try {
            return (SDOProperty) property;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("sdo_helper_invalid_property", new Object[] { property.getClass() }));
        }
    }

    /**
     * Return the EclipseLink implementation of DataObject. The given DataObject is assumed to be an
     * instance of {@link org.eclipse.persistence.sdo.SDODataObject}. If not, an exception will be
     * thrown.
     * 
     * @param dataObject
     * @return
     * @throws IllegalArgumentException
     */
    public static SDODataObject getDataObject(DataObject dataObject) throws IllegalArgumentException {
        try {
            return (SDODataObject) dataObject;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("sdo_helper_invalid_dataobject", new Object[] { dataObject.getClass() }));
        }
    }

    /**
     * Return the EclipseLink implementation of ChangeSummary. The given ChangeSummary is assumed to
     * be an instance of {@link org.eclipse.persistence.sdo.SDOChangeSummary}. If not, an exception
     * will be thrown.
     * 
     * @param changeSummary
     * @return
     * @throws IllegalArgumentException
     */
    public static SDOChangeSummary getChangeSummary(ChangeSummary changeSummary) throws IllegalArgumentException {
        try {
            return (SDOChangeSummary) changeSummary;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("sdo_helper_invalid_changesummary", new Object[] { changeSummary.getClass() }));
        }
    }

    /**
     * Return the EclipseLink implementation of Sequence. The given Sequence is assumed to be an
     * instance of {@link org.eclipse.persistence.sdo.SDOSequence}. If not, an exception will be
     * thrown.
     * 
     * @param sequence
     * @return
     * @throws IllegalArgumentException
     */
    public static SDOSequence getSequence(Sequence sequence) throws IllegalArgumentException {
        try {
            return (SDOSequence) sequence;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("sdo_helper_invalid_sequence", new Object[] { sequence.getClass() }));
        }
    }

    /**
     * Return the EclipseLink implementation of HelperContext.  If the given HelperContext
     * is an instance of the DefaultContext, an SDOHelperContext will be obtained via 
     * SDOHelperContext.getHelperContext().  Otherwise, the given HelperContext is assumed to be an
     * instance of {@link org.eclipse.persistence.sdo.helper.SDOHelperContext}. If not, an exception 
     * will be thrown.

     * @param helperContext
     * @return
     * @throws IllegalArgumentException
     */
    public static SDOHelperContext getHelperContext(HelperContext helperContext) throws IllegalArgumentException {
        if (helperContext == HelperProvider.getDefaultContext()) {
            return (SDOHelperContext) SDOHelperContext.getHelperContext();
        }
        try {
            return (SDOHelperContext) helperContext;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("sdo_helper_invalid_helpercontext", new Object[] { helperContext.getClass() }));
        }
    }

    /**
     * Return the EclipseLink implementation of CopyHelper. The given CopyHelper is assumed to be an
     * instance of {@link org.eclipse.persistence.sdo.helper.SDOCopyHelper}. If not, an exception 
     * will be thrown.
     * 
     * @param copyHelper
     * @return
     * @throws IllegalArgumentException
     */
    public static SDOCopyHelper getCopyHelper(CopyHelper copyHelper) throws IllegalArgumentException {
        try {
            return (SDOCopyHelper) copyHelper;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("sdo_helper_invalid_copyhelper", new Object[] { copyHelper.getClass() }));
        }
    }

    /**
     * Return the EclipseLink implementation of DataFactory.  The given DataFactory is assumed to be an
     * instance of {@link org.eclipse.persistence.sdo.helper.SDODataFactory}. If not, an exception 
     * will be thrown.
     * 
     * @param dataFactory
     * @return
     * @throws IllegalArgumentException
     */
    public static SDODataFactory getDataFactory(DataFactory dataFactory) throws IllegalArgumentException {
        try {
            return (SDODataFactory) dataFactory;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("sdo_helper_invalid_datafactory", new Object[] { dataFactory.getClass() }));
        }
    }

    /**
     * Return the EclipseLink implementation of DataHelper.  The given DataHelper is assumed to be an
     * instance of {@link org.eclipse.persistence.sdo.helper.SDODataHelper}. If not, an exception 
     * will be thrown.
     * 
     * @param dataHelper
     * @return
     * @throws IllegalArgumentException
     */
    public static SDODataHelper getDataHelper(DataHelper dataHelper) throws IllegalArgumentException {
        try {
            return (SDODataHelper) dataHelper;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("sdo_helper_invalid_datahelper", new Object[] { dataHelper.getClass() }));
        }
    }

    /**
     * Return the EclipseLink implementation of EqualityHelper.  The given EqualityHelper is assumed to be an
     * instance of {@link org.eclipse.persistence.sdo.helper.SDOEqualityHelper}. If not, an exception 
     * will be thrown.
     * 
     * @param equalityHelper
     * @return
     * @throws IllegalArgumentException
     */
    public static SDOEqualityHelper getEqualityHelper(EqualityHelper equalityHelper) throws IllegalArgumentException {
        try {
            return (SDOEqualityHelper) equalityHelper;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("sdo_helper_invalid_equalityhelper", new Object[] { equalityHelper.getClass() }));
        }
    }

    /**
     * Return the EclipseLink implementation of TypeHelper.  The given TypeHelper is assumed to be an
     * instance of {@link org.eclipse.persistence.sdo.helper.SDOTypeHelper}. If not, an exception 
     * will be thrown.
     * 
     * @param typeHelper
     * @return
     * @throws IllegalArgumentException
     */
    public static SDOTypeHelper getTypeHelper(TypeHelper typeHelper) throws IllegalArgumentException {
        try {
            return (SDOTypeHelper) typeHelper;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("sdo_helper_invalid_typehelper", new Object[] { typeHelper.getClass() }));
        }
    }

    /**
     * Return the EclipseLink implementation of XMLHelper.  The given XMLHelper is assumed to be an
     * instance of {@link org.eclipse.persistence.sdo.helper.SDOXMLHelper}. If not, an exception 
     * will be thrown.
     * 
     * @param xmlHelper
     * @return
     * @throws IllegalArgumentException
     */
    public static SDOXMLHelper getXMLHelper(XMLHelper xmlHelper) throws IllegalArgumentException {
        try {
            return (SDOXMLHelper) xmlHelper;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("sdo_helper_invalid_xmlhelper", new Object[] { xmlHelper.getClass() }));
        }
    }

    /**
     * Return the EclipseLink implementation of XSDHelper.  The given XSDHelper is assumed to be an
     * instance of {@link org.eclipse.persistence.sdo.helper.SDOXSDHelper}. If not, an exception 
     * will be thrown.
     * 
     * @param xsdHelper
     * @return
     * @throws IllegalArgumentException
     */
    public static SDOXSDHelper getXSDHelper(XSDHelper xsdHelper) throws IllegalArgumentException {
        try {
            return (SDOXSDHelper) xsdHelper;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("sdo_helper_invalid_xsdhelper", new Object[] { xsdHelper.getClass() }));
        }
    }

    /**
     * Unwraps a given Type resulting in an EclipseLink SDOType. Assumes that the
     * given Type is an instance of EclipseLink SDOType, and clazz is one of:
     *   org.eclipse.persistence.sdo.SDOType,
     *   org.eclipse.persistence.sdo.type.SDOTypeType,
     *   org.eclipse.persistence.sdo.type.SDOPropertyType,
     *   org.eclipse.persistence.sdo.type.SDOChangeSummaryType,
     *   org.eclipse.persistence.sdo.type.SDODataObjectType,
     *   org.eclipse.persistence.sdo.type.SDODataType,
     *   org.eclipse.persistence.sdo.type.SDOOpenSequencedType,
     *   org.eclipse.persistence.sdo.type.SDOObjectType,
     *   org.eclipse.persistence.sdo.type.SDOWrapperType,
     *   org.eclipse.persistence.sdo.type.SDOXMLHelperLoadOptionsType
     * 
     * @param <T>
     * @param type
     * @param clazz
     * @return
     * @throws IllegalArgumentException
     */
    public static <T> T unwrap(Type type, Class<T> clazz) throws IllegalArgumentException {
        if (clazz == SDOType.class) {
            return (T) getType(type);
        }
        try {
            if (clazz == SDOTypeType.class) {
                return (T) ((SDOTypeType) type);
            }
            if (clazz == SDOPropertyType.class) {
                return (T) ((SDOPropertyType) type);
            }
            if (clazz == SDOChangeSummaryType.class) {
                return (T) ((SDOChangeSummaryType) type);
            }
            if (clazz == SDODataObjectType.class) {
                return (T) ((SDODataObjectType) type);
            }
            if (clazz == SDODataType.class) {
                return (T) ((SDODataType) type);
            }
            if (clazz == SDOOpenSequencedType.class) {
                return (T) ((SDOOpenSequencedType) type);
            }
            if (clazz == SDOWrapperType.class) {
                return (T) ((SDOWrapperType) type);
            }
            if (clazz == SDOXMLHelperLoadOptionsType.class) {
                return (T) ((SDOXMLHelperLoadOptionsType) type);
            }
            if (clazz == SDOObjectType.class) {
                return (T) ((SDOObjectType) type);
            }
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("sdo_helper_invalid_target_for_type", new Object[] { clazz }));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("sdo_helper_invalid_type", new Object[] { type.getClass() }));
        }
    }

    /**
     * Unwraps a given Property resulting in an EclipseLink SDOProperty. Assumes that the
     * given Property is an instance of EclipseLink SDOProperty, and clazz is
     * org.eclipse.persistence.sdo.SDOProperty.
     * 
     * @param <T>
     * @param property
     * @param clazz
     * @return
     * @throws IllegalArgumentException
     */
    public static <T> T unwrap(Property property, Class<T> clazz) throws IllegalArgumentException {
        if (clazz == org.eclipse.persistence.sdo.SDOProperty.class) {
            return (T) getProperty(property);
        }
        throw new IllegalArgumentException(ExceptionLocalization
                .buildMessage("sdo_helper_invalid_target_for_property", new Object[] { clazz }));
    }

    /**
     * Unwraps a given DataObject resulting in an EclipseLink SDODataObject. Assumes that the
     * given DataObject is an instance of EclipseLink SDODataObject, and clazz is
     * org.eclipse.persistence.sdo.SDODataObject.
     * 
     * @param <T>
     * @param dataObject
     * @param clazz
     * @return
     * @throws IllegalArgumentException
     */
    public static <T> T unwrap(DataObject dataObject, Class<T> clazz) throws IllegalArgumentException {
        if (clazz == org.eclipse.persistence.sdo.SDODataObject.class) {
            return (T) getDataObject(dataObject);
        }
        throw new IllegalArgumentException(ExceptionLocalization
                .buildMessage("sdo_helper_invalid_target_for_dataobject", new Object[] { clazz }));
    }

    /**
     * Unwraps a given ChangeSummary resulting in an EclipseLink SDOChangeSummary. Assumes that the
     * given ChangeSummary is an instance of EclipseLink SDOChangeSummary, and clazz is
     * org.eclipse.persistence.sdo.SDOChangeSummary.
     * 
     * @param <T>
     * @param changeSummary
     * @param clazz
     * @return
     * @throws IllegalArgumentException
     */
    public static <T> T unwrap(ChangeSummary changeSummary, Class<T> clazz) throws IllegalArgumentException {
        if (clazz == org.eclipse.persistence.sdo.SDOChangeSummary.class) {
            return (T) getChangeSummary(changeSummary);
        }
        throw new IllegalArgumentException(ExceptionLocalization
                .buildMessage("sdo_helper_invalid_target_for_changesummary", new Object[] { clazz }));
    }

    /**
     * Unwraps a given Sequence resulting in an EclipseLink SDOSequence. Assumes that the
     * given Sequence is an instance of EclipseLink SDOSequence, and clazz is
     * org.eclipse.persistence.sdo.SDOSequence.
     * 
     * @param <T>
     * @param sequence
     * @param clazz
     * @return
     * @throws IllegalArgumentException
     */
    public static <T> T unwrap(Sequence sequence, Class<T> clazz) throws IllegalArgumentException {
        if (clazz == org.eclipse.persistence.sdo.SDOSequence.class) {
            return (T) getSequence(sequence);
        }
        throw new IllegalArgumentException(ExceptionLocalization
                .buildMessage("sdo_helper_invalid_target_for_sequence", new Object[] { clazz }));
    }

    /**
     * Unwraps a given HelperContext resulting in an EclipseLink SDOHelperContext. Assumes that the
     * given HelperContext is an instance of EclipseLink SDOHelperContext, and clazz is
     * org.eclipse.persistence.sdo.helper.SDOHelperContext.
     * 
     * @param <T>
     * @param helperContext
     * @param clazz
     * @return
     * @throws IllegalArgumentException
     */
    public static <T> T unwrap(HelperContext helperContext, Class<T> clazz) throws IllegalArgumentException {
        if (clazz == org.eclipse.persistence.sdo.helper.SDOHelperContext.class) {
            return (T) getHelperContext(helperContext);
        }
        throw new IllegalArgumentException(ExceptionLocalization
                .buildMessage("sdo_helper_invalid_target_for_helpercontext", new Object[] { clazz }));
    }

    /**
     * Unwraps a given CopyHelper resulting in an EclipseLink SDOCopyHelper. Assumes that the
     * given CopyHelper is an instance of EclipseLink SDOCopyHelper, and clazz is
     * org.eclipse.persistence.sdo.helper.SDOCopyHelper.
     * 
     * @param <T>
     * @param copyHelper
     * @param clazz
     * @return
     * @throws IllegalArgumentException
     */
    public static <T> T unwrap(CopyHelper copyHelper, Class<T> clazz) throws IllegalArgumentException {
        if (clazz == org.eclipse.persistence.sdo.helper.SDOCopyHelper.class) {
            return (T) getCopyHelper(copyHelper);
        }
        throw new IllegalArgumentException(ExceptionLocalization
                .buildMessage("sdo_helper_invalid_target_for_copyhelper", new Object[] { clazz }));
    }

    /**
     * Unwraps a given DataFactory resulting in an EclipseLink SDODataFactory. Assumes that the
     * given DataFactory is an instance of EclipseLink SDODataFactory, and clazz is
     * org.eclipse.persistence.sdo.helper.SDODataFactory.
     * 
     * @param <T>
     * @param dataFactory
     * @param clazz
     * @return
     * @throws IllegalArgumentException
     */
    public static <T> T unwrap(DataFactory dataFactory, Class<T> clazz) throws IllegalArgumentException {
        if (clazz == org.eclipse.persistence.sdo.helper.SDODataFactory.class) {
            return (T) getDataFactory(dataFactory);
        }
        throw new IllegalArgumentException(ExceptionLocalization
                .buildMessage("sdo_helper_invalid_target_for_datafactory", new Object[] { clazz }));
    }

    /**
     * Unwraps a given DataHelper resulting in an EclipseLink SDODataHelper. Assumes that the
     * given DataHelper is an instance of EclipseLink SDODataHelper, and clazz is
     * org.eclipse.persistence.sdo.helper.SDODataHelper.
     * 
     * @param <T>
     * @param dataHelper
     * @param clazz
     * @return
     * @throws IllegalArgumentException
     */
    public static <T> T unwrap(DataHelper dataHelper, Class<T> clazz) throws IllegalArgumentException {
        if (clazz == org.eclipse.persistence.sdo.helper.SDODataHelper.class) {
            return (T) getDataHelper(dataHelper);
        }
        throw new IllegalArgumentException(ExceptionLocalization
                .buildMessage("sdo_helper_invalid_target_for_datahelper", new Object[] { clazz }));
    }

    /**
     * Unwraps a given EqualityHelper resulting in an EclipseLink SDOEqualityHelper. Assumes that the
     * given EqualityHelper is an instance of EclipseLink SDOEqualityHelper, and clazz is
     * org.eclipse.persistence.sdo.helper.SDOEqualityHelper.
     * 
     * @param <T>
     * @param equalityHelper
     * @param clazz
     * @return
     * @throws IllegalArgumentException
     */
    public static <T> T unwrap(EqualityHelper equalityHelper, Class<T> clazz) throws IllegalArgumentException {
        if (clazz == org.eclipse.persistence.sdo.helper.SDOEqualityHelper.class) {
            return (T) getEqualityHelper(equalityHelper);
        }
        throw new IllegalArgumentException(ExceptionLocalization
                .buildMessage("sdo_helper_invalid_target_for_equalityhelper", new Object[] { clazz }));
    }

    /**
     * Unwraps a given TypeHelper resulting in an EclipseLink SDOTypeHelper. Assumes that the
     * given TypeHelper is an instance of EclipseLink SDOTypeHelper, and clazz is
     * org.eclipse.persistence.sdo.helper.SDOTypeHelper.
     * 
     * @param <T>
     * @param typeHelper
     * @param clazz
     * @return
     * @throws IllegalArgumentException
     */
    public static <T> T unwrap(TypeHelper typeHelper, Class<T> clazz) throws IllegalArgumentException {
        if (clazz == org.eclipse.persistence.sdo.helper.SDOTypeHelper.class) {
            return (T) getTypeHelper(typeHelper);
        }
        throw new IllegalArgumentException(ExceptionLocalization
                .buildMessage("sdo_helper_invalid_target_for_typehelper", new Object[] { clazz }));
    }

    /**
     * Unwraps a given XMLHelper resulting in an EclipseLink SDOXMLHelper or an EclipseLink 
     * XMLContext depending on clazz.  Assumes that the given XMLHelper is an instance of 
     * EclipseLink SDOXMLHelper, and clazz is one of 
     * org.eclipse.persistence.sdo.helper.SDOXMLHelper, or
     * org.eclipse.persistence.oxm.XMLContext.  If not, an exception will be thrown.
     * 
     * @param <T>
     * @param xmlHelper
     * @param clazz
     * @return
     * @throws IllegalArgumentException
     */
    public static <T> T unwrap(XMLHelper xmlHelper, Class<T> clazz) throws IllegalArgumentException {
        try {
            org.eclipse.persistence.sdo.helper.SDOXMLHelper xmlHelperImpl = (org.eclipse.persistence.sdo.helper.SDOXMLHelper) xmlHelper;
            if (clazz == org.eclipse.persistence.sdo.helper.SDOXMLHelper.class) {
                return (T) xmlHelperImpl;
            }
            if (clazz == XMLContext.class) {
                return (T) xmlHelperImpl.getXmlContext();
            }
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("sdo_helper_invalid_target_for_xmlhelper", new Object[] { clazz }));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("sdo_helper_invalid_xmlhelper", new Object[] { xmlHelper.getClass() }));
        }
    }

    /**
     * Unwraps a given XSDHelper resulting in an EclipseLink SDOXSDHelper. Assumes that the
     * given XSDHelper is an instance of EclipseLink SDOXSDHelper, and clazz is
     * org.eclipse.persistence.sdo.helper.SDOXSDHelper.
     * 
     * @param <T>
     * @param xsdHelper
     * @param clazz
     * @return
     * @throws IllegalArgumentException
     */
    public static <T> T unwrap(XSDHelper xsdHelper, Class<T> clazz) throws IllegalArgumentException {
        if (clazz == org.eclipse.persistence.sdo.helper.SDOXSDHelper.class) {
            return (T) getXSDHelper(xsdHelper);
        }
        throw new IllegalArgumentException(ExceptionLocalization
                .buildMessage("sdo_helper_invalid_target_for_xsdhelper", new Object[] { clazz }));
    }
}
