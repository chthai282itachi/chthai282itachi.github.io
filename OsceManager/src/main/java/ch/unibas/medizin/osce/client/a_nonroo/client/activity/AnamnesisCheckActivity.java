package ch.unibas.medizin.osce.client.a_nonroo.client.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.unibas.medizin.osce.client.a_nonroo.client.place.AnamnesisCheckDetailsPlace;
import ch.unibas.medizin.osce.client.a_nonroo.client.place.AnamnesisCheckPlace;
import ch.unibas.medizin.osce.client.a_nonroo.client.receiver.OSCEReceiver;
import ch.unibas.medizin.osce.client.a_nonroo.client.request.OsMaRequestFactory;
import ch.unibas.medizin.osce.client.a_nonroo.client.ui.AnamnesisCheckTitlePopupView;
import ch.unibas.medizin.osce.client.a_nonroo.client.ui.AnamnesisCheckTitlePopupViewImpl;
import ch.unibas.medizin.osce.client.a_nonroo.client.ui.AnamnesisCheckView;
import ch.unibas.medizin.osce.client.a_nonroo.client.ui.AnamnesisCheckViewImpl;
import ch.unibas.medizin.osce.client.a_nonroo.client.ui.examination.MessageConfirmationDialogBox;
import ch.unibas.medizin.osce.client.a_nonroo.client.util.ApplicationLoadingScreenEvent;
import ch.unibas.medizin.osce.client.a_nonroo.client.util.ApplicationLoadingScreenHandler;
import ch.unibas.medizin.osce.client.a_nonroo.client.util.MenuClickEvent;
import ch.unibas.medizin.osce.client.managed.request.AnamnesisCheckProxy;
import ch.unibas.medizin.osce.client.managed.request.AnamnesisCheckRequest;
import ch.unibas.medizin.osce.client.managed.request.AnamnesisCheckTitleProxy;
import ch.unibas.medizin.osce.client.managed.request.AnamnesisCheckTitleRequest;
import ch.unibas.medizin.osce.client.style.widgetsnewcustomsuggestbox.test.client.ui.widget.suggest.impl.simple.DefaultSuggestOracle;
import ch.unibas.medizin.osce.shared.Operation;
import ch.unibas.medizin.osce.shared.i18n.OsceConstants;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.requestfactory.client.RequestFactoryEditorDriver;
import com.google.gwt.requestfactory.shared.EntityProxyId;
import com.google.gwt.requestfactory.shared.Receiver;
import com.google.gwt.requestfactory.shared.Request;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.Range;

public class AnamnesisCheckActivity extends AbstractActivity implements
        AnamnesisCheckView.Presenter, 
        AnamnesisCheckView.Delegate,
        AnamnesisCheckTitlePopupView.Delegate {

    private OsMaRequestFactory requests;
    private PlaceController placeController;
    private AnamnesisCheckPlace place;
    private AcceptsOneWidget widget;
    private AnamnesisCheckView view;
    private HandlerRegistration rangeChangeHandler;
    private HandlerRegistration selectionChangeHandler;
    private ActivityManager activityManger;
    private AnamnesisCheckDetailsActivityMapper anamnesisCheckDetailsActivityMapper;
    private final OsceConstants constants = GWT.create(OsceConstants.class);
    static AnamnesisCheckRequest request = null;
    private static final String placeToken = "AnamnesisCheckPlace";
    private HandlerRegistration placeChangeHandlerRegistration;
    
    private Integer sort_order = 0;
	private String title = "";
	 public AnamnesisCheckView innerSystemStartView; 
    private List<AnamnesisCheckTitleProxy> anamnesisCheckTitles = new ArrayList<AnamnesisCheckTitleProxy>();

    public AnamnesisCheckActivity(OsMaRequestFactory requests,
            PlaceController placeController, AnamnesisCheckPlace place) {
        this.requests = requests;
        this.placeController = placeController;
        anamnesisCheckDetailsActivityMapper = new AnamnesisCheckDetailsActivityMapper(
                requests, placeController);
        this.activityManger = new ActivityManager(
                anamnesisCheckDetailsActivityMapper, requests.getEventBus());
        this.place = place;
    }

    /**
     * Called when the activity stops
     */
    @Override
    public void onStop() {
        activityManger.setDisplay(null);
        if (rangeChangeHandler != null) {
            rangeChangeHandler.removeHandler();
            rangeChangeHandler = null;
        }

        if (selectionChangeHandler != null) {
            selectionChangeHandler.removeHandler();
            selectionChangeHandler = null;
        }

	    if (placeChangeHandlerRegistration != null) {
	        placeChangeHandlerRegistration.removeHandler();
	    }
	    request = null;
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
    public String mayStop() {
        return null;
    }


    /**
     * The activity has started
     */
    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        Log.info("SystemStartActivity.start()");
        AnamnesisCheckView systemStartView = new AnamnesisCheckViewImpl();
        innerSystemStartView= systemStartView;
//        AnamnesisCheckView systemStartView = getAnamnesisCheckView();
        systemStartView.setPresenter(this);
        this.widget = panel;
        this.view = systemStartView;

        MenuClickEvent.register(requests.getEventBus(), (AnamnesisCheckViewImpl)view);
        
        widget.setWidget(systemStartView.asWidget());
        init();

        activityManger.setDisplay(view.getDetailsPanel());
        placeChangeHandlerRegistration = eventBus.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {

            @Override
            public void onPlaceChange(PlaceChangeEvent event) {
                if (event.getNewPlace() instanceof AnamnesisCheckDetailsPlace) {
                	requests.getEventBus().fireEvent(new ApplicationLoadingScreenEvent(true));
                	view.setDetailPanel(true);
                	requests.getEventBus().fireEvent(new ApplicationLoadingScreenEvent(false));
                    AnamnesisCheckDetailsPlace place = (AnamnesisCheckDetailsPlace) event.getNewPlace();
                    if (place.getOperation() == Operation.NEW) {
                    	requests.getEventBus().fireEvent(new ApplicationLoadingScreenEvent(false));
                        getSearchStringByEntityProxyId((EntityProxyId<AnamnesisCheckProxy>)place.getProxyId());
                    }
                } else if (event.getNewPlace() instanceof AnamnesisCheckPlace) {
                    view.setDetailPanel(false);
                    AnamnesisCheckPlace place = (AnamnesisCheckPlace) event.getNewPlace();
                    if (place.getToken().contains("!DELETED")) {
//                        initSearch();
                    }
                }
            }
        });
    }

    /**
      * Used to fill table and search field after creating new entity.
      * @param entityId
      */
     private void getSearchStringByEntityProxyId(EntityProxyId<AnamnesisCheckProxy> entityId) {
         requests.find(entityId).with("anamnesisCheckTitle").fire(new OSCEReceiver<AnamnesisCheckProxy>() {

            @Override
            public void onSuccess(AnamnesisCheckProxy proxy) {
                if (proxy != null) {
//                    view.getSearchBox().setText(proxy.getText());
                    view.filterTitle(proxy.getAnamnesisCheckTitle());
                    performSearch();
                }
            }
         });
     }

    private void init() {
    	Log.debug("AnamnesisCheckActivity.init()");
//        view.getFilterTitle().clear();
//        view.getAnamnesisCheckPanel().clear();
    	fireGetAllTitlesRequest(new FilterTitleReceiver());
        getTitlesBySearchStringAndFilter();

        if (place.getSearchStr().equals("")) {
            view.setSearchBoxShown(place.DEFAULT_SEARCHSTR);
//            initSearch();
            if (place.getFilterTileId().equals("")) {
                view.setSearchFocus(false);
            } else {

                view.setSearchFocus(true);
            }
        } else {
            view.setSearchBoxShown(place.getSearchStr());
//            initSearch();
            view.setSearchFocus(true);
        }

        view.setDelegate(this);
    }
    
    private class MoveProxyReceiver extends OSCEReceiver<Void> {
    	AnamnesisCheckTitleProxy title;
    	public MoveProxyReceiver(AnamnesisCheckTitleProxy title) {
    		this.title = title;
    	}
    	
    	@Override
        public void onSuccess(Void response) {
            setQuestionTableData(title);
        }
    }

    @Override
    public void moveUp(final AnamnesisCheckTitleProxy title, AnamnesisCheckProxy proxy) {
        requests.anamnesisCheckRequestNonRoo().moveUp().using(proxy).fire(new MoveProxyReceiver(title));
    }

    @Override
    public void moveDown(final AnamnesisCheckTitleProxy title, AnamnesisCheckProxy proxy) {
        requests.anamnesisCheckRequestNonRoo().moveDown().using(proxy).fire(new MoveProxyReceiver(title));
    }

    @Override
    public void deleteClicked(AnamnesisCheckProxy proxy) {
        // TODO implement deletion of anamnesis check via table view
    }

    protected Request<java.util.List<AnamnesisCheckProxy>> createRangeRequest(
            String q, AnamnesisCheckProxy title, Range range) {
        return requests.anamnesisCheckRequestNonRoo()
                .findAnamnesisChecksBySearchWithTitle(q, title, range.getStart(),
                        range.getLength());
    }

    protected void fireCountRequest(String q, AnamnesisCheckProxy title,
            Receiver<Long> callback) {
        requests.anamnesisCheckRequestNonRoo()
                .countAnamnesisChecksBySearchWithTitle(q, title).fire(callback);
    }

    protected void fireGetAllTitlesRequest(Receiver<List<AnamnesisCheckTitleProxy>> callback) {
    	requests.anamnesisCheckTitleRequest().findAllAnamnesisCheckTitles().fire(callback);
    }

    // find check value by title
    protected void fireCheckValueRequest(String searchValue,
            AnamnesisCheckProxy title,
            Receiver<List<AnamnesisCheckProxy>> callback) {
        requests.anamnesisCheckRequestNonRoo()
                .findAnamnesisChecksByTitle(searchValue, title)
                .with(view.getPaths()).fire(callback);
    }

    @Override
    public void newDetailClicked(String titleId) {
        Log.debug("newClicked()");
        goTo(new AnamnesisCheckDetailsPlace(Operation.CREATE , titleId));
    }

    @Override
    public void performSearch() {
        Log.debug("Search for " + view.getSearchBox().getText());
        getTitlesBySearchStringAndFilter();
//        initSearch();
    }

    String getSelectedTitleId() {
    	//Issue # 122 : Replace pull down with autocomplete.
        /*return view.getFilterTitle().getValue(
                view.getFilterTitle().getSelectedIndex());*/
    	if(view.getNewFilterTitle().getSelected()!=null)
    		return view.getNewFilterTitle().getSelected().getId().toString();
    	else
    		return "";
      //Issue # 122 : Replace pull down with autocomplete.
    }

    @Override
    public void goTo(Place place) {
        placeController.goTo(place);
    }

    /**
     * change Filter Title ListBox selectedValue
     */
    @SuppressWarnings("deprecation")
	@Override
    public void changeFilterTitleShown(String selectedtTitle) {
    	Log.debug("###########this is changeFilterTitleShown");

//		Window.alert("getSelectedFilterTitle() = " + getSelectedFilterTitle());
    	requests.anamnesisCheckRequestNonRoo().findTitlesContatisAnamnesisChecksWithSearching(place.getSearchStr(), getSelectedFilterTitle()).fire(new Receiver<List<AnamnesisCheckTitleProxy>>(){

			@Override
			public void onSuccess(List<AnamnesisCheckTitleProxy> response) {
				Log.debug("????????in changeFilterTitleShown response = "+response.size());
				view.getAnamnesisCheckPanel().clear();
				if((place.getSearchStr() == null || place.getSearchStr().equals("")) && getSelectedFilterTitle() == null){
					view.loadAnamnesisCheckPanel(response, false);
				}else{
					view.loadAnamnesisCheckPanel(response, true);
				}
			
			}
    		
    	});
    }
    
    private class FilterTitleReceiver extends OSCEReceiver<List<AnamnesisCheckTitleProxy>> {
    	@Override
    	public void onSuccess(List<AnamnesisCheckTitleProxy> response) {
    		anamnesisCheckTitles = response;
    		setAnamnesisCheckTitleList(response);
    	}
    }
    
	@SuppressWarnings("deprecation")
	private void getTitlesBySearchStringAndFilter() {
		requests.anamnesisCheckRequestNonRoo()
				.findTitlesContatisAnamnesisChecksWithSearching(view.getSearchBox().getText(), getSelectedFilterTitle())
				.fire(new FilteredTitleReceiver());
	}
	
	private class FilteredTitleReceiver extends OSCEReceiver<List<AnamnesisCheckTitleProxy>> {
		@Override
		public void onSuccess(List<AnamnesisCheckTitleProxy> response) {
			Log.debug("findTitlesContatisAnamnesisChecksWithSearching size = " + response.size());
			String query = view.getSearchBox().getText().trim();
			if ((query == null || query.equals("")) && getSelectedFilterTitle() == null) {
				view.loadAnamnesisCheckPanel(response, false);
			} else {
				view.loadAnamnesisCheckPanel(response, true);
			}
		}
	}

    /**
     * get Fileter Title AnamnesisCheckProxy
     *
     * @return AnamnesisCheckProxy
     */
    private AnamnesisCheckTitleProxy getSelectedFilterTitle() {
        for (AnamnesisCheckTitleProxy title : anamnesisCheckTitles) {
        	//Issue # 122 : Replace pull down with autocomplete.
            /*if (view.getFilterTitle().getSelectedIndex() != -1 && getSelectedTitleId().equals(String.valueOf(title.getId()))) {
                return title;
            }
            */
        	if (view.getNewFilterTitle().getSelected() != null && getSelectedTitleId().equals(String.valueOf(title.getId()))) {
                return title;
            }
            
          //Issue # 122 : Replace pull down with autocomplete.
        }
        return null;
    }

    /**
     * set Fileter Title ListBox install Value
     *
     * @param type
     *
     */
    public void setAnamnesisCheckTitleList(List<AnamnesisCheckTitleProxy> titles) {
    	//Issue # 122 : Replace pull down with autocomplete.
    	/*
 		view.getFilterTitle().clear();
    	Log.debug("fireTitleValueRequest sucess and getFilterTitle = "+place.getFilterTileId());
        view.getFilterTitle().addItem(constants.filterTitle(),"");
        view.getFilterTitle().setSelectedIndex(0);

        int idx = 1;
        for (AnamnesisCheckTitleProxy title : titles) {
            view.getFilterTitle().addItem(title.getText(),
                    String.valueOf(title.getId()));

            if (place.getFilterTileId() != null
                    && place.getFilterTileId().equals(
                            title.getId().toString())) {
                view.getFilterTitle().setSelectedIndex(idx);
            }
            idx++;
        }
        */
    	DefaultSuggestOracle<AnamnesisCheckTitleProxy> suggestOracle1 = (DefaultSuggestOracle<AnamnesisCheckTitleProxy>) view.getNewFilterTitle().getSuggestOracle();
    	suggestOracle1.setPossiblilities(titles);
    	view.getNewFilterTitle().setSuggestOracle(suggestOracle1);
    	
    	view.getNewFilterTitle().setRenderer(new AbstractRenderer<AnamnesisCheckTitleProxy>() {

			@Override
			public String render(AnamnesisCheckTitleProxy object) {
				// TODO Auto-generated method stub
				if(object!=null)
				{
				return object.getText();
				}
				else
				{
					return "";
				}
			}
		});

    	

    }

    @Override
    public void orderEdited(final AnamnesisCheckProxy proxy, String sortOrderStr) {
    	try {
    		requests.anamnesisCheckRequestNonRoo().
    				changeSortOrder(Integer.parseInt(sortOrderStr)).
    				using(proxy).fire(new OSCEReceiver<Void>() {

						@Override
						public void onSuccess(Void response) {
							// TODO Auto-generated method stub
							 view.filterTitle(proxy.getAnamnesisCheckTitle());
			                 performSearch();
						}
    				});
    	} catch (Exception e) {
    		Log.error(e.getMessage());
    	}

    }
    
    HashMap<AnamnesisCheckTitleProxy, ListDataProvider<AnamnesisCheckProxy>> mapTitlesToProviders = 
    		new HashMap<AnamnesisCheckTitleProxy, ListDataProvider<AnamnesisCheckProxy>>();
	private AnamnesisCheckTitleProxy editedTitle;
	private AnamnesisCheckTitleProxy deletedTitle;

    @Override
    public void addDataProvider(AnamnesisCheckTitleProxy title, ListDataProvider<AnamnesisCheckProxy> dataProvider) {
		mapTitlesToProviders.put(title, dataProvider);
//    	setQuestionTableData(dataProvider, title);
    }
    
    private void removeDataProvider(AnamnesisCheckTitleProxy title) {
    	mapTitlesToProviders.remove(title);
    }
    
    private class AnamnesisCheckReceiver extends OSCEReceiver<List<AnamnesisCheckProxy>> {
    	private ListDataProvider<AnamnesisCheckProxy> dataProvider;

		public AnamnesisCheckReceiver(ListDataProvider<AnamnesisCheckProxy> dataProvider) {
    		this.dataProvider = dataProvider;
    	}

		@Override
		public void onSuccess(List<AnamnesisCheckProxy> response) {
			Log.debug("setQuestionTableData() - sueccess and response size = " + response.size());
			Log.debug("dataProvider.getList().size() = " + dataProvider.getList().size());
			
			
			dataProvider.getList().clear();
			dataProvider.getList().addAll(response);
			//AnamnesisCheckActivity.this.view.getTable().setPageSize(dataProvider.getList().size());
			AnamnesisCheckActivity.this.view.setTableSize(dataProvider.getList().size());
			Log.info("page size--"+AnamnesisCheckActivity.this.view.getTable().getPageSize());
			AnamnesisCheckActivity.this.view.getTable().redraw();
			dataProvider.refresh();
			AnamnesisCheckActivity.this.view.getTable().redraw();
			//innerSystemStartView.getTable().cellTable.setPageSize(25);
			//anamnesisCheckTable.cellTable.setPageSize(25);
			Log.info("data provider--"+dataProvider.getList().size());
		}
    }
    
	@SuppressWarnings("deprecation")
	@Override
	public void setQuestionTableData(AnamnesisCheckTitleProxy title) {
		if (mapTitlesToProviders.size() < 1) {
			Log.warn("No data providers!?");
		}
				
		ListDataProvider<AnamnesisCheckProxy> dataProvider = mapTitlesToProviders.get(title);
		
		if (dataProvider.getList() != null) {
			Log.debug("view.getSearchBoxShown() = " + view.getSearchBoxShown());
			requests.anamnesisCheckRequestNonRoo().findAnamnesisChecksBySearchWithAnamnesisCheckTitle(view.getSearchBoxShown(), title).
					with("anamnesisCheckTitle").
					fire(new AnamnesisCheckReceiver(dataProvider));
		}
	}
	
    /**
     * Called from the table selection handler
     *
     * @param anamnesisCheck
     */
	@Override
    public void showDetails(AnamnesisCheckProxy anamnesisCheck) {

        Log.debug(anamnesisCheck.getId().toString());

        goTo(new AnamnesisCheckDetailsPlace(anamnesisCheck.stableId(),
                Operation.DETAILS));
    }
	
	private class TitleMovedReceiver extends OSCEReceiver<Void> {
		@Override
		public void onSuccess(Void response) {
			Log.debug("title moved");
		}
	}

	@Override
	public void moveDownTitle(AnamnesisCheckTitleProxy proxy) {
		requests.anamnesisCheckTitleRequestNonRoo().moveDown().using(proxy).fire(new TitleMovedReceiver());
		
	}

	@Override
	public void moveUpTitle(AnamnesisCheckTitleProxy proxy) {
		requests.anamnesisCheckTitleRequestNonRoo().moveUp().using(proxy).fire(new TitleMovedReceiver());
		
	}

	@Override
	public void editTitle(AnamnesisCheckTitleProxy title, UIObject refObj) {
//		if (titlePopupView != null && titlePopupView.isShowing()) {
//			titlePopupView.hide();
//			titlePopupView = null;
//		}
		editedTitle = title;
		titlePopupView = new AnamnesisCheckTitlePopupViewImpl(refObj);
		titleEditorDriver = titlePopupView.createEditorDriver();
		titlePopupView.setDelegate(this);
		requests.anamnesisCheckTitleRequest().findAnamnesisCheckTitle(title.getId()).fire(new OSCEReceiver<AnamnesisCheckTitleProxy>() {

			@Override
			public void onSuccess(AnamnesisCheckTitleProxy response) {
				AnamnesisCheckTitleRequest request = requests.anamnesisCheckTitleRequest();
				request.persist().using(response);
				titleEditorDriver.edit(response, request);
				titleEditorDriver.flush();
			}
		});
	}

	@SuppressWarnings("deprecation")
	@Override
	public void deleteTitle(final AnamnesisCheckTitleProxy title) {
		
		deletedTitle=title;
		
		requests.anamnesisCheckTitleRequestNonRoo().deleteTitleFromSpPortal(deletedTitle.getId()).fire(new OSCEReceiver<Boolean>() {

			@Override
			public void onSuccess(Boolean response) {
				if(response==null){
					showErrorMessageToUser("System could not delete AnamnesisCheckTitle from SpPortal for id : " +deletedTitle.getId());
				}else{

					requests.anamnesisCheckTitleRequest().remove().using(title).fire(new OSCEReceiver<Void>() {
	
						
						@Override
						public void onSuccess(Void response) {
							
	
							removeDataProvider(title);
							view.filterTitle(null);
							fireGetAllTitlesRequest(new FilterTitleReceiver());
					        getTitlesBySearchStringAndFilter();
						}
						
					});
				}
			}
			
		});
		
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void addNewTitleClicked(String titleText) {
		//Log.info("saveClicked");
		//Log.info("Map Size: " + view.getAnamnesisCheckTitleMap().size());
		
		sort_order = 0;
		title = titleText;
		requests.anamnesisCheckTitleRequestNonRoo().findMaxSortOrder().fire(new OSCEReceiver<Integer>() {
			@SuppressWarnings("deprecation")
			@Override
			public void onSuccess(Integer response) {
				if (response == null) {
					sort_order = 0;
				} else {
					sort_order = response;
				}
				
				if (title.trim().equals(""))
					title = null;
				
				AnamnesisCheckTitleRequest request = requests.anamnesisCheckTitleRequest();
				final AnamnesisCheckTitleProxy anamnesisCheckTitle = request.create(AnamnesisCheckTitleProxy.class);
				anamnesisCheckTitle.setText(title);
				anamnesisCheckTitle.setSort_order((sort_order+1));
				
				// Highlight onViolation
				request.persist().using(anamnesisCheckTitle).fire(new OSCEReceiver<Void>(view.getAnamnesisCheckTitleMap()) {
				// E Highlight onViolation
					@SuppressWarnings("deprecation")
					@Override
					public void onSuccess(Void response) {
						//Create Anamnisis check title in spportal
						requests.anamnesisCheckTitleRequestNonRoo().saveAnamnesisCheckTitleInSpPortal(anamnesisCheckTitle).fire(new OSCEReceiver<Boolean>() {

							@Override
							public void onSuccess(Boolean response) {
								if(response==null){
									showErrorMessageToUser("System could not create AnamnesisCheckTitle in Spportal");
								}else{
									Log.info("AnamnesisCheck title is created in sp portal successfully");
								}
							}
							
						});
						
						view.filterTitle(null);
						fireGetAllTitlesRequest(new FilterTitleReceiver());
				        getTitlesBySearchStringAndFilter();
					}
				});
			}
		});
	}
	
	private RequestFactoryEditorDriver<AnamnesisCheckTitleProxy,AnamnesisCheckTitlePopupViewImpl> titleEditorDriver;
	private AnamnesisCheckTitlePopupView titlePopupView;

	@SuppressWarnings("deprecation")
	@Override
	public void saveEditedTitle() {
		titleEditorDriver.flush().fire(new OSCEReceiver<Void>() {

			@SuppressWarnings("deprecation")
			@Override
			public void onSuccess(Void response) {
				
				requests.anamnesisCheckTitleRequestNonRoo().edittitleInSpportal(editedTitle.getId()).fire(new OSCEReceiver<Boolean>() {

					@Override
					public void onSuccess(Boolean response) {
						if(response==null){
							showErrorMessageToUser("System could not edit AnamnesisCheckTitle in spportal");
						}
					}
				});
				
				titlePopupView.hide();
				titlePopupView = null;
				titleEditorDriver = null;
				fireGetAllTitlesRequest(new FilterTitleReceiver());
				getTitlesBySearchStringAndFilter();
			}
		});
	}
	
	public void showErrorMessageToUser(String message){
		final MessageConfirmationDialogBox confirmationDialogBox =new MessageConfirmationDialogBox(constants.warning());
		confirmationDialogBox.showConfirmationDialog(message);
	}
}
