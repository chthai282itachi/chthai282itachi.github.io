// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package ch.unibas.medizin.osce.domain;

import ch.unibas.medizin.osce.domain.RoleTableItem;
import ch.unibas.medizin.osce.domain.StandardizedRole;
import java.lang.String;

privileged aspect RoleTableItemValue_Roo_JavaBean {
    
    public String RoleTableItemValue.getValue() {
        return this.value;
    }
    
    public void RoleTableItemValue.setValue(String value) {
        this.value = value;
    }
    
    public RoleTableItem RoleTableItemValue.getRoleTableItem() {
        return this.roleTableItem;
    }
    
    public void RoleTableItemValue.setRoleTableItem(RoleTableItem roleTableItem) {
        this.roleTableItem = roleTableItem;
    }
    
    public StandardizedRole RoleTableItemValue.getStandardizedRole() {
        return this.standardizedRole;
    }
    
    public void RoleTableItemValue.setStandardizedRole(StandardizedRole standardizedRole) {
        this.standardizedRole = standardizedRole;
    }
    
}