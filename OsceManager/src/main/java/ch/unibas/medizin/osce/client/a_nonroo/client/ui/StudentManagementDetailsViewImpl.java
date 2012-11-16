package ch.unibas.medizin.osce.client.a_nonroo.client.ui;

import java.util.ArrayList;
import java.util.List;

import ch.unibas.medizin.osce.client.managed.request.OsceProxy;
import ch.unibas.medizin.osce.client.managed.request.StudentOscesProxy;
import ch.unibas.medizin.osce.client.managed.request.StudentProxy;
import ch.unibas.medizin.osce.client.style.resources.AdvanceCellTable;
import ch.unibas.medizin.osce.client.style.resources.MyCellTableResources;
import ch.unibas.medizin.osce.client.style.widgets.ScrolledTabLayoutPanel;
import ch.unibas.medizin.osce.shared.OsMaConstant;
import ch.unibas.medizin.osce.shared.i18n.OsceConstants;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class StudentManagementDetailsViewImpl extends Composite implements StudentManagementDetailsView {

	private static StudentManagementDetailsViewImplUiBinder uiBinder = GWT
			.create(StudentManagementDetailsViewImplUiBinder.class);

	interface StudentManagementDetailsViewImplUiBinder extends UiBinder<Widget,StudentManagementDetailsViewImpl> {
	}
	
	
	private final OsceConstants constants = GWT.create(OsceConstants.class);
	
	public Delegate delegate;
	
	public StudentProxy studentProxy;
	
	public Presenter presenter;
	
	public List<String> paths =new ArrayList<String>();
	
	@UiField
	TabPanel studentDetailPanel;
	@UiField
	HorizontalPanel generalInformationHorizontalPanel;
	
	@UiField
	VerticalPanel generalInformationHorizontalVerticalPanel1;
	
	@UiField
	VerticalPanel generalInformationHorizontalVerticalPanel2;
	
	@UiField
	public Label Name;
	
	@UiField
	public Label Prename;
	
	@UiField
	public Label Street;
	
	@UiField
	public Label City;
	
	@UiField
	public Label Gender;
	
	@UiField
	public Label Email;
	
	@UiField(provided = true)
	public AdvanceCellTable<OsceProxy> table;
	
	public StudentManagementDetailsViewImpl() {
		
		CellTable.Resources tableResources = GWT.create(MyCellTableResources.class);
		table = new AdvanceCellTable<OsceProxy>(OsMaConstant.TABLE_PAGE_SIZE, tableResources);
		
		initWidget(uiBinder.createAndBindUi(this));
		init();
		
	}


	public void init(){
		
		studentDetailPanel.setVisible(true);
		studentDetailPanel.getTabBar().selectTab(0);
		
		paths.add("name");
		table.addColumn(new TextColumn<OsceProxy>() {
			
			{ this.setSortable(true); }	

			Renderer<java.lang.String> renderer = new AbstractRenderer<java.lang.String>() {

				public String render(java.lang.String obj) {
					return obj == null ? "" : String.valueOf(obj);
				}
			};

			@Override
			public String getValue(OsceProxy object) {
				return renderer.render(object.getName());
			}
		}, constants.name());
		
		paths.add("semester");
		table.addColumn(new TextColumn<OsceProxy>() {
			
			{ this.setSortable(true); }	

			Renderer<java.lang.String> renderer = new AbstractRenderer<java.lang.String>() {

				public String render(java.lang.String obj) {
					return obj == null ? "" : String.valueOf(obj);
				}
			};

			@Override
			public String getValue(OsceProxy object) {
				return renderer.render(object.getSemester().getCalYear().toString());
			}
		}, constants.preName());
		
		addColumn(new ActionCell<OsceProxy>(
				OsMaConstant.EDIT_ICON, new ActionCell.Delegate<OsceProxy>() {
					public void execute(OsceProxy osceProxy) {
						//showEditPopUp(nation);
					}
				}), "", new GetValue<OsceProxy>() {
			public OsceProxy getValue(OsceProxy osceProxy) {
				return osceProxy;
			}
		}, null);
		}

	private <C> void addColumn(Cell<C> cell, String headerText,
			final GetValue<C> getter, FieldUpdater<OsceProxy, C> fieldUpdater) {
		Column<OsceProxy, C> column = new Column<OsceProxy, C>(cell) {
			@Override
			public C getValue(OsceProxy object) {
				return getter.getValue(object);
			}
		};
		column.setFieldUpdater(fieldUpdater);
		if (cell instanceof AbstractEditableCell<?, ?>) {
			editableCells.add((AbstractEditableCell<?, ?>) cell);
		}
		table.addColumn(column);
	}
	
	/**
	 * Get a cell value from a record.
	 *
	 * @param <C> the cell type
	 */
	private static interface GetValue<C> {
		C getValue(OsceProxy contact);
	}

	private List<AbstractEditableCell<?, ?>> editableCells;

	

	
	@Override
	public void setDelegate(Delegate delegate) {
		this.delegate=delegate;
		
	}


	@Override
	public void setPresenter(Presenter systemStartActivity) {
		this.presenter=systemStartActivity;
	}


	@Override
	public void setStudentProxy(StudentProxy studentProxy) {
		
		this.studentProxy=studentProxy;
	}


	@Override
	public AdvanceCellTable<OsceProxy> getTable() {
		return table;
	}


	@Override
	public List<String> getPaths() {
		return paths;
	}

}
