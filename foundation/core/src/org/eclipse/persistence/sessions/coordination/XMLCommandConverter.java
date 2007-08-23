/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sessions.coordination;

import java.io.*;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.*;
import org.w3c.dom.Document;
import org.xml.sax.*;
import org.eclipse.persistence.internal.sessions.coordination.CommandProject;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetClassLoaderForClass;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import org.eclipse.persistence.sessions.coordination.MergeChangeSetCommand;
import deprecated.transform.ObjectTransformer;
import deprecated.transform.xml.XMLSource;
import deprecated.transform.xml.XMLResult;

/**
 * <p>
 * <b>Purpose</b>:
 * <p> This is TopLink Command converter class that impelments the public API org.eclipse.persistence.sessions.coordination.CommandConverter
 * <b>Description</b>:
 * <p> The converter provides the conversion between TopLink proprietary cache synchronization command,
 *  org.eclipse.persistence.sessions.coordination.MergeChangeSetCommand and XML org.w3c.dom.Document.  When this
 *  command converter is plugged into org.eclipse.persistence.sessions.coordination.CommandManager, it enables third party
 *  application to participate in TopLink cluster in the form of receiving or sending cache synchronization command
 *  in XML org.w3c.dom.Document format.
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Convert between MergeChangeSetCommand and XML Document
 * </ul>
 * @author Steven Vo
 * @since OracleAS TopLink 10<i>g</i> (9.0.4)
 * @see org.eclipse.persistence.sessions.coordination.CommandManager
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  There is no direct replacement API.
 */
public class XMLCommandConverter implements CommandConverter {
    // Schema file
    protected final static String SCHEMA = "command_904.xsd";

    // Style sheet files that transform user xml to TL XML and vice versa
    protected final static String USER_CMD_STYLE_SHEET = "user_command_904.xsl";
    protected final static String TL_CMD_STYLE_SHEET = "toplink_command_904.xsl";

    // transformer transforms MergeChangeSetCommand to XML Document or vice versa
    ObjectTransformer transformer;

    // schma and style sheet Documents
    protected URL schemaDoc;

    // schma and style sheet Documents
    protected URL userCmdStyleSheetDoc;

    // schma and style sheet Documents
    protected URL tlCmdStyleSheetDoc;
    
    // parsers to be used
    protected XMLParser nonValidatingParser, validatingParser;    
    
    public XMLCommandConverter() {
        super();
        initialize();
    }

    /**
     * INTERNAL:
     * initialize attributes for reuse purpose
     */
    protected void initialize() {
        transformer = new ObjectTransformer(new CommandProject());

        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
            try{
                ClassLoader classloader = (ClassLoader)AccessController.doPrivileged(new PrivilegedGetClassLoaderForClass(getClass()));
                schemaDoc = classloader.getResource(SCHEMA);
                userCmdStyleSheetDoc = classloader.getResource(USER_CMD_STYLE_SHEET);
                tlCmdStyleSheetDoc = classloader.getResource(TL_CMD_STYLE_SHEET);
            }catch (PrivilegedActionException ex){
                throw (RuntimeException)ex.getCause();
            }
        }else{
            schemaDoc = PrivilegedAccessHelper.getClassLoaderForClass(getClass()).getResource(SCHEMA);
            userCmdStyleSheetDoc = PrivilegedAccessHelper.getClassLoaderForClass(getClass()).getResource(USER_CMD_STYLE_SHEET);
            tlCmdStyleSheetDoc = PrivilegedAccessHelper.getClassLoaderForClass(getClass()).getResource(TL_CMD_STYLE_SHEET);
        }
        if ((schemaDoc == null) || (userCmdStyleSheetDoc == null) || (tlCmdStyleSheetDoc == null)) {
            Object[] resources = { SCHEMA + ", " + USER_CMD_STYLE_SHEET + ", " + TL_CMD_STYLE_SHEET };
            throw new RuntimeException(ExceptionLocalization.buildMessage("error_loading_resources", resources));
        }
    }

    /**
     * PUBLIC:
     * Convert input command to org.eclipse.persistence.sessions.coordination.MergeChangeSetCommand object and throw exception if it cannot convert.
     * Note that the converter will validate the input command against TopLink schema file command_904.xsd that is in the resource of
     * TopLink library during the conversion.
     * @param xmlCommand must be of type String or java.io.Reader or a oracle.xml.parser.v2.XMLDocument
     * @exception java.lang.RuntimeException if there is error during the conversion or the command parameter is not the expected type
     * @return a MergeChangeSetCommand
     */
    public Command convertToTopLinkCommand(Object xmlCommand) {
        Document cmdDoc = null;
        Reader xmlReader = null;

        if (xmlCommand instanceof Document) {
            cmdDoc = (Document)xmlCommand;

        } else if (xmlCommand instanceof String) {
            xmlReader = new StringReader((String)xmlCommand);

        } else if (xmlCommand instanceof Reader) {
            xmlReader = (Reader)xmlCommand;
        } else {
            Object[] inputArg = { xmlCommand };
            throw new RuntimeException(ExceptionLocalization.buildMessage("unexpect_argument", inputArg));
        }

        try {
            if (cmdDoc == null) {
                // parse and validate xml source
                XMLParser validatingParser = getValidatingDomParser();
                cmdDoc = validatingParser.parse(xmlReader);
            } else {
                XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
                xmlPlatform.validateDocument(cmdDoc, schemaDoc, null);
            }
        } catch (Error e1) {
            // Handle older parser version (i.e. use 9.0.4 xdk) error such as java.lang.NoSuchMethodError and use non-validated Document
            if(cmdDoc == null) {                
                cmdDoc = getNonValidatingDomParser().parse(xmlReader);
            }
        }

        // transfrom source document
        Document transformDoc = transform(cmdDoc, tlCmdStyleSheetDoc);

        XMLSource xmlSource = new XMLSource();
        xmlSource.setSourceDocument(transformDoc);

        // read and  build MergeChangeSetCommand object from transformed Document    
        Vector result = transformer.buildObjects(MergeChangeSetCommand.class, xmlSource);
        if (result.isEmpty()) {
            return null;
        } else {
            // There is only one command to convert 
            return (Command)result.firstElement();
        }
    }

    /**
     * PUBLIC:
     * Convert TopLink command to org.w3c.dom.Document object and throw exception if it cannot convert
     * @param command must be of type org.eclipse.persistence.sessions.coordination.MergeChangeSetCommand
     * @exception java.lang.RuntimeException if there is error during the conversion or the command parameter is not the expected type
     * @return org.w3c.dom.Document object
     */
    public Object convertToUserCommand(Command command) {
        if (command instanceof MergeChangeSetCommand) {
            Vector resultObjects = new Vector();
            resultObjects.add(command);

            // convert objects to Document
            XMLResult xmlResult = new XMLResult();
            transformer.storeObjects(resultObjects, xmlResult);
            Document cmdDoc = xmlResult.getResultDocument();

            return transform(cmdDoc, userCmdStyleSheetDoc);
        }
        Object[] inputArg = { command };
        throw new RuntimeException(ExceptionLocalization.buildMessage("unexpect_argument", inputArg));
    }

    protected Document transform(Document sourceDoc, URL styleSheetDoc) {
        try {
            XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
            XMLTransformer xmlTransformer = xmlPlatform.newXMLTransformer();

            Document doc = xmlPlatform.createDocument();
            xmlTransformer.transform(sourceDoc, doc, styleSheetDoc);
            return doc;
        } catch (Exception e) {
            throw new RuntimeException(ExceptionLocalization.buildMessage("parsing_error") + "\n" + Helper.printStackTraceToString(e));
        }
    }

    /**
     * INTERNAL:
     * return a non-validating parser
     */
    protected XMLParser getNonValidatingDomParser() {
        if(nonValidatingParser == null) {
            XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
            nonValidatingParser = xmlPlatform.newXMLParser();
            nonValidatingParser.setNamespaceAware(false);
            nonValidatingParser.setWhitespacePreserving(true);
            nonValidatingParser.setErrorHandler(new XMLErrorHanlder());
        }
        return nonValidatingParser;
    }

    /**
     * INTERNAL:
     * return a validating parser against schema
     */
     
    protected XMLParser getValidatingDomParser() {
        if(validatingParser == null) {
            XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
            XMLParser validatingParser = xmlPlatform.newXMLParser();
            validatingParser.setNamespaceAware(false);
            validatingParser.setValidationMode(XMLParser.SCHEMA_VALIDATION);
            validatingParser.setWhitespacePreserving(true);
            validatingParser.setErrorHandler(new XMLErrorHanlder());
    
            try {
                validatingParser.setXMLSchema(schemaDoc);
            } catch (Exception e) {
                throw new RuntimeException(ExceptionLocalization.buildMessage("parsing_error") + "\n" + Helper.printStackTraceToString(e));
            }
        }
        return validatingParser;
    }

    /**
     * INTERNAL:
     * <p><b>Purpose</b>: Provide a mechanism for parser's exception handling
     *
     * @see org.xml.sax.ErrorHandler
     * @since OracleAS TopLink 10<i>g</i> (9.0.4)
     * @author Steven Vo
     */
    class XMLErrorHanlder implements org.xml.sax.ErrorHandler {
        public void warning(SAXParseException e) throws SAXException {
            System.out.println(ExceptionLocalization.buildMessage("parsing_warning") + "\n" + Helper.printStackTraceToString(e));
        }

        public void error(SAXParseException e) throws SAXException {
            throw new SAXException(ExceptionLocalization.buildMessage("parsing_error"), e);
        }

        public void fatalError(SAXParseException e) throws SAXException {
            throw new SAXException(ExceptionLocalization.buildMessage("parsing_fatal_error"), e);
        }
    }
}