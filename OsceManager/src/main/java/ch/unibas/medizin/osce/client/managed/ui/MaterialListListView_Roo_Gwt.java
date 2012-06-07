// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.

package ch.unibas.medizin.osce.client.managed.ui;

import ch.unibas.medizin.osce.client.managed.request.MaterialListProxy;
import ch.unibas.medizin.osce.client.managed.request.UsedMaterialProxy;
import ch.unibas.medizin.osce.client.scaffold.place.AbstractProxyListView;
import ch.unibas.medizin.osce.shared.MaterialType;
import ch.unibas.medizin.osce.shared.PriceType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.text.client.DateTimeFormatRenderer;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import java.util.HashSet;
import java.util.Set;

public abstract class MaterialListListView_Roo_Gwt extends AbstractProxyListView<MaterialListProxy> {

    @UiField
    CellTable<MaterialListProxy> table;

    protected Set<String> paths = new HashSet<String>();

    public void init() {
        paths.add("id");
        table.addColumn(new TextColumn<MaterialListProxy>() {

            Renderer<java.lang.Long> renderer = new AbstractRenderer<java.lang.Long>() {

                public String render(java.lang.Long obj) {
                    return obj == null ? "" : String.valueOf(obj);
                }
            };

            @Override
            public String getValue(MaterialListProxy object) {
                return renderer.render(object.getId());
            }
        }, "Id");
        paths.add("version");
        table.addColumn(new TextColumn<MaterialListProxy>() {

            Renderer<java.lang.Integer> renderer = new AbstractRenderer<java.lang.Integer>() {

                public String render(java.lang.Integer obj) {
                    return obj == null ? "" : String.valueOf(obj);
                }
            };

            @Override
            public String getValue(MaterialListProxy object) {
                return renderer.render(object.getVersion());
            }
        }, "Version");
        paths.add("name");
        table.addColumn(new TextColumn<MaterialListProxy>() {

            Renderer<java.lang.String> renderer = new AbstractRenderer<java.lang.String>() {

                public String render(java.lang.String obj) {
                    return obj == null ? "" : String.valueOf(obj);
                }
            };

            @Override
            public String getValue(MaterialListProxy object) {
                return renderer.render(object.getName());
            }
        }, "Name");
        paths.add("type");
        table.addColumn(new TextColumn<MaterialListProxy>() {

            Renderer<ch.unibas.medizin.osce.shared.MaterialType> renderer = new AbstractRenderer<ch.unibas.medizin.osce.shared.MaterialType>() {

                public String render(ch.unibas.medizin.osce.shared.MaterialType obj) {
                    return obj == null ? "" : String.valueOf(obj);
                }
            };

            @Override
            public String getValue(MaterialListProxy object) {
                return renderer.render(object.getType());
            }
        }, "Type");
        paths.add("price");
        table.addColumn(new TextColumn<MaterialListProxy>() {

            Renderer<java.lang.Integer> renderer = new AbstractRenderer<java.lang.Integer>() {

                public String render(java.lang.Integer obj) {
                    return obj == null ? "" : String.valueOf(obj);
                }
            };

            @Override
            public String getValue(MaterialListProxy object) {
                return renderer.render(object.getPrice());
            }
        }, "Price");
        paths.add("priceType");
        table.addColumn(new TextColumn<MaterialListProxy>() {

            Renderer<ch.unibas.medizin.osce.shared.PriceType> renderer = new AbstractRenderer<ch.unibas.medizin.osce.shared.PriceType>() {

                public String render(ch.unibas.medizin.osce.shared.PriceType obj) {
                    return obj == null ? "" : String.valueOf(obj);
                }
            };

            @Override
            public String getValue(MaterialListProxy object) {
                return renderer.render(object.getPriceType());
            }
        }, "Price Type");
        paths.add("usedMaterials");
        table.addColumn(new TextColumn<MaterialListProxy>() {

            Renderer<java.util.Set> renderer = ch.unibas.medizin.osce.client.scaffold.place.CollectionRenderer.of(ch.unibas.medizin.osce.client.managed.ui.UsedMaterialProxyRenderer.instance());

            @Override
            public String getValue(MaterialListProxy object) {
                return renderer.render(object.getUsedMaterials());
            }
        }, "Used Materials");
    }
}
