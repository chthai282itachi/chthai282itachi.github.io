// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.

package ch.unibas.medizin.osce.client.managed.ui;

import ch.unibas.medizin.osce.client.managed.request.RoleBaseItemProxy;
import ch.unibas.medizin.osce.client.managed.request.RoleItemAccessProxy;
import ch.unibas.medizin.osce.client.managed.request.RoleTableItemProxy;
import ch.unibas.medizin.osce.client.managed.request.RoleTemplateProxy;
import ch.unibas.medizin.osce.client.scaffold.place.ProxyDetailsView;
import ch.unibas.medizin.osce.shared.ItemDefination;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.List;
import java.util.Set;

public abstract class RoleBaseItemMobileDetailsView_Roo_Gwt extends Composite implements ProxyDetailsView<RoleBaseItemProxy> {

    @UiField
    Element id;

    @UiField
    Element version;

    @UiField
    Element item_defination;

    @UiField
    Element item_name;

    @UiField
    Element deleted;

    @UiField
    Element roleTemplate;

    @UiField
    Element sort_order;

    @UiField
    Element roleTableItem;

    @UiField
    Element roleItemAccess;

    RoleBaseItemProxy proxy;

    public void setValue(RoleBaseItemProxy proxy) {
        this.proxy = proxy;
        id.setInnerText(proxy.getId() == null ? "" : String.valueOf(proxy.getId()));
        version.setInnerText(proxy.getVersion() == null ? "" : String.valueOf(proxy.getVersion()));
        item_defination.setInnerText(proxy.getItem_defination() == null ? "" : String.valueOf(proxy.getItem_defination()));
        item_name.setInnerText(proxy.getItem_name() == null ? "" : String.valueOf(proxy.getItem_name()));
        deleted.setInnerText(proxy.getDeleted() == null ? "" : String.valueOf(proxy.getDeleted()));
        roleTemplate.setInnerText(proxy.getRoleTemplate() == null ? "" : ch.unibas.medizin.osce.client.managed.ui.RoleTemplateProxyRenderer.instance().render(proxy.getRoleTemplate()));
        sort_order.setInnerText(proxy.getSort_order() == null ? "" : String.valueOf(proxy.getSort_order()));
        roleTableItem.setInnerText(proxy.getRoleTableItem() == null ? "" : ch.unibas.medizin.osce.client.scaffold.place.CollectionRenderer.of(ch.unibas.medizin.osce.client.managed.ui.RoleTableItemProxyRenderer.instance()).render(proxy.getRoleTableItem()));
        roleItemAccess.setInnerText(proxy.getRoleItemAccess() == null ? "" : ch.unibas.medizin.osce.client.scaffold.place.CollectionRenderer.of(ch.unibas.medizin.osce.client.managed.ui.RoleItemAccessProxyRenderer.instance()).render(proxy.getRoleItemAccess()));
    }
}
