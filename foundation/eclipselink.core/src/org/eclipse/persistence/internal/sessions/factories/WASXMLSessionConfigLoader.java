/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.factories;

import java.util.Map;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.w3c.dom.Document;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.exceptions.SessionLoaderException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;

/**
 * Provide a mechanism for loading configuration XML files,
 * as classpath resources, and processing them via TopLink O-X.
 *
 * Provides backwards compatability to the WASXMLLoader.
 *
 * @since TopLink 10.1.3
 * @author Guy Pelletier
 */
public class WASXMLSessionConfigLoader extends XMLSessionConfigLoader {
    private AbstractSession m_loadedSession;

    /**
     * PUBLIC:
     * This constructor is used when the file resource named 'toplink-ejb-jar.xml'
     * will be parsed for configuration.
     */
    public WASXMLSessionConfigLoader() {
        this("meta-inf/toplink-ejb-jar.xml");
    }

    /**
     * PUBLIC:
     * This constructor is used when passing in the resource name of the
     * configuration file that should be parsed
     */
    public WASXMLSessionConfigLoader(String resourceName) {
        super(resourceName);
        this.entityResolver = new WASPersistenceEntityResolver();
    }

    /**
     * Return the last loaded session
     */
    public AbstractSession getLoadedSession() {
        return m_loadedSession;
    }

    /**
     * INTERNAL:
     * This method instantiates the parser and builds the document based on the
     * schema. If the document is loaded without errors, then the configs are
     * converted to sessions and stored on the session manager and true is
     * returned to indicate success.
     */
    public boolean load(SessionManager sessionManager, ClassLoader loader) {
        Document document = loadDocument(loader);

        if (document.getDocumentElement().getTagName().equals("sessions")) {
            if (getExceptionStore().isEmpty()) {
                // No errors occured, unmarshal the document which will return a 
                // SessionConfigs containing 1 SessionConfig. Send it through
                // the factory to create an actual Session
                XMLContext context = new XMLContext(getProject());
                XMLUnmarshaller unmarshaller = context.createUnmarshaller();
                SessionConfigs configs = (SessionConfigs)unmarshaller.unmarshal(document);
                Map sessions = new SessionsFactory().buildSessionConfigs(configs, loader);

                // For WAS, only one session should ever be loaded from the xml 
                // file. Because of schema validation it is impossible to get 
                // this far unless one valid session has been specified in the 
                // xml file.
                m_loadedSession = (AbstractSession)sessions.values().iterator().next();

                if (sessionManager.getSessions().get(m_loadedSession.getName()) == null) {
                    // Add the newly loaded session
                    m_loadedSession.getDatasourcePlatform().getConversionManager().setLoader(loader);
                    sessionManager.addSession(m_loadedSession);
                } else {
                    // Store the already loaded session with that name
                    m_loadedSession = (AbstractSession)sessionManager.getSessions().get(m_loadedSession.getName());
                }

                return true;
            } else {
                // Throw the exceptions we encountered
                throw SessionLoaderException.finalException(getExceptionStore());
            }
        } else {
            // 9.0.4 session.xml, return false to indicate we should load with the XMLLoader
            return false;
        }
    }

    /**
     * INTERNAL:
     * Load a session.xml document. The error handler will capture all the
     * errors and allow for a document to be returned.
     */
    protected Document loadDocument(ClassLoader loader) {
        try {
            return super.loadDocument(loader);
        } catch (ValidationException e) {
            // The file's path is case sensitive. Try loading 
            // "META-INF/toplink-ejb-jar.xml" if loading 
            // "meta-inf/toplink-ejb-jar.xml" failed
            try {
                this.resourceName = "META-INF/toplink-ejb-jar.xml";
                return super.loadDocument(loader);
            } catch (ValidationException exception) {
                // Throw different exception indicating no toplink-ejb-jar.xml file 
                // is found instead of no sessions.xml file is found
                if (exception.getErrorCode() == ValidationException.NO_SESSIONS_XML_FOUND) {
                    throw ValidationException.noTopLinkEjbJarXMLFound();
                } else {
                    throw exception;
                }
            }
        }
    }
}