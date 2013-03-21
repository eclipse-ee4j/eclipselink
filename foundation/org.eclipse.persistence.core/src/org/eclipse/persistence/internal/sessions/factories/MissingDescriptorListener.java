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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.factories;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.eis.EISObjectPersistenceXMLProject;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;

/**
 * INTERNAL:
 * Event listener class used to lazy-load the descriptors for EIS and XDB,
 * as they have external jar dependencies that may not be on the classpath.
 *
 * @since TopLink 10
 * @author James Sutherland
 */
public class MissingDescriptorListener extends SessionEventAdapter {
    protected static String XML_TYPE_CLASS = "org.eclipse.persistence.mappings.xdb.DirectToXMLTypeMapping";
    protected static String EIS_DESCRIPTOR_CLASS = "org.eclipse.persistence.eis.EISDescriptor";
    protected static String XML_INTERACTION_CLASS = "org.eclipse.persistence.eis.interactions.XMLInteraction";
    protected static String EIS_LOGIN_CLASS = "org.eclipse.persistence.eis.EISLogin";
    protected static String XML_BINARY_MAPPING_CLASS = "org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping";
    protected static String XML_BINARY_COLLECTION_MAPPING_CLASS = "org.eclipse.persistence.oxm.mappings.XMLBinaryDataCollectionMapping";

    public void missingDescriptor(SessionEvent event) {
        String name = ((Class)event.getResult()).getName();
        DatabaseSession session = (DatabaseSession) ((AbstractSession) event.getSession()).getRootSession(null);
        Project project = session.getProject();
        String namespaceXPath = "";
        NamespaceResolverWithPrefixes namespaceResolverWithPrefixes;
        if (project instanceof NamespaceResolvableProject) {
            // this should always be true
            NamespaceResolvableProject nrpProject = (NamespaceResolvableProject)project;
            namespaceXPath = nrpProject.getPrimaryNamespaceXPath();
            namespaceResolverWithPrefixes = nrpProject.getNamespaceResolver();
        }
        else {
            // this shouldn't happen - but if it does, build a new (empty) NamespaceResolverWithPrefixes
            namespaceResolverWithPrefixes = new NamespaceResolverWithPrefixes();
        }
	    DirectToXMLTypeMappingHelper.getInstance().addXDBDescriptors(name,
	        (DatabaseSessionImpl)session, namespaceResolverWithPrefixes);

        if (name.equals(EIS_DESCRIPTOR_CLASS) || name.equals(XML_INTERACTION_CLASS) || name.equals(EIS_LOGIN_CLASS)) {
            try {
                Class javaClass = null;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try{
                        javaClass = (Class) AccessController.doPrivileged(new PrivilegedClassForName(XML_INTERACTION_CLASS));
                    }catch (PrivilegedActionException ex){
                        if (ex.getCause() instanceof ClassNotFoundException){
                            throw (ClassNotFoundException) ex.getCause();
                        }
                        throw (RuntimeException) ex.getCause();
                    }
                }else{
                    javaClass = PrivilegedAccessHelper.getClassForName(XML_INTERACTION_CLASS);
                }
                session.getDescriptor(Call.class).getInheritancePolicy().addClassIndicator(javaClass,
                    namespaceXPath + "xml-interaction" );
            } catch (Exception classLoadFailure) {
                throw ValidationException.fatalErrorOccurred(classLoadFailure);
            }
            session.addDescriptors(new EISObjectPersistenceXMLProject(namespaceResolverWithPrefixes));
        }
        if(name.equals(XML_BINARY_MAPPING_CLASS) || name.equals(XML_BINARY_COLLECTION_MAPPING_CLASS)) {
            session.addDescriptors(new OXMObjectPersistenceRuntimeXMLProject(namespaceResolverWithPrefixes));
        }
    }
}
