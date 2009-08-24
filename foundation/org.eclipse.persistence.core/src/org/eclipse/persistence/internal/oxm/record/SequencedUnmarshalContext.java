// Copyright (c) 1998, 2007, Oracle. All rights reserved. 
package org.eclipse.persistence.internal.oxm.record;

import org.eclipse.persistence.internal.oxm.ContainerValue;
import org.eclipse.persistence.internal.oxm.Reference;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.oxm.sequenced.SequencedObject;
import org.eclipse.persistence.oxm.sequenced.Setting;

public class SequencedUnmarshalContext implements UnmarshalContext {
    
    private Setting currentSetting;
        
    public void startElement(UnmarshalRecord unmarshalRecord) {
        Setting parentSetting;
        if(null == currentSetting) {
            parentSetting = null;
        } else if(XMLConstants.TEXT.equals(currentSetting.getName())) {
            parentSetting = null;
        } else {
            parentSetting = currentSetting;
        }
        XPathFragment xPathFragment = unmarshalRecord.getXPathNode().getXPathFragment();
        if(null != xPathFragment) {
            currentSetting = new Setting(xPathFragment.getNamespaceURI(), xPathFragment.getLocalName());
        } else {
            currentSetting = new Setting();
        }
        int levelIndex = unmarshalRecord.getLevelIndex();
        if(0 == levelIndex) {
        } else if(1 == levelIndex) {
            ((SequencedObject) unmarshalRecord.getCurrentObject()).getSettings().add(currentSetting);
        } else {
            parentSetting.addChild(currentSetting);
        }
    }

    public void characters(UnmarshalRecord unmarshalRecord) {
        if(null == currentSetting || null == currentSetting.getName()) {
            currentSetting = new Setting(null, XMLConstants.TEXT);
            ((SequencedObject) unmarshalRecord.getCurrentObject()).getSettings().add(currentSetting);
        }else if(!XMLConstants.TEXT.equals(currentSetting.getName())) {
            Setting parentSetting = currentSetting;
            currentSetting = new Setting(null, XMLConstants.TEXT);
            if(null != parentSetting) {
                parentSetting.addChild(currentSetting);
            }
        }
    }

    public void endElement(UnmarshalRecord unmarshalRecord) {
        if(null == currentSetting) {
            return;
        }
        if(XMLConstants.TEXT.equals(currentSetting.getName())) {
            if(null == currentSetting.getParent()) {
                currentSetting = null;
            } else {
                currentSetting = currentSetting.getParent().getParent();
            }
        } else {
            currentSetting = currentSetting.getParent();
        }
    }

    public void setAttributeValue(UnmarshalRecord unmarshalRecord, Object value, DatabaseMapping mapping) {
        currentSetting.setMapping(mapping);
        currentSetting.setObject(unmarshalRecord.getCurrentObject());
        currentSetting.setValue(value);
    }

    public void addAttributeValue(UnmarshalRecord unmarshalRecord, ContainerValue containerValue, Object value) {
        addAttributeValue(unmarshalRecord, containerValue, value, unmarshalRecord.getContainerInstance(containerValue));
    }
    
    public void addAttributeValue(UnmarshalRecord unmarshalRecord, ContainerValue containerValue, Object value, Object collection) {
        currentSetting.setMapping(containerValue.getMapping());
        currentSetting.setObject(unmarshalRecord.getCurrentObject());
        currentSetting.addValue(value, true, collection);
    }

    public void reference(Reference reference) {
        currentSetting.setObject(reference.getSourceObject());
        currentSetting.setMapping((DatabaseMapping) reference.getMapping());
        reference.setSetting(currentSetting);
    }

    public void unmappedContent(UnmarshalRecord unmarshalRecord) {
        Setting parentSetting = currentSetting.getParent();
        if(null == parentSetting) {
            ((SequencedObject)unmarshalRecord.getCurrentObject()).getSettings().remove(currentSetting);
        }
        currentSetting = parentSetting;
    }

}