/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial impl
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

/**
 * INTERNAL:
 * A metadata factory is used to extract class information. It is used when
 * processing the metadata model. By default, metadata processing uses an 
 * ASM factory, however tools that require a different form of processing,
 * like the APT processor which uses mirrors, can build their own factory
 * by sub-classing this one and supply it at processing time.
 * 
 * @author James Sutherland
 * @since EclipseLink 1.2
 */
public abstract class MetadataFactory {
    /** Backdoor to allow mapping of JDK classes. */
    public static boolean ALLOW_JDK = false;
    
    /** Stores all metadata for classes. */
    protected Map<String, MetadataClass> m_metadataClasses;
    
    protected MetadataLogger m_logger;
    protected ClassLoader m_loader;

    /**
     * INTERNAL:
     */
    public MetadataFactory(MetadataLogger logger, ClassLoader loader) {
        m_logger = logger;
        m_loader = loader;
        
        m_metadataClasses = new HashMap<String, MetadataClass>();
        m_metadataClasses.put("void", new MetadataClass(this, void.class));
        m_metadataClasses.put("", new MetadataClass(this, void.class));
        m_metadataClasses.put(null, new MetadataClass(this, void.class));
        m_metadataClasses.put("int", new MetadataClass(this, int.class));
        m_metadataClasses.put("long", new MetadataClass(this, long.class));
        m_metadataClasses.put("short", new MetadataClass(this, short.class));
        m_metadataClasses.put("boolean", new MetadataClass(this, boolean.class));
        m_metadataClasses.put("float", new MetadataClass(this, float.class));
        m_metadataClasses.put("double", new MetadataClass(this, double.class));
        m_metadataClasses.put("char", new MetadataClass(this, char.class));
        m_metadataClasses.put("byte", new MetadataClass(this, byte.class));
    }
    
    /**
     * INTERNAL:
     */
    public void addMetadataClass(MetadataClass metadataClass) {
        addMetadataClass(metadataClass.getName(), metadataClass);
        
    }
    
    /**
     * INTERNAL:
     */
    public void addMetadataClass(String name, MetadataClass metadataClass) {
        m_metadataClasses.put(name, metadataClass);
    }
    
    /**
     * INTERNAL:
     */
    public ClassLoader getLoader() {
        return m_loader;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataLogger getLogger() {
        return m_logger;
    }

    /**
     * INTERNAL:
     */
    protected Map<String, MetadataClass> getMetadataClasses() {
        return m_metadataClasses;
    }
    
    /**
     * INTERNAL:
     */
    protected boolean metadataClassExists(String className) {
        return m_metadataClasses.containsKey(className);
    }
    
    /**
     * Return the class metadata for the class name.
     */
    public abstract MetadataClass getMetadataClass(String className);
    
    /**
     * Return the class metadata for the class name.
     */
    public abstract MetadataClass getMetadataClass(String className, boolean isLazy);
    
    /**
     * INTERNAL:
     */
    public void setLoader(ClassLoader loader) {
        m_loader = loader;
    }
    
    /**
     * INTERNAL:
     */
    public void setLogger(MetadataLogger logger) {
        m_logger = logger;
    }
    
    /**
     * INTERNAL:
     * This method resolves generic types based on the ASM class metadata.
     * Unless every other factory (e.g. APT mirror factory) respects the generic
     * format as built from ASM this method will not work since it is very tied
     * to it.
     */
    public abstract void resolveGenericTypes(MetadataClass child, List<String> genericTypes, MetadataClass parent, MetadataDescriptor descriptor);
}
