/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.models.optimisticlocking;

import java.util.List;
import java.util.ArrayList;

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;

public class GamesConsole extends AbstractVideogameObject {

    protected List<Controller> controllers;
    protected ValueHolderInterface camera;
    protected PowerSupplyUnit psu;
    protected List<Gamer> gamers;

    public GamesConsole() {
        this(null, null);
    }

    public GamesConsole(String name, String description) {
        super(name, description);
        this.camera = new ValueHolder();
        this.controllers = new ArrayList();
        this.gamers = new ArrayList();
    }

    public List<Controller> getControllers() {
        return controllers;
    }

    public void setControllers(List<Controller> controllers) {
        this.controllers = controllers;
    }

    public Camera getCamera() {
        return (Camera)getCameraHolder().getValue();
    }

    private ValueHolderInterface getCameraHolder() {
        return camera;
    }

    public void setCamera(Camera camera) {
        getCameraHolder().setValue(camera);
    }

    private void setCameraHolder(ValueHolderInterface holder) {
        this.camera = holder;
    }

    public PowerSupplyUnit getPsu() {
        return psu;
    }

    public void setPsu(PowerSupplyUnit psu) {
        this.psu = psu;
    }

    public void addController(Controller controller) {
        if (controller != null && !getControllers().contains(controller)) {
            getControllers().add(controller);
            controller.setConsole(this);
        }
    }

    public void removeController(Controller controller) {
        if (controller != null && getControllers().contains(controller)) {
            getControllers().remove(controller);
            controller.setConsole(null);
        }
    }

    public void addGamer(Gamer gamer) {
        if (gamer != null && !getGamers().contains(gamer)) {
            getGamers().add(gamer);
        }
    }

    public void removeGamer(Gamer gamer) {
        if (gamer != null && getGamers().contains(gamer)) {
            getGamers().add(gamer);
        }
    }

    public List<Gamer> getGamers() {
        return gamers;
    }

    public void setGamers(List<Gamer> gamers) {
        this.gamers = gamers;
    }

}
