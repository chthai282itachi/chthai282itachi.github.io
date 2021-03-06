package ch.unibas.medizin.osce.domain;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import ch.unibas.medizin.osce.domain.spportal.SPPortalPerson;
import ch.unibas.medizin.osce.domain.spportal.SpOsceDate;
import ch.unibas.medizin.osce.domain.spportal.SpPatientInSemester;
import ch.unibas.medizin.osce.domain.spportal.SpSemester;
import ch.unibas.medizin.osce.domain.spportal.SpStandardizedPatient;
import ch.unibas.medizin.osce.domain.spportal.SpTrainingBlock;
import ch.unibas.medizin.osce.domain.spportal.SpTrainingDate;
import ch.unibas.medizin.osce.server.util.email.impl.EmailServiceImpl;
import ch.unibas.medizin.osce.shared.Semesters;
import ch.unibas.medizin.osce.shared.StandardizedPatientStatus;
import ch.unibas.medizin.osce.shared.SurveyStatus;

@RooJavaBean
@RooToString
@RooEntity
public class Semester {

	@PersistenceContext(unitName="persistenceUnit")
    transient EntityManager entityManager;
	
    private static Logger Log = Logger.getLogger(Semester.class);
	
	@NotNull
    @Enumerated
    private Semesters semester;

    private Integer calYear;
    
    private Double maximalYearEarnings;
    
    private Double pricestatist;
    
    private Double priceStandardizedPartient;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "semesters")
    private Set<Administrator> administrators = new HashSet<Administrator>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "semester")
    private Set<Osce> osces = new HashSet<Osce>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "semester")
    private Set<PatientInSemester> patientsInSemester = new HashSet<PatientInSemester>();
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "semester")
    private Set<TrainingBlock> trainingBlocks = new HashSet<TrainingBlock>();
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "semester")
    private Set<OsceDate> osceDates = new HashSet<OsceDate>();
    
    private Integer preparationRing;
    
    @Enumerated
    private SurveyStatus surveyStatus;   
    
    public static List<Semester> findAllSemesterOrderByYearAndSemester()
    {
    	EntityManager em = entityManager();
    	String query="select sem from Semester as sem order by sem.calYear desc, sem.semester asc";
    	TypedQuery<Semester> q = em.createQuery(query, Semester.class);
    	Log.info("Query String: " + query);
    	return q.getResultList();
    }    

    public static List<OsceDay> findAllOsceDayBySemester(Long semesterId)
    {
    	EntityManager em = entityManager();
    	String sql = "SELECT od FROM OsceDay od WHERE od.osce.semester.id = " + semesterId;
    	TypedQuery<OsceDay> query = em.createQuery(sql, OsceDay.class);
    	return query.getResultList();
    }
    
    public static Boolean surveyIsStartedSoPushDataToSpPortal(Long semId){
    	Log.info("Pushing data from osce to spportal");
    	try{
    	
    		Semester semester = new Semester();
    		return semester.pushDateToSpportalFromOsce(semId);
    	}catch (Exception e) {
    		Log.error(e.getMessage(), e);
			return null;
		}
    }


  //  @Transactional
	private Boolean pushDateToSpportalFromOsce(Long semId) {
		try{
    		
			Semester semester2 = Semester.findSemester(semId);
    		
    		SpSemester spSemester = SpSemester.findSemesterBasedOnYearAndSemester(semester2.getCalYear(), semester2.getSemester().ordinal());
    		
    		if(spSemester==null){
    			Log.info("Pushing semester to sp portal");
    			spSemester=new SpSemester();
    			spSemester.setCalYear(semester2.getCalYear());
    			spSemester.setMaximalYearEarnings(semester2.getMaximalYearEarnings());
    			spSemester.setPreparationRing(semester2.getPreparationRing());
    			spSemester.setPriceStandardizedPartient(semester2.getPriceStandardizedPartient());
    			spSemester.setPricestatist(semester2.getPricestatist());
    			spSemester.setSemester(semester2.getSemester());
    			spSemester.setSurveyStatus(semester2.getSurveyStatus());
    			spSemester.setId(semester2.getId());
    			spSemester.setSurveyStatus(SurveyStatus.OPEN);
    			spSemester.persist();
    			
    		}else{
    			spSemester = SpSemester.findSpSemester(spSemester.getId());
    			
    			spSemester.setSurveyStatus(SurveyStatus.OPEN);
        		
        		spSemester.persist();
    		}
    		this.pushTraingBlockAndTrainingDateToSpPortal(semester2,spSemester);
    		
    		this.pushOsceDateToSpPortal(semester2,spSemester);
    		
    		this.pushStandardizedDateToSpPortal(semester2,spSemester);
    		
    		this.pushPatientInSemesterDateToSpPortal(semester2,spSemester);
    		
    		this.setSPStatusOfExportedSp(semester2.getId());
    		
    		semester2.setSurveyStatus(SurveyStatus.OPEN);
    		
    		semester2.persist();
    		
    		//spSemester.findSpSemester(spSemester.getId());
    		
    	}catch (Exception e) {
			Log.error(e.getMessage(), e);
			return null;
		}
    	
    	return true;
	}
   
	@Transactional(propagation=Propagation.REQUIRED)
	private  void pushTraingBlockAndTrainingDateToSpPortal(Semester semster,SpSemester spSemester)throws Exception {
		try{
			
			//semster=Semester.findSemester(semster.getId());
			
			//spSemester=SpSemester.findSpSemester(spSemester.getId());
			
			Set<TrainingBlock> trainingBlockSet = semster.getTrainingBlocks();
			
			if(trainingBlockSet==null){
				return;
			}
			for (Iterator iterator = trainingBlockSet.iterator(); iterator.hasNext();) {
				
				TrainingBlock trainingBlock = (TrainingBlock) iterator.next();
				//fetching TB data from sp portal to avoid duplication.
				SpTrainingBlock spTb = SpTrainingBlock.findTrainingBlockBasedOnDateAndSemesterData(trainingBlock.getStartDate(),semster.getCalYear(),semster.getSemester().ordinal());
				
				if(spTb==null){
					//Training block is not exist in spportal so creating this and all associated training dates.
					SpTrainingBlock spTrainingBlock = new SpTrainingBlock();
					spSemester=SpSemester.findSpSemester(spSemester.getId());
					spTrainingBlock.setSemester(spSemester);
					spTrainingBlock.setStartDate(trainingBlock.getStartDate());
					spTrainingBlock.setId(trainingBlock.getId());
					
					Set<TrainingDate> trainingDateSet = trainingBlock.getTrainingDates();
					
					Set<SpTrainingDate> spTrainingDateSet =new HashSet<SpTrainingDate>();
					
					if(trainingDateSet !=null){
						
						for (Iterator iterator2 = trainingDateSet.iterator(); iterator2.hasNext();) {
							
							TrainingDate trainingDate = (TrainingDate) iterator2.next();
						
							SpTrainingDate spTrainingDate = new SpTrainingDate();
							spTrainingDate.setIsAfternoon(trainingDate.getIsAfternoon());
							spTrainingDate.setTrainingBlock(spTrainingBlock);
							spTrainingDate.setTrainingDate(trainingDate.getTrainingDate());
							spTrainingDate.setId(trainingDate.getId());
							
							spTrainingDateSet.add(spTrainingDate);
						}
						spTrainingBlock.setTrainingDates(spTrainingDateSet);
						spTrainingBlock.persist();
					}
				}else{
					//Training block is exist in spportal so creating training dates that is not in spportal db.
					Set<TrainingDate> trainingDateSet = trainingBlock.getTrainingDates();
					
					if(trainingDateSet==null){
						return;
					}
					for (Iterator iterator2 = trainingDateSet.iterator(); iterator2.hasNext();) {
						
						TrainingDate trainingDate = (TrainingDate) iterator2.next();
					
						int isAfternoon;
						if(trainingDate.getIsAfternoon()){
							isAfternoon=1;
						}else{
							isAfternoon=0;
						}
						SpTrainingDate spTrainingDate = SpTrainingDate.findTrainingDateBasedOnDateAndTrainingBlock(trainingDate.getTrainingDate(),isAfternoon,spTb.getId());
						
						if(spTrainingDate==null){
						
							SpTrainingDate newSpTrainingDate = new SpTrainingDate();
							newSpTrainingDate.setIsAfternoon(trainingDate.getIsAfternoon());
							spTb=SpTrainingBlock.findSpTrainingBlock(spTb.getId());
							newSpTrainingDate.setTrainingBlock(spTb);
							newSpTrainingDate.setTrainingDate(trainingDate.getTrainingDate());
							newSpTrainingDate.setId(trainingDate.getId());
							newSpTrainingDate.persist();
						}
						
					}
				}
			}
			
		}catch (Exception e) {
			Log.error(e.getMessage(), e);
			throw e;
		}
		
	}
	@Transactional(propagation=Propagation.REQUIRED)
	private void pushOsceDateToSpPortal(Semester semster,SpSemester spSemester)throws Exception {
		try{
			
			//semster=Semester.findSemester(semster.getId());
			
			//spSemester=SpSemester.findSpSemester(spSemester.getId());
			
			Set<OsceDate> osceDateSet = semster.getOsceDates();
			
			if(osceDateSet!=null){
				
				for (Iterator iterator = osceDateSet.iterator(); iterator.hasNext();) {
					
					OsceDate osceDate = (OsceDate) iterator.next();
				
					SpOsceDate sposceDate = SpOsceDate.findOsceDateBasedOnDateAndSemesterData(osceDate.getOsceDate(),semster.getCalYear(),semster.getSemester().ordinal());
					
					if(sposceDate==null){
						SpOsceDate newOsceDate = new SpOsceDate();
						newOsceDate.setOsceDate(osceDate.getOsceDate());
						spSemester=SpSemester.findSpSemester(spSemester.getId());
						newOsceDate.setSemester(spSemester);
						newOsceDate.setId(osceDate.getId());
						newOsceDate.persist();
					}
				}
			}
		}catch (Exception e) {
			Log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	private void pushStandardizedDateToSpPortal(Semester semster,SpSemester spSemester)throws Exception {
		try{
			//semster=Semester.findSemester(semster.getId());
			List<StandardizedPatient> allActiveSps = StandardizedPatient.findAllSPWithStatusActive(semster.getId());
			if(allActiveSps !=null){
				for (StandardizedPatient standardizedPatient : allActiveSps) {
	
						SpStandardizedPatient newSpStandardizedPatient = new SpStandardizedPatient();
						newSpStandardizedPatient.setId(standardizedPatient.getId());
						SPPortalPerson spPortalPerson = SPPortalPerson.findSPPortalPerson(standardizedPatient.getSpPortalPersonId());
						newSpStandardizedPatient.setPerson(spPortalPerson);
						newSpStandardizedPatient.persist();
						
						standardizedPatient.setStatus(StandardizedPatientStatus.INSURVEY);
						standardizedPatient.persist();
				}
				// Un-comment call of following method to send email to all sps as survey is started.
				sendEmailToAllActiveSPsInformingServeyIsStart(allActiveSps);
			}
			
			
		}catch (Exception e) {
			Log.error(e.getMessage(), e);
			throw e;
		}
		
	}

	@Transactional(propagation=Propagation.REQUIRED)
	private  void pushPatientInSemesterDateToSpPortal(Semester semster,SpSemester spSemester)throws Exception {
		try{
						

			EntityManager em = SpSemester.entityManager();
			 
			
			//semster = Semester.findSemester(semster.getId());
		
			//finding list of all patient in sem in spportal
			/*List<SpPatientInSemester> spPatientInSemesterList = SpPatientInSemester.findPatientInSemesterBasedOnSemesterId(spSemester.getId());
			
			Long lastspPatientInSemId=0L;
			if date is found taking last persisted id as reference based on this id I will find all patient in semester in osce that is persisted after this id for given semester and
			 * if found such data than persist that in spportal this also help me that I don't have to check for duplicate entry. 
			
			if(spPatientInSemesterList!=null && spPatientInSemesterList.size() > 0){
			
				 lastspPatientInSemId= spPatientInSemesterList.get(0).getId();
			}*/
			List<PatientInSemester> patientInSemsList =PatientInSemester.findPatientInSemesterBasedOnSemAndId(/*lastspPatientInSemId,*/semster.getId());
			
			if(patientInSemsList==null){
				return;
			}
			//StringBuilder sql = new StringBuilder("INSERT INTO `patient_in_semester` (`id`,`accepted`,`value`,`version`,`person`,`semester`,`standardized_patient`) VALUES ");
			
			for(PatientInSemester patientInSem : patientInSemsList){
				
				SpPatientInSemester spPatientInSemester = new SpPatientInSemester();
				spPatientInSemester.setId(patientInSem.getId());
				spPatientInSemester.setAccepted(patientInSem.getAccepted());
				spPatientInSemester.setPerson(SPPortalPerson.findSPPortalPerson(patientInSem.getSpPortalPersonId()));
				spPatientInSemester.setSemester(spSemester);
				spPatientInSemester.setStandardizedPatient(SpStandardizedPatient.findSpStandardizedPatient(patientInSem.getStandardizedPatient().getId()));
				spPatientInSemester.setValue(patientInSem.getValue());
				
				spPatientInSemester.persist();
				/*sql.append(" (").append(patientInSem.getId()).append(", ").append(patientInSem.getAccepted()==true ? 1 : 0).append(", ").append(patientInSem.getValue()).append(", ").append("0, ")
				.append(patientInSem.getSpPortalPersonId()).append(", ").append(patientInSem.getSemester().getId()).append(", ").append(patientInSem.getStandardizedPatient().getId())
				.append("),");*/
			}
			
			/*String queryString = sql.toString().substring(0, sql.toString().length()-1);
			//queryString+=";";
			System.out.println("Query is :" + queryString);
		
			Query query =  em.createNativeQuery(queryString);
		
			//EntityTransaction transaction =	em.getTransaction();
			
			//transaction.begin();
			
			int totalEntryCreated =query.executeUpdate();*/
			
			//transaction.commit();
			
			//Log.info(totalEntryCreated +" patient in semester is created in spportal");
			
		}catch (Exception e) {
			Log.error(e.getMessage(), e);
			throw e;
		}
		
	}
	@Transactional(propagation=Propagation.REQUIRED)
    private void setSPStatusOfExportedSp(Long semId)throws Exception{
    	try{
			List<StandardizedPatient> allExportedSps = StandardizedPatient.findAllSPWithStatusExported(semId);
			if(allExportedSps !=null){
				for (StandardizedPatient standardizedPatient : allExportedSps) {
	
						standardizedPatient.setStatus(StandardizedPatientStatus.EXPORTED_AND_SURVEY);
						standardizedPatient.persist();
				}
			}
		}catch (Exception e) {
			Log.error(e.getMessage(), e);
			throw e;
		}
		
	}

    @Transactional
    private void sendEmailToAllActiveSPsInformingServeyIsStart(List<StandardizedPatient> allActiveSps) {
		try{
			
			@SuppressWarnings("deprecation")
			HttpServletRequest request = com.google.gwt.requestfactory.server.RequestFactoryServlet.getThreadLocalRequest();
			
			HttpSession session = request.getSession();
			
			ServletContext servletContex =session.getServletContext();
			
			WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContex);
			 
			
			EmailServiceImpl emailServiceImpl =applicationContext.getBean(EmailServiceImpl.class);
			 
			VelocityEngine velocityEngine = applicationContext.getBean(VelocityEngine.class);
			
			velocityEngine.init();
			
			VelocityContext velocityContext = new VelocityContext();
			
			Template template = velocityEngine.getTemplate("templates/surveyStartedEmailTemplate.vm");
			
			Properties prop = new Properties();
			
			prop.load(applicationContext.getResource("classpath:META-INF/spring/smtp.properties").getInputStream());
			
			String subjec=prop.getProperty("spportal.surveyStarted.subject");
			
			for(StandardizedPatient sp : allActiveSps){

				StringWriter writer =new StringWriter();
				
				template.merge(velocityContext,writer);
				
				String emailContent = writer.toString(); //VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,"/templates/emailTemplate.vm", "UTF-8",null);
				Log.info("email that is sening to sp is" + emailContent);
				
				//sending email to user
				emailServiceImpl.sendMail( new String [] {sp.getEmail()},subjec,emailContent);
			}
		}catch (Exception e) {
			Log.error(e.getMessage(), e);
		}
    	
	}
    
    public static Boolean stopSurveyAndPushDateToOsceFromSpPortal(Long semId){
    	Log.info("Pushing data from spportal to osce");
    	try{
    		
    		Semester semester = Semester.findSemester(semId);
    		
    		semester.setSurveyStatus(SurveyStatus.CLOSED);
    		semester.persist();

    		Semester semester2 = new Semester();
    		return semester2.pushDateToOsceFromSPPortal(semId);

    	}catch (Exception e) {
    		Log.error(e.getMessage(), e);
			return null;
		}
    	
    }

   @Transactional
   private Boolean pushDateToOsceFromSPPortal(Long semId) {
	
	try{
		
		Semester semester = Semester.findSemester(semId);;
		
		SpSemester spSemester = SpSemester.findSemesterBasedOnYearAndSemester(semester.getCalYear(), semester.getSemester().ordinal());
		
		if(spSemester!=null){
			
			//spSemester.setSurveyStatus(SurveyStatus.CLOSED);
			//spSemester.persist();
			
			this.pushPatientInSemesterDataToOsce(semId,spSemester.getId());
			this.pushAcceptedOsceDataToOsce(spSemester.getId());
			this.pushAcceptedTrainingDateToOsce(spSemester.getId());

			/*spSemester.setSurveyStatus(SurveyStatus.CLOSED);
			spSemester.persist();*/
			this.updateSemesterStatus(spSemester.getId());
			this.createAcceptedOsceDayInOsce(spSemester.getId());
			this.deleteSpFromSpPortalWithStatusInSurvey(semId);
			this.setSpStatusFromExportedInServeyToExported(semId);
		}
		return true;
	}catch (Exception e) {
		Log.error(e.getMessage(), e);
		return null;
	}
}
	@Transactional
	private void pushPatientInSemesterDataToOsce(Long semId,Long spSemesterId)throws Exception {
	
		try{
			
			EntityManager em = Semester.entityManager();
			
			Semester semester = Semester.findSemester(semId);
			
			SpSemester spSemester = SpSemester.findSpSemester(spSemesterId);
			
			List<PatientInSemester> patientInSemList = PatientInSemester.findPatientInSemesterBasedOnSemesterId(semester.getId());
			
			Long lastspPatientInSemId=0L;
			/*if date is found taking last persisted id as reference based on this id I will find all patient in semester in osce that is persisted after this id for given semester and
			 * if found such data than persist that in spportal this also help me that I don't have to check for duplicate entry. 
			*/
			if(patientInSemList!=null && patientInSemList.size() > 0){
			
				 lastspPatientInSemId= patientInSemList.get(0).getId();
			}
			
			Set<SpPatientInSemester> setPatientInSemes= spSemester.getPatientsInSemester();
		
			if(setPatientInSemes!=null && setPatientInSemes.size() > 0){
				//Updating status of patient in sem as Patient may accepted it.
				for(SpPatientInSemester spPatientInSem : setPatientInSemes){
				
					String isAccepted=spPatientInSem.getAccepted() ? "1" :"0";
					
					String sql ="UPDATE `patient_in_semester` set accepted=" +isAccepted  +" WHERE id="+spPatientInSem.getId();
					
					Query query =  em.createNativeQuery(sql);
					
					query.executeUpdate();
				}
				
			}
			/*List<SpPatientInSemester> spPatientInSemsList =SpPatientInSemester.findPatientInSemesterBasedOnSemAndId(lastspPatientInSemId,spSemester.getId());
			
			if(spPatientInSemsList==null){
				return;
			}
			StringBuilder sql = new StringBuilder("INSERT INTO `patient_in_semester` (`id`,`accepted`,`value`,`version`,`semester`,`standardized_patient`,`sp_portal_person_id`) VALUES ");
			
			for(SpPatientInSemester patientInSem : spPatientInSemsList){
				
				sql.append(" (").append(patientInSem.getId()).append(", ").append(patientInSem.getAccepted()==true ? 1 : 0).append(", ").append(patientInSem.getValue()).append(", ").append("0, ")
				.append(patientInSem.getSemester().getId()).append(", ").append(patientInSem.getStandardizedPatient().getId()).append(", ").append(patientInSem.getPerson().getId()).append("),");
			}
			String queryString = sql.toString().substring(0, sql.toString().length()-1);
	
			Log.info("Query is :" + queryString);
		
			Query query =  em.createNativeQuery(queryString);
		
			//EntityTransaction transaction =	em.getTransaction();
			
			//transaction.begin();
			
			int totalEntryCreated =query.executeUpdate();*/
			
			//transaction.commit();
			
			//Log.info(totalEntryCreated +" patient in semester is created in spportal");
		}catch (Exception e) {
			Log.error(e.getMessage(), e);
			throw e;
		}
	
	}
	@Transactional
	private void pushAcceptedOsceDataToOsce(Long spSemesterId) {
		
		SpSemester spSemester = SpSemester.findSpSemester(spSemesterId);
		
		Set<SpPatientInSemester> spPatientInSemSet=spSemester.getPatientsInSemester();
		
		if(spPatientInSemSet!=null){
			
			for(SpPatientInSemester patientInSem : spPatientInSemSet){
				
				deleteOldAcceptedOsceDateBasedOnPatientInSemId(patientInSem.getId());
				
				Set<SpOsceDate> osceDateSet= patientInSem.getOsceDates();
				
				if(osceDateSet!=null && osceDateSet.size() > 0){
				
					createAccptedOsceDateInOsce(patientInSem.getId(),osceDateSet);
						
				}
				
			}
		}
		
	}


	@Transactional
	private void deleteOldAcceptedOsceDateBasedOnPatientInSemId(Long patientInSemId) {
		Log.info("Deleting old data from accepted_osce");
		EntityManager em = Semester.entityManager();
		
		StringBuilder sql = new StringBuilder("DELETE FROM `accepted_osce` WHERE `patient_in_semesters`="+patientInSemId);
		
		Query query =  em.createNativeQuery(sql.toString());
		
		int totalEntryDeleted =query.executeUpdate();
		
		Log.info(totalEntryDeleted +" accepted_osce are deleted from osce");
		
	}
	
	@Transactional
	private void createAccptedOsceDateInOsce(Long patientInSemId,Set<SpOsceDate> osceDateSet) {
		Log.info("creating data for accepted_osce");
		EntityManager em = Semester.entityManager();
		
		StringBuilder sql = new StringBuilder("INSERT INTO `accepted_osce` (`patient_in_semesters`,`osce_dates`) VALUES ");
		
		for(SpOsceDate osceDate : osceDateSet){
			
			sql.append(" (").append(patientInSemId).append(", ").append(osceDate.getId()).append("),");
		}
		
		String queryString = sql.toString().substring(0, sql.toString().length()-1);
		
		Log.info("Query is :" + queryString);
	
		Query query =  em.createNativeQuery(queryString);
		
		int totalEntryDeleted =query.executeUpdate();
		
		Log.info(totalEntryDeleted +" accepted_osce are created in osce");
	}
	
	@Transactional
	private void pushAcceptedTrainingDateToOsce(Long spSemesterId) {
		
		SpSemester spSemester = SpSemester.findSpSemester(spSemesterId);
		
		Set<SpPatientInSemester> spPatientInSemSet=spSemester.getPatientsInSemester();
		
		if(spPatientInSemSet!=null){
			
			for(SpPatientInSemester patientInSem : spPatientInSemSet){
				
				deleteOldAcceptedTrainingDataBasedOnPatientInSemId(patientInSem.getId());
				
				Set<SpTrainingDate> trainingDateSet= patientInSem.getTrainingDates();
				
				if(trainingDateSet!=null && trainingDateSet.size() > 0){
				
					createAccptedTrainingDateInOsce(patientInSem.getId(),trainingDateSet);
						
				}
				
			}
		}
		
	}
	@Transactional
	private void deleteOldAcceptedTrainingDataBasedOnPatientInSemId(Long patientInSemId) {
		Log.info("Deleting old data from accepted_trainings");
		EntityManager em = Semester.entityManager();
		
		StringBuilder sql = new StringBuilder("DELETE FROM `accepted_trainings` WHERE `patient_in_semesters`="+patientInSemId);
		
		Query query =  em.createNativeQuery(sql.toString());
		
		int totalEntryDeleted =query.executeUpdate();
		
		Log.info(totalEntryDeleted +"  accepted_trainings are deleted from osce");
		
	}
	@Transactional
	private void createAccptedTrainingDateInOsce(Long patientInSemId,Set<SpTrainingDate> trainingDateSet) {
		
		Log.info("creating data for accepted_trainings");
		
		EntityManager em = Semester.entityManager();
		
		StringBuilder sql = new StringBuilder("INSERT INTO `accepted_trainings` (`patient_in_semesters`,`training_dates`) VALUES ");
		
		for(SpTrainingDate trainingDate : trainingDateSet){
			
			sql.append(" (").append(patientInSemId).append(", ").append(trainingDate.getId()).append("),");
		}
		
		String queryString = sql.toString().substring(0, sql.toString().length()-1);
		
		Log.info("Query is :" + queryString);
	
		Query query =  em.createNativeQuery(queryString);
		
		int totalEntryDeleted =query.executeUpdate();
		
		Log.info(totalEntryDeleted +" accepted_trainings are created in osce");
	}

	@Transactional
	private void updateSemesterStatus(Long spSemId){
		
		EntityManager em = SpSemester.entityManager();
		
		StringBuilder sql = new StringBuilder("UPDATE `semester` set `survey_status`=2 where id="+spSemId);
		
		String queryString = sql.toString();
		
		Log.info("Query is :" + queryString);
	
		Query query =  em.createNativeQuery(queryString);
		
		query.executeUpdate();
		
		Log.info("Semester status updated in sp portal");
	}
	
	@Transactional
	private void createAcceptedOsceDayInOsce(Long spSemesterId) {
		Log.info("creating osce day with same date as accepted osce dates");
		
		SpSemester spSemester = SpSemester.findSpSemester(spSemesterId);
		
		Set<SpPatientInSemester> spPatientInSemSet=spSemester.getPatientsInSemester();
		
		if(spPatientInSemSet!=null){
			
			for(SpPatientInSemester patientInSem : spPatientInSemSet){
				
				Set<SpOsceDate> acceptedOsceDates = patientInSem.getOsceDates();
				
				Set<OsceDay> setOsceDay = new HashSet<OsceDay>();
				
				if(acceptedOsceDates!=null && acceptedOsceDates.size() > 0 ){
					
					for(SpOsceDate spOsceDate : acceptedOsceDates){
						OsceDay osceDay = new OsceDay();
						osceDay.setOsceDate(spOsceDate.getOsceDate());
						osceDay.persist();
						setOsceDay.add(osceDay);
					}
					PatientInSemester oscePatientInSem = PatientInSemester.findPatientInSemester(patientInSem.getId());
					oscePatientInSem.setOsceDays(setOsceDay);
					oscePatientInSem.persist();
				}
			}
		}
	}
	@Transactional
	private void deleteSpFromSpPortalWithStatusInSurvey(Long semId){
		
		Log.info("Deleting sp from spportal whose status is insurvey");
		
		List<StandardizedPatient> allInSurveySps=StandardizedPatient.findAllSPWithStatusInSurvey(semId);
		
		deletePatientInSemester(allInSurveySps);
		
		deleteSPFormSpPortal(allInSurveySps);
	}

	@Transactional
	private void deletePatientInSemester(List<StandardizedPatient> allInSurveySps) {
		
		EntityManager em = SpSemester.entityManager();
		
		StringBuilder sql = new StringBuilder("DELETE FROM `patient_in_semester` WHERE `standardized_patient` IN ( "+getIdOfSP(allInSurveySps) + " )");
		
		System.out.println("Query is" + sql.toString());
		
		Query query =  em.createNativeQuery(sql.toString());
		
		int totalEntryDeleted =query.executeUpdate();
		
		Log.info(totalEntryDeleted +"  patient in semester are deleted from spportal");
		
	}
	@Transactional
	private void deleteSPFormSpPortal(List<StandardizedPatient> allInSurveySps) {

		EntityManager em = SpSemester.entityManager();
		
		StringBuilder sql = new StringBuilder("DELETE FROM `standardized_patient` WHERE `id` IN ( "+getIdOfSP(allInSurveySps) + " )");
		
		System.out.println("Query is" + sql.toString());
		
		Query query =  em.createNativeQuery(sql.toString());
		
		int totalEntryDeleted =query.executeUpdate();
		
		Log.info(totalEntryDeleted +"  sps are deleted from spportal with status in survey");
	}

	private static String getIdOfSP(List<StandardizedPatient> spList) {

		if (spList == null|| spList.size() == 0) {
			Log.info("Return as null");
			return "";
		}
		Iterator<StandardizedPatient> splistIterator = spList.iterator();
		StringBuilder spIds = new StringBuilder();
		//spIds.append(",");
		while (splistIterator.hasNext()) {
			
			StandardizedPatient pis = splistIterator.next();

			spIds.append("'"+pis.getId()+"'");
			if (splistIterator.hasNext()) {
				spIds.append(" ,");
			}
		}
		
		return spIds.toString();
	}
	
	@Transactional
	private void setSpStatusFromExportedInServeyToExported(Long semId) {
		Log.info("Changeing status of sp from EXPORTED_AND_SURVEY TO EXPORTED");
		
		List<StandardizedPatient> allExportedAndSurveyStatusSp = StandardizedPatient.findAllSPWithStatusExportedANDSurvey(semId);
		if(allExportedAndSurveyStatusSp !=null){
			for(StandardizedPatient sp : allExportedAndSurveyStatusSp){
				sp.setStatus(StandardizedPatientStatus.EXPORTED);
				sp.persist();
			}
		}
	}
	
	public static Boolean checkAllSpIsAssignInRole(Long semId){
		try{
			boolean result=true;
			Log.info("checking whether All Sp Is Assign In Role");
			EntityManager em = Semester.entityManager();
			String query="select op from OscePost as op where op.osceSequence IN " +
					"(select id from OsceSequence as os where os.osceDay IN (select id from OsceDay as od where od.osce IN " +
					"(select id from Osce as o where o.semester.id="+semId+")))";
			Log.info("Query Is :" + query);
			
			TypedQuery<OscePost> q = em.createQuery(query, OscePost.class);
		
			List<OscePost> resultList = q.getResultList();
			
			for(OscePost op : resultList){
				
				if(op.getPatientInRole().size()==0){
					result=false;
					break;
				}
			}
			return result;
				
		}catch (Exception e) {
			Log.error(e.getMessage(), e);
			return null;
		}
		
	}
	
	public static List<OscePost> findTotalPostOfTheSemester(Long semId){
		try{

			Log.info("fininda ALl osce post of given semesrter : " + semId);
		
			EntityManager em = Semester.entityManager();
			
			String query="select op from OscePost as op where op.osceSequence IN " +
					"(select id from OsceSequence as os where os.osceDay IN (select id from OsceDay as od where od.osce IN " +
					"(select id from Osce as o where o.semester.id="+semId+")))";
			Log.info("Query Is :" + query);
			
			TypedQuery<OscePost> q = em.createQuery(query, OscePost.class);
		
			List<OscePost> resultList = q.getResultList();
		
			return resultList;
		
						
		}catch (Exception e) {
			Log.error(e.getMessage(), e);
			return null;
		}
		
	}
	
	public static Boolean findIsAssignTrainingDateAndOsceDate(Long semId){
	
		try{

			Log.info("fininda is atleast one osce date and training date is assigned for sem : " + semId);
		
			EntityManager em = Semester.entityManager();
			
			Semester sem = Semester.findSemester(semId);
			Set<OsceDate> setOsceDate = sem.getOsceDates();
			if(setOsceDate!=null && setOsceDate.size() > 0){
			
				String query="select td from TrainingDate as td where td.trainingBlock.semester.id="+semId;
				Log.info("Query Is :" + query);
				
				TypedQuery<TrainingDate> q = em.createQuery(query, TrainingDate.class);
				
				List<TrainingDate> resultList = q.getResultList();
				
				if(resultList!=null && resultList.size() >0 ){
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
			
		}catch (Exception e) {
			Log.error(e.getMessage(), e);
			return null;
		}
	}
}

