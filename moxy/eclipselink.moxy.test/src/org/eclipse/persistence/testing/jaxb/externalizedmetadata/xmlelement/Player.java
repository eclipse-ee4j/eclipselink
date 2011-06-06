package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelement;

import java.io.Serializable;

public class Player implements Serializable {
    private String name;
    private String nickname;

    public Player() {
    }

    public Player(String name, String nickname) {
        setName(name);
        setNickname(nickname);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
