package com.asolutions.scmsshd.model.security;

/**
* @author Oleg Ilyenko
*/
public class PrivilegeOwner<T> {
    private T owner;
    private Privilege privilege;

    public PrivilegeOwner(T owner, Privilege privilege) {
        this.owner = owner;
        this.privilege = privilege;
    }

    public T getOwner() {
        return owner;
    }

    public Privilege getPrivilege() {
        return privilege;
    }
}
