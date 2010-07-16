/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.models.wdf.jpa1.node;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

@Entity
@Table(name = "TMP_NODE")
public class Node {

    @Id
    protected int id;

    @ManyToOne
    @JoinColumn(name = "PARENT")
    protected Node parent;

    @OneToMany(mappedBy = "parent")
    protected Set<Node> children;

    @Transient
    protected boolean postUpdateCalled;

    @Transient
    protected boolean throwsPostPersistException;

    @Version
    @Column(name = "VERSIONCOLUMN")
    protected Long version;

    @Basic
    protected String nodeName;

    public Node() {

    }

    public Node(int anId, Node aParent) {
        id = anId;
        parent = aParent;
        if (parent != null) {
            parent.addChild(this);
        }
        throwsPostPersistException = false;
    }

    public Node(int anId, boolean postPersistException) {
        id = anId;
        throwsPostPersistException = postPersistException;
    }

    /**
     * @return Returns the children.
     */
    public Set<Node> getChildren() {
        return children;
    }

    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }

    public void setChildren(Set<Node> theChildren) {
        children = theChildren;
    }

    /**
     * @return Returns the parent.
     */
    public Node getParent() {
        return parent;
    }

    private void addChild(Node child) {
        if (children == null) {
            children = new HashSet<Node>();
        }
        children.add(child);
    }

    /**
     * @param parent
     *            The parent to set.
     */
    public void setParent(Node aParent) {
        parent = aParent;
    }

    public void setName(String aName) {
        nodeName = aName;
    }

    public String getName() {
        return nodeName;
    }

    public long getVersion() {
        return version;
    }

    public void clearPostUpdate() {
        postUpdateCalled = false;
    }

    @PostUpdate
    public void postUpdate() {
        postUpdateCalled = true;

    }

    public boolean postUpdateWasCalled() {
        return postUpdateCalled;
    }

    @PostPersist
    public void postPersist() {
        if (throwsPostPersistException) {
            throw new MyRuntimeException();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node other = (Node) obj;
            return id == other.id;

        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return id;
    }

    public class MyRuntimeException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public MyRuntimeException() {
            super();
        }

        public MyRuntimeException(String message) {
            super(message);
        }

        public MyRuntimeException(String message, Throwable cause) {
            super(message, cause);
        }

        public MyRuntimeException(Throwable cause) {
            super(cause);
        }
    }
}
