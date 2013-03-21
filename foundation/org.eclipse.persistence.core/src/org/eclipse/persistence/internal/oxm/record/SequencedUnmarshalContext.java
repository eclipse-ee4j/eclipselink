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
 *     Oracle - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm.record;

import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.ContainerValue;
import org.eclipse.persistence.internal.oxm.Reference;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.sequenced.SequencedObject;
import org.eclipse.persistence.oxm.sequenced.Setting;

public class SequencedUnmarshalContext implements UnmarshalContext {
    
    private Setting currentSetting;
        
    public void startElement(UnmarshalRecord unmarshalRecord) {
        Setting parentSetting;
        if(null == currentSetting) {
            parentSetting = null;
        } else if(Constants.TEXT.equals(currentSetting.getName())) {
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
            currentSetting = new Setting(null, Constants.TEXT);
            ((SequencedObject) unmarshalRecord.getCurrentObject()).getSettings().add(currentSetting);
        }else if(!Constants.TEXT.equals(currentSetting.getName())) {
            Setting parentSetting = currentSetting;
            currentSetting = new Setting(null, Constants.TEXT);
            if(null != parentSetting) {
                parentSetting.addChild(currentSetting);
            }
        }
    }

    public void endElement(UnmarshalRecord unmarshalRecord) {
        if(null == currentSetting) {
            return;
        }
        if(Constants.TEXT.equals(currentSetting.getName())) {
            if(null == currentSetting.getParent()) {
                currentSetting = null;
            } else {
                currentSetting = currentSetting.getParent().getParent();
            }
        } else {
            currentSetting = currentSetting.getParent();
        }
    }

    public void setAttributeValue(UnmarshalRecord unmarshalRecord, Object value, Mapping mapping) {
        currentSetting.setMapping((DatabaseMapping) mapping);
        currentSetting.setObject(unmarshalRecord.getCurrentObject());
        currentSetting.setValue(value);
    }

    public void addAttributeValue(UnmarshalRecord unmarshalRecord, ContainerValue containerValue, Object value) {
        addAttributeValue(unmarshalRecord, containerValue, value, unmarshalRecord.getContainerInstance(containerValue));
    }
    
    public void addAttributeValue(UnmarshalRecord unmarshalRecord, ContainerValue containerValue, Object value, Object collection) {
        currentSetting.setMapping((DatabaseMapping) containerValue.getMapping());
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