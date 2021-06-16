/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.oxm.record;

import org.eclipse.persistence.core.mappings.CoreMapping;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.ContainerValue;
import org.eclipse.persistence.internal.oxm.Reference;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.oxm.sequenced.SequencedObject;
import org.eclipse.persistence.oxm.sequenced.Setting;

public class SequencedUnmarshalContext implements UnmarshalContext {

    private Setting currentSetting;

    @Override
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

    @Override
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

    @Override
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

    @Override
    public void setAttributeValue(UnmarshalRecord unmarshalRecord, Object value, Mapping mapping) {
        currentSetting.setMapping((CoreMapping) mapping);
        currentSetting.setObject(unmarshalRecord.getCurrentObject());
        currentSetting.setValue(value);
    }

    @Override
    public void addAttributeValue(UnmarshalRecord unmarshalRecord, ContainerValue containerValue, Object value) {
        addAttributeValue(unmarshalRecord, containerValue, value, unmarshalRecord.getContainerInstance(containerValue));
    }

    @Override
    public void addAttributeValue(UnmarshalRecord unmarshalRecord, ContainerValue containerValue, Object value, Object collection) {
        currentSetting.setMapping((CoreMapping) containerValue.getMapping());
        currentSetting.setObject(unmarshalRecord.getCurrentObject());
        currentSetting.addValue(value, true, collection);
    }

    @Override
    public void reference(Reference reference) {
        currentSetting.setObject(reference.getSourceObject());
        currentSetting.setMapping((CoreMapping) reference.getMapping());
        reference.setSetting(currentSetting);
    }

    @Override
    public void unmappedContent(UnmarshalRecord unmarshalRecord) {
        Setting parentSetting = currentSetting.getParent();
        if(null == parentSetting) {
            ((SequencedObject)unmarshalRecord.getCurrentObject()).getSettings().remove(currentSetting);
        }
        currentSetting = parentSetting;
    }

}
