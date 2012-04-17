/****************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      tware - 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.metadata;

/**
 * Used to persist information about the location of an application
 * @author tware
 *
 */

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class Application {

    @Id
    private String name;

    private String persistenceXMLURL;
    
    public Application(){
    }
    
    public Application(String name, String url){
        this.name = name;
        this.persistenceXMLURL = url;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPersistenceXMLURL() {
        return persistenceXMLURL;
    }

    public void setPersistenceXMLURL(String persistenceXMLURL) {
        this.persistenceXMLURL = persistenceXMLURL;
    }

}
