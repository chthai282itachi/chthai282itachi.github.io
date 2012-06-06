// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.

package ch.unibas.medizin.osce.client.managed.ui;

import ch.unibas.medizin.osce.client.managed.request.ClinicProxy;
import ch.unibas.medizin.osce.client.managed.request.DoctorProxy;
import ch.unibas.medizin.osce.client.managed.request.OfficeProxy;
import ch.unibas.medizin.osce.client.managed.request.RoleParticipantProxy;
import ch.unibas.medizin.osce.client.managed.request.SpecialisationProxy;
import ch.unibas.medizin.osce.client.scaffold.place.AbstractProxyListView;
import ch.unibas.medizin.osce.shared.Gender;
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

public abstract class DoctorListView_Roo_Gwt extends AbstractProxyListView<DoctorProxy> {

    @UiField
    CellTable<DoctorProxy> table;

    protected Set<String> paths = new HashSet<String>();

    public void init() {
        paths.add("id");
        table.addColumn(new TextColumn<DoctorProxy>() {

            Renderer<java.lang.Long> renderer = new AbstractRenderer<java.lang.Long>() {

                public String render(java.lang.Long obj) {
                    return obj == null ? "" : String.valueOf(obj);
                }
            };

            @Override
            public String getValue(DoctorProxy object) {
                return renderer.render(object.getId());
            }
        }, "Id");
        paths.add("version");
        table.addColumn(new TextColumn<DoctorProxy>() {

            Renderer<java.lang.Integer> renderer = new AbstractRenderer<java.lang.Integer>() {

                public String render(java.lang.Integer obj) {
                    return obj == null ? "" : String.valueOf(obj);
                }
            };

            @Override
            public String getValue(DoctorProxy object) {
                return renderer.render(object.getVersion());
            }
        }, "Version");
        paths.add("gender");
        table.addColumn(new TextColumn<DoctorProxy>() {

            Renderer<ch.unibas.medizin.osce.shared.Gender> renderer = new AbstractRenderer<ch.unibas.medizin.osce.shared.Gender>() {

                public String render(ch.unibas.medizin.osce.shared.Gender obj) {
                    return obj == null ? "" : String.valueOf(obj);
                }
            };

            @Override
            public String getValue(DoctorProxy object) {
                return renderer.render(object.getGender());
            }
        }, "Gender");
        paths.add("title");
        table.addColumn(new TextColumn<DoctorProxy>() {

            Renderer<java.lang.String> renderer = new AbstractRenderer<java.lang.String>() {

                public String render(java.lang.String obj) {
                    return obj == null ? "" : String.valueOf(obj);
                }
            };

            @Override
            public String getValue(DoctorProxy object) {
                return renderer.render(object.getTitle());
            }
        }, "Title");
        paths.add("name");
        table.addColumn(new TextColumn<DoctorProxy>() {

            Renderer<java.lang.String> renderer = new AbstractRenderer<java.lang.String>() {

                public String render(java.lang.String obj) {
                    return obj == null ? "" : String.valueOf(obj);
                }
            };

            @Override
            public String getValue(DoctorProxy object) {
                return renderer.render(object.getName());
            }
        }, "Name");
        paths.add("preName");
        table.addColumn(new TextColumn<DoctorProxy>() {

            Renderer<java.lang.String> renderer = new AbstractRenderer<java.lang.String>() {

                public String render(java.lang.String obj) {
                    return obj == null ? "" : String.valueOf(obj);
                }
            };

            @Override
            public String getValue(DoctorProxy object) {
                return renderer.render(object.getPreName());
            }
        }, "Pre Name");
        paths.add("email");
        table.addColumn(new TextColumn<DoctorProxy>() {

            Renderer<java.lang.String> renderer = new AbstractRenderer<java.lang.String>() {

                public String render(java.lang.String obj) {
                    return obj == null ? "" : String.valueOf(obj);
                }
            };

            @Override
            public String getValue(DoctorProxy object) {
                return renderer.render(object.getEmail());
            }
        }, "Email");
        paths.add("telephone");
        table.addColumn(new TextColumn<DoctorProxy>() {

            Renderer<java.lang.String> renderer = new AbstractRenderer<java.lang.String>() {

                public String render(java.lang.String obj) {
                    return obj == null ? "" : String.valueOf(obj);
                }
            };

            @Override
            public String getValue(DoctorProxy object) {
                return renderer.render(object.getTelephone());
            }
        }, "Telephone");
        paths.add("clinic");
        table.addColumn(new TextColumn<DoctorProxy>() {

            Renderer<ch.unibas.medizin.osce.client.managed.request.ClinicProxy> renderer = ch.unibas.medizin.osce.client.managed.ui.ClinicProxyRenderer.instance();

            @Override
            public String getValue(DoctorProxy object) {
                return renderer.render(object.getClinic());
            }
        }, "Clinic");
        paths.add("office");
        table.addColumn(new TextColumn<DoctorProxy>() {

            Renderer<ch.unibas.medizin.osce.client.managed.request.OfficeProxy> renderer = ch.unibas.medizin.osce.client.managed.ui.OfficeProxyRenderer.instance();

            @Override
            public String getValue(DoctorProxy object) {
                return renderer.render(object.getOffice());
            }
        }, "Office");
        paths.add("isActive");
        table.addColumn(new TextColumn<DoctorProxy>() {

            Renderer<java.lang.Boolean> renderer = new AbstractRenderer<java.lang.Boolean>() {

                public String render(java.lang.Boolean obj) {
                    return obj == null ? "" : String.valueOf(obj);
                }
            };

            @Override
            public String getValue(DoctorProxy object) {
                return renderer.render(object.getIsActive());
            }
        }, "Is Active");
        paths.add("specialisation");
        table.addColumn(new TextColumn<DoctorProxy>() {

            Renderer<ch.unibas.medizin.osce.client.managed.request.SpecialisationProxy> renderer = ch.unibas.medizin.osce.client.managed.ui.SpecialisationProxyRenderer.instance();

            @Override
            public String getValue(DoctorProxy object) {
                return renderer.render(object.getSpecialisation());
            }
        }, "Specialisation");
        paths.add("roleParticipants");
        table.addColumn(new TextColumn<DoctorProxy>() {

            Renderer<java.util.Set> renderer = ch.unibas.medizin.osce.client.scaffold.place.CollectionRenderer.of(ch.unibas.medizin.osce.client.managed.ui.RoleParticipantProxyRenderer.instance());

            @Override
            public String getValue(DoctorProxy object) {
                return renderer.render(object.getRoleParticipants());
            }
        }, "Role Participants");
    }
}
