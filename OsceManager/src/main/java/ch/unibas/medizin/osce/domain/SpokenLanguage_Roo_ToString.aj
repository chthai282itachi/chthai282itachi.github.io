// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package ch.unibas.medizin.osce.domain;

import java.lang.String;

privileged aspect SpokenLanguage_Roo_ToString {
    
    public String SpokenLanguage.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("Langskills: ").append(getLangskills() == null ? "null" : getLangskills().size()).append(", ");
        sb.append("LanguageName: ").append(getLanguageName()).append(", ");
        sb.append("Version: ").append(getVersion());
        return sb.toString();
    }
    
}
