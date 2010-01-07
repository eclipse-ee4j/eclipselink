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
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.scplugin.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


public class EisPlatformManager extends SCPlatformManager {

    public static final String AQ_ID = "Oracle AQ";
    public static final String JMS_ID = "JMS";
    public static final String MQ_ID = "IBM MQSeries";
    public static final String XML_ID = "XML File";
	
	private static EisPlatformManager INSTANCE;
	
	private Map connectionSpecs;

	private EisPlatformManager() {
		super();
	}
	/**
	 * singleton support
	 */
	public static synchronized EisPlatformManager instance() {
		if( INSTANCE == null) {
			INSTANCE = new EisPlatformManager();
		}
		return INSTANCE;
	}	

	public Iterator platformDisplayNames() {
		ArrayList<String> platformNames = new ArrayList<String>();
		platformNames.add(AQ_ID);
		platformNames.add(JMS_ID);
		platformNames.add(MQ_ID);
		platformNames.add(XML_ID);
		return platformNames.listIterator();
	}
	

	protected void buidPlatforms() {

	    this.addPlatform( AQ_ID, "org.eclipse.persistence.eis.adapters.aq.AQPlatform");
	    this.addPlatform( JMS_ID, "org.eclipse.persistence.eis.adapters.jms.JMSPlatform");
	    this.addPlatform( MQ_ID, "org.eclipse.persistence.eis.adapters.mqseries.MQPlatform");
	    this.addPlatform( XML_ID, "org.eclipse.persistence.eis.adapters.xmlfile.XMLFilePlatform");
	}
	
	private void buidConnectionSpecs() {

	    this.connectionSpecs.put( AQ_ID, "org.eclipse.persistence.eis.adapters.aq.AQEISConnectionSpec");
	    this.connectionSpecs.put( JMS_ID, "org.eclipse.persistence.eis.adapters.jms.JMSEISConnectionSpec");
	    this.connectionSpecs.put( MQ_ID, "org.eclipse.persistence.eis.adapters.mqseries.MQConnectionSpec");
	    this.connectionSpecs.put( XML_ID, "org.eclipse.persistence.eis.adapters.xmlfile.XMLFileEISConnectionSpec");
	}
	
	public String getRuntimeConnectionSpecClassName( String platformClassName) {
	    
	    String shortClassName = ClassTools.shortNameForClassNamed( platformClassName);
	    String id = this.getIdFor( shortClassName);
	    return ( String)this.connectionSpecs.get( id);
	}
	
	public Iterator connectionSpecNames() {
	    
	    return this.connectionSpecs.values().iterator();
	}
	
	protected void initialize() {
	    super.initialize();

		this.connectionSpecs = new HashMap();
		this.buidConnectionSpecs();
	}
}
