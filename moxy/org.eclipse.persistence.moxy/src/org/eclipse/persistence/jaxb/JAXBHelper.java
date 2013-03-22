/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jaxb;

import org.eclipse.persistence.oxm.XMLBinder;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.jaxb.JAXBContext;

/**
 * This class provides a mechanism to obtain the EclipseLink implementation of various JAXB runtime
 * classes based on a given JAXB class/interface. This is useful for accessing extended EclipseLink
 * features. Using this helper class will alleviate the need for consumers of EclipseLink JAXB to
 * perform casts where their code makes use of the standard JAXB API. In addition, a given JAXB
 * class/interface can be unwrapped based on a user-specified class. This will allow access - in
 * certain cases - to a given JAXB implementation class' underlying class(es). For example, a
 * Marshaller could be unwrapped resulting in an EclipseLink JAXBMarshaller, or its underlying
 * XMLMarshaller.
 */
public class JAXBHelper {

    /**
     * Return the EclipseLink implementation of JAXBContext. The given JAXBContext is assumed to be
     * an instance of {@link org.eclipse.persistence.jaxb.JAXBContext}. If not, an exception will be
     * thrown.
     * 
     * @param jaxbContext
     * @return
     * @throws IllegalArgumentException
     */
    public static JAXBContext getJAXBContext(javax.xml.bind.JAXBContext jaxbContext) throws IllegalArgumentException {
        try {
            return (JAXBContext) jaxbContext;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("jaxb_helper_invalid_jaxbcontext", new Object[] { jaxbContext.getClass() }));
        }
    }

    /**
     * Return the EclipseLink implementation of Unmarshaller. The given Unmarshaller is assumed to
     * be an instance of {@link org.eclipse.persistence.jaxb.JAXBUnmarshaller}. If not, an exception
     * will be thrown.
     * 
     * @param jaxbUnmarshaller
     * @return
     * @throws IllegalArgumentException
     */
    public static JAXBUnmarshaller getUnmarshaller(javax.xml.bind.Unmarshaller jaxbUnmarshaller) throws IllegalArgumentException {
        try {
            return (JAXBUnmarshaller) jaxbUnmarshaller;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("jaxb_helper_invalid_unmarshaller", new Object[] { jaxbUnmarshaller.getClass() }));
        }
    }

    /**
     * Return the EclipseLink implementation of Marshaller. The given Marshaller is assumed to be an
     * instance of {@link org.eclipse.persistence.jaxb.JAXBMarshaller}. If not, an exception will be
     * thrown.
     * 
     * @param jaxbMarshaller
     * @return
     * @throws IllegalArgumentException
     */
    public static JAXBMarshaller getMarshaller(javax.xml.bind.Marshaller jaxbMarshaller) throws IllegalArgumentException {
        try {
            return (JAXBMarshaller) jaxbMarshaller;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("jaxb_helper_invalid_marshaller", new Object[] { jaxbMarshaller.getClass() }));
        }
    }

    /**
     * Return the EclipseLink implementation of Binder. The given Binder is assumed to be an
     * instance of {@link org.eclipse.persistence.jaxb.JAXBBinder}. If not, an exception will be
     * thrown.
     * 
     * @param jaxbBinder
     * @return
     * @throws IllegalArgumentException
     */
    public static JAXBBinder getBinder(javax.xml.bind.Binder jaxbBinder) throws IllegalArgumentException {
        try {
            return (JAXBBinder) jaxbBinder;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("jaxb_helper_invalid_binder", new Object[] { jaxbBinder.getClass() }));
        }
    }

    /**
     * Unwraps a given JAXBContext resulting in an EclipseLink JAXBContext, or the EclipseLink
     * JAXBContext's underlying XMLContext. Assumes that the given JAXBContext is an instance of
     * EclipseLink JAXBContext, and clazz is one of org.eclipse.persistence.jaxb.JAXBContext or
     * org.eclipse.persistence.oxm.XMLContext.
     * 
     * @param <T>
     * @param jaxbContext
     * @param clazz
     * @return
     * @see org.eclipse.persistence.jaxb.JAXBContext
     * @see org.eclipse.persistence.oxm.XMLContext
     * @throws IllegalArgumentException
     */
    public static <T> T unwrap(javax.xml.bind.JAXBContext jaxbContext, Class<T> clazz) throws IllegalArgumentException {
        try {
            JAXBContext jaxbContextImpl = (JAXBContext) jaxbContext;
            if (clazz == org.eclipse.persistence.jaxb.JAXBContext.class) {
                return (T) jaxbContextImpl;
            }
            if (clazz == XMLContext.class) {
                return (T) jaxbContextImpl.getXMLContext();
            }
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("jaxb_helper_invalid_target_for_jaxbcontext", new Object[] { clazz }));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("jaxb_helper_invalid_jaxbcontext", new Object[] { jaxbContext.getClass() }));
        }
    }

    /**
     * Unwraps a given Unmarshaller resulting in an EclipseLink JAXBUnmarshaller, or the EclipseLink
     * JAXBUnmarshaller's underlying XMLUnmarshaller. Assumes that the given Unmarshaller is an
     * instance of EclipseLink JAXBUnmarshaller, and clazz is one of
     * org.eclipse.persistence.jaxb.JAXBUnmarshaller or org.eclipse.persistence.oxm.XMLUnmarshaller.
     * 
     * @param <T>
     * @param jaxbUnmarshaller
     * @param clazz
     * @return
     * @see org.eclipse.persistence.jaxb.JAXBUnmarshaller
     * @see org.eclipse.persistence.oxm.XMLUnmarshaller
     * @throws IllegalArgumentException
     */
    public static <T> T unwrap(javax.xml.bind.Unmarshaller jaxbUnmarshaller, Class<T> clazz) throws IllegalArgumentException {
        try {
            JAXBUnmarshaller jaxbUnmarshallerImpl = (JAXBUnmarshaller) jaxbUnmarshaller;
            if (clazz == org.eclipse.persistence.jaxb.JAXBUnmarshaller.class) {
                return (T) jaxbUnmarshallerImpl;
            }
            if (clazz == XMLUnmarshaller.class) {
                return (T) jaxbUnmarshallerImpl.getXMLUnmarshaller();
            }
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("jaxb_helper_invalid_target_for_unmarshaller", new Object[] { clazz }));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("jaxb_helper_invalid_unmarshaller", new Object[] { jaxbUnmarshaller.getClass() }));
        }
    }

    /**
     * Unwraps a given Marshaller resulting in an EclipseLink JAXBMarshaller, or the EclipseLink
     * JAXBMarshaller's underlying XMLMarshaller. Assumes that the given Marshaller is an instance
     * of EclipseLink JAXBMarshaller, and clazz is one of
     * org.eclipse.persistence.jaxb.JAXBMarshaller or org.eclipse.persistence.oxm.XMLMarshaller.
     * 
     * @param <T>
     * @param jaxbMarshaller
     * @param clazz
     * @return
     * @see org.eclipse.persistence.jaxb.JAXBMarshaller
     * @see org.eclipse.persistence.oxm.XMLMarshaller
     * @throws IllegalArgumentException
     */
    public static <T> T unwrap(javax.xml.bind.Marshaller jaxbMarshaller, Class<T> clazz) throws IllegalArgumentException {
        try {
            JAXBMarshaller jaxbMarshallerImpl = (JAXBMarshaller) jaxbMarshaller;
            if (clazz == org.eclipse.persistence.jaxb.JAXBMarshaller.class) {
                return (T) jaxbMarshallerImpl;
            }
            if (clazz == XMLMarshaller.class) {
                return (T) jaxbMarshallerImpl.getXMLMarshaller();
            }
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("jaxb_helper_invalid_target_for_marshaller", new Object[] { clazz }));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("jaxb_helper_invalid_marshaller", new Object[] { jaxbMarshaller.getClass() }));
        }
    }

    /**
     * Unwraps a given Binder resulting in an EclipseLink JAXBBinder, or the EclipseLink
     * JAXBBinder's underlying XMLBinder. Assumes that the given Binder is an instance of
     * EclipseLink JAXBBinderr, and clazz is one of org.eclipse.persistence.jaxb.JAXBinder or
     * org.eclipse.persistence.oxm.XMLBinder.
     * 
     * @param <T>
     * @param jaxbBinder
     * @param clazz
     * @return
     * @see org.eclipse.persistence.jaxb.JAXBBinder
     * @see org.eclipse.persistence.oxm.XMLBinder
     * @throws IllegalArgumentException
     */
    public static <T> T unwrap(javax.xml.bind.Binder jaxbBinder, Class<T> clazz) throws IllegalArgumentException {
        try {
            JAXBBinder jaxbBinderImpl = (JAXBBinder) jaxbBinder;
            if (clazz == org.eclipse.persistence.jaxb.JAXBBinder.class) {
                return (T) jaxbBinderImpl;
            }
            if (clazz == XMLBinder.class) {
                return (T) jaxbBinderImpl.getXMLBinder();
            }
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("jaxb_helper_invalid_target_for_binder", new Object[] { clazz }));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ExceptionLocalization
                    .buildMessage("jaxb_helper_invalid_binder", new Object[] { jaxbBinder.getClass() }));
        }
    }
}
