// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package ch.unibas.medizin.osce.domain;

import java.lang.String;

privileged aspect Keyword_Roo_ToString {
    
    public String Keyword.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("Name: ").append(getName()).append(", ");
        sb.append("StandardizedRoles: ").append(getStandardizedRoles() == null ? "null" : getStandardizedRoles().size()).append(", ");
        sb.append("Version: ").append(getVersion());
        return sb.toString();
    }
    
}