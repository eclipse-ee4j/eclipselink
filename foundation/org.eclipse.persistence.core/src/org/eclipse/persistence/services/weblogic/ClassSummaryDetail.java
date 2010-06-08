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
 *     @author  mobrien
 *     @since   EclipseLink 1.1 enh# 248748
 *     10/20/2008-1.1M4 Michael O'Brien 
 *       - 248748: Add WebLogic 10.3 specific JMX MBean attributes and functions
 *       see <link>http://wiki.eclipse.org/EclipseLink/DesignDocs/248748</link>
 ******************************************************************************/  
package org.eclipse.persistence.services.weblogic;


import javax.management.openmbean.CompositeType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.OpenDataException;

/**
 * The class is used internally by the Portable JMX Framework to convert 
 * model specific classes into Open Types so that the attributes of model class can
 * be exposed by MBeans.
 */
public class ClassSummaryDetail {
    /**
     * Construct a ClassSummaryDetail instance. The PropertyNames annotation is used 
     * to be able to construct a ClassSummaryDetail instance out of a CompositeData
     * instance. See MXBeans documentation for more details.
     */
    public ClassSummaryDetail(String className, String cacheType, String configuredSize,String currentSize , String parentClassName) {
        this.className = className;
        this.cacheType = cacheType;
        this.configuredSize = configuredSize;
        this.currentSize = currentSize;
        this.parentClassName = parentClassName;
    }
    
    private String className;
    private String cacheType;
    private String configuredSize;
    private String currentSize;
    private String parentClassName;
    
    // The corresponding CompositeType for this class
    private static CompositeType cType_= null;

    private static final String[] itemNames_= 
        {"Class Name", "Cache Type", "Configured Size",
         "Current Size","Parent Class Name"}; 

    static {
        try {
            OpenType[] itemTypes = {
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.STRING};
            cType_ = new CompositeType("org.eclipse.persistence.services.weblogic",
                                       // this should be a localized description
                                       // but isn't really required since the attribute
                                       // or parameter description should suffice
                                       // however this value cannot be null or empty
                                       "org.eclipse.persistence.services.weblogic.ClassSummaryDetail",
                                       itemNames_,
                                       // this should be a localized description
                                       // but isn't really required since the attribute
                                       // or parameter description should suffice
                                       // however this value cannot be null or empty
                                       itemNames_,
                                       itemTypes);
        }  catch(OpenDataException ode) {
            // this won't happen, but in case it does we should log
            throw new RuntimeException(ode);
        }
    }

    /**
     * Returns the CompositeType that describes this model
     * specific class
     */
    public static CompositeType toCompositeType() {
        return cType_;
    } 
    
    /**
     * Convert an instance of this model specific type to 
     * a CompositeData. This ensure that clients that do not
     * have access to the model specific class can still
     * use the MBean. The MXBean framework can perform this
     * conversion automatically.  
     * 
     * @param ct - This parameter is for JDK 1.6 compatibility reasons
     */
    public CompositeData toCompositeData(CompositeType ct) {
        Object[] itemValues = {
                this.className,
                this.cacheType,
                this.configuredSize,
                this.currentSize,
                this.parentClassName};

        CompositeData cData= null;
        try {
            cData= new CompositeDataSupport(cType_, itemNames_, itemValues);
        } catch( OpenDataException ode) {
            // this won't happen, but in case it does we should log
            throw new RuntimeException(ode);
        }
        return cData;
    }
    
    /**
     * Create an instance of the model specific class out of
     * an associated CompositeData instance
     */
    public static ClassSummaryDetail from(CompositeData cd) {   
        if (cd==null) { 
            return null;
        }

        return new ClassSummaryDetail( 
                (String)cd.get("Class Name"),
                (String)cd.get("Cache Type"),
                (String)cd.get("Current Size"),
                (String)cd.get("Parent Class Name"),
                (String)cd.get("Configured Size")
                );
    }

    public String getClassName() {
        return className;
    }

    public String getCacheType() {
        return cacheType;
    }

    public String getConfiguredSize() {
        return configuredSize;
    }

    public String getCurrentSize() {
        return currentSize;
    }

    public String getParentClassName() {
        return parentClassName;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public void setCacheType(String cacheType) {
        this.cacheType = cacheType;
    }

    public void setConfiguredSize(String configuredSize) {
        this.configuredSize = configuredSize;
    }

    public void setCurrentSize(String currentSize) {
        this.currentSize = currentSize;
    }

    public void setParentClassName(String parentClassName) {
        this.parentClassName = parentClassName;
    }
}
