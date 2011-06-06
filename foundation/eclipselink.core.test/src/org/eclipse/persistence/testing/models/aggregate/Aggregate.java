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
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.aggregate;

import java.util.*;

public class Aggregate {

    protected List<String> nicknames;
    protected List<Child> children;
    protected List<Relative> relatives;
    protected StepChild stepChild;
    protected Cousin cousin;
    
    public Aggregate() {
        super();
        nicknames = new ArrayList<String>();
        children = new ArrayList<Child>();
        relatives = new ArrayList<Relative>();
    }

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    public StepChild getStepChild() {
        return stepChild;
    }

    public void setStepChild(StepChild stepChild) {
        this.stepChild = stepChild;
    }

    public Cousin getCousin() {
        return cousin;
    }

    public List<String> getNicknames() {
        return nicknames;
    }
    
    public void setNicknames(List<String> nicknames) {
        this.nicknames = nicknames;
    }

    public void setCousin(Cousin cousin) {
        this.cousin = cousin;
    }

    public List<Relative> getRelatives() {
        return relatives;
    }

    public void setRelatives(List<Relative> relatives) {
        this.relatives = relatives;
    }
    
    public void addNickname(String nickname) {
        getNicknames().add(nickname);
    }
    
}
