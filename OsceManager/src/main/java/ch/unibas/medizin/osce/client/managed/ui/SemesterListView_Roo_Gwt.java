// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.

package ch.unibas.medizin.osce.client.managed.ui;

import ch.unibas.medizin.osce.client.managed.request.AdministratorProxy;
import ch.unibas.medizin.osce.client.managed.request.OsceProxy;
import ch.unibas.medizin.osce.client.managed.request.PatientInSemesterProxy;
import ch.unibas.medizin.osce.client.managed.request.SemesterProxy;
import ch.unibas.medizin.osce.client.scaffold.place.AbstractProxyListView;
import ch.unibas.medizin.osce.shared.Semesters;
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

public abstract class SemesterListView_Roo_Gwt extends AbstractProxyListView<SemesterProxy> {

    @UiField
    CellTable<SemesterProxy> table;

    protected Set<String> paths = new HashSet<String>();

    public void init() {
        paths.add("id");
        table.addColumn(new TextColumn<SemesterProxy>() {

            Renderer<java.lang.Long> renderer = new AbstractRenderer<java.lang.Long>() {

                public String render(java.lang.Long obj) {
                    return obj == null ? "" : String.valueOf(obj);
                }
            };

            @Override
            public String getValue(SemesterProxy object) {
                return renderer.render(object.getId());
            }
        }, "Id");
        paths.add("version");
        table.addColumn(new TextColumn<SemesterProxy>() {

            Renderer<java.lang.Integer> renderer = new AbstractRenderer<java.lang.Integer>() {

                public String render(java.lang.Integer obj) {
                    return obj == null ? "" : String.valueOf(obj);
                }
            };

            @Override
            public String getValue(SemesterProxy object) {
                return renderer.render(object.getVersion());
            }
        }, "Version");
        paths.add("semester");
        table.addColumn(new TextColumn<SemesterProxy>() {

            Renderer<ch.unibas.medizin.osce.shared.Semesters> renderer = new AbstractRenderer<ch.unibas.medizin.osce.shared.Semesters>() {

                public String render(ch.unibas.medizin.osce.shared.Semesters obj) {
                    return obj == null ? "" : String.valueOf(obj);
                }
            };

            @Override
            public String getValue(SemesterProxy object) {
                return renderer.render(object.getSemester());
            }
        }, "Semester");
        paths.add("calYear");
        table.addColumn(new TextColumn<SemesterProxy>() {

            Renderer<java.lang.Integer> renderer = new AbstractRenderer<java.lang.Integer>() {

                public String render(java.lang.Integer obj) {
                    return obj == null ? "" : String.valueOf(obj);
                }
            };

            @Override
            public String getValue(SemesterProxy object) {
                return renderer.render(object.getCalYear());
            }
        }, "Cal Year");
        paths.add("maximalYearEarnings");
        table.addColumn(new TextColumn<SemesterProxy>() {

            Renderer<java.lang.Double> renderer = new AbstractRenderer<java.lang.Double>() {

                public String render(java.lang.Double obj) {
                    return obj == null ? "" : String.valueOf(obj);
                }
            };

            @Override
            public String getValue(SemesterProxy object) {
                return renderer.render(object.getMaximalYearEarnings());
            }
        }, "Maximal Year Earnings");
        paths.add("pricestatist");
        table.addColumn(new TextColumn<SemesterProxy>() {

            Renderer<java.lang.Double> renderer = new AbstractRenderer<java.lang.Double>() {

                public String render(java.lang.Double obj) {
                    return obj == null ? "" : String.valueOf(obj);
                }
            };

            @Override
            public String getValue(SemesterProxy object) {
                return renderer.render(object.getPricestatist());
            }
        }, "Pricestatist");
        paths.add("priceStandardizedPartient");
        table.addColumn(new TextColumn<SemesterProxy>() {

            Renderer<java.lang.Double> renderer = new AbstractRenderer<java.lang.Double>() {

                public String render(java.lang.Double obj) {
                    return obj == null ? "" : String.valueOf(obj);
                }
            };

            @Override
            public String getValue(SemesterProxy object) {
                return renderer.render(object.getPriceStandardizedPartient());
            }
        }, "Price Standardized Partient");
        paths.add("administrators");
        table.addColumn(new TextColumn<SemesterProxy>() {

            Renderer<java.util.Set> renderer = ch.unibas.medizin.osce.client.scaffold.place.CollectionRenderer.of(ch.unibas.medizin.osce.client.managed.ui.AdministratorProxyRenderer.instance());

            @Override
            public String getValue(SemesterProxy object) {
                return renderer.render(object.getAdministrators());
            }
        }, "Administrators");
        paths.add("osces");
        table.addColumn(new TextColumn<SemesterProxy>() {

            Renderer<java.util.Set> renderer = ch.unibas.medizin.osce.client.scaffold.place.CollectionRenderer.of(ch.unibas.medizin.osce.client.managed.ui.OsceProxyRenderer.instance());

            @Override
            public String getValue(SemesterProxy object) {
                return renderer.render(object.getOsces());
            }
        }, "Osces");
        paths.add("patientsInSemester");
        table.addColumn(new TextColumn<SemesterProxy>() {

            Renderer<java.util.Set> renderer = ch.unibas.medizin.osce.client.scaffold.place.CollectionRenderer.of(ch.unibas.medizin.osce.client.managed.ui.PatientInSemesterProxyRenderer.instance());

            @Override
            public String getValue(SemesterProxy object) {
                return renderer.render(object.getPatientsInSemester());
            }
        }, "Patients In Semester");
    }
}
