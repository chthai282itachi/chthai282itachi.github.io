// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.

package ch.unibas.medizin.osce.client.managed.ui;

import ch.unibas.medizin.osce.client.managed.request.MaterialListProxy;
import ch.unibas.medizin.osce.client.managed.request.UsedMaterialProxy;
import ch.unibas.medizin.osce.client.scaffold.place.ProxyDetailsView;
import ch.unibas.medizin.osce.client.scaffold.place.ProxyListView;
import ch.unibas.medizin.osce.shared.MaterialType;
import ch.unibas.medizin.osce.shared.PriceType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
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
import java.util.Set;

public abstract class MaterialListDetailsView_Roo_Gwt extends Composite implements ProxyDetailsView<MaterialListProxy> {

    @UiField
    SpanElement id;

    @UiField
    SpanElement version;

    @UiField
    SpanElement name;

    @UiField
    SpanElement type;

    @UiField
    SpanElement price;

    @UiField
    SpanElement priceType;

    @UiField
    SpanElement usedMaterials;

    MaterialListProxy proxy;

    @UiField
    SpanElement displayRenderer;

    public void setValue(MaterialListProxy proxy) {
        this.proxy = proxy;
        id.setInnerText(proxy.getId() == null ? "" : String.valueOf(proxy.getId()));
        version.setInnerText(proxy.getVersion() == null ? "" : String.valueOf(proxy.getVersion()));
        name.setInnerText(proxy.getName() == null ? "" : String.valueOf(proxy.getName()));
        type.setInnerText(proxy.getType() == null ? "" : String.valueOf(proxy.getType()));
        price.setInnerText(proxy.getPrice() == null ? "" : String.valueOf(proxy.getPrice()));
        priceType.setInnerText(proxy.getPriceType() == null ? "" : String.valueOf(proxy.getPriceType()));
        usedMaterials.setInnerText(proxy.getUsedMaterials() == null ? "" : ch.unibas.medizin.osce.client.scaffold.place.CollectionRenderer.of(ch.unibas.medizin.osce.client.managed.ui.UsedMaterialProxyRenderer.instance()).render(proxy.getUsedMaterials()));
        displayRenderer.setInnerText(MaterialListProxyRenderer.instance().render(proxy));
    }
}
