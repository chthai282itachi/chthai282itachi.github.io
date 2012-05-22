// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.

package ch.unibas.medizin.osce.client.managed.activity;

import ch.unibas.medizin.osce.client.managed.activity.MaterialListEditActivityWrapper.View;
import ch.unibas.medizin.osce.client.managed.request.ApplicationRequestFactory;
import ch.unibas.medizin.osce.client.managed.request.MaterialListProxy;
import ch.unibas.medizin.osce.client.managed.request.UsedMaterialProxy;
import ch.unibas.medizin.osce.client.managed.ui.UsedMaterialSetEditor;
import ch.unibas.medizin.osce.client.scaffold.activity.IsScaffoldMobileActivity;
import ch.unibas.medizin.osce.client.scaffold.place.ProxyEditView;
import ch.unibas.medizin.osce.client.scaffold.place.ProxyListPlace;
import ch.unibas.medizin.osce.client.scaffold.place.ProxyPlace;
import ch.unibas.medizin.osce.shared.MaterialType;
import ch.unibas.medizin.osce.shared.PriceType;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.requestfactory.shared.EntityProxyId;
import com.google.gwt.requestfactory.shared.Receiver;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class MaterialListEditActivityWrapper_Roo_Gwt implements Activity, IsScaffoldMobileActivity {

    protected Activity wrapped;

    protected View<?> view;

    protected ApplicationRequestFactory requests;

    @Override
    public void start(AcceptsOneWidget display, EventBus eventBus) {
        view.setTypePickerValues(Arrays.asList(MaterialType.values()));
        view.setPriceTypePickerValues(Arrays.asList(PriceType.values()));
        view.setUsedMaterialsPickerValues(Collections.<UsedMaterialProxy>emptyList());
        requests.usedMaterialRequest().findUsedMaterialEntries(0, 50).with(ch.unibas.medizin.osce.client.managed.ui.UsedMaterialProxyRenderer.instance().getPaths()).fire(new Receiver<List<UsedMaterialProxy>>() {

            public void onSuccess(List<UsedMaterialProxy> response) {
                List<UsedMaterialProxy> values = new ArrayList<UsedMaterialProxy>();
                values.add(null);
                values.addAll(response);
                view.setUsedMaterialsPickerValues(values);
            }
        });
        wrapped.start(display, eventBus);
    }

    public interface View_Roo_Gwt<V extends ch.unibas.medizin.osce.client.scaffold.place.ProxyEditView<ch.unibas.medizin.osce.client.managed.request.MaterialListProxy, V>> extends ProxyEditView<MaterialListProxy, V> {

        void setTypePickerValues(Collection<MaterialType> values);

        void setPriceTypePickerValues(Collection<PriceType> values);

        void setUsedMaterialsPickerValues(Collection<UsedMaterialProxy> values);
    }
}
