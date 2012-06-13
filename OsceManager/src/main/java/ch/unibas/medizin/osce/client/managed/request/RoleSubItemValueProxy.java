// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.

package ch.unibas.medizin.osce.client.managed.request;

import com.google.gwt.requestfactory.shared.EntityProxy;
import com.google.gwt.requestfactory.shared.ProxyForName;
import org.springframework.roo.addon.gwt.RooGwtMirroredFrom;

@RooGwtMirroredFrom("ch.unibas.medizin.osce.domain.RoleSubItemValue")
@ProxyForName("ch.unibas.medizin.osce.domain.RoleSubItemValue")
public interface RoleSubItemValueProxy extends EntityProxy {

    abstract Integer getId();

    abstract void setId(Integer id);

    abstract Integer getVersion();

    abstract void setVersion(Integer version);

    abstract String getItemText();

    abstract void setItemText(String itemText);

    abstract RoleBaseItemProxy getRoleBaseItem();

    abstract void setRoleBaseItem(RoleBaseItemProxy roleBaseItem);

    abstract StandardizedRoleProxy getStandardizedRole();

    abstract void setStandardizedRole(StandardizedRoleProxy standardizedRole);
}