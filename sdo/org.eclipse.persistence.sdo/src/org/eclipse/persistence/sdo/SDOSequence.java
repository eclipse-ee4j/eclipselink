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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.sdo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import javax.xml.namespace.QName;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.helper.ListWrapper;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLObjectReferenceMapping;
import org.eclipse.persistence.oxm.sequenced.Setting;
import org.eclipse.persistence.mappings.DatabaseMapping;

public class SDOSequence implements Sequence {
    private static final String TEXT_XPATH = "text()";
    private SDODataObject dataObject;
    private List<Setting> settings;
    private Map<Key, Setting> valuesToSettings;

    public SDOSequence(SDODataObject dataObject) {
        // catch a null dataObject early before we get NPE on any update
        // operations during add/remove
        if (null == dataObject) {
            throw SDOException.sequenceDataObjectInstanceFieldIsNull();
        }
        this.dataObject = dataObject;
        this.settings = new ArrayList<Setting>();
        this.valuesToSettings = new HashMap<Key, Setting>();
    }

    public SDODataObject getDataObject() {
        return dataObject;
    }

    public List<Setting> getSettings() {
        return settings;
    }

    protected Map<Key, Setting> getValuesToSettings() {
        return valuesToSettings;
    }

    public void add(int index, int propertyIndex, Object value) {
        Property property = dataObject.getInstanceProperty(propertyIndex);
        add(index, property, value);
    }

    public boolean add(int propertyIndex, Object value) {
        Property property = dataObject.getInstanceProperty(propertyIndex);
        return add(property, value);
    }

    public void add(int index, Property property, Object value) {
        if (!isAllowedInSequence(property)) {
            return;
        }

        if (property != null && property.isOpenContent() && dataObject.getType().isOpen()) {
            dataObject.addOpenContentProperty(property);
        }
        // Update the data object
        if (property.isMany()) {
            ListWrapper listWrapper = (ListWrapper) dataObject.getList(property);
            if (value instanceof List) {
                // iterate list
                for (Iterator i = ((List) value).iterator(); i.hasNext();) {
                    // add a setting to the end of the sequence
                    Object aValue = i.next();
                    Setting setting = convertToSetting(property, aValue);
                    valuesToSettings.put(new Key(property, aValue), setting);
                    settings.add(index++, setting);
                    // no need to check updateContainment flag -
                    // ListWrapper.add() will not pass an entire List here
                    listWrapper.add(aValue, false);
                }
            } else {
                // set individual list item
                // add a setting to the end of the sequence
                Setting setting = convertToSetting(property, value);
                valuesToSettings.put(new Key(property, value), setting);
                settings.add(index, setting);
                int listIdx = getIndexInList(property, value);
                if (listIdx != -1) {
                    listWrapper.add(listIdx, value, false);
                } else {
                    listWrapper.add(value, false);
                }
            }
        } else {
            dataObject.setPropertyInternal((SDOProperty) property, value, false);
            // Update the settings
            Setting setting = convertToSetting(property, value);
            valuesToSettings.put(new Key(property, value), setting);
            settings.add(index, setting);
        }
    }

    private boolean isAllowedInSequence(Property property) {
        // Disallow the addition of a null Property
        if (null == property) {
            return false;
        }
        // Disallow the addition of a read only Property
        if (((SDOProperty)property).isReadOnly()) {
            return false;
        }
        // Disallow the addition of a Properties representing an XML attribute
        if (dataObject.getType().getHelperContext().getXSDHelper().isAttribute(property)) {
            throw SDOException.sequenceAttributePropertyNotSupported(property.getName());
        }
        // Disallow an open Property on a closed Type
        if (property.isOpenContent() && !dataObject.getType().isOpen()) {
            return false;
        }
        // Disallow the addition of an isMany==false Property that is already
        // set
        if (property.isMany()) {
            return true;
        }
        if (dataObject.isSet(property)) {
            throw SDOException.sequenceDuplicateSettingNotSupportedForComplexSingleObject(getIndexForProperty(property), property.getName());
        }
        return true;
    }

    public void add(int index, String propertyName, Object value) {
        Property property = dataObject.getInstanceProperty(propertyName);
        if (property == null) {
            // Property with given name does not exist - create an open content
            // property
            property = dataObject.defineOpenContentProperty(propertyName, value);
            ((SDOProperty) property).setMany(true);
        }
        add(index, property, value);
    }

    public void add(int index, String text) {
        addText(index, text);
    }

    public boolean add(Property property, Object value) {
        if (addSettingWithoutModifyingDataObject(property, value)) {
            if (property != null && property.isOpenContent() && dataObject.getType().isOpen()) {
                dataObject.addOpenContentProperty(property);
            }
            if (value instanceof XMLRoot) {
                value = ((XMLRoot) value).getObject();
            }
            if (property.isMany()) {
                ListWrapper listWrapper = (ListWrapper) dataObject.getList(property);
                listWrapper.add(value, false);
            } else {
                dataObject.setPropertyInternal((SDOProperty) property, value, false);
            }
            return true;
        }
        return false;
    }

    public boolean add(String propertyName, Object value) {
        Property property = dataObject.getInstanceProperty(propertyName);
        if (property == null) {
            // Property with given name does not exist - create an open content
            // property
            property = dataObject.defineOpenContentProperty(propertyName, value);
            ((SDOProperty) property).setMany(true);
        }
        return add(property, value);
    }

    public void add(String text) {
        addText(text);
    }

    public void addText(int index, String text) {
        // Trigger store of original sequence
        dataObject._setModified(true);
        Setting textSetting = new Setting(null, TEXT_XPATH);
        textSetting.setObject(dataObject);
        textSetting.setValue(text, false);
        settings.add(index, textSetting);
    }

    public void addText(String text) {
        // Trigger store of original sequence
        dataObject._setModified(true);
        Setting textSetting = new Setting(null, TEXT_XPATH);
        textSetting.setObject(dataObject);
        textSetting.setValue(text, false);
        settings.add(textSetting);
    }

    public SDOProperty getProperty(int index) {
        try {
            return getProperty(settings.get(index));
        } catch (IndexOutOfBoundsException iobex) {
            throw SDOException.invalidIndex(iobex, index);
        }
    }

    public SDOProperty getProperty(Setting setting) {
        DatabaseMapping mapping = setting.getMapping();
        if (null == mapping) {
            List<Setting> children = setting.getChildren();
            if (null != children && children.size() > 0) {
                return getProperty(children.get(0));
            }
        } else {
            SDOProperty property = null;
            if (null == setting.getName()) {
                Object value = setting.getValue();
                if (value instanceof SDODataObject) {
                    SDOProperty containmentProp = ((SDODataObject) value).getContainmentProperty();
                    if (containmentProp != null) {
                        property = dataObject.getInstanceProperty(containmentProp.getName());
                    }
                    if (property == null) {
                        XMLDescriptor desc = ((SDODataObject) value).getType().getXmlDescriptor();

                        String qualifiedName = desc.getDefaultRootElement();
                        int index = qualifiedName.indexOf(':');
                        String localName = null;
                        if (index > -1) {
                            localName = qualifiedName.substring(index + 1, qualifiedName.length());
                        } else {
                            localName = qualifiedName;
                        }
                        property = dataObject.getInstanceProperty(localName);
                    }
                } else {
                    XMLRoot xmlRoot = (XMLRoot) value;
                    if (null != xmlRoot) {
                        property = dataObject.getInstanceProperty(xmlRoot.getLocalName());
                    }
                }
            } else {
                property = dataObject.getInstanceProperty(mapping.getAttributeName());
            }
            return property;
        }
        return null;
    }

    public Object getValue(int index) {
        try {
            return getValue(settings.get(index));
        } catch (IndexOutOfBoundsException iobex) {
            throw SDOException.invalidIndex(iobex, index);
        }
    }

    private Object getValue(Setting setting) {
        if (null != setting.getMapping() || (setting.getName() != null && setting.getName().equals(TEXT_XPATH))) {
            Object value = setting.getValue();
            if (value instanceof XMLRoot) {
                value = ((XMLRoot) value).getObject();
            }
            return value;
        }

        if (null == setting.getChildren() || setting.getChildren().size() == 0) {
            return null;
        }
        return getValue(setting.getChildren().get(0));
    }

    public void move(int toIndex, int fromIndex) {
        if (toIndex == fromIndex) {
            return;
        }
        // verify indexes are in range
        int size = settings.size();
        if (fromIndex < 0 || fromIndex >= size) {
            throw SDOException.invalidIndex(null, fromIndex);
        }
        if (toIndex < 0 || toIndex >= size) {
            throw SDOException.invalidIndex(null, toIndex);
        }
        // trigger deep copy of sequence (if applicable)
        dataObject._setModified(true);

        Setting setting = settings.remove(fromIndex);
        settings.add(toIndex, setting);

        SDOProperty prop = getProperty(setting);
        if (prop != null && prop.isMany()) {
            ListWrapper lw = (ListWrapper) dataObject.getList(prop);
            Object value = getValue(setting);
            int currentIndexInLw = lw.indexOf(value);
            lw.remove(currentIndexInLw, false);
            int newIndexInLw = getIndexInList(prop, value);
            lw.add(newIndexInLw, value, false);
        }
    }

    public void remove(int index) {
        Setting setting = settings.get(index);
        remove(setting);
        settings.remove(setting);
    }

    private void remove(Setting setting) {
        DatabaseMapping mapping = setting.getMapping();
        if (null != mapping) {
            Property property = null;
            if (null == setting.getName()) {
                XMLRoot xmlRoot = (XMLRoot) setting.getValue();
                if (null != xmlRoot) {
                    property = dataObject.getInstanceProperty(xmlRoot.getLocalName());
                    valuesToSettings.remove(new Key(property, setting.getValue()));
                }
            } else {
                property = dataObject.getInstanceProperty(mapping.getAttributeName());
                valuesToSettings.remove(new Key(property, setting.getValue()));
            }
            if (property.isMany()) {
                ListWrapper listWrapper = (ListWrapper) dataObject.getList(property);
                listWrapper.remove(setting.getValue(), false, false);
            } else {
                dataObject.unset(property, false, false);
            }
        } else if (setting.getName() != null && setting.getName().equals(TEXT_XPATH)) {
            // Handle "text()"
            dataObject._setModified(true);
        }
        List<Setting> children = setting.getChildren();
        if (null != children) {
            int childrenSize = children.size();
            for (int x = 0; x < childrenSize; x++) {
                remove(children.get(x));
            }
        }
    }

    /**
     * INTERNAL:
     * 
     * @param setting
     */
    public void addValueToSettings(Setting setting) {
        valuesToSettings.put(new Key(getProperty(setting), getValue(setting)), setting);
    }

    /**
     * INTERNAL:
     * 
     * @param setting
     */
    public void removeValueToSettings(Setting setting) {
        valuesToSettings.remove(new Key(getProperty(setting), getValue(setting)));
    }

    public Object setValue(int index, Object value) {
        return setValue(settings.get(index), value);
    }

    private Object setValue(Setting setting, Object value) {
        if (null == setting.getMapping()) {
            if (setting.getName() != null && setting.getName().equals(TEXT_XPATH)) {
                dataObject._setModified(true);
                Object oldValue = setting.getValue();
                setting.setValue(value, false);
                return oldValue;
            }
            List<Setting> children = setting.getChildren();
            if (null != children && children.size() > 0) {
                return setValue(children.get(0), value);
            }
            return null;
        }

        SDOProperty property = getProperty(setting);
        Object oldValue = setting.getValue();

        if (property.isMany()) {
            ListWrapper listValue = (ListWrapper) dataObject.getList(property);
            int valueIndex = listValue.indexOf(oldValue);
            listValue.remove(oldValue, property.isContainment(), false);
            listValue.add(valueIndex, value, false);
            setting.setValue(value, false);
        } else {
            if (dataObject.isSet(property)) {
                updateSettingWithoutModifyingDataObject(property, dataObject.get(property), value);
                setting.setValue(value);
            } else {
                addSettingWithoutModifyingDataObject(property, value);
            }
            dataObject.setPropertyInternal(property, value, false);
        }
        return oldValue;
    }

    public int size() {
        return settings.size();
    }

    private Setting convertToSetting(Property property, Object value) {
        SDOProperty sdoProperty = (SDOProperty) property;
        DatabaseMapping mapping = sdoProperty.getXmlMapping();
        Setting setting = new Setting();
        SDOType sdoType = dataObject.getType();
        XMLDescriptor xmlDescriptor = sdoType.getXmlDescriptor();
        if (null == mapping) {
            setting.setObject(dataObject);

            mapping = xmlDescriptor.getMappingForAttributeName("openContentProperties");
            setting.setMapping(mapping);
            XMLRoot xmlRoot = new XMLRoot();
            if (value instanceof XMLRoot) {
                xmlRoot.setLocalName(((XMLRoot) value).getLocalName());
                xmlRoot.setNamespaceURI(((XMLRoot) value).getNamespaceURI());
                xmlRoot.setObject(((XMLRoot) value).getObject());
            } else {
                xmlRoot.setLocalName(sdoProperty.getName());
                xmlRoot.setNamespaceURI(sdoProperty.getUri());
                xmlRoot.setObject(value);
                // do not set schema type for global properties
                SDOTypeHelper hlpr = (SDOTypeHelper) dataObject.getType().getHelperContext().getTypeHelper();
                if (hlpr.getOpenContentProperty(sdoProperty.getUri(), sdoProperty.getName()) == null) {
                    QName schemaTypeQName = hlpr.getXSDTypeFromSDOType(property.getType());
                    if (schemaTypeQName != null && schemaTypeQName != XMLConstants.STRING_QNAME) {
                        xmlRoot.setSchemaType(schemaTypeQName);
                    }
                }
            }
            setting.setValue(xmlRoot, false);
        } else {
            setting = convertToSetting(mapping, value);
        }
        return setting;
    }

    private Setting convertToSetting(DatabaseMapping mapping, Object value) {
        XMLDescriptor xmlDescriptor = (XMLDescriptor) mapping.getDescriptor();
        NamespaceResolver nsResolver = xmlDescriptor.getNamespaceResolver();
        Setting rootSetting = new Setting();
        XMLField xmlField = (XMLField) mapping.getField();
        if (xmlField == null) {
            if (mapping instanceof XMLObjectReferenceMapping) {
                xmlField = (XMLField) ((XMLObjectReferenceMapping) mapping).getFields().get(0);
            } else if (mapping instanceof XMLCollectionReferenceMapping) {
                xmlField = (XMLField) ((XMLCollectionReferenceMapping) mapping).getFields().get(0);
            }
        }
        Setting setting = rootSetting;
        if (xmlField != null) {
            XPathFragment xPathFragment = xmlField.getXPathFragment();
            rootSetting = convertToSetting(xPathFragment, nsResolver);
            setting = rootSetting;

            while (xPathFragment.getNextFragment() != null) {
                xPathFragment = xPathFragment.getNextFragment();
                Setting childSetting = convertToSetting(xPathFragment, nsResolver);
                setting.addChild(childSetting);
                setting = childSetting;
            }
        }
        setting.setObject(dataObject);
        setting.setMapping(mapping);
        setting.setValue(value, false);
        return rootSetting;
    }

    private Setting convertToSetting(XPathFragment xPathFragment, NamespaceResolver nsResolver) {
        Setting setting = new Setting();
        String name = xPathFragment.getLocalName();
        if (null == name) {
            name = xPathFragment.getShortName();
        }
        setting.setName(name);
        if (xPathFragment.hasNamespace()) {
            setting.setNamespaceURI(nsResolver.resolveNamespacePrefix(xPathFragment.getPrefix()));
        }
        return setting;
    }

    public SDOSequence copy() {
        SDOSequence copy = new SDOSequence(dataObject);
        for (int index = 0, size = settings.size(); index < size; index++) {
            Setting settingCopy = settings.get(index).copy();
            copy.getSettings().add(settingCopy);
            copy.getValuesToSettings().put(new Key(getProperty(settingCopy), getValue(settingCopy)), settingCopy);
        }
        return copy;
    }

    /**
     * INTERNAL: Add a setting to the list at the specified index. The owning
     * DataObject will not be made aware of this addition.
     * 
     * @param index
     *            the index at which to add the new Setting in the Settings list
     * @param property
     * @param value
     * @return true if the a Setting was successfully added to the list,
     *         otherwise false
     */
    public boolean addSettingWithoutModifyingDataObject(int index, Property property, Object value) {
        Setting setting = convertToSetting(property, value);
        valuesToSettings.put(new Key(property, value), setting);
        if (index >= 0) {
            settings.add(index, setting);
        } else {
            settings.add(setting);
        }
        return true;
    }

    /**
     * INTERNAL:
     */
    public boolean addSettingWithoutModifyingDataObject(Property property, Object value) {
        return addSettingWithoutModifyingDataObject(property, value, true);
    }

    /**
     * INTERNAL:
     */
    public boolean addSettingWithoutModifyingDataObject(Property property, Object value, boolean checkAllowed) {
        if (checkAllowed && !isAllowedInSequence(property)) {
            return false;
        }
        Setting setting = convertToSetting(property, value);
        valuesToSettings.put(new Key(property, value), setting);
        settings.add(setting);
        return true;
    }

    /**
     * INTERNAL:
     */
    public void updateSettingWithoutModifyingDataObject(Property property, Object oldValue, Object newValue) {
        Key key = new Key(property, oldValue);
        Setting setting = valuesToSettings.get(key);
        valuesToSettings.remove(key);
        valuesToSettings.put(new Key(property, newValue), setting);
        // set the new value on the appropriate setting
        while (setting.getMapping() == null) {
            List<Setting> children = setting.getChildren();
            if (children != null && children.size() > 0) {
                setting = children.get(0);
            }
        }
        setting.setValue(newValue, false);
    }

    /**
     * INTERNAL:
     */
    public void removeSettingWithoutModifyingDataObject(Property property, Object value) {
        settings.remove(valuesToSettings.remove(new Key(property, value)));
    }

    /**
     * INTERNAL:
     */
    public void removeSettingWithoutModifyingDataObject(Property property) {
        List<Key> keys = new ArrayList<Key>(valuesToSettings.keySet());
        int size = valuesToSettings.keySet().size();
        for (int i = size - 1; i >= 0; i--) {
            Key nextKey = keys.get(i);
            if (nextKey.getProperty() == property) {
                settings.remove(valuesToSettings.remove(nextKey));
            }
        }
    }

    /**
     * INTERNAL: Convenience method that returns the index of the Setting
     * associated with a given property in the Settings list
     * 
     * @param property
     * @return index of the Setting associated with a given property in the
     *         Settings list or -1 if not found
     */
    public int getIndexForProperty(Property property) {
        List<Key> keys = new ArrayList<Key>(valuesToSettings.keySet());
        for (int i = 0; i < keys.size(); i++) {
            Key nextKey = keys.get(i);
            if (nextKey.getProperty() == property) {
                return settings.indexOf(valuesToSettings.get(nextKey));
            }
        }
        return -1;
    }

    /**
     * INTERNAL: Convenience method that, given a many property and a value,
     * returns the associated Setting's index in the Settings list. For example,
     * if a sequence contains many properties "letters" and "numbers", such as
     * [A, 1, C, 2, B, D], and we are looking for the letter B, this method will
     * return 2. Although B is at index 4 of the Settings list, it is at index 2
     * of the list of "letters" - [A, C, B, D].
     * 
     * @param property
     * @return index of the value's Setting in the list relative to a given
     *         property or -1 if not found.
     */
    private int getIndexInList(Property manyProp, Object value) {
        int returnIndex = -1;
        for (int i = 0; i < settings.size(); i++) {
            Setting nextSetting = settings.get(i);
            SDOProperty prop = getProperty(nextSetting);
            if (prop.equals(manyProp)) {
                returnIndex++;
                if (value.equals(getValue(nextSetting))) {
                    return returnIndex;
                }
            }
        }
        return returnIndex;
    }

    /**
     * INTERNAL: Get the root Setting for a given Setting.
     * 
     * @param setting
     * @return the root Setting or this Setting if it is a root
     */
    public static Setting getRootSetting(Setting setting) {
        Setting rootSetting = setting;
        while (rootSetting.getParent() != null) {
            rootSetting = rootSetting.getParent();
        }
        return rootSetting;
    }

    /**
     * INTERNAL: Ensure that each Setting in the settings list is also present
     * in the valuesToSettings map
     */
    public void afterUnmarshal() {
        for (Iterator<Setting> setIt = getSettings().iterator(); setIt.hasNext();) {
            addValueToSettings(setIt.next());
        }
    }

    private static class Key {
        private Property property;
        private Object value;

        public Key(Property property, Object value) {
            this.property = property;
            this.value = value;
        }

        protected Property getProperty() {
            return this.property;
        }

        protected Object getValue() {
            return this.value;
        }

        public boolean equals(Object object) {
            try {
                Key key = (Key) object;
                return property == key.getProperty() && value == key.getValue();
            } catch (ClassCastException e) {
                return false;
            }

        }

        public int hashCode() {
            if (value == null) {
                return 0;
            }
            return value.hashCode();
        }
    }
}
