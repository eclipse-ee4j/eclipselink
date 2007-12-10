/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.oxm.sequenced;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.mappings.ContainerMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * <b>Example 1</b>
 * <pre>
 * Setting piSetting = new Setting(null, "personal-info");
 * 
 * Setting fnSetting = new Setting(null, "first-name");
 * piSetting.addChild(fnSetting);
 * 
 * Setting fnTextSetting = new Setting(null, "text()");
 * fnTextSetting.setValue("Jane");
 * fnSetting.addChild(fnTextSetting);
 *
 * Setting lnSetting = new Setting(null, "last-name");
 * piSetting.addChild(lnSetting);
 * 
 * Setting lnTextSetting = new Setting(null, "text()");
 * lnTextSetting.setValue("Doe");
 * lnSetting.getSequence().add(lnTextSetting);
 * </pre> 
 * <pre>
 * &lt;personal-info&gt;
 *      &lt;first-name&gt;Jane&lt;/first-name&gt;
 *      &lt;last-name&gt;Doe&lt;/last-name&gt;
 * &lt;/personal-info&gt;
 * </pre>
 * <b>Example 2</b>
 * <pre>
 * Setting fnpiSetting = new Setting(null, "personal-info");
 * 
 * Setting fnSetting = new Setting(null, "first-name");
 * fnpiSetting.addChild(fnSetting);
 * 
 * Setting fnTextSetting = new Setting(null, "text()");
 * fnTextSetting.setValue("Jane");
 * fnSetting.addChild(fnTextSetting);
 *
 * Setting lnpiSetting = new Setting(null, "personal-info");

 * Setting lastNameSetting = new Setting(null, "last-name");
 * lnpiSetting.addChild(lnSetting);
 * 
 * Setting lnTextSetting = new Setting(null, "text()");
 * lnTextSetting.setValue("Doe");
 * lnSetting.addChild(lnTextSetting);
 * </pre> 
 * <pre>
 * &lt;personal-info&gt;
 *      &lt;first-name&gt;Jane&lt;/first-name&gt;
 * &lt;/personal-info&gt;
 * &lt;personal-info&gt;
 *      &lt;last-name&gt;Doe&lt;/last-name&gt;
 * &lt;/personal-info&gt;
 * </pre>
 */
public class Setting {

    private String name;
    private String namespaceURI;
    private Object value;
    private Object object;
    private DatabaseMapping mapping;
    private Setting parent;
    private List<Setting> children;

    public Setting() {
        super();
    }

    public Setting(String namespaceURI, String name) {
        super();
        this.setNamespaceURI(namespaceURI);
        this.setName(name);
    }
	
    /**
     * <p>Return the name of the setting.  The name of the setting corresponds 
     * to a fragment of an XPath in an object-to-XML mapping.</p>
     * <b>Example</b>
     * <p>For the XPath personal-info/first-name/text() would correspond to 3 
     * Setting objects with names "personal-info", "first-name", and "text()"
     * </p>  
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Specify the name of the setting.  The name of the setting corresponds 
     * to a fragment of an XPath in an object-to-XML mapping.</p>
     * <b>Example</b>
     * <p>For the XPath personal-info/first-name/text() would correspond to 3 
     * Setting objects with names "personal-info", "first-name", and "text()"
     * </p>  
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the namespace URI that qualifies the name of the Setting (if there 
     * is one).
     */
    public String getNamespaceURI() {
        return namespaceURI;
    }

    /**
     * Specify the namespace URI that qualifies the name of the Setting (if 
     * there is one).
     */
    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    /**
     * Return the value corresponding to this setting.  For sequenced object the 
     * property values correspond to the setting and not the domain object. 
     */
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        setValue(value, true);
    }

    public void setValue(Object value, boolean updateObject) {
        this.value = value;
        if(updateObject) {
            if(null != children) {
                return;
            }
            mapping.setAttributeValueInObject(object, value);
        }              
    }

    public void addValue(Object value, boolean updateObject, Object container) {
        this.value = value;

        if(updateObject) {
            if(null != children) {
                return;
            }

            ContainerMapping containerMapping = (ContainerMapping) mapping;
            ContainerPolicy containerPolicy = containerMapping.getContainerPolicy();
            if(null == container) {
                container = containerPolicy.containerInstance();
                mapping.setAttributeValueInObject(object, container);
            }
            containerMapping.getContainerPolicy().addInto(value, container, null);
        }              
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public DatabaseMapping getMapping() {
        return mapping;
    }

    public void setMapping(DatabaseMapping mapping) {
        this.mapping = mapping;
    }

    public void addChild(Setting childSetting) {
        childSetting.setParent(this);
        if(null == children) {
            children = new ArrayList<Setting>();
        }
        children.add(childSetting);
    }

    public Setting getParent() {
        return parent;
    }

    public void setParent(Setting parentSetting) {
        this.parent = parentSetting;
    }

    public List<Setting> getChildren() {
        return children;
    }

    public Setting copy() {
        Setting copy = new Setting();
        copy.setName(name);
        copy.setNamespaceURI(namespaceURI);
        copy.setObject(object);
        copy.setMapping(mapping);
        copy.setValue(value, false);
        if(null != children) {
            for(int index=0, size=children.size(); index<size; index++) {
                copy.addChild(children.get(index).copy());
            }
        }
        return copy;
    }

}