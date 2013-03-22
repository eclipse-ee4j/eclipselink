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
package org.eclipse.persistence.oxm.sequenced;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.mappings.ContainerMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * <p>Setting objects are used to control the order in which the
 * mappings for Sequenced Objects are processed.</p>
 * 
 * <b>Example 1</b>
 * <pre>
 * Setting piSetting = new Setting(null, "personal-info");
 * 
 * Setting fnSetting = new Setting(null, "first-name");
 * piSetting.addChild(fnSetting);
 * 
 * Setting fnTextSetting = new Setting(null, "text()");
 * fnTextSetting.setObject(customerObject);
 * fnTextSetting.setMapping(customerFirstNameMapping);
 * fnTextSetting.setValue("Jane");
 * fnSetting.addChild(fnTextSetting);
 *
 * Setting lnSetting = new Setting(null, "last-name");
 * piSetting.addChild(lnSetting);
 * 
 * Setting lnTextSetting = new Setting(null, "text()");
 * lnTextSetting.setObject(customerObject);
 * lnTextSetting.setMapping(customerLastNameMapping);
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
 * fnTextSetting.setObject(customerObject);
 * fnTextSetting.setMapping(customerFirstNameMapping);
 * fnTextSetting.setValue("Jane");
 * fnSetting.addChild(fnTextSetting);
 *
 * Setting lnpiSetting = new Setting(null, "personal-info");

 * Setting lastNameSetting = new Setting(null, "last-name");
 * lnpiSetting.addChild(lnSetting);
 * 
 * Setting lnTextSetting = new Setting(null, "text()");
 * lnTextSetting.setObject(customerObject);
 * lnTextSetting.setMapping(customerLastNameMapping);
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
     * @return The namespace URI that qualifies the name of the Setting (if there 
     * is one).
     */
    public String getNamespaceURI() {
        return namespaceURI;
    }

    /**
     * @param namespaceURI Specify the namespace URI that qualifies the name of the Setting (if 
     * there is one).
     */
    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    /**
     * @return The value corresponding to this setting.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Set the value on the Setting.  This method will also update the corresponding 
     * domain object using the specified mapping.
     * @param value
     */
    public void setValue(Object value) {
        setValue(value, true);
    }

    /**
     * @param value The value to be set on the Setting.
     * @param updateObject This flag indicates if an update is performed 
     * on the corresponding domain object using the specified mapping.
     */
    public void setValue(Object value, boolean updateObject) {
        this.value = value;
        if(updateObject) {
            if(null != children) {
                return;
            }
            if(!mapping.isWriteOnly()) {
                mapping.setAttributeValueInObject(object, value);
            }
        }              
    }

    /**
     * @param value
     * @param updateObject
     * @param container
     */
    public void addValue(Object value, boolean updateObject, Object container) {
        this.value = value;

        if(updateObject) {
            if(null != children) {
                return;
            }

            ContainerMapping containerMapping = (ContainerMapping) mapping;
            ContainerPolicy containerPolicy = containerMapping.getContainerPolicy();
            if(null == container && !(mapping.isWriteOnly())) {
                container = containerPolicy.containerInstance();
                mapping.setAttributeValueInObject(object, container);
            }
            containerMapping.getContainerPolicy().addInto(value, container, null);
        }              
    }

    /**
     * @return The domain object to which this Setting applies. 
     */
    public Object getObject() {
        return object;
    }

    /**
     * @param object This is the domain object to which this Setting belongs.
     */
    public void setObject(Object object) {
        this.object = object;
    }

    /**
     * @return The mapping for the domain object that corresponds to this Setting.
     */
    public DatabaseMapping getMapping() {
        return mapping;
    }

    /**
     * @param mapping The mapping for the domain object that corresponds to this Setting.
     */
    public void setMapping(DatabaseMapping mapping) {
        this.mapping = mapping;
    }

    /**
     * 
     * @param childSetting This setting will be added to the parent.  The parenting 
     * information will be updated automatically.  A child must only be added to one parent.
     */
    public void addChild(Setting childSetting) {
        childSetting.setParent(this);
        if(null == children) {
            children = new ArrayList<Setting>();
        }
        children.add(childSetting);
    }

    /**
     * @return The parent Setting or null if this setting has not parent.
     */
    public Setting getParent() {
        return parent;
    }

    /**
     * @param parentSetting The parent Setting or null if this setting has not parent. 
     */
    public void setParent(Setting parentSetting) {
        this.parent = parentSetting;
    }

    /**
     * @return The child Settings or null if this setting has no children.
     */
    public List<Setting> getChildren() {
        return children;
    }

    /**
     * @return A copy of the Setting object and its child Setting objects.  The copy
     * contains references to the original object, mapping, and value.
     */
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

    public Setting copy(Object newParent) {
        Setting copy = new Setting();
        copy.setName(name);
        copy.setNamespaceURI(namespaceURI);
        if(getObject() != null){
          copy.setObject(newParent);
        }
        copy.setMapping(mapping);
        copy.setValue(value, false);
        if(null != children) {
            for(int index=0, size=children.size(); index<size; index++) {
                copy.addChild(children.get(index).copy(newParent));
            }
        }
        return copy;
    }
    
    public Setting copy(Object newParent, Object copyValue) {
        Setting copy = new Setting();
        copy.setName(name);
        copy.setNamespaceURI(namespaceURI);
        if(getObject() != null){
          copy.setObject(newParent);
        }
        copy.setMapping(mapping);
        copy.setValue(copyValue, false);
        if(null != children) {
            for(int index=0, size=children.size(); index<size; index++) {
                copy.addChild(children.get(index).copy(newParent,copyValue));
            }
        }
        return copy;
    }
}
