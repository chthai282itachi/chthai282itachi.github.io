package ch.unibas.medizin.osce.client.a_nonroo.client.activity;

import java.util.List;

import ch.unibas.medizin.osce.client.a_nonroo.client.place.OsceDetailsPlace;
import ch.unibas.medizin.osce.client.a_nonroo.client.place.OscePlace;
import ch.unibas.medizin.osce.client.a_nonroo.client.receiver.OSCEReceiver;
import ch.unibas.medizin.osce.client.a_nonroo.client.request.OsMaRequestFactory;
import ch.unibas.medizin.osce.client.a_nonroo.client.ui.examination.OsceView;
import ch.unibas.medizin.osce.client.a_nonroo.client.ui.examination.OsceViewImpl;
import ch.unibas.medizin.osce.client.a_nonroo.client.util.ApplicationLoadingScreenEvent;
import ch.unibas.medizin.osce.client.a_nonroo.client.util.ApplicationLoadingScreenHandler;
import ch.unibas.medizin.osce.client.a_nonroo.client.util.MenuClickEvent;
import ch.unibas.medizin.osce.client.a_nonroo.client.util.RecordChangeEvent;
import ch.unibas.medizin.osce.client.a_nonroo.client.util.SelectChangeEvent;
import ch.unibas.medizin.osce.client.a_nonroo.client.util.SelectChangeHandler;
import ch.unibas.medizin.osce.client.managed.request.MaterialListProxy;
import ch.unibas.medizin.osce.client.managed.request.OsceProxy;
import ch.unibas.medizin.osce.client.managed.request.SemesterProxy;
import ch.unibas.medizin.osce.client.style.resources.AdvanceCellTable;
import ch.unibas.medizin.osce.shared.Operation;
import ch.unibas.medizin.osce.shared.Sorting;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.cellview.client.AbstractHasData;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;

public class OsceActivity extends AbstractActivity implements OsceView.Presenter, OsceView.Delegate {

	private OsMaRequestFactory requests;
	private PlaceController placeController;	
	private AcceptsOneWidget widget;
	private OsceView view;
	private OsceView systemStartView;
	
	//cell table changes
	private AdvanceCellTable<OsceProxy> table;
	int x;
	int y;
	/*private CellTable<OsceProxy> table;*/
	//cell table changes
	private SingleSelectionModel<OsceProxy> selectionModel;
	private HandlerRegistration rangeChangeHandler;
	private ActivityManager activityManager;
	private OsceDetailsActivityMapper osceDetailsActivityMapper;
	//public SemesterProxy semesterProxy;

	// G: SPEC START =
			public  SemesterProxy semesterProxy;
			private HandlerManager handlerManager;// = new HandlerManager(this);
			private OscePlace place;
		// G: SPEC END =


	@Inject
	public OsceActivity(OsMaRequestFactory requests, PlaceController placeController) {
		this.requests = requests;
		this.placeController = placeController;
		osceDetailsActivityMapper = new OsceDetailsActivityMapper(requests, placeController);
		this.activityManager = new ActivityManager(osceDetailsActivityMapper, requests.getEventBus());
	}

	
	// G: SPEC START =
	public OsceActivity(OsMaRequestFactory requests,PlaceController placeController, OscePlace oscePlace) 
	{					
		
		this.requests = requests;
		this.placeController = placeController;
		osceDetailsActivityMapper = new OsceDetailsActivityMapper(requests, placeController);
		this.activityManager = new ActivityManager(osceDetailsActivityMapper, requests.getEventBus());
		this.place=oscePlace;
		this.handlerManager = oscePlace.handler;
		
		OsceDetailsActivity.osceActivity=this;
		OsceEditActivity.osceActivity=this;
		semesterProxy = oscePlace.semesterProxy;
		OsceEditActivity.semester=oscePlace.semesterProxy;
		Log.info("Semester Proxy Get:" + semesterProxy.getCalYear());
		
		this.addSelectChangeHandler(new SelectChangeHandler() 
		{			
			@Override
			public void onSelectionChange(SelectChangeEvent event) 
			{				
				Log.info("onSelectionChange Get Semester: " + event.getSemesterProxy().getCalYear());
				OsceEditActivity.semester=event.getSemesterProxy();
				semesterProxy=event.getSemesterProxy();
				init();			
			}
		});
	}
	public void addSelectChangeHandler(SelectChangeHandler handler) 
	{
		handlerManager.addHandler(SelectChangeEvent.getType(), handler);
	
	}
	// G: SPEC END =

	
	public void onStop(){
		activityManager.setDisplay(null);
	}

	public void registerLoading() {
		ApplicationLoadingScreenEvent.register(requests.getEventBus(),
				new ApplicationLoadingScreenHandler() {
					@Override
					public void onEventReceived(
							ApplicationLoadingScreenEvent event) {
						Log.info(" ApplicationLoadingScreenEvent onEventReceived Called");
					event.display();
					}
				});
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		Log.info("SystemStartActivity.start()");
		 systemStartView = new OsceViewImpl();
		systemStartView.setPresenter(this);
		this.widget = panel;
		this.view = systemStartView;
		widget.setWidget(systemStartView.asWidget());
		
		//by spec
		RecordChangeEvent.register(requests.getEventBus(), (OsceViewImpl) view);
		//by spec
		
		MenuClickEvent.register(requests.getEventBus(), (OsceViewImpl) view);
		
		setTable(view.getTable());
		
		
		//celltable changes start
		table.addHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				// TODO Auto-generated method stub
				Log.info("mouse down");
				x = event.getClientX();
				y = event.getClientY();

				if(table.getRowCount()>0)
				{
				Log.info(table.getRowElement(0).getAbsoluteTop() + "--"+ event.getClientY());

				
				if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT&& event.getClientY() < table.getRowElement(0).getAbsoluteTop()) {
					
					table.getPopup().setPopupPosition(x, y);
					table.getPopup().show();

					Log.info("right event");
				}
				}
				else
				{
					if(event.getNativeButton() == NativeEvent.BUTTON_RIGHT)
					{
						table.getPopup().setPopupPosition(x, y);
						table.getPopup().show();
					}
				}
			}
		}, MouseDownEvent.getType());
				
				
				table.getPopup().addDomHandler(new MouseOutHandler() {

					@Override
					public void onMouseOut(MouseOutEvent event) {
						// TODO Auto-generated method stub
						//addColumnOnMouseout();
						table.getPopup().hide();
						
					}
				}, MouseOutEvent.getType());
				
			 table.addColumnSortHandler(new ColumnSortEvent.Handler() {

					@Override
					public void onColumnSort(ColumnSortEvent event) {
						// By SPEC[Start

						Column<OsceProxy, String> col = (Column<OsceProxy, String>) event.getColumn();
						
						
						int index = table.getColumnIndex(col);
						
						
						/*String[] path =	systemStartView.getPaths();	            			
						sortname = path[index];
						sortorder=(event.isSortAscending())?Sorting.ASC:Sorting.DESC;	*/
						
						if (index % 2 == 1 ) {
							
							table.getPopup().setPopupPosition(x, y);
							table.getPopup().show();

						} /*else {
							// path = systemStartView.getPaths();
							//String[] path =	systemStartView.getPaths();	
							//int index = table.getColumnIndex(col);
							String[] path = systemStartView.getPaths();
							sortname = path[index];
							sortorder = (event.isSortAscending()) ? Sorting.ASC
									: Sorting.DESC;
							// By SPEC]end
							systemStartView.this.onRangeChanged();
							
							
						}*/
					}
				});
		/*requests.semesterRequest().findSemester(1L).fire(new OSCEReceiver<SemesterProxy>() {

			@Override
			public void onSuccess(SemesterProxy response) {
				// TODO Auto-generated method stub
				System.out.println("find semster:-"+response);
				semesterProxy=response;
			}
		});*/
		//spec start
		
		/*PlaceChangeEvent.Handler placeChangeHandler = new PlaceChangeEvent.Handler() {
			@Override
			public void onPlaceChange(PlaceChangeEvent event) {
				Log.debug("PlaceChangeEvent: " + event.getNewPlace().toString());
				if (event.getNewPlace() instanceof OsceDetailsPlace) {
					OsceDetailsPlace oscePlace = (OsceDetailsPlace) event.getNewPlace();
					Operation op = oscePlace.getOperation();
					System.out.println("If");
					if (op == Operation.NEW) {
						getSearchStringByEntityProxyId((EntityProxyId<StandardizedPatientProxy>)spdPlace.getProxyId());
					}
				} else if (event.getNewPlace() instanceof OscePlace) {
					OscePlace place = (OscePlace) event.getNewPlace();
					if (place.getToken().contains("DELETED")) {
						init();
					}
					System.out.println("else");
				}
			}
		};
		*/
		
		//spec end
		

		activityManager.setDisplay(view.getDetailsPanel());

		// Inherit the view's key provider
		ProvidesKey<OsceProxy> keyProvider = ((AbstractHasData<OsceProxy>) table).getKeyProvider();
		selectionModel = new SingleSelectionModel<OsceProxy>(keyProvider);
		table.setSelectionModel(selectionModel);
		
		//spec start
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange(SelectionChangeEvent event) {
				OsceProxy selectedObject = selectionModel
						.getSelectedObject();
				if (selectedObject != null) {
					Log.debug("OSCE with id " + selectedObject.getId() + " selected!");
					
					showDetails(selectedObject);
				}
			}
		});
			
		init();	
		//spec end
		view.setDelegate(this);
		
		/*requests.find(place.semesterProxy.stableId()).fire(new OSCEReceiver<Object>() {

			@Override
			public void onSuccess(Object response) {
				semesterProxy = (SemesterProxy)response;
				init();				
			}
			
		});*/

	}

	public void init() {
		
		/*requests.osceRequestNonRoo().findAllOsce().fire(new OSCEReceiver<List<OsceProxy>>() {

			@Override
			public void onSuccess(List<OsceProxy> response) {
				// TODO Auto-generated method stub
				System.out.println("Osce size--"+response.size());
				view.getTable().setRowCount(response.size(), false);
				view.getTable().setRowData(0, response);
			}
		});*/
		
		requests.osceRequestNonRoo().findAllOsceBySemster(semesterProxy.getId()).fire(new OSCEReceiver<List<OsceProxy>>() {

			@Override
			public void onSuccess(List<OsceProxy> response) {
				// TODO Auto-generated method stub
				System.out.println("Osce size--"+response.size());
				view.getTable().setRowCount(response.size(), false);
				view.getTable().setRowData(0, response);
			}
		});
		//spec start
		/*requests.osceRequest().countOsces().fire(new Receiver<Long>() {
			@Override
			public void onSuccess(Long response) {
				if (view == null) {
					// This activity is dead
					return;
				}
				Log.debug("Geholte Intitution aus der Datenbank: " + response);
				view.getTable().setRowCount(response.intValue(), true);

				onRangeChanged();
			}

		});

		rangeChangeHandler = table.addRangeChangeHandler(new RangeChangeEvent.Handler() {
			public void onRangeChange(RangeChangeEvent event) {
				OsceActivity.this.onRangeChanged();
			}
		});*/
		
		//spec end
	}

	protected void showDetails(OsceProxy osce) {
		Log.debug("show details for osce with id " + osce.getId());
		requests.getEventBus().fireEvent(new ApplicationLoadingScreenEvent(true));
		view.setDetailPanel(true);
		goTo(new OsceDetailsPlace(osce.stableId(), Operation.DETAILS));
		requests.getEventBus().fireEvent(new ApplicationLoadingScreenEvent(false));
		
	}
	
	

	//spec start
	/*protected void onRangeChanged() {
		final Range range = table.getVisibleRange();

		final Receiver<List<OsceProxy>> callback = new Receiver<List<OsceProxy>>() {
			@Override
			public void onSuccess(List<OsceProxy> values) {
				if (view == null) {
					// This activity is dead
					return;
				}
				//				idToRow.clear();
				//				idToProxy.clear();
				//				for (int i = 0, row = range.getStart(); i < values.size(); i++, row++) {
				//					OsceProxy administrator = values.get(i);
				//					@SuppressWarnings("unchecked")
				//					// Why is this cast needed?
				//					EntityProxyId<OsceProxy> proxyId = (EntityProxyId<OsceProxy>) administrator
				//							.stableId();
				//
				//				}
				table.setRowData(range.getStart(), values);

				// finishPendingSelection();
				if (widget != null) {
					widget.setWidget(view.asWidget());
				}
			}
		};

		fireRangeRequest(range, callback);
	}*/
	
	/*
	private void fireRangeRequest(final Range range, final Receiver<List<OsceProxy>> callback) {
		createRangeRequest(range).with(view.getPaths()).fire(callback);
	}

	protected Request<java.util.List<ch.unibas.medizin.osce.client.managed.request.OsceProxy>> createRangeRequest(Range range) {
		return requests.osceRequest().findOsceEntries(range.getStart(), range.getLength());
	}
*/
	//spec end

	public void setTable(CellTable<OsceProxy> table) {
		//this.table = table;
		//cell table changes
		this.table = (AdvanceCellTable<OsceProxy>)table;
	}

	@Override
	public void newClicked() {
		Log.info("create clicked");
		requests.getEventBus().fireEvent(new ApplicationLoadingScreenEvent(true));
		view.setDetailPanel(true);
		placeController.goTo(new OsceDetailsPlace(Operation.CREATE));
		requests.getEventBus().fireEvent(new ApplicationLoadingScreenEvent(false));
	
	}

	
	
	@Override
	public void goTo(Place place) {
		placeController.goTo(place);
	}

	@Override
	public CellTable<OsceProxy> getTable() {
		return table;
	}
	
	public SemesterProxy getSemester()
	{
		return semesterProxy;
	}
	
}
