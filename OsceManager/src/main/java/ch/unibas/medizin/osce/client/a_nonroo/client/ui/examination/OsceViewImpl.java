/**
 * 
 */
package ch.unibas.medizin.osce.client.a_nonroo.client.ui.examination;

import java.util.HashSet;
import java.util.Set;

import ch.unibas.medizin.osce.client.a_nonroo.client.OsMaMainNav;
import ch.unibas.medizin.osce.client.a_nonroo.client.activity.OsceEditActivity;
import ch.unibas.medizin.osce.client.a_nonroo.client.util.MenuClickEvent;
import ch.unibas.medizin.osce.client.a_nonroo.client.util.MenuClickHandler;
import ch.unibas.medizin.osce.client.a_nonroo.client.util.RecordChangeEvent;
import ch.unibas.medizin.osce.client.a_nonroo.client.util.RecordChangeHandler;
import ch.unibas.medizin.osce.client.managed.request.OsceProxy;
import ch.unibas.medizin.osce.client.style.resources.MyCellTableResources;
import ch.unibas.medizin.osce.shared.OsMaConstant;
import ch.unibas.medizin.osce.shared.i18n.OsceConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author dk
 *
 */
public class OsceViewImpl extends Composite implements  OsceView, RecordChangeHandler, MenuClickHandler {

	private static OsceViewUiBinder uiBinder = GWT
			.create(OsceViewUiBinder.class);

	interface OsceViewUiBinder extends UiBinder<Widget, OsceViewImpl> {
	}

	private Delegate delegate;
	private final OsceConstants constants = GWT.create(OsceConstants.class);

	@UiField
	SplitLayoutPanel splitLayoutPanel;

	@UiField
	Button newButton;
	
	

	@UiField
	SimplePanel detailsPanel;

	/*@UiField (provided = true)
	SimplePager pager;
*/
	@UiField (provided = true)
	CellTable<OsceProxy> table;

	protected Set<String> paths = new HashSet<String>();

	private Presenter presenter;

	@UiField
	HTMLPanel westPanel;
	
	@UiField
	ScrollPanel scrollPanel;
	
	int widthSize=1225,decreaseSize=0;
	Timer timer;
	
	@UiHandler ("newButton")
	public void newButtonClicked(ClickEvent event) {
		delegate.newClicked();
	}

	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
	public OsceViewImpl() {
		CellTable.Resources tableResources = GWT.create(MyCellTableResources.class);
		table = new CellTable<OsceProxy>(OsMaConstant.TABLE_PAGE_SIZE, tableResources);

	/*	SimplePager.Resources pagerResources = GWT.create(MySimplePagerResources.class);
		pager = new SimplePager(SimplePager.TextLocation.RIGHT, pagerResources, true, OsMaConstant.TABLE_JUMP_SIZE, true);
*/
		initWidget(uiBinder.createAndBindUi(this));
		init();
		splitLayoutPanel.setWidgetMinSize(splitLayoutPanel.getWidget(0), OsMaConstant.SPLIT_PANEL_MINWIDTH);
		newButton.setText(constants.addOsce());
	}

	public String[] getPaths() {
		return paths.toArray(new String[paths.size()]);
	}

	public void init() {
		
		int left = (OsMaMainNav.getMenuStatus() == 0) ? 40 : 225;
		
		// bugfix to avoid hiding of all panels (maybe there is a better solution...?!)
		DOM.setElementAttribute(splitLayoutPanel.getElement(), "style", "position: absolute; left: "+left+"px; top: 30px; right: 5px; bottom: 0px;");

		//spec start add tabel data
		
		paths.add("OSCE");
		table.addColumn(new TextColumn<OsceProxy>() {
			{ this.setSortable(true); }

			Renderer<java.lang.String> renderer = new AbstractRenderer<java.lang.String>() {

				public String render(java.lang.String obj) {
					return obj == null ? "" : String.valueOf(obj);
				}
			};

			@Override
			public String getValue(OsceProxy object) {
				if(object.getStudyYear()==null)
				{
					return " ";
				}
				else
				{
					String s=" "+object.getStudyYear()+"."+OsceEditActivity.semester.getSemester();
					if(object.getIsRepeOsce()==true)
					{
						s=s+" rape";
					}
				//String s=""+object.getStudyYear().ordinal();
				return renderer.render(s);
				}
			}
		}, "OSCE");
		
		paths.add("maxNumberStudents");
		table.addColumn(new TextColumn<OsceProxy>() {
			{ this.setSortable(true); }

			Renderer<java.lang.String> renderer = new AbstractRenderer<java.lang.String>() {

				public String render(java.lang.String obj) {
					return obj == null ? "" : String.valueOf(obj);
				}
			};

			@Override
			public String getValue(OsceProxy object) {
				if(object.getMaxNumberStudents()==null)
				{
					return " ";
				}
				else
				{
				return renderer.render(object.getMaxNumberStudents().toString());
				}
			}
		}, constants.maxStudents());
		
		paths.add("numberCourses");
		table.addColumn(new TextColumn<OsceProxy>() {
			{ this.setSortable(true); }

			Renderer<java.lang.String> renderer = new AbstractRenderer<java.lang.String>() {

				public String render(java.lang.String obj) {
					return obj == null ? "" : String.valueOf(obj);
				}
			};

			@Override
			public String getValue(OsceProxy object) {
				if(object.getNumberCourses()==null)
				{
					return " ";
				}
				else
				{
				return renderer.render(object.getNumberCourses().toString());
				}
			}
		}, constants.maxCircuits());
		
		paths.add("postLength");
		table.addColumn(new TextColumn<OsceProxy>() {
			{ this.setSortable(true); }

			Renderer<java.lang.String> renderer = new AbstractRenderer<java.lang.String>() {

				public String render(java.lang.String obj) {
					return obj == null ? "" : String.valueOf(obj);
				}
			};

			@Override
			public String getValue(OsceProxy object) {
				if(object.getPostLength()==null)
				{
					return " ";
				}
				else
				{
				return renderer.render(object.getPostLength().toString());
				}
			}
		}, constants.stationLength());
		
		
		paths.add("osceBreak");
		table.addColumn(new TextColumn<OsceProxy>() {
			{ this.setSortable(true); }

			Renderer<java.lang.String> renderer = new AbstractRenderer<java.lang.String>() {

				public String render(java.lang.String obj) {
					return obj == null ? "" : String.valueOf(obj);
				}
			};

			@Override
			public String getValue(OsceProxy object) {
				String min="";
				if(object.getShortBreak()!=null)
				{
					min=min+object.getShortBreak();
					
				}
				if(object.getLongBreak()!=null)
				{
					min=min+"/"+object.getLongBreak();
				}
				else
				{
					min=min+"/-";
				}
				
				if(object.getLunchBreak()!=null)
				{
					min=min+"/"+object.getLunchBreak();
				}
				else
				{
					min=min+"/-";
				}
				
				min=min+" min";
				return min;
				//return renderer.render(object.getShortBreak().toString()+"/"+object.getLongBreak().toString()+"/"+object.getLunchBreak().toString()+" min");
			}
		}, constants.osceBreak());
		
		
		
		
		//spec end 
		/*paths.add("OSCE");
		table.addColumn(new TextColumn<OsceProxy>() {

			Renderer<java.lang.Long> renderer = new AbstractRenderer<java.lang.Long>() {

				public String render(java.lang.Long obj) {
					return obj == null ? "" : String.valueOf(obj);
				}
			};

			@Override
			public String getValue(OsceProxy object) {
				return renderer.render(object.getId());
			}
		}, "OSCE");
		paths.add("max");
		table.addColumn(new TextColumn<OsceProxy>() {

			Renderer<String> renderer = new AbstractRenderer<String>() {

				public String render(String obj) {
					return obj == null ? "" : String.valueOf(obj);
				}
			};

			@Override
			public String getValue(OsceProxy object) {
				return renderer.render(object.getStudyYear().toString());
			}
		}, constants.studyYear());
		paths.add("preName");
		table.addColumn(new TextColumn<OsceProxy>() {

			Renderer<Integer> renderer = new AbstractRenderer<Integer>() {

				public String render(Integer obj) {
					return obj == null ? "" : String.valueOf(obj);
				}
			};

			@Override
			public String getValue(OsceProxy object) {
				return renderer.render(object.getMaxNumberStudents());
			}
		}, constants.maxStudents());
		paths.add("email");
		table.addColumn(new TextColumn<OsceProxy>() {

			Renderer<Integer> renderer = new AbstractRenderer<Integer>() {

				public String render(Integer obj) {
					return obj == null ? "" : String.valueOf(obj);
				}
			};

			@Override
			public String getValue(OsceProxy object) {
				return renderer.render(object.getNumberCourses());
			}
		}, constants.maxCircuits());
		//	        paths.add("responsibilities");
		//	        table.addColumn(new TextColumn<OsceProxy>() {
		//
		//	            Renderer<java.util.Set> renderer = ch.unibas.medizin.osce.client.scaffold.place.CollectionRenderer.of(ch.unibas.medizin.osce.client.managed.ui.ResponsibilityProxyRenderer.instance());
		//
		//	            @Override
		//	            public String getValue(OsceProxy object) {
		//	                return renderer.render(object.getResponsibilities());
		//	            }
		//	        }, "Responsibilities");
*/	}

	@Override
	public CellTable<OsceProxy> getTable() {
		return table;
	}

	@Override
	public void setDelegate(Delegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public SimplePanel getDetailsPanel() {
		return detailsPanel;
	}

	public void setDetailPanel(boolean isDetailPlace) {

		/*splitLayoutPanel.animate(150000);
//		widthSize = 1200;
//		decreaseSize = 0;
//		splitLayoutPanel.setWidgetSize(westPanel, widthSize);
		if (isDetailPlace) {

			timer = new Timer() {
				@Override
				public void run() {
					if (decreaseSize <= 705) {
						splitLayoutPanel.setWidgetSize(westPanel, 1225
								- decreaseSize);
						decreaseSize += 5;
					} else {
						timer.cancel();
					}
				}
			};
			timer.schedule(1);
			timer.scheduleRepeating(1);

		} else {
			widthSize = 1225;
			decreaseSize = 0;
			splitLayoutPanel.setWidgetSize(westPanel, widthSize);
		}*/
		splitLayoutPanel.setWidgetSize(westPanel, Integer.parseInt(constants.widthSize()) - Integer.parseInt(constants.widthMin()) );
		splitLayoutPanel.animate(Integer.parseInt(constants.animationTime()));	
	}
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	// by spec
	@Override
	public void onRecordChange(RecordChangeEvent event) {
		int pagesize = 0;

		if (event.getRecordValue() == "ALL") {
			pagesize = table.getRowCount();
			OsMaConstant.TABLE_PAGE_SIZE = pagesize;
		} else {
			pagesize = Integer.parseInt(event.getRecordValue());
		}

		table.setPageSize(pagesize);
	}
	// by spec

	
	@Override
	public void onMenuClicked(MenuClickEvent event) {
		
		OsMaMainNav.setMenuStatus(event.getMenuStatus());		
		int left = (OsMaMainNav.getMenuStatus() == 0) ? 40 : 225;
		
		DOM.setElementAttribute(splitLayoutPanel.getElement(), "style", "position: absolute; left: "+left+"px; top: 30px; right: 5px; bottom: 0px;");
		
		if(splitLayoutPanel.getWidget(0).getOffsetWidth() >= 1220){
			
			if(OsMaMainNav.getMenuStatus() == 0)
				splitLayoutPanel.setWidgetSize(splitLayoutPanel.getWidget(0), 1412);
			else
				splitLayoutPanel.setWidgetSize(splitLayoutPanel.getWidget(0), 1220);
		}
			
	}



}
