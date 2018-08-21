/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.helper;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.eclipse.persistence.internal.oxm.OXMSystemProperties;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class XMLHelper {

    // LJ - does not cache logger to allow its dynamic reconfiguration

    // not in older JDK, so must be duplicated here, otherwise javax.xml.XMLConstants should be used
    public static final String ACCESS_EXTERNAL_SCHEMA = "http://javax.xml.XMLConstants/property/accessExternalSchema";
    public static final String ACCESS_EXTERNAL_DTD = "http://javax.xml.XMLConstants/property/accessExternalDTD";

    private static final String PROP_ACCESS_EXTERNAL_SCHEMA = "javax.xml.accessExternalSchema";
    private static final String PROP_ACCESS_EXTERNAL_DTD = "javax.xml.accessExternalDTD";

    /**
     * If true XML security features when parsing XML documents will be disabled.
     * The default value is false.
     *
     * Boolean
     * @since 2.6.3
     */
    private static final boolean XML_SECURITY_DISABLED = PrivilegedAccessHelper.getSystemPropertyBoolean(OXMSystemProperties.DISABLE_SECURE_PROCESSING, false);

    private static boolean isXMLSecurityDisabled(boolean runtimeSetting) {
        return XML_SECURITY_DISABLED || runtimeSetting;
    }

    /**
     * Returns properly configured (e.g. security features) schema factory 
     * - namespaceAware == true
     * - securityProcessing == is set based on security processing property, default is true
     */
    public static SchemaFactory createSchemaFactory(final String language, boolean disableSecureProcessing) throws IllegalStateException {
        SessionLog logger = AbstractSessionLog.getLog();
        try {
            SchemaFactory factory = SchemaFactory.newInstance(language);
            
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "schema_factory", new Object[] {factory});
            }
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, !isXMLSecurityDisabled(disableSecureProcessing));
            return factory;
        } catch (SAXNotRecognizedException ex) {
            logger.logThrowable(SessionLog.SEVERE, SessionLog.MOXY, ex);
            throw new IllegalStateException(ex);
        } catch (SAXNotSupportedException ex) {
            logger.logThrowable(SessionLog.SEVERE, SessionLog.MOXY, ex);
            throw new IllegalStateException(ex);
        } catch (AbstractMethodError er) {
            logger.logThrowable(SessionLog.SEVERE, SessionLog.MOXY, er);
            throw new IllegalStateException(er);
        }
    }

    /**
     * Returns properly configured (e.g. security features) parser factory 
     * - namespaceAware == true
     * - securityProcessing == is set based on security processing property, default is true
     */
    public static SAXParserFactory createParserFactory(boolean disableSecureProcessing) throws IllegalStateException {
        SessionLog logger = AbstractSessionLog.getLog();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "saxparser_factory", new Object[] {factory});
            }
            factory.setNamespaceAware(true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, !isXMLSecurityDisabled(disableSecureProcessing));
            return factory;
        } catch (ParserConfigurationException ex) {
            logger.logThrowable(SessionLog.SEVERE, SessionLog.MOXY, ex);
            throw new IllegalStateException( ex);
        } catch (SAXNotRecognizedException ex) {
            logger.logThrowable(SessionLog.SEVERE, SessionLog.MOXY, ex);
            throw new IllegalStateException( ex);
        } catch (SAXNotSupportedException ex) {
            logger.logThrowable(SessionLog.SEVERE, SessionLog.MOXY, ex);
            throw new IllegalStateException( ex);
        } catch (AbstractMethodError er) {
            logger.logThrowable(SessionLog.SEVERE, SessionLog.MOXY, er);
            throw new IllegalStateException(er);
        }
    }

    /**
     * Returns properly configured (e.g. security features) factory 
     * - securityProcessing == is set based on security processing property, default is true
     */
    public static XPathFactory createXPathFactory(boolean disableSecureProcessing) throws IllegalStateException {
        SessionLog logger = AbstractSessionLog.getLog();
        try {
            XPathFactory factory = XPathFactory.newInstance();
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "xpath_factory", new Object[] {factory});
            }
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, !isXMLSecurityDisabled(disableSecureProcessing));
            return factory;
        } catch (XPathFactoryConfigurationException ex) {
            logger.logThrowable(SessionLog.SEVERE, SessionLog.MOXY, ex);
            throw new IllegalStateException( ex);
        } catch (AbstractMethodError er) {
            logger.logThrowable(SessionLog.SEVERE, SessionLog.MOXY, er);
            throw new IllegalStateException(er);
        }
    }

    /**
     * Returns properly configured (e.g. security features) factory 
     * - securityProcessing == is set based on security processing property, default is true
     */
    public static TransformerFactory createTransformerFactory(boolean disableSecureProcessing) throws IllegalStateException {
        SessionLog logger = AbstractSessionLog.getLog();
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "transformer_factory", new Object[] {factory});
            }
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, !isXMLSecurityDisabled(disableSecureProcessing));
            return factory;
        } catch (TransformerConfigurationException ex) {
            logger.logThrowable(SessionLog.SEVERE, SessionLog.MOXY, ex);
            throw new IllegalStateException( ex);
        } catch (AbstractMethodError er) {
            logger.logThrowable(SessionLog.SEVERE, SessionLog.MOXY, er);
            throw new IllegalStateException(er);
        }
    }

    /**
     * Returns properly configured (e.g. security features) factory 
     * - namespaceAware == true
     * - securityProcessing == is set based on security processing property, default is true
     */
    public static DocumentBuilderFactory createDocumentBuilderFactory(boolean disableSecureProcessing) throws IllegalStateException {
        SessionLog logger = AbstractSessionLog.getLog();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "documentbuilder_factory", new Object[] {factory});
            }
            factory.setNamespaceAware(true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, !isXMLSecurityDisabled(disableSecureProcessing));
            return factory;
        } catch (ParserConfigurationException ex) {
            logger.logThrowable(SessionLog.SEVERE, SessionLog.MOXY, ex);
            throw new IllegalStateException( ex);
        } catch (AbstractMethodError er) {
            logger.logThrowable(SessionLog.SEVERE, SessionLog.MOXY, er);
            throw new IllegalStateException(er);
        }
    }

    public static SchemaFactory allowExternalAccess(SchemaFactory sf, String value, boolean disableSecureProcessing) {
        SessionLog logger = AbstractSessionLog.getLog();
        // if xml security (feature secure processing) disabled, nothing to do, no restrictions applied
        if (isXMLSecurityDisabled(disableSecureProcessing)) {
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "jaxp_sec_disabled", new Object[] {"xsd"});
            }
            return sf;
        }

        if (PrivilegedAccessHelper.getSystemProperty(PROP_ACCESS_EXTERNAL_SCHEMA) != null) {
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "jaxp_sec_explicit", new Object[] {"xsd"});
            }
            return sf;
        }

        try {
            sf.setProperty(ACCESS_EXTERNAL_SCHEMA, value);
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "jaxp_sec_prop_supported", new Object[] {ACCESS_EXTERNAL_SCHEMA});
            }
        } catch (SAXException ignored) {
            // nothing to do; support depends on version JDK or SAX implementation
            if (logger.shouldLog(SessionLog.CONFIG, SessionLog.MOXY)) {
                logger.log(SessionLog.CONFIG, SessionLog.MOXY, "jaxp_sec_prop_not_supported", new Object[] {ACCESS_EXTERNAL_SCHEMA});
                logger.logThrowable(SessionLog.CONFIG, SessionLog.MOXY, ignored);
            }
        }
        return sf;
    }

    public static TransformerFactory allowExternalAccess(TransformerFactory tf, String value, boolean disableSecureProcessing) {
        SessionLog logger = AbstractSessionLog.getLog();
        // if xml security (feature secure processing) disabled, nothing to do, no restrictions applied
        if (isXMLSecurityDisabled(disableSecureProcessing)) {
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "jaxp_sec_disabled", new Object[] {"xsd"});
            }
            return tf;
        }

        if (PrivilegedAccessHelper.getSystemProperty(PROP_ACCESS_EXTERNAL_SCHEMA) != null) {
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "jaxp_sec_explicit", new Object[] {"xsd"});
            }
            return tf;
        }

        try {
            tf.setAttribute(ACCESS_EXTERNAL_SCHEMA, value);
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "jaxp_sec_prop_supported", new Object[] {ACCESS_EXTERNAL_SCHEMA});
            }
        } catch (IllegalArgumentException ignored) {
            // nothing to do; support depends on version JDK or SAX implementation
            if (logger.shouldLog(SessionLog.CONFIG, SessionLog.MOXY)) {
                logger.log(SessionLog.CONFIG, SessionLog.MOXY, "jaxp_sec_prop_not_supported", new Object[] {ACCESS_EXTERNAL_SCHEMA});
                logger.logThrowable(SessionLog.CONFIG, SessionLog.MOXY, ignored);
            }
        }
        return tf;
    }

    public static DocumentBuilderFactory allowExternalAccess(DocumentBuilderFactory dbf, String value, boolean disableSecureProcessing) {
        SessionLog logger = AbstractSessionLog.getLog();
        // if xml security (feature secure processing) disabled, nothing to do, no restrictions applied
        if (isXMLSecurityDisabled(disableSecureProcessing)) {
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "jaxp_sec_disabled", new Object[] {"xsd"});
            }
            return dbf;
        }

        if (PrivilegedAccessHelper.getSystemProperty(PROP_ACCESS_EXTERNAL_SCHEMA) != null) {
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "jaxp_sec_explicit", new Object[] {"xsd"});
            }
            return dbf;
        }

        try {
            dbf.setAttribute(ACCESS_EXTERNAL_SCHEMA, value);
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "jaxp_sec_prop_supported", new Object[] {ACCESS_EXTERNAL_SCHEMA});
            }
        } catch (IllegalArgumentException ignored) {
            // nothing to do; support depends on version JDK or SAX implementation
            if (logger.shouldLog(SessionLog.CONFIG, SessionLog.MOXY)) {
                logger.log(SessionLog.CONFIG, SessionLog.MOXY, "jaxp_sec_prop_not_supported", new Object[] {ACCESS_EXTERNAL_SCHEMA});
                logger.logThrowable(SessionLog.CONFIG, SessionLog.MOXY, ignored);
            }
        }
        return dbf;
    }

    public static XMLReader allowExternalAccess(XMLReader xmlReader, String value, boolean disableSecureProcessing) {
        SessionLog logger = AbstractSessionLog.getLog();
        // if xml security (feature secure processing) disabled, nothing to do, no restrictions applied
        if (isXMLSecurityDisabled(disableSecureProcessing)) {
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "jaxp_sec_disabled", new Object[] {"xsd"});
            }
            return xmlReader;
        }

        if (PrivilegedAccessHelper.getSystemProperty(PROP_ACCESS_EXTERNAL_SCHEMA) != null) {
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "jaxp_sec_explicit", new Object[] {"xsd"});
            }
            return xmlReader;
        }

        try {
            xmlReader.setProperty(ACCESS_EXTERNAL_SCHEMA, value);
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "jaxp_sec_prop_supported", new Object[] {ACCESS_EXTERNAL_SCHEMA});
            }
        } catch (SAXException ignored) {
            // nothing to do; support depends on version JDK or SAX implementation
            if (logger.shouldLog(SessionLog.CONFIG, SessionLog.MOXY)) {
                logger.log(SessionLog.CONFIG, SessionLog.MOXY, "jaxp_sec_prop_not_supported", new Object[] {ACCESS_EXTERNAL_SCHEMA});
                logger.logThrowable(SessionLog.CONFIG, SessionLog.MOXY, ignored);
            }
        }
        return xmlReader;
    }

    public static SchemaFactory allowExternalDTDAccess(SchemaFactory sf, String value, boolean disableSecureProcessing) {
        SessionLog logger = AbstractSessionLog.getLog();
        // if xml security (feature secure processing) disabled, nothing to do, no restrictions applied
        if (isXMLSecurityDisabled(disableSecureProcessing)) {
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "jaxp_sec_disabled", new Object[] {"DTD"});
            }
            return sf;
        }

        if (PrivilegedAccessHelper.getSystemProperty(PROP_ACCESS_EXTERNAL_DTD) != null) {
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "jaxp_sec_explicit", new Object[] {"DTD"});
            }
            return sf;
        }

        try {
            sf.setProperty(ACCESS_EXTERNAL_DTD, value);
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "jaxp_sec_prop_supported", new Object[] {ACCESS_EXTERNAL_DTD});
            }
        } catch (SAXException ignored) {
            // nothing to do; support depends on version JDK or SAX implementation
            if (logger.shouldLog(SessionLog.CONFIG, SessionLog.MOXY)) {
                logger.log(SessionLog.CONFIG, SessionLog.MOXY, "jaxp_sec_prop_not_supported", new Object[] {ACCESS_EXTERNAL_DTD});
                logger.logThrowable(SessionLog.CONFIG, SessionLog.MOXY, ignored);
            }
        }
        return sf;
    }

    public static DocumentBuilderFactory allowExternalDTDAccess(DocumentBuilderFactory dbf, String value, boolean disableSecureProcessing) {
        SessionLog logger = AbstractSessionLog.getLog();
        // if xml security (feature secure processing) disabled, nothing to do, no restrictions applied
        if (isXMLSecurityDisabled(disableSecureProcessing)) {
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "jaxp_sec_disabled", new Object[] {"DTD"});
            }
            return dbf;
        }

        if (PrivilegedAccessHelper.getSystemProperty(PROP_ACCESS_EXTERNAL_DTD) != null) {
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "jaxp_sec_explicit", new Object[] {"DTD"});
            }
            return dbf;
        }

        try {
            dbf.setAttribute(ACCESS_EXTERNAL_DTD, value);
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "jaxp_sec_prop_supported", new Object[] {ACCESS_EXTERNAL_DTD});
            }
        } catch (IllegalArgumentException ignored) {
            // nothing to do; support depends on version JDK or SAX implementation
            if (logger.shouldLog(SessionLog.CONFIG, SessionLog.MOXY)) {
                logger.log(SessionLog.CONFIG, SessionLog.MOXY, "jaxp_sec_prop_not_supported", new Object[] {ACCESS_EXTERNAL_DTD});
                logger.logThrowable(SessionLog.CONFIG, SessionLog.MOXY, ignored);
            }
        }
        return dbf;
    }

    public static XMLReader allowExternalDTDAccess(XMLReader xmlReader, String value, boolean disableSecureProcessing) {
        SessionLog logger = AbstractSessionLog.getLog();
        // if xml security (feature secure processing) disabled, nothing to do, no restrictions applied
        if (isXMLSecurityDisabled(disableSecureProcessing)) {
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "jaxp_sec_disabled", new Object[] {"DTD"});
            }
            return xmlReader;
        }

        if (PrivilegedAccessHelper.getSystemProperty(PROP_ACCESS_EXTERNAL_DTD) != null) {
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "jaxp_sec_explicit", new Object[] {"DTD"});
            }
            return xmlReader;
        }

        try {
            xmlReader.setProperty(ACCESS_EXTERNAL_DTD, value);
            if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
                logger.log(SessionLog.FINE, SessionLog.MOXY, "jaxp_sec_prop_supported", new Object[] {ACCESS_EXTERNAL_DTD});
            }
        } catch (SAXException ignored) {
            // nothing to do; support depends on version JDK or SAX implementation
            if (logger.shouldLog(SessionLog.CONFIG, SessionLog.MOXY)) {
                logger.log(SessionLog.CONFIG, SessionLog.MOXY, "jaxp_sec_prop_not_supported", new Object[] {ACCESS_EXTERNAL_DTD});
                logger.logThrowable(SessionLog.CONFIG, SessionLog.MOXY, ignored);
            }
        }
        return xmlReader;
    }
}
