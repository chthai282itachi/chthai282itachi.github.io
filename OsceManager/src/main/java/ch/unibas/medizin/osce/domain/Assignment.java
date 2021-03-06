package ch.unibas.medizin.osce.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import ch.unibas.medizin.osce.server.OsMaFilePathConstant;
import ch.unibas.medizin.osce.server.util.file.ExcelUtil;
import ch.unibas.medizin.osce.server.util.file.QwtUtil;
import ch.unibas.medizin.osce.shared.AssignmentTypes;
import ch.unibas.medizin.osce.shared.BellAssignmentType;
import ch.unibas.medizin.osce.shared.OsceStatus;
import ch.unibas.medizin.osce.shared.PostType;
import ch.unibas.medizin.osce.shared.RoleTypes;
import ch.unibas.medizin.osce.shared.Sorting;
import ch.unibas.medizin.osce.shared.TimeBell;


@RooJavaBean
@RooToString
@RooEntity(finders = { "findAssignmentsByOscePostRoomAndOsceDayAndTypeAndSequenceNumber" })
public class Assignment {

	@PersistenceContext(unitName="persistenceUnit")
    transient EntityManager entityManager;
	
	private static Logger Log = Logger.getLogger(Assignment.class);
	
    @Enumerated
    private AssignmentTypes type;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date timeStart;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date timeEnd;

    @NotNull
    @ManyToOne
    private OsceDay osceDay;

    @ManyToOne
    private OscePostRoom oscePostRoom;

    @ManyToOne
    private StudentOsces osceStudent;
    
    @ManyToOne
    private Student student;

    @ManyToOne
    private PatientInRole patientInRole;

    @ManyToOne
    private Doctor examiner;

    private Integer sequenceNumber;
    
    private Integer rotationNumber;
    /**
	 * Create new student assignment
	 * @param osceDay day on which this assignment takes place
	 * @param oscePR post-room-assignment
	 * @param studentIndex student which is examined in this assignment
	 * @param startTime time when the assignment starts
	 * @param endTime time when the assignment ends
	 * @param rotation is in which rotation this student is assigned
	 * @return
	 */
	public static Assignment createStudentAssignment(OsceDay osceDay, OscePostRoom oscePR, int studentIndex, Date startTime, Date endTime, int rotation) {
		Assignment ass2 = new Assignment();
		ass2.setType(AssignmentTypes.STUDENT);
		ass2.setOsceDay(osceDay);
		ass2.setSequenceNumber(studentIndex);
		ass2.setTimeStart(startTime);
		ass2.setTimeEnd(endTime);
		ass2.setOscePostRoom(oscePR);
		ass2.setRotationNumber(rotation);
		return ass2;
	}

    public static List<Assignment> retrieveAssignmentsOfTypeSP(Osce osce) {
        Log.info("retrieveAssignmenstOfTypeSP :");
        EntityManager em = entityManager();        
        String queryString = "SELECT o FROM Assignment AS o WHERE o.osceDay.osce = :osce AND o.type = :type AND o.oscePostRoom IS NOT NULL";
        TypedQuery<Assignment> q = em.createQuery(queryString, Assignment.class);
        q.setParameter("osce", osce);
        q.setParameter("type", AssignmentTypes.PATIENT);
        List<Assignment> assignmentList = q.getResultList();
        Log.info("retrieveAssignmenstOfTypeSP query String :" + queryString);
        Log.info("Assignment List Size :" + assignmentList.size());
        return assignmentList;
    }
    
    public static void updateSequenceNumbersOfTypeSPByTime(int sequenceNumber, Date timeStart, Date timeEnd) {
        EntityManager em = entityManager();
        String queryString = "SELECT o FROM Assignment AS o WHERE ((o.timeStart <= :timeEnd AND :timeStart <= o.timeEnd) OR (o.timeStart = :timeStart AND o.timeEnd = :timeEnd)) AND o.type = :type AND o.oscePostRoom IS NOT NULL";
        TypedQuery<Assignment> q = em.createQuery(queryString, Assignment.class);
        q.setParameter("timeStart", timeStart);
        q.setParameter("timeEnd", timeEnd);
        q.setParameter("type", AssignmentTypes.PATIENT);
        List<Assignment> assignmentList = q.getResultList();
        
        Iterator<Assignment> it = assignmentList.iterator();
        while (it.hasNext()) {
			Assignment assignment = (Assignment) it.next();
			assignment.setSequenceNumber(sequenceNumber);
			assignment.flush();
		}
    }
    
    /**
	 * Clear all SP break assignments (necessary since another run
	 * of the IFS might give different SP allocations and therefore
	 * different SP break assignments).
	 */
    public static void clearSPBreakAssignments(Osce osce) {
    	EntityManager em = entityManager();
    	String queryString = "SELECT o FROM Assignment AS o WHERE o.osceDay.osce = :osce AND o.type = :type AND oscePostRoom IS NULL";
        TypedQuery<Assignment> q = em.createQuery(queryString, Assignment.class);
        q.setParameter("osce", osce);
        q.setParameter("type", AssignmentTypes.PATIENT);
        List<Assignment> assignmentList = q.getResultList();
        
        Iterator<Assignment> it = assignmentList.iterator();
        while (it.hasNext()) {
        	Assignment assignment = (Assignment) it.next();
        	assignment.remove();
        }
    }
    
    //spec[
    public static void clearSPBreakAssignmentsByOsceDay(OsceDay osceDay) {
    	EntityManager em = entityManager();
    	String queryString = "SELECT o FROM Assignment AS o WHERE o.osceDay = :osceDay AND o.type = :type AND oscePostRoom IS NULL";
        TypedQuery<Assignment> q = em.createQuery(queryString, Assignment.class);
        q.setParameter("osceDay", osceDay);
        q.setParameter("type", AssignmentTypes.PATIENT);
        List<Assignment> assignmentList = q.getResultList();
        
        Iterator<Assignment> it = assignmentList.iterator();
        while (it.hasNext()) {
        	Assignment assignment = (Assignment) it.next();
        	assignment.remove();
        }
    }
    //spec]
    
    public static List<Assignment> retrieveAssignmentsOfTypeStudent(Long osceId) {
        EntityManager em = entityManager();
        String queryString = "SELECT o FROM Assignment AS o WHERE o.osceDay.osce.id = :osceId AND o.type = :type";
        TypedQuery<Assignment> q = em.createQuery(queryString, Assignment.class);
        q.setParameter("osceId", osceId);
        q.setParameter("type", AssignmentTypes.STUDENT);
        List<Assignment> assignmentList = q.getResultList();
        Log.info("retrieveAssignmentsOfTypeStudent query String :" + queryString);
        return assignmentList;
    }
    
    public static List<Assignment> retrieveAssignmentsOfTypeSPUniqueTimes(Osce osce) {
        EntityManager em = entityManager();
        String queryString = "SELECT o FROM Assignment AS o WHERE o.osceDay.osce = :osce AND o.type = :type AND o.oscePostRoom.oscePost.oscePostBlueprint.postType = :postType GROUP BY o.timeStart ORDER BY o.timeStart";
        TypedQuery<Assignment> q = em.createQuery(queryString, Assignment.class);
        q.setParameter("osce", osce);
        q.setParameter("postType", PostType.NORMAL);
        q.setParameter("type", AssignmentTypes.PATIENT);
        List<Assignment> assignmentList = q.getResultList();
        
        if (assignmentList != null && assignmentList.size() == 0)
        {
        	String queryString1 = "SELECT o FROM Assignment AS o WHERE o.osceDay.osce = :osce AND o.type = :type AND o.oscePostRoom.oscePost.oscePostBlueprint.postType IN (:postType) AND o.oscePostRoom.oscePost.oscePostBlueprint.isFirstPart = :isFirstPart GROUP BY o.timeStart ORDER BY o.timeStart";
            TypedQuery<Assignment> q1 = em.createQuery(queryString1, Assignment.class);
            q1.setParameter("osce", osce);
            List<PostType> postTypeList = new ArrayList<PostType>();
            postTypeList.add(PostType.ANAMNESIS_THERAPY);
            postTypeList.add(PostType.PREPARATION);
            q1.setParameter("postType", postTypeList);
            q1.setParameter("type", AssignmentTypes.PATIENT);
            q1.setParameter("isFirstPart", false);
            assignmentList = q1.getResultList();
        }
        
        return assignmentList;
    }
    
    //spec[
    public static List<Assignment> retrieveAssignmentsOfTypeSPUniqueTimesByOsceDay(OsceDay osceDay) {
        EntityManager em = entityManager();
        String queryString = "SELECT o FROM Assignment AS o WHERE o.osceDay = :osceDay AND o.type = :type AND o.oscePostRoom.oscePost.oscePostBlueprint.postType = :postType GROUP BY o.timeStart ORDER BY o.timeStart";
        TypedQuery<Assignment> q = em.createQuery(queryString, Assignment.class);
        q.setParameter("osceDay", osceDay);
        q.setParameter("postType", PostType.NORMAL);
        q.setParameter("type", AssignmentTypes.PATIENT);
        List<Assignment> assignmentList = q.getResultList();
        return assignmentList;
    }
  //spec]
    
    public Assignment retrieveAssignmentNeighbourOfTypeSP(int neighbour) {
        EntityManager em = Assignment.entityManager();
        String sortOrder = (neighbour == -1 ? "DESC" : "ASC");
        TypedQuery<Assignment> q = em.createQuery("SELECT o FROM Assignment AS o WHERE o.oscePostRoom = :oscePostRoom AND o.osceDay = :osceDay AND o.type = :type ORDER BY o.timeStart " + sortOrder, Assignment.class);
        q.setParameter("oscePostRoom", this.getOscePostRoom());
        q.setParameter("osceDay", this.getOsceDay());
        q.setParameter("type", AssignmentTypes.PATIENT);
        q.setMaxResults(1);
        
        if(q.getResultList().size() == 1) {
        	return q.getSingleResult();
        }
        
        return null;
    }

    public static List<Assignment> retrieveAssignments(Long osceDayId, Long osceSequenceId, Long courseId, Long oscePostId) {
        Log.info("retrieveAssignments :");
        EntityManager em = entityManager();
        String queryString = "SELECT  a FROM Assignment as a where a.osceDay=" + osceDayId + "  and a.oscePostRoom in(select opr.id from OscePostRoom as opr where opr.oscePost=" + oscePostId + " and opr.course=" + courseId + " ) order by a.timeStart asc";
        TypedQuery<Assignment> query = em.createQuery(queryString, Assignment.class);
        List<Assignment> assignmentList = query.getResultList();
        Log.info("retrieveAssignments query String :" + queryString);
        Log.info("Assignment List Size :" + assignmentList.size());
        return assignmentList;
    }

    public static List<Assignment> retrieveAssignmenstOfTypeStudent(Long osceDayId, Long osceSequenceId, Long courseId, Long oscePostId) {
    	
    	
    	
        Log.info("retrieveAssignmenstOfTypeStudent :");
        EntityManager em = entityManager();
        //String queryString = "SELECT  a FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=0 and a.oscePostRoom in(select opr.id from OscePostRoom as opr where opr.oscePost=" + oscePostId + " and opr.course=" + courseId + " ) order by a.timeStart asc";
      //  String queryString = "SELECT  a FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=0 and a.oscePostRoom in(select opr.id from OscePostRoom as opr where opr.room in (select rm.room from OscePostRoom as rm where rm.oscePost = " + oscePostId +  " and rm.course= " + courseId + " and rm.version<999) and opr.course=" + courseId + " ) order by a.timeStart asc";
        //String queryString = "SELECT  a FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=0 and a.oscePostRoom in(select opr.id from OscePostRoom as opr where (opr.room in (select rm.room from OscePostRoom as rm where rm.oscePost = " + oscePostId +  " and rm.course= " + courseId + " and rm.version<999) or opr.room is null) and opr.course=" + courseId + " and opr.oscePost = " + oscePostId +  " ) order by a.timeStart asc";
        String queryString = "SELECT  a FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=0 and a.oscePostRoom in(select opr.id from OscePostRoom as opr where (opr.room in (select rm.room from OscePostRoom as rm where rm.oscePost = " + oscePostId +  " and rm.course= " + courseId + " and rm.version<999 ) or (opr.room is null  and opr.oscePost.id="+oscePostId+" and opr.oscePost.oscePostBlueprint.postType = " + OscePost.findOscePost(oscePostId).getOscePostBlueprint().getPostType().ordinal() + ")) and opr.course=" + courseId + "  ) order by a.timeStart asc";
        TypedQuery<Assignment> query = em.createQuery(queryString, Assignment.class);
        List<Assignment> assignmentList = query.getResultList();
        Log.info("retrieveAssignmenstOfTypeStudent query String :" + queryString);
        Log.info("Assignment List Size :" + assignmentList.size());
        return assignmentList;
    }

    public static List<Assignment> retrieveAssignmenstOfTypeSP(Long osceDayId, Long osceSequenceId, Long courseId, Long oscePostId) {
        Log.info("retrieveAssignmenstOfTypeSP :");
        EntityManager em = entityManager();
        String queryString = "SELECT  a FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=1 and a.oscePostRoom in(select opr.id from OscePostRoom as opr where opr.oscePost=" + oscePostId + " and opr.course=" + courseId + " ) order by a.timeStart asc";
        TypedQuery<Assignment> query = em.createQuery(queryString, Assignment.class);
        List<Assignment> assignmentList = query.getResultList();
        Log.info("retrieveAssignmenstOfTypeSP query String :" + queryString);
        Log.info("Assignment List Size :" + assignmentList.size());
        return assignmentList;
    }

 public static Assignment findNxtSPSlot(Long osceDayId, Long osceSequenceId, Long courseId, Long oscePostId,Date timeStart) {
        Log.info("retrieveAssignmenstOfTypeSP :");
        EntityManager em = entityManager();
        String queryString = "SELECT  a FROM Assignment as a where a.timeStart<=:timeStart and a.timeEnd>=:timeEnd and a.osceDay=" + osceDayId + "  and type=1 and a.oscePostRoom in(select opr.id from OscePostRoom as opr where opr.oscePost=" + oscePostId + " and opr.course=" + courseId + " ) order by a.timeStart asc";
        TypedQuery<Assignment> query = em.createQuery(queryString, Assignment.class);
        query.setParameter("timeStart", timeStart);
        query.setParameter("timeEnd", timeStart);
        List<Assignment> assignmentList = query.getResultList();
        Log.info("retrieveAssignmenstOfTypeSP query String :" + queryString);
        Log.info("Assignment List Size :" + assignmentList.size());
        
        if(assignmentList.size()>0)
        return assignmentList.get(0);
        else
        	return null;
    }

    public static List<Assignment> retrieveAssignmenstOfTypeExaminer(Long osceDayId, Long osceSequenceId, Long courseId, Long oscePostId) {
        Log.info("retrieveAssignmenstOfTypeExaminer :");
        EntityManager em = entityManager();
        String queryString = "SELECT  a FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=2 and a.oscePostRoom in(select opr.id from OscePostRoom as opr where opr.oscePost=" + oscePostId + " and opr.course=" + courseId + " ) order by a.timeStart asc ";
        TypedQuery<Assignment> query = em.createQuery(queryString, Assignment.class);
        List<Assignment> assignmentList = query.getResultList();
        Log.info("retrieveAssignmenstOfTypeExaminer query String :" + queryString);
        Log.info("Assignment List Size :" + assignmentList.size());
        return assignmentList;
    }
    
    //retrieve Logical break post
    public static List<Assignment> retrieveAssignmentOfLogicalBreakPost(Long osceDayId,Long osceSequenceId)
    {
    	Log.info("retrieveAssignmentOfLogicalBreakPost :");
    	 EntityManager em = entityManager();
    	 OsceSequence seq=OsceSequence.findOsceSequence(osceSequenceId);
    	 Long courseId=seq.getCourses().get(0).getId();
    	 
    	 String maxTimeStartString= "SELECT  max(a.timeStart) FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=1 and a.oscePostRoom in(select opr.id from OscePostRoom as opr where  opr.course=" + courseId + " ) order by a.timeStart asc";
    	 TypedQuery<Date> maxTimeStartquery = em.createQuery(maxTimeStartString, Date.class);
    	 Date maxTimeStart=maxTimeStartquery.getResultList().get(0);
    	 
    	 String minTimeStartString= "SELECT  min(a.timeStart) FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=1 and a.oscePostRoom in(select opr.id from OscePostRoom as opr where  opr.course=" + courseId + " ) order by a.timeStart asc";
    	 TypedQuery<Date> minTimeStartquery = em.createQuery(minTimeStartString, Date.class);
    	 Date minTimeStart=minTimeStartquery.getResultList().get(0);
    	 
         String queryString = "SELECT  a FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=1 and a.oscePostRoom=null and a.timeStart <= :maxtimeStart and a.timeStart >= :mintimeStart order by a.timeStart asc ";
         TypedQuery<Assignment> query = em.createQuery(queryString, Assignment.class);
         query.setParameter("maxtimeStart", maxTimeStart);
         query.setParameter("mintimeStart", minTimeStart);
         List<Assignment> assignmentList = query.getResultList();
         Log.info("retrieveAssignmentOfLogicalBreakPost query String :" + queryString);
         Log.info("Assignment List Size :" + assignmentList.size());
         return assignmentList;
    }
    
  //retrieve Logical break post
    public static List<PatientInRole> findNxtSPLogicalBreak(Long osceDayId,Long osceSequenceId,Date timeStart)
    {
    	Log.info("findNxtSPLogicalBreak :");
    	 EntityManager em = entityManager();
    	 OsceSequence seq=OsceSequence.findOsceSequence(osceSequenceId);
    	 Long courseId=seq.getCourses().get(0).getId();
    	 
    	/* String maxTimeStartString= "SELECT  max(a.timeStart) FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=1 and a.oscePostRoom in(select opr.id from OscePostRoom as opr where  opr.course=" + courseId + " ) order by a.timeStart asc";
    	 TypedQuery<Date> maxTimeStartquery = em.createQuery(maxTimeStartString, Date.class);
    	 Date maxTimeStart=maxTimeStartquery.getResultList().get(0);
    	 
    	 String minTimeStartString= "SELECT  min(a.timeStart) FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=1 and a.oscePostRoom in(select opr.id from OscePostRoom as opr where  opr.course=" + courseId + " ) order by a.timeStart asc";
    	 TypedQuery<Date> minTimeStartquery = em.createQuery(minTimeStartString, Date.class);
    	 Date minTimeStart=minTimeStartquery.getResultList().get(0);*/
    	 
         String queryString = "SELECT  a.patientInRole FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=1 and a.oscePostRoom=null and a.timeStart <= :timeStart and a.timeEnd >= :timeEnd order by a.timeStart asc ";
         TypedQuery<PatientInRole> query = em.createQuery(queryString, PatientInRole.class);
         query.setParameter("timeStart", timeStart);
         query.setParameter("timeEnd", timeStart);
         List<PatientInRole> assignmentList = query.getResultList();
         Log.info("retrieveAssignmentOfLogicalBreakPost query String :" + queryString);
         Log.info("Assignment List Size :" + assignmentList.size());
         
         
         
         return assignmentList;
    }
    
  //retrieve Logical break post
    public static List<Assignment> retreiveSPBreak(Long osceDayId,Long osceSequenceId,int rotationId)
    {
    	Log.info("retrieveAssignmentOfLogicalBreakPost :");
    	 EntityManager em = entityManager();
         String queryString = "SELECT  a FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=1 and a.oscePostRoom=null and rotationNumber="+rotationId+"  order by a.timeStart asc ";
         TypedQuery<Assignment> query = em.createQuery(queryString, Assignment.class);
         List<Assignment> assignmentList = query.getResultList();
         Log.info("retrieveAssignmentOfLogicalBreakPost query String :" + queryString);
         Log.info("Assignment List Size :" + assignmentList.size());
         return assignmentList;
    }
    
  //Testing task {

    // Test Case 2
    public static List<Assignment> findAssignmentForTestBasedOnCriteria(Long osceDayId,List<AssignmentTypes> type){
    	Log.info("Inside findAssignmentForTestBasedOnCriteria() ");
    /*	EntityManager em = entityManager();
    	String query="select a from Assignment a where a.osceDay = " + osceDayId+" and a.type In(:osceType) and a.oscePostRoom = "+ postRoomId+" order by a.type, a.oscePostRoom, a.timeStart";
    	TypedQuery<Assignment> q = em.createQuery(query, Assignment.class);
    	q.setParameter("osceType",type==null ? AssignmentTypes.STUDENT+","+AssignmentTypes.PATIENT+","+AssignmentTypes.EXAMINER : type);  */
    	
    	//q.setParameter("postRoomId", postRoomId<=0  ? "a.oscePostRoom": postRoomId);
    	//q.setParameter("courseId", courseId==0 || courseId < 0 ?"a.oscePostRoom.course" : courseId);
    	
    	CriteriaBuilder criteriaBuilder = entityManager().getCriteriaBuilder();
		CriteriaQuery<Assignment> criteriaQuery = criteriaBuilder.createQuery(Assignment.class);
		Root<Assignment> from = criteriaQuery.from(Assignment.class);
		CriteriaQuery<Assignment> select = criteriaQuery.select(from);
		
		select.orderBy(criteriaBuilder.asc(from.get("type")), criteriaBuilder.asc(from.get("oscePostRoom")), criteriaBuilder.asc(from.get("timeStart")));
		
		Predicate pre1 = criteriaBuilder.disjunction();
		pre1 = criteriaBuilder.equal(from.get("osceDay"), osceDayId);
		
		/*if (postRoomId > 0)
		{
			Predicate pre2 = criteriaBuilder.disjunction();
			pre2 = criteriaBuilder.equal(from.get("oscePostRoom"), postRoomId);
			pre1 = criteriaBuilder.and(pre1,pre2);
		}*/
		
		if (!type.equals(null) && type.size() > 0)
		{
			Log.info("Is Side When Type is > 0 ");
			Predicate pre3 = criteriaBuilder.disjunction();
			pre3 =from.get("type").in(type);
			pre1 = criteriaBuilder.and(pre1, pre3);
		}
		
		criteriaQuery.where(pre1);
    	
		TypedQuery<Assignment> typedQuery = entityManager().createQuery(select);
		Log.info("~~QUERY : " + typedQuery.unwrap(Query.class).getQueryString());	
		
    	//Log.info("Query is :" +query);
		List<Assignment> assignmentList = typedQuery.getResultList();
		Log.info("~~RESULT SIZE : " + assignmentList.size());
		
    	return assignmentList;
    }
    // Test Case 3
    public static Integer findTotalStudentsBasedOnOsce(Long osceId){
    	
    	Log.info("Inside findTotalStudentsBasedOnOsce() ");
    	EntityManager em = entityManager();
    	String query="select count(a) from Assignment as a,OsceDay as od where od.osce="+osceId+ " and a.osceDay=od.id and a.type = :type";
    	TypedQuery<Long> q = em.createQuery(query, Long.class);
    	q.setParameter("type", AssignmentTypes.STUDENT);
    	Log.info("Query is :" +query);
    	Integer result = q.getSingleResult() != null && q.getSingleResult() != 0 ? q.getSingleResult().intValue() : 0 ;
    	return result;
    }
    // Test Case 4
    public static List<Assignment> findAssignmentBasedOnOsceDay(Long osceDayId){
    	
    	Log.info("Inside findAssignmentBasedOnOsceDay() ");
    	EntityManager em = entityManager();
    	String query="select a from Assignment a where a.osceDay="+osceDayId + " order by a.type,a.oscePostRoom,a.timeStart";
    	TypedQuery<Assignment> q = em.createQuery(query, Assignment.class);
    	Log.info("Query is :" +query + "Result Size" + q.getResultList().size());
    	return q.getResultList();
    }
    // Test case 5 
    public static List<Course> findParcoursForOsce(Long osceId){
    	
    	Log.info("Inside findParcoursForOsce() With Osce IS "+ osceId);
    	EntityManager em = entityManager();
    	String query="select c from Course as c,OsceSequence as os, OsceDay as od where od.osce="+osceId + " and os.osceDay=od.id and c.osceSequence=os.id" ;
    	TypedQuery<Course> q = em.createQuery(query, Course.class);
    	Log.info("Query is :" +query);
    	return q.getResultList();
    }
    public static List<OscePostBlueprint>findOscePostBluePrintForOsceWithTypePreparation(Long osceId){

    	Log.info("Inside findOscePostBluePrintForOsceWithTypePreparation() With Osce IS "+ osceId);
    	EntityManager em = entityManager();
    	String query="select opb from OscePostBlueprint as opb where opb.osce="+osceId + " and opb.postType=3 order by opb.sequenceNumber";
    	TypedQuery<OscePostBlueprint> q = em.createQuery(query, OscePostBlueprint.class);
    	Log.info("Query is :" +query);
    	return q.getResultList();
    }
    
    public static List<Assignment> findAssignmentBasedOnGivenCourseAndPost(Long courseId,Long bluePrintId){
    	
    	Log.info("Inside findAssignmentBasedOnGivenCourseAndPost() With course Id IS "+ courseId + " and bluePrint Id Is :" + bluePrintId);
    	EntityManager em = entityManager();
    	String query="select a from Assignment as a where a.type=0 and a.oscePostRoom IN (select opr.id from OscePostRoom as opr where opr.course=" + courseId + 
    			" and opr.oscePost IN(select op.id from OscePost as op where op.oscePostBlueprint="+bluePrintId +")) order by a.sequenceNumber";
    	TypedQuery<Assignment> q = em.createQuery(query, Assignment.class);
    	Log.info("Query is :" +query);
    	return q.getResultList();
    }
   
    // Test Case 6
    public static List<OscePostBlueprint> findOscePostBluePrintForOsce(Long osceId){
    	
    	Log.info("Inside findOscePostBluePrintForOsce() With Osce IS "+ osceId);
    	EntityManager em = entityManager();
    	String query="select opb from OscePostBlueprint as opb where opb.osce="+osceId + " and opb.postType=2 order by opb.sequenceNumber";
    	TypedQuery<OscePostBlueprint> q = em.createQuery(query, OscePostBlueprint.class);
    	Log.info("Query is :" +query);
    	Log.info("Result is :" + q.getResultList().size());
    	return q.getResultList();
    }
    
    public static OscePostRoom findRoomForCourseAndBluePrint(Long courseId,Long blueprintId){
    	
    	Log.info("Inside findRoomForCourseAndBluePrint() With course Id IS "+ courseId + " and bluePrint Id Is " + blueprintId);
    	EntityManager em = entityManager();
    	String query="select opr from OscePostRoom as opr,OscePost as op where op.oscePostBlueprint="+blueprintId + " and opr.oscePost=op.id and opr.course="+courseId ;
    	TypedQuery<OscePostRoom> q = em.createQuery(query, OscePostRoom.class);
    	Log.info("Query is :" +query);
    	return q.getSingleResult();
    }
    
    public static List<Integer> findAllSequenceNumberForAssignment(Long osceDayId){
    	Log.info(" In side findAllSequenceNumberForAssignment() with OsceDay id" + osceDayId);	
		EntityManager em = entityManager();		
		String queryString = "select distinct sequenceNumber from Assignment where osceDay= "+osceDayId;
		Log.info("Query String: " + queryString);
		TypedQuery<Integer> q = em.createQuery(queryString,Integer.class);		
		List<Integer> result  = q.getResultList();        
		Log.info("findAllSequenceNumberForAssignment List Size is :"+result.size());
        return result;
    }
    
    // Test Case 7
    public static List<Assignment> findAssignmtForOsceDayAndSeq(Integer studentSeqNo,Long osceDayId){
    	
    	Log.info("Inside findAssignmentsByOsceDayAndSequenceNo() ");
    	EntityManager em = entityManager();
    	String query="select a from Assignment as a where a.osceDay="+osceDayId + " and a.type=0 and a.sequenceNumber="+studentSeqNo +" order by a.timeStart";
    	TypedQuery<Assignment> q = em.createQuery(query, Assignment.class);
    	Log.info("Query is :" +query);
    	return q.getResultList();
    }
    
    //Testing task }
    
 // Module10 Create plans
    //Find  Assignemtn by SP Id and Semester Id
    public static List<Assignment> findAssignmentsBySPIdandSemesterId(long spId,long semId,long pirId)
    {
		Log.info("Call findAssignmentBySPIdandSemesterId for SP id" + spId + "for Semester" +semId);	
		EntityManager em = entityManager();
		/*select * from assignment where patient_in_role in (
	    		select patient_in_role.id from patient_in_role where patient_in_semester in (select patient_in_semester.id from patient_in_semester,standardized_patient 
	    		where  patient_in_semester.standardized_patient=standardized_patient.id
	    		       and standardized_patient.id=19
	    		       and patient_in_semester.semester=1));*/
		String queryString = "select assi from Assignment assi where assi.patientInRole in (select pir.id from PatientInRole pir where pir.patientInSemester in " +
				"(select pis.id from PatientInSemester pis, StandardizedPatient sp where pis.standardizedPatient=sp.id and sp.id="+spId+" and pis.semester="+semId+")) and assi.patientInRole="+pirId;
		Log.info("Query String: " + queryString);
		TypedQuery<Assignment> q = em.createQuery(queryString,Assignment.class);		
		List<Assignment> result  = q.getResultList();        
		Log.info("EXECUTION IS SUCCESSFUL: RECORDS FOUND "+result.size());
        return result;    	    
    }
    
    public static List<Assignment> findAssignmentsByOsceDayAndPatientInRole(long osceDayId,long patientInrRoleId)
    {
		Log.info("Call findAssignmentsByOsceDayAndPatientInRole for OsceDay id" + osceDayId + "for Patient_In_Role" +patientInrRoleId);	
		EntityManager em = entityManager();
		/*select * from assignment where patient_in_role in (
	    		select patient_in_role.id from patient_in_role where patient_in_semester in (select patient_in_semester.id from patient_in_semester,standardized_patient 
	    		where  patient_in_semester.standardized_patient=standardized_patient.id
	    		       and standardized_patient.id=19
	    		       and patient_in_semester.semester=1));*/
		String queryString = "select assi from Assignment assi where assi.osceDay= "+osceDayId + "and patientInRole= " + patientInrRoleId;
		Log.info("Query String: " + queryString);
		TypedQuery<Assignment> q = em.createQuery(queryString,Assignment.class);		
		List<Assignment> result  = q.getResultList();        
		Log.info("EXECUTION IS SUCCESSFUL: RECORDS FOUND "+result.size());
        return result;    	    
    }
    
    public static List<Long> findDistinctOsceDayByStudentId(long osceId, long studId)
    {
    	Log.info("Call findDistinctOsceDayByStudentId for osce Id: "+osceId +"Student id: " + studId);	
		EntityManager em = entityManager();		
		String queryString = "select distinct osceDay.id from Assignment where osceDay in (select distinct od.id from OsceDay as od where od.osce="+osceId + ") and student="+studId+" order by osceDay";		
		Log.info("Query String: " + queryString);
		TypedQuery<Long> q = em.createQuery(queryString,Long.class);		
		List<Long> result  = q.getResultList();        
		Log.info("EXECUTION IS SUCCESSFUL: RECORDS FOUND "+result.size());
        return result;    	    
    }
    
    public static List<Assignment> findAssignmentsByOsceDayAndStudent(long osceDayId,long studentId)
    {
		Log.info("Call findAssignmentsByOsceDayAndStudent for OsceDay id" + osceDayId + "for Student" +studentId);	
		EntityManager em = entityManager();		
		//[SPEC String queryString = "select assi from Assignment assi where assi.osceDay= "+osceDayId + "and student= " + studentId;
		String queryString = "select assi from Assignment assi where assi.osceDay="+osceDayId +" and student= " + studentId + " and assi.type=0 order by assi.timeStart";
		
		Log.info("Query String: " + queryString);
		TypedQuery<Assignment> q = em.createQuery(queryString,Assignment.class);		
		List<Assignment> result  = q.getResultList();        
		Log.info("EXECUTION IS SUCCESSFUL: RECORDS FOUND "+result.size());
        return result;    	    
    }
    
    public static List<Long> findDistinctOsceDayByExaminerId(long examinerId,long osceId)
    {
    	Log.info("Call findDistinctOsceDayByExaminerId for Student id" + examinerId + " OsceId: " + osceId);	
		EntityManager em = entityManager();		
		//String queryString = "select distinct osceDay.id from Assignment where examiner="+examinerId;
		 
		String queryString = "select distinct osceDay.id from Assignment where osceDay in (select distinct od.id from OsceDay as od where od.osce="+osceId + ") and examiner="+examinerId+" order by osceDay";
		Log.info("Query String: " + queryString);
		TypedQuery<Long> q = em.createQuery(queryString,Long.class);		
		List<Long> result  = q.getResultList();        
		Log.info("EXECUTION IS SUCCESSFUL: RECORDS FOUND "+result.size());
        return result;    	    
    }
    /*
    public static List<Assignment> findAssignmentsByOsceDayAndExaminer(long osceDayId,long examinerId)
    {
		Log.info("Call findAssignmentsByOsceDayAndStudent for OsceDay id" + osceDayId + "for Student" +examinerId);	
		EntityManager em = entityManager();		
		String queryString = "select assi from Assignment assi where assi.osceDay= "+osceDayId + "and examiner= " + examinerId;
		Log.info("Query String: " + queryString);
		TypedQuery<Assignment> q = em.createQuery(queryString,Assignment.class);		
		List<Assignment> result  = q.getResultList();        
		Log.info("EXECUTION IS SUCCESSFUL: RECORDS FOUND "+result.size());
        return result;    	    
    }*/
    
    public static List<Long> findDistinctPIRByOsceDayAndExaminer(long osceDayId,long examinerId)
    {
		Log.info("Call findDistinctPIRByOsceDayAndExaminer for OsceDay id" + osceDayId + "for Student" +examinerId);	
		EntityManager em = entityManager();		
		//select distinct patient_in_role from assignment where osce_day=1 and examiner=5;
		String queryString = "select distinct patientInRole.id from Assignment where osceDay= "+osceDayId + "and examiner= " + examinerId;
		Log.info("Query String: " + queryString);
		TypedQuery<Long> q = em.createQuery(queryString,Long.class);		
		List<Long> result  = q.getResultList();        
		Log.info("EXECUTION IS SUCCESSFUL: RECORDS FOUND "+result.size());
        return result;    	    
    }
    
    public static List<Assignment> findAssignmentsByOsceDayExaminerAndPIR(long osceDayId,long examinerId,long pirId)
    {
		Log.info("Call findAssignmentsByOsceDayExaminerAndPIR for OsceDay id" + osceDayId + "for Examiner" +examinerId + "PIR Id " + pirId);	
		EntityManager em = entityManager();		
		String queryString = "select assi from Assignment assi where assi.osceDay= "+osceDayId + " and assi.examiner= " + examinerId +" and assi.patientInRole= "+ pirId;
		//String queryString = "select assi from Assignment assi where assi.osceDay= "+osceDayId + " and assi.examiner= " + examinerId +" and assi.oscePostRoom= "+ pirId;
		Log.info("Query String: " + queryString);
		TypedQuery<Assignment> q = em.createQuery(queryString,Assignment.class);		
		List<Assignment> result  = q.getResultList();        
		Log.info("EXECUTION IS SUCCESSFUL: RECORDS FOUND "+result.size());
        return result;    	    
    }
    
    public static List<Assignment> findAssignmentsByOsceDayExaminerAndOscePostRoomId(long osceDayId,long examinerId,long oscePostRoomId)
    {
		Log.info("Call findAssignmentsByOsceDayExaminerAndPIR for OsceDay id" + osceDayId + "for Examiner" +examinerId + "oscePostRoom Id " + oscePostRoomId);	
		EntityManager em = entityManager();		
		//String queryString = "select assi from Assignment assi where assi.osceDay= "+osceDayId + " and assi.examiner= " + examinerId +" and assi.patientInRole= "+ pirId;
		String queryString = "select assi from Assignment assi where assi.osceDay= "+osceDayId + " and assi.examiner= " + examinerId +" and assi.oscePostRoom= "+ oscePostRoomId;
		Log.info("Query String: " + queryString);
		TypedQuery<Assignment> q = em.createQuery(queryString,Assignment.class);		
		List<Assignment> result  = q.getResultList();        
		Log.info("EXECUTION IS SUCCESSFUL: RECORDS FOUND "+result.size());
        return result;    	    
    }
    
    public static List<Long> findDistinctoscePostRoomByOsceDayAndExaminer(long osceDayId,long examinerId)
    {
		Log.info("Call findDistinctoscePostRoomByOsceDayAndExaminer for OsceDay id" + osceDayId + "for Student" +examinerId);	
		EntityManager em = entityManager();		
		//select distinct patient_in_role from assignment where osce_day=1 and examiner=5;
		String queryString = "select distinct oscePostRoom.id from Assignment where osceDay= "+osceDayId + "and examiner= " + examinerId;
		Log.info("Query String: " + queryString);
		TypedQuery<Long> q = em.createQuery(queryString,Long.class);		
		List<Long> result  = q.getResultList();        
		Log.info("EXECUTION IS SUCCESSFUL: RECORDS FOUND "+result.size());
        return result;    	    
    }
    
    public static List<Long> findDistinctOsceDayIdByPatientInRoleId(long pirId)
    {
    	Log.info("Call findDistinctOsceDayByPatientInRoleIdId for Student id" + pirId);	
		EntityManager em = entityManager();		
		String queryString = "select distinct osceDay.id from Assignment where patientInRole="+pirId;
		Log.info("Query String: " + queryString);
		TypedQuery<Long> q = em.createQuery(queryString,Long.class);		
		List<Long> result  = q.getResultList();        
		Log.info("EXECUTION IS SUCCESSFUL: RECORDS FOUND "+result.size());
        return result;    	    
    }
    
    /*public static List<Assignment> findAssignmentsByOsceDayAndPIRId(long osceDayId,long pirId)
    {
		//Log.info("Call findAssignmentsByOsceDayAndPIRId for OsceDay id " + osceDayId +" PIR Id " + pirId);	
		EntityManager em = entityManager();		
		String queryString = "select assi from Assignment assi where assi.osceDay= "+osceDayId +" and assi.patientInRole= "+ pirId + "order by assi.timeStart";
		//Log.info("Query String: " + queryString);
		TypedQuery<Assignment> q = em.createQuery(queryString,Assignment.class);		
		List<Assignment> result  = q.getResultList();        
		//Log.info("EXECUTION IS SUCCESSFUL: RECORDS FOUND "+result.size());
        return result;    	    
    }*/
    
    public static List<Assignment> findAssignmentsByOsceDayAndPIRId(long osceDayId,List<PatientInRole> pirList)
    {
		//Log.info("Call findAssignmentsByOsceDayAndPIRId for OsceDay id " + osceDayId +" PIR Id " + pirId);	
		EntityManager em = entityManager();		
		String queryString = "select assi from Assignment assi where assi.osceDay= "+osceDayId +" and assi.patientInRole in (:patientInRoleList) order by assi.timeStart";
		Log.info("Query String: " + queryString);
		TypedQuery<Assignment> q = em.createQuery(queryString,Assignment.class);
		q.setParameter("patientInRoleList", pirList);
		List<Assignment> result  = q.getResultList();        
		//Log.info("EXECUTION IS SUCCESSFUL: RECORDS FOUND "+result.size());
        return result;    	    
    }
        
    // E Module10 Create plans    
    
    //Module 9
    
    public static List<StandardizedPatient> findAssignedSP(Long semesterId)  
   	{
   		EntityManager em = entityManager();
   		
   		String queryString="select distinct sp ";
   		queryString += "from Assignment a, StandardizedPatient sp, PatientInSemester pis, PatientInRole pir ";
   		queryString += "where pis.semester = "+semesterId+" ";
   		queryString += "and pis.id = pir.patientInSemester ";
   		queryString += "and pir.id = a.patientInRole ";
   		queryString += "and sp.id=pis.standardizedPatient ";
   		
   		TypedQuery<StandardizedPatient> q = em.createQuery(queryString, StandardizedPatient.class);
   		List<StandardizedPatient> result = q.getResultList();
   		return result;
   	}
    
    public static List<Doctor> findAssignedExaminer(Long semesterId)  
   	{
   		EntityManager em = entityManager();
   		
   		String queryString="select distinct d ";
   		queryString += "from Assignment a, Osce o, OsceDay od, Doctor d ";
   		queryString += "where o.semester = "+semesterId+" ";
   		queryString += "and o.id = od.osce ";
   		queryString += "and od.id = a.osceDay ";
   		queryString += "and d.id = a.examiner ";
   		
   		TypedQuery<Doctor> q = em.createQuery(queryString, Doctor.class);
   		List<Doctor> result = q.getResultList();
   		return result;
   	}
    
    // Module 9
    
	// Module : 15

    public static List<Assignment> getAssignmentsBySemester(Long semesterId){
    	EntityManager em = entityManager();
    	String sql = "SELECT a FROM Assignment a WHERE a.type = 0 AND a.osceDay.osce.semester.id = "+ semesterId +" GROUP BY a.timeStart ORDER BY a.osceDay, a.timeStart";
    	TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
    	return query.getResultList();
    }
    
	/*public static List<Assignment> getAssignmentsBySemester(Long semesterId) {
		Log.info("retrieveAssignmenstOfTypeExaminer :");
		EntityManager em = entityManager();

		List<Assignment> assignmentList = new ArrayList<Assignment>();

		List<OsceDay> osceDays = OsceDay.findAllOsceDaysOrderByDate();

		OsceDay osceDay = null;
		OsceSequence osceSequence = null;
		OscePost oscePost = null;
		Course course = null;

		for (Iterator<OsceDay> iterator = osceDays.iterator(); iterator
				.hasNext();) {

			osceDay = (OsceDay) iterator.next();

			List<OsceSequence> osceSequences = osceDay.getOsceSequences();

			if (osceSequences != null && osceSequences.size() > 0) {
				osceSequence = osceSequences.get(0);

				List<OscePost> oscePosts = osceSequence.getOscePosts();

				if (oscePosts != null && oscePosts.size() > 0) {
					oscePost = oscePosts.get(0);
				}

				List<Course> courses = osceSequence.getCourses();

				if (courses != null && courses.size() > 0) {
					course = courses.get(0);
				}

			}

			// SELECT o.osce_date,a.* FROM osce.assignment a ,osce.osce_day o
			// where
			// osce_day=1 and a.type=0 and a.osce_day = o.id and
			// a.osce_post_room
			// in(select opr.id from osce_post_room opr where osce_post=1 and
			// course=1 ) order by a.osce_day asc,o.osce_date asc,a.time_start
			// asc ;
			// and op.osce_sequence=1;

			String queryString = "SELECT distinct a FROM Assignment as a where a.type=0 and a.osceDay.osce.semester.id="
					+ semesterId
					+ " and a.osceDay.id="
					+ osceDay.getId()
//					+ "  and a.oscePostRoom.id in(select opr.id from OscePostRoom opr where opr.oscePost.id="
//					+ oscePost.getId()
//					+ " and opr.course.id="
//					+ course.getId()
//					+ " ) " 
					+"order by a.osceDay asc,a.osceDay.osceDate asc,a.timeStart asc ";
			String queryString = "SELECT distinct a FROM Assignment a WHERE a.type = 0 AND a.osceDay.osce.semester.id = " + semesterId + " AND a.osceDay.id = " + osceDay.getId() + " GROUP BY a.timeStart ORDER BY a.osceDay, a.timeStart ASC";
			
			TypedQuery<Assignment> query = em.createQuery(queryString,
					Assignment.class);

			assignmentList.addAll(query.getResultList());

			Log.info("retrieveAssignmenstOfTypeExaminer query String :"
					+ queryString);
			Log.info("Assignment List Size :" + assignmentList.size());
		}
		// return bellAssignmentTypes;
		return assignmentList;
	}*/

	public static Integer getCountAssignmentsBySemester(Long semesterId) {
		return new Integer(getAssignmentsBySemester(semesterId).size());
	}

	/*public static String getQwtBellSchedule(// List<Assignment> assignments,
			Long semesterId, Integer time, TimeBell isPlusTime) {
		try {
			List<Assignment> assignments = getAssignmentsBySemester(semesterId);
			Semester semester = Semester.findSemester(semesterId);

			QwtUtil qwtUtil = new QwtUtil();

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();

			String fileName = new String(dateFormat.format(date) + ".qwt");
			//Feature : 154
			qwtUtil.open(StandardizedPatient.fetchRealPath() + fileName, false);

			List<BellAssignmentType> bellAssignmentTypes = QwtUtil
					.getBellAssignmentType(assignments, time, isPlusTime,
							semester);
			qwtUtil.writeQwt(bellAssignmentTypes);

			// qwtUtil.writeQwt(assignments);

			qwtUtil.close();
			//Feature : 154
			return StandardizedPatient.fetchContextPath() + fileName;

		} catch (Exception e) {
			e.printStackTrace();
		}
		// return OsMaConstant.FILENAME;
		return "";
	}*/

	public static String getQwtBellSchedule(// List<Assignment> assignments,
			Long semesterId, Integer time, TimeBell isPlusTime) {
		try {
			QwtUtil qwtUtil = new QwtUtil();

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();

			String fileName = new String(dateFormat.format(date) + ".qwt");
			//Feature : 154
			qwtUtil.open(StandardizedPatient.fetchRealPath() + fileName, false);			
			
			
			List<Assignment> assignments = getAssignmentsBySemester(semesterId);
			Semester semester = Semester.findSemester(semesterId);			

			List<BellAssignmentType> bellAssignmentTypes = QwtUtil
					.getBellAssignmentType(assignments, time, isPlusTime,
							semester);
			qwtUtil.writeQwt(bellAssignmentTypes);

			// qwtUtil.writeQwt(assignments);

			qwtUtil.close();
			//Feature : 154
			return StandardizedPatient.fetchContextPath() + fileName;

		} catch (Exception e) {
			e.printStackTrace();
		}
		// return OsMaConstant.FILENAME;
		return "";
	}

	
	// Module : 15
	
		public static List<Assignment> findAssignmentByOscePostRoom(Long id, Long osceId, int rotationNumber)
    {
    	EntityManager em = entityManager();
    	String query = "SELECT a FROM Assignment a WHERE a.oscePostRoom.id = " + id + " AND a.type = 0 AND a.osceDay.osce.id = " + osceId + " and a.rotationNumber = " + rotationNumber + " ORDER BY a.timeStart";
    	TypedQuery<Assignment> q = em.createQuery(query, Assignment.class);
    	return q.getResultList();
    }
    
		public static List<Assignment> findAssignmentExamnierByOscePostRoom(Long id, Long osceId, Date startTime, Date endTime)
	    {
	    	EntityManager em = entityManager();
	    	//String query = "SELECT a FROM Assignment a WHERE a.oscePostRoom.id = " + id + " AND a.type = 2 AND a.timeStart > '" + time_start + "' AND timeStart < '" + time_end +"' ORDER BY timeStart";
	    	String query = "SELECT a FROM Assignment a WHERE a.oscePostRoom.id = " + id + " AND a.type = 2 AND a.osceDay.osce.id = " + osceId + " AND a.timeStart <= '" + startTime + "' AND a.timeEnd >= '" + endTime + "' ORDER BY a.timeStart";
	    	//System.out.println("EXAMINER QUERY : " + query);
	    	TypedQuery<Assignment> q = em.createQuery(query, Assignment.class);
	    	return q.getResultList();
	    }
    
    public static List<Assignment> findAssignedDoctorBySpecialisation(Long specialisationId, Long clinicId)
    {
    	EntityManager em = entityManager();
    	String sql = "SELECT a FROM Assignment AS a WHERE a.examiner.specialisation = " + specialisationId + " AND a.examiner.clinic = " + clinicId + " GROUP BY a.examiner";
    	TypedQuery<Assignment> q = em.createQuery(sql, Assignment.class);
    	return q.getResultList();
    }
    
    public static List<Assignment> findAssignmentsByOsceDayExaminer(long osceDayId,long examinerId)
    {
		Log.info("Call findAssignmentsByOsceDayExaminerAndPIR for OsceDay id" + osceDayId + "for Examiner" +examinerId);	
		EntityManager em = entityManager();		
		//String queryString = "select assi from Assignment assi where assi.osceDay in (select distinct od.id from OsceDay as od where od.osce="+osceId + ") and student= " + studentId + " and assi.type=0 order by assi.timeStart";
		String queryString = "select assi from Assignment assi where assi.osceDay= "+osceDayId + " and assi.examiner= " + examinerId +" and assi.type=2 order by assi.timeStart";
		//String queryString = "select assi from Assignment assi where assi.osceDay= "+osceDayId + " and assi.examiner= " + examinerId +" and assi.oscePostRoom= "+ pirId;
		Log.info("Query String: " + queryString);
		TypedQuery<Assignment> q = em.createQuery(queryString,Assignment.class);		
		List<Assignment> result  = q.getResultList();        
		Log.info("EXECUTION IS SUCCESSFUL: RECORDS FOUND "+result.size());
        return result;    	    
    }
    
    
    public static List<Assignment> findAssignmentBasedOnGivenOsceDayExaminerAndOscePostRoom(long osceDayId,long examinerId,long oscePostRoomId)
    {
		Log.info("Call findAssignmentBasedOnGivenOsceDayExaminerAndOscePostRoom for OsceDay id" + osceDayId + "for Examiner" +examinerId +"and OscePostRoom Id: " + oscePostRoomId);	
		EntityManager em = entityManager();		 
		//String queryString = "select assi from Assignment assi where assi.osceDay in (select distinct od.id from OsceDay as od where od.osce="+osceId + ") and student= " + studentId + " and assi.type=0 order by assi.timeStart";
		//String queryString = "select assi from Assignment assi where assi.osceDay= "+osceDayId + " and assi.examiner= " + examinerId +" and assi.type=2 order by assi.timeStart";
		//String queryString = "select assi from Assignment assi where assi.osceDay= "+osceDayId + " and assi.examiner= " + examinerId +" and assi.oscePostRoom= "+ pirId;
		String queryString = "select assi from Assignment assi where assi.osceDay= "+osceDayId + " and assi.examiner= " + examinerId +" and assi.oscePostRoom="+oscePostRoomId+" and assi.type=2 order by assi.timeStart";
		//select * from assignment where osce_day=97 and examiner=61 and osce_post_room=3121 and type=2;
		Log.info("Query String: " + queryString);
		TypedQuery<Assignment> q = em.createQuery(queryString,Assignment.class);		
		List<Assignment> result  = q.getResultList();        
		Log.info("EXECUTION IS SUCCESSFUL: RECORDS FOUND "+result.size());
        return result;    	    
    }
    
    //by spec[
    public static void updateAssignmentByDiff(Long osceDayId, int diff, Date endTimeSlot, boolean isChangeStartTime)
    {
    	EntityManager em = entityManager();
    	String sql = "";
    	TypedQuery<Assignment> q;
    	if (isChangeStartTime)
    	{
    		sql = "SELECT a FROM Assignment AS a WHERE a.osceDay = " + osceDayId +" ORDER BY a.timeStart";
    		q = em.createQuery(sql, Assignment.class);
    	}
    	else
    	{
    		sql = "SELECT a FROM Assignment AS a WHERE a.osceDay = " + osceDayId +" AND a.timeEnd > :endTimeSlot";
    		q = em.createQuery(sql, Assignment.class);
        	q.setParameter("endTimeSlot", endTimeSlot);
    	}    	
    	
    	Iterator<Assignment> assList = q.getResultList().iterator();
    	
    	while (assList.hasNext())
    	{
    		Assignment ass = assList.next();
    		
    		Date timeStartDt = dateAddMin(ass.getTimeStart(), diff);
    		Date timeEndDt = dateAddMin(ass.getTimeEnd(), diff);
    		if(ass.getTimeStart().before(endTimeSlot) && ass.getType().equals(AssignmentTypes.EXAMINER))
    		{
    		
    		}
    		else
    	 	    ass.setTimeStart(timeStartDt);
    		ass.setTimeEnd(timeEndDt);
    		
    		ass.persist();    		
    	}
    	
    	OsceDay osceDay=OsceDay.findOsceDay(osceDayId);
    	osceDay.setIsTimeSlotShifted(true);
    	osceDay.persist();
    	
    }
    
    public static Date dateAddMin(Date date, long minToAdd) {
		return new Date((long) (date.getTime() + minToAdd * 60 * 1000));
	}
    
    public static boolean isSPinOsceDay(PatientInSemester ps, Assignment assignment)
 	{
     	Log.info("Call isSPinOsceDay for Patient in Semester id" + ps.getId() + " of assignment id " + assignment.getId());	
     	boolean flag = false;
     	EntityManager em = entityManager();		 
 		String queryString = "select assi from Assignment assi where assi.type=1 and assi.osceDay= " + assignment.getOsceDay().getId() + " and assi.patientInRole in ( select id from PatientInRole pr where pr.patientInSemester = " + ps.getId() +")";
 		//select * from assignment where osce_day=97 and examiner=61 and osce_post_room=3121 and type=2;
 		Log.info("Query String: " + queryString);
 		TypedQuery<Assignment> q = em.createQuery(queryString,Assignment.class);		
 		List<Assignment> result  = q.getResultList();		
 		if(result!=null && result.size()>0)
 			flag=true;
 		Log.info("EXECUTION IS SUCCESSFUL: RECORDS FOUND "+result.size());
         return flag; 
 	}
    
    public static List<Assignment> retrieveAssignmentsOfTypeSPByOsceDay(OsceDay osceDay) {
        Log.info("retrieveAssignmenstOfTypeSP :");
        EntityManager em = entityManager();
        String queryString = "SELECT o FROM Assignment AS o WHERE o.osceDay = :osceDay AND o.type = :type AND o.oscePostRoom IS NOT NULL";
        TypedQuery<Assignment> q = em.createQuery(queryString, Assignment.class);
        q.setParameter("osceDay", osceDay);
        q.setParameter("type", AssignmentTypes.PATIENT);
        List<Assignment> assignmentList = q.getResultList();
        Log.info("retrieveAssignmenstOfTypeSP query String :" + queryString);
        Log.info("Assignment List Size :" + assignmentList.size());
        return assignmentList;
    }
    
    
     public static List<Date> minmumStartTime(Long osceDayId, Long osceSequenceId, Long courseId) {
    	
    	
    	
        Log.info("retrieveAssignmenstOfTypeStudent :");
        EntityManager em = entityManager();
        //String queryString = "SELECT  a FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=0 and a.oscePostRoom in(select opr.id from OscePostRoom as opr where opr.oscePost=" + oscePostId + " and opr.course=" + courseId + " ) order by a.timeStart asc";
        String queryString = "SELECT  min(a.timeStart), max(a.timeEnd) FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=0 and a.oscePostRoom in(select opr.id from OscePostRoom as opr where opr.room in (select rm.room from OscePostRoom as rm where  rm.course= " + courseId + " and rm.version<999) and opr.course=" + courseId + " ) order by a.timeStart asc";
        
        List<Date> startEndTimeList = new ArrayList<Date>();
        List<Object[]> resultList = em.createQuery(queryString).getResultList();
		 
		 if (resultList.size() > 0)
		 {
			 Object[] result = resultList.get(0);
			 startEndTimeList.add(((Date)result[0]));
			 startEndTimeList.add(((Date)result[1]));
		 }
        
        return startEndTimeList;
    }
     
     
    //by spec]
    
     //payment module
     public static String findAssignmentByPatinetInRole(Long semesterId) 
     {
     	String fileName = "";
     	try
     	{
     		ExcelUtil excelUtil = new ExcelUtil();
         	
         	List<Osce> osceList = Osce.findAllOsceBySemster(semesterId);
         	
         	for (Osce osce : osceList)
         	{
         		Map<Long, List<Long>> mainMap = new HashMap<Long, List<Long>>();
         		
         		EntityManager em = entityManager();
             	
             	String sql = "SELECT ps.standardizedPatient.id AS stdpat, a.osceDay AS assOsDay, pr.oscePost AS prOsPt, sr.roleType AS srRoTy, MIN(a.timeStart) AS minTimeSt, MAX(a.timeEnd) AS maxTimeEnd" +
         				" FROM Assignment AS a, PatientInRole AS pr, PatientInSemester AS ps, OscePost AS op, StandardizedRole AS sr " +
         				" WHERE a.type = 1 AND a.osceDay IN (SELECT od FROM OsceDay od WHERE od.osce = " + osce.getId() + ")" +
         				" AND a.patientInRole = pr.id" +
         				" AND pr.patientInSemester = ps.id" +
         				" AND pr.oscePost = op.id " +
         				" AND op.standardizedRole = sr.id" +
         				" GROUP BY ps.standardizedPatient, a.osceDay, pr.oscePost, sr.roleType";
             	
             	javax.persistence.Query query = em.createQuery(sql);
             	List list = query.getResultList();    	
             	
             	Long spHrs = 0l;
         		Long statistHrs = 0l;
             	
         		for (int i=0; i<list.size(); i++)
             	{
             		Object[] custom = (Object[]) list.get(i);
             	
             		Long newSp = (Long)custom[0];
             	            		
             		Date startDate = (Date)custom[4];
             		Date endDate = (Date)custom[5];
             		
             		Long min = (endDate.getTime() - startDate.getTime()) / (60 * 1000);
             		
             		if (checkLunchBreak(startDate, endDate,((OsceDay)custom[1]).getLunchBreakStart()))
             		{
             			min = min - osce.getLunchBreak();
             		}
             		
             		//System.out.println("SP : " + oldSp + "  ~~MIN : " + min);
             		
             		if (((RoleTypes)custom[3]) == RoleTypes.Simpat)
             			spHrs = spHrs + min;
             		else if (((RoleTypes)custom[3]) == RoleTypes.Statist)
             			statistHrs = statistHrs + min;
             		
             		//System.out.println("SPHRS : " + spHrs);
             		if (i == 0)
             		{
             			List<Long> hrsList = new ArrayList<Long>();
             			hrsList.add(spHrs);
             			hrsList.add(statistHrs);
             			
             			mainMap.put(newSp, hrsList);
             			
             			spHrs = 0l;
             			statistHrs = 0l;
             		}
             		
             		if (i > 0)
             		{
             			Object[] oldObject = (Object[]) list.get(i-1);
             			Long id = (Long) oldObject[0];
             			if (!newSp.equals(id))
             			{
             				//System.out.println("SP : " + newSp + "  ~~SP : " + spHrs + " ~~STATIST : " + statistHrs);
             				
             				List<Long> hrsList = new ArrayList<Long>();
                 			hrsList.add(spHrs);
                 			hrsList.add(statistHrs);
                 			
                 			mainMap.put(newSp, hrsList);
                 			
                 			spHrs = 0l;
                 			statistHrs = 0l;
             			}
             		}
             	}
         		
             	excelUtil.writeSheet(mainMap, (osce.getName() == null || osce.getName().isEmpty()) ? osce.getStudyYear().toString() : osce.getName(), semesterId);
         	}
         	
         	Semester semester = Semester.findSemester(semesterId);
         	fileName = semester.getCalYear().toString() + ".xls";
         	excelUtil.writeExcel(fileName);
         	System.out.println("File Name : " + fileName);
     	}
     	catch(Exception e)
     	{
     		e.printStackTrace();
     		Log.info("ERROR : " + e.getMessage());
     	}
     	
     	//return StandardizedPatient.fetchContextPath() + fileName;
     	return OsMaFilePathConstant.DOWNLOAD_DIR_PATH + "/" + fileName;
     }
     
     public static boolean checkLunchBreak(Date timeStart, Date timeEnd, Date startLunchBreak)
     {
     	Boolean test = (startLunchBreak.after(timeStart) && startLunchBreak.before(timeEnd));    	
     	return test;	
     }
     //payment module
     
     public static Assignment findExaminersRoationAndCourseWise(Long osceDayId,int rotation,Long courseId,Long postId)
     {
    	 Log.info("findExaminersRoationAndCourseWise :");
         EntityManager em = entityManager();
         //String queryString = "SELECT  a FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=0 and a.oscePostRoom in(select opr.id from OscePostRoom as opr where opr.oscePost=" + oscePostId + " and opr.course=" + courseId + " ) order by a.timeStart asc";
         String queryString = "SELECT  a FROM Assignment as a where a.osceDay="+osceDayId+"  and type=2 and " +
         		"a.oscePostRoom in(select opr.id from OscePostRoom as opr where opr.oscePost="+postId+" and opr.course="+courseId+" )" +
         		"and timeStart <=(select min(timeStart) from Assignment as a where type=0  and osceDay="+osceDayId+" and rotationNumber="+rotation+" and " +
         		"a.oscePostRoom in(select opr.id from OscePostRoom as opr where opr.room in (select rm.room from OscePostRoom as rm where rm.oscePost ="+postId+" and "+
         		"rm.course= "+courseId+" and rm.version<999) and opr.course="+courseId+" )) and timeEnd >= (select max(timeEnd) from Assignment as a where type=0  and osceDay="+osceDayId+" and rotationNumber="+rotation+" and "+
         		"a.oscePostRoom in(select opr.id from OscePostRoom as opr where opr.room in (select rm.room from OscePostRoom "+
         		"as rm where rm.oscePost = "+postId+" and rm.course="+courseId+"  and rm.version<999) and opr.course="+courseId+" ))";
         
         TypedQuery<Assignment> query = em.createQuery(queryString, Assignment.class);
         Assignment assignmentList=null;
         if(query.getResultList().size() > 0)
          assignmentList = query.getResultList().get(0);
         Log.info("retrieveAssignmenstOfTypeStudent query String :" + queryString);
         Log.info("Assignment List Size :" + assignmentList);
         return assignmentList;
     }
     
     public static List<Assignment> findAssignmentRotationAndCourseWise(Long osceDayId,int rotation,Long courseId,int type)
     {
    	 Log.info("findAssignmentRotationAndCourseWise");
    	 /*String queryString="select a from Assignment as a where type="+type+" and osceDay="+osceDayId+" and rotationNumber = "+rotation+" and " +
    	 		"a.oscePostRoom in(select opr.id from OscePostRoom as opr where (opr.room in (select rm.room from OscePostRoom as rm where  " +
    	 		"rm.course="+courseId+" and rm.version<999)  or opr.room is null) and opr.course="+courseId+" ) or (oscePostRoom is null   and rotationNumber = "+rotation+" and sequenceNumber in (select distinct (sequenceNumber) from Assignment where type=0 and osceDay="+osceDayId+" and oscePostRoom in (select id from OscePostRoom where course="+courseId+")) )  order by timeStart";*/
    	 
    	 /*String queryString="select a from Assignment as a where type="+type+" and osceDay="+osceDayId+" and rotationNumber = "+rotation+" and " +
     	 		"a.oscePostRoom in(select opr.id from OscePostRoom as opr where opr.room in (select rm.room from OscePostRoom as rm where  " +
     	 		"rm.course="+courseId+" and rm.version<999) and opr.course="+courseId+" ) or (oscePostRoom is null   and rotationNumber = "+rotation+" and sequenceNumber in (select distinct (sequenceNumber) from Assignment where type=0 and osceDay="+osceDayId+" and oscePostRoom in (select id from OscePostRoom where course="+courseId+")) )  order by timeStart";*/
    	 
    	 String queryString = "select a from Assignment as a where a.type=" + type + " and a.osceDay.id=" + osceDayId + " and a.rotationNumber = " + rotation + " and (oscePostRoom is null or oscePostRoom in (select opr.id from OscePostRoom opr where opr.course = " + courseId + "))"
    			 			  + " and a.sequenceNumber in (select distinct (sequenceNumber) from Assignment where type=" + type + " and osceDay.id=" + osceDayId + " and oscePostRoom in (select id from OscePostRoom where course=" + courseId + "))"
    	 					  + " order by timeStart";
    	 
    	 if(type ==1)
    	 {
    		 queryString="select a from Assignment as a where type="+type+" and osceDay="+osceDayId+" and  " +
    	    	 		"a.oscePostRoom in(select opr.id from OscePostRoom as opr where  opr.course="+courseId+" ) order by timeStart, a.oscePostRoom.id";
    	 }
    	 EntityManager em = entityManager();
    	 TypedQuery<Assignment> query = em.createQuery(queryString, Assignment.class);
         List<Assignment> assignmentList = query.getResultList();
         Log.info("findAssignmentRotationAndCourseWise query String :" + queryString);
         Log.info("Assignment List Size :" + assignmentList);
         return assignmentList;

    	
     }
     
     public static List<Date> findDistinctTimeStartRotationWise(Long osceDayId,int rotation,int type)
     {
    	 Log.info("findDistinctTimeStartRotationWise");
    	 
    	 String queryString="";
    	 if(type==0)
    	  queryString="select distinct (timeStart) from Assignment a where type=0 and osceDay="+osceDayId+" and rotationNumber = "+rotation+" order by timeStart";
    	 
    	 if(type==1)
    	 {
    		queryString="select distinct (timeEnd) from Assignment a where type=1 and osceDay="+osceDayId+" and timeStart >= (select min(timeStart) from Assignment where type=0 and osceDay = " + osceDayId + " and rotationNumber = " + rotation + ")" 
    				 + " and timeEnd <= (select max(timeEnd) from Assignment where type=0 and osceDay = " + osceDayId + " and rotationNumber = " + rotation + ") order by timeStart";
    		 //queryString="select distinct (timeEnd) from Assignment a where type=0 and osceDay="+osceDayId+" and rotationNumber = "+rotation+" order by timeStart";
    	 }
    	 
    	 EntityManager em = entityManager();
    	 TypedQuery<Date> query = em.createQuery(queryString, Date.class);
         List<Date> assignmentList = query.getResultList();
         Log.info("findDistinctTimeStartRotationWise query String :" + queryString);
         Log.info("Assignment List Size :" + assignmentList);
         return assignmentList;
    	
     }
   
     public static List<Date> findDistinctTimeStartForSP(Long osceDayId,int rotation,int type)
     {
    	 Log.info("findDistinctTimeStartRotationWise");
    	 
    	 String queryString="";
    	 if(type==0)
    	  queryString="select distinct (timeStart) from Assignment a where type=1 and osceDay="+osceDayId+" and rotationNumber = "+rotation+" order by timeStart";
    	 
    	 if(type==1)
    		 queryString="select distinct (timeEnd) from Assignment a where type=1 and osceDay="+osceDayId+" and rotationNumber = "+rotation+" order by timeStart";
    	 
    	 EntityManager em = entityManager();
    	 TypedQuery<Date> query = em.createQuery(queryString, Date.class);
         List<Date> assignmentList = query.getResultList();
         Log.info("findDistinctTimeStartRotationWise query String :" + queryString);
         Log.info("Assignment List Size :" + assignmentList);
         return assignmentList;
    	
     }
     
     //by spec change[
     public static Boolean exchangeStudent(Assignment ass, Long studentId)
     {
    	 EntityManager em = entityManager();
    	 
    	 Student exchangeStudent = Student.findStudent(studentId);
    	 String sql = "";
    	 
    	 Student oldStudent = null;
    	 if (ass.getStudent() == null)
    	 {
    		 int seqNo = ass.getSequenceNumber();
    		 sql = "SELECT a FROM Assignment a WHERE a.sequenceNumber = " + seqNo + " AND a.osceDay.osce = " + ass.getOsceDay().getOsce().getId();
    	 }
    	 else
    	 {
    		 oldStudent = ass.getStudent();
    		 sql = "SELECT a FROM Assignment a WHERE a.student = " + oldStudent.getId() + " AND a.osceDay.osce = " + ass.getOsceDay().getOsce().getId();
    	 }
    	 
    	 
    	 TypedQuery<Assignment> oldStudQuery = em.createQuery(sql, Assignment.class);
    	 Iterator<Assignment> oldStudItr = oldStudQuery.getResultList().iterator();
    	 
    	 sql = "SELECT a FROM Assignment a WHERE a.student = " + studentId + " AND a.osceDay.osce = " + ass.getOsceDay().getOsce().getId();
    	 TypedQuery<Assignment> exchangeStudQuery = em.createQuery(sql, Assignment.class);
    	 Iterator<Assignment> exchangeStudItr = exchangeStudQuery.getResultList().iterator();
    	 
    	 while (oldStudItr.hasNext())
    	 {
    		 Assignment assignment = oldStudItr.next();
    		 assignment.setStudent(exchangeStudent);
    		 assignment.persist();
    	 }   	 
    	 
    	 while (exchangeStudItr.hasNext())
    	 {
    		 Assignment assignment = exchangeStudItr.next();
    		 assignment.setStudent(oldStudent);
    		 assignment.persist();
    	 }
    	 
    	 return true;
    	 
     }
     
     /*public static Boolean exchangeStandardizedPatient(Assignment ass, PatientInRole exchangePir)
     {
    	 PatientInRole oldSp = ass.getPatientInRole();
    	 EntityManager em = entityManager();
    		 
    	 if (PostType.DUALSP.equals(ass.getOscePostRoom().getOscePost().getOscePostBlueprint().getPostType()))
    	 {
    		 List<PatientInRole> breakSPList = PatientInRole.findPatientInRoleByRotation(ass);
    		 
    		 String sql = "SELECT a FROM Assignment a WHERE a.patientInRole = " + ass.getPatientInRole().getId() + " AND a.osceDay.osce = " + ass.getOsceDay().getOsce().getId() + " AND a.sequenceNumber = " + ass.getSequenceNumber();
        	 TypedQuery<Assignment> oldSpQuery = em.createQuery(sql, Assignment.class);
        	 Iterator<Assignment> oldSpItr = oldSpQuery.getResultList().iterator();
        	 
        	 sql = "SELECT a FROM Assignment a WHERE a.patientInRole = " + exchangePir.getId() + " AND a.osceDay.osce = " + ass.getOsceDay().getOsce().getId() + " AND a.sequenceNumber = " + ass.getSequenceNumber();
        	 TypedQuery<Assignment> exchangeSpQuery = em.createQuery(sql, Assignment.class);
        	 Iterator<Assignment> exchangeSpItr = exchangeSpQuery.getResultList().iterator();
        	 
    		 while (oldSpItr.hasNext())
        	 {
        		 if (ass.getPatientInRole().getOscePost() != null && ass.getPatientInRole().getOscePost().getStandardizedRole() != null)
        		 {
        			 String pirSql = "SELECT pir FROM PatientInRole pir WHERE pir.patientInSemester = " + exchangePir.getPatientInSemester().getId() + " AND pir.oscePost IS NOT NULL AND pir.oscePost.standardizedRole.id = " + ass.getPatientInRole().getOscePost().getStandardizedRole().getId();
        			 TypedQuery<PatientInRole> pirQuery = em.createQuery(pirSql, PatientInRole.class);
        			 
        			 if (pirQuery.getResultList().size() > 0)
            		 {
        				 PatientInRole exchangePatientInRole = pirQuery.getResultList().get(0);
        				 Assignment assignment = oldSpItr.next();
                		 assignment.setPatientInRole(exchangePatientInRole);
                		 assignment.persist();
            		 }
        		 }
        	 }
        	 
        	 while (exchangeSpItr.hasNext())
        	 {
        		 String pirSql = "SELECT pir FROM PatientInRole pir WHERE pir.patientInSemester = " + oldSp.getPatientInSemester().getId() + " AND pir.oscePost IS NULL";
        		 TypedQuery<PatientInRole> pirQuery = em.createQuery(pirSql, PatientInRole.class);
        		 PatientInRole oldPatientInRole = pirQuery.getSingleResult();
        		 
        		 Assignment assignment = exchangeSpItr.next();
        		 assignment.setPatientInRole(oldPatientInRole);
        		 assignment.persist();
        	 }
        	 
        	 String dualSPsql = "SELECT a FROM Assignment a WHERE a.type = 1 AND a.oscePostRoom.id = " + ass.getOscePostRoom().getId() + " AND a.timeStart = '" + ass.getTimeStart() + "' AND a.timeEnd = '" + ass.getTimeEnd() + "' AND a.osceDay.osce = " + ass.getOsceDay().getOsce().getId() + " AND a.sequenceNumber = " + ass.getSequenceNumber();
        	 TypedQuery<Assignment> dualOldSpQuery = em.createQuery(dualSPsql, Assignment.class);
        	 List<Assignment> dualOldSpAssList = dualOldSpQuery.getResultList();
        	 Assignment dualSpOldAss = null;
        	 for (Assignment tempAss : dualOldSpAssList)
        	 {
        		if (tempAss.getId().equals(ass.getId()) == false)
        		{
        			dualSpOldAss = tempAss;
        			break;
        		}
        	 }
        	 
        	 if (dualSpOldAss != null)
        	 {
        		 PatientInRole dualExchangeSp = null;
        		 for (PatientInRole pir : breakSPList)
        		 {
            		 if (pir.getId().equals(exchangePir.getId()) == false)
        			 {
        				 dualExchangeSp = pir;
        				 break;
        			 }
        		 }
        		 
        		 if (dualSpOldAss != null && dualExchangeSp != null)
        		 {
        			 String dualSpExchangeSql = "SELECT a FROM Assignment a WHERE a.patientInRole = " + dualExchangeSp.getId() + " AND a.osceDay.osce = " + ass.getOsceDay().getOsce().getId() + " AND a.sequenceNumber = " + ass.getSequenceNumber();
                	 TypedQuery<Assignment> dualExchangeSpQuery = em.createQuery(dualSpExchangeSql, Assignment.class);
                	 List<Assignment> dualExchangeSpList = dualExchangeSpQuery.getResultList();
                	 if (dualExchangeSpList.size() == 1 && dualSpOldAss.getPatientInRole() != null)
                	 {
                		 Assignment dualExchageSPAss = dualExchangeSpList.get(0);
                	
                		 String pirSql = "SELECT pir FROM PatientInRole pir WHERE pir.patientInSemester = " + dualSpOldAss.getPatientInRole().getPatientInSemester().getId() + " AND pir.oscePost IS NULL";
                		 TypedQuery<PatientInRole> pirQuery = em.createQuery(pirSql, PatientInRole.class);
                		 PatientInRole oldPatientInRole = pirQuery.getSingleResult();
                		 
                		 Assignment ass1 = dualExchageSPAss;
                		 ass1.setPatientInRole(oldPatientInRole);
                		 ass1.persist();
                		 
                		 String pirSql1 = "SELECT pir FROM PatientInRole pir WHERE pir.patientInSemester = " + dualExchangeSp.getPatientInSemester().getId() + " AND pir.oscePost IS NOT NULL AND pir.oscePost.standardizedRole.id = " + ass.getPatientInRole().getOscePost().getStandardizedRole().getId();
            			 TypedQuery<PatientInRole> pirQuery1 = em.createQuery(pirSql1, PatientInRole.class);
            			 
            			 if (pirQuery1.getResultList().size() > 0)
                		 {
            				 PatientInRole exchangePatientInRole = pirQuery1.getResultList().get(0);
            				 dualSpOldAss.setPatientInRole(exchangePatientInRole);
            				 dualSpOldAss.persist();
                		 }
                	 }
        		 }
        	 }
    	 }
    	 else
    	 {
    		 String sql = "SELECT a FROM Assignment a WHERE a.patientInRole = " + ass.getPatientInRole().getId() + " AND a.osceDay.osce = " + ass.getOsceDay().getOsce().getId() + " AND a.sequenceNumber = " + ass.getSequenceNumber();
        	 TypedQuery<Assignment> oldSpQuery = em.createQuery(sql, Assignment.class);
        	 Iterator<Assignment> oldSpItr = oldSpQuery.getResultList().iterator();
        	 
        	 sql = "SELECT a FROM Assignment a WHERE a.patientInRole = " + exchangePir.getId() + " AND a.osceDay.osce = " + ass.getOsceDay().getOsce().getId() + " AND a.sequenceNumber = " + ass.getSequenceNumber();
        	 TypedQuery<Assignment> exchangeSpQuery = em.createQuery(sql, Assignment.class);
        	 Iterator<Assignment> exchangeSpItr = exchangeSpQuery.getResultList().iterator();
        	 
    		 while (oldSpItr.hasNext())
        	 {
        		 if (ass.getPatientInRole().getOscePost() != null && ass.getPatientInRole().getOscePost().getStandardizedRole() != null)
        		 {
        			 String pirSql = "SELECT pir FROM PatientInRole pir WHERE pir.patientInSemester = " + exchangePir.getPatientInSemester().getId() + " AND pir.oscePost IS NOT NULL AND pir.oscePost.standardizedRole.id = " + ass.getPatientInRole().getOscePost().getStandardizedRole().getId();
        			 TypedQuery<PatientInRole> pirQuery = em.createQuery(pirSql, PatientInRole.class);
        			 
        			 if (pirQuery.getResultList().size() > 0)
            		 {
        				 PatientInRole exchangePatientInRole = pirQuery.getResultList().get(0);
        				 Assignment assignment = oldSpItr.next();
                		 assignment.setPatientInRole(exchangePatientInRole);
                		 assignment.persist();
            		 }
        		 }
        	 }
        	 
        	 while (exchangeSpItr.hasNext())
        	 {
        		 String pirSql = "SELECT pir FROM PatientInRole pir WHERE pir.patientInSemester = " + oldSp.getPatientInSemester().getId() + " AND pir.oscePost IS NULL";
        		 TypedQuery<PatientInRole> pirQuery = em.createQuery(pirSql, PatientInRole.class);
        		 PatientInRole oldPatientInRole = pirQuery.getSingleResult();
        		 
        		 Assignment assignment = exchangeSpItr.next();
        		 assignment.setPatientInRole(oldPatientInRole);
        		 assignment.persist();
        	 }
    	 }
    	 
    	
    	 
    	 return true;
     }*/
     
     public static Boolean exchangeStandardizedPatient(Assignment ass, PatientInRole exchangePir)
     {
    	 PatientInRole oldSp = ass.getPatientInRole();
    	 EntityManager em = entityManager();
    	 String sql = "SELECT a FROM Assignment a WHERE a.patientInRole = " + ass.getPatientInRole().getId() + " AND a.osceDay.osce = " + ass.getOsceDay().getOsce().getId() + " AND a.sequenceNumber = " + ass.getSequenceNumber();
    	 TypedQuery<Assignment> oldSpQuery = em.createQuery(sql, Assignment.class);
    	 Iterator<Assignment> oldSpItr = oldSpQuery.getResultList().iterator();
    	 
    	 sql = "SELECT a FROM Assignment a WHERE a.patientInRole = " + exchangePir.getId() + " AND a.osceDay.osce = " + ass.getOsceDay().getOsce().getId() + " AND a.sequenceNumber = " + ass.getSequenceNumber();
    	 TypedQuery<Assignment> exchangeSpQuery = em.createQuery(sql, Assignment.class);
    	 Iterator<Assignment> exchangeSpItr = exchangeSpQuery.getResultList().iterator();
    	 
    	 while (oldSpItr.hasNext())
    	 {
    		 if (ass.getPatientInRole().getOscePost() != null && ass.getPatientInRole().getOscePost().getStandardizedRole() != null)
    		 {
    			 String pirSql = "SELECT pir FROM PatientInRole pir WHERE pir.patientInSemester = " + exchangePir.getPatientInSemester().getId() + " AND pir.oscePost IS NOT NULL AND pir.oscePost.standardizedRole.id = " + ass.getPatientInRole().getOscePost().getStandardizedRole().getId();
    			 TypedQuery<PatientInRole> pirQuery = em.createQuery(pirSql, PatientInRole.class);
    			 
    			 if (pirQuery.getResultList().size() > 0)
        		 {
    				 PatientInRole exchangePatientInRole = pirQuery.getResultList().get(0);
    				 Assignment assignment = oldSpItr.next();
            		 assignment.setPatientInRole(exchangePatientInRole);
            		 assignment.persist();
        		 }
    		 }
    	 }
    	 
    	 while (exchangeSpItr.hasNext())
    	 {
    		 String pirSql = "SELECT pir FROM PatientInRole pir WHERE pir.patientInSemester = " + oldSp.getPatientInSemester().getId() + " AND pir.oscePost IS NULL";
    		 TypedQuery<PatientInRole> pirQuery = em.createQuery(pirSql, PatientInRole.class);
    		 PatientInRole oldPatientInRole = pirQuery.getSingleResult();
    		 
    		 Assignment assignment = exchangeSpItr.next();
    		 assignment.setPatientInRole(oldPatientInRole);
    		 assignment.persist();
    	 }
    	 
    	 return true;
     }

       /*   public static List<List<Assignment>> retrieveLogicalStudentBreak(Long osceDayId, Long courseId, Long oscePostId)
     {
    	 //retrieve distinct rotation
    	 EntityManager em = entityManager();
    	 String rotationsSQL="select distinct(rotationNumber) FROM Assignment where type=0 and osceDay="+osceDayId+" and oscePostRoom in(select id from oscePostRoom where oscePost="+oscePostId+" and course="+courseId+") order by rotationNumber";
    	 TypedQuery<Long> rotationsQuery = em.createQuery(rotationsSQL, Long.class);
    	 List<Long> rotations=rotationsQuery.getResultList();
    	 
    	 //loop through rotations
    	 for(int i=0;i<rotations.size();i++)
    	 {
    		 	//get set of distinct time start for this rotation		 
    		 	String timeStartString = "SELECT  distinct(timeStart) FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=0 and a.oscePostRoom in(select opr.id from OscePostRoom as opr where opr.room in (select rm.room from OscePostRoom as rm where rm.oscePost = " + oscePostId +  " and rm.course= " + courseId + " and rm.version<999) and opr.course=" + courseId + " ) and rotationNumber="+rotations.get(i)+" order by a.timeStart asc";
    	        
    	        TypedQuery<Date> query = em.createQuery(timeStartString, Date.class);
    	        List<Date> distinctTimeStart = query.getResultList();
    	        
    	        //get distinct sequence number for this rotation
    	        String sequenceString = "SELECT  distinct(sequenceNumber) FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=0 and a.oscePostRoom in(select opr.id from OscePostRoom as opr where opr.room in (select rm.room from OscePostRoom as rm where rm.oscePost = " + oscePostId +  " and rm.course= " + courseId + " and rm.version<999) and opr.course=" + courseId + " ) and rotationNumber="+rotations.get(i)+" order by a.timeStart asc";
    	        
    	        TypedQuery<Long> sequencequery = em.createQuery(sequenceString, Long.class);
    	        List<Long> distinctSequence = sequencequery.getResultList();
    	        
    	        //loop through all timeStart
    	       
    	        for(int j=0;j<distinctTimeStart.size();j++)
    	        {
    	        	String assignmentsString = "SELECT  a FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=0 and a.oscePostRoom in(select opr.id from OscePostRoom as opr where opr.room in (select rm.room from OscePostRoom as rm where  rm.course= " + courseId + " and rm.version<999) and opr.course=" + courseId + " ) and rotationNumber="+rotations.get(i)+" and timeStart = :timeStart";
        	        
        	        TypedQuery<Assignment> assignmentsquery = em.createQuery(assignmentsString, Assignment.class);
        	        assignmentsquery.setParameter(0, distinctTimeStart.get(j));
        	       
        	        List<Assignment> assignments = assignmentsquery.getResultList();
        	        
        	        //loop through distinctSequence to find student in break
        	        
        	        Set<Integer> sequencesInBreak=new HashSet<Integer>();
        	        for(int k=0;k<distinctSequence.size();k++)
        	        {
        	        	for(int l=0;l<assignments.size();l++)
        	        	{
	        	        	if(assignments.get(l).getSequenceNumber().intValue()!=distinctSequence.get(k).intValue())
	        	        	{
	        	        		sequencesInBreak.add(distinctSequence.get(k).intValue());
	        	        		
	        	        		break;
	        	        	}
        	        	}
        	        }
        	        
        	      //get assignments
        	        List<Assignment> assingmentInBreak=new ArrayList<Assignment>();
        	        
        	        for(int m=0;m<sequencesInBreak.size();m++)
        	        {
        	        	String assignmentInBreakString = "SELECT  a FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=0 and a.oscePostRoom in(select opr.id from OscePostRoom as opr where opr.room in (select rm.room from OscePostRoom as rm where  rm.course= " + courseId + " and rm.version<999) and opr.course=" + courseId + " ) and rotationNumber="+rotations.get(i)+" and timeStart = :timeStart and sequenceNumber";
            	        
        	        }
        	        
    	        }
    	        
    	        
    	       
    	 }
    	 
    	 return null;
    	 
     }
     */
      
     
        public static List<Assignment> retrieveLogicalStudentInBreak(Long osceDayId,Long courseId)
     {
    	 EntityManager em = entityManager();
    	 String query="SELECT  a FROM Assignment as a where a.osceDay="+osceDayId+"  and type=0 and oscePostRoom is null and sequenceNumber in (select distinct (sequenceNumber) from Assignment where type=0 and osceDay="+osceDayId+" and oscePostRoom in (select id from OscePostRoom where course="+courseId+")) order by a.timeStart asc";
    	 TypedQuery<Assignment> typedQuery = em.createQuery(query, Assignment.class);
    	 List<Assignment> assignments=typedQuery.getResultList();
    	 Log.info("retrieveLogicalStudentInBreak query :" +query);
    	 
    	 return assignments;
     }
     //by spec change]
     
     public static List<Assignment> findAssignmentByExaminerAndSemester(Long semesterId,Long examinerId) {
    	 
    	 Log.info("findAssignmentByStudentAndSemester :");
         EntityManager em = entityManager();
         String queryString = "select a from Assignment as a where a.examiner = " +examinerId+ " and a.osceDay.osce.semester = " +semesterId + " order by a.timeStart"; 
         
         TypedQuery<Assignment> query = em.createQuery(queryString, Assignment.class);
         
         return query.getResultList(); 
     }
     
   //payment change
     public static Long countStandardizedPatientBySemester(Long semesterId)
     {
    	 EntityManager em = entityManager();
    	 
    	 String sql = "SELECT DISTINCT ps.standardizedPatient" +
  				" FROM Assignment AS a, PatientInRole AS pr, PatientInSemester AS ps, OscePost AS op, StandardizedRole AS sr " +
  				" WHERE a.type = 1 AND a.osceDay IN (SELECT od FROM OsceDay od WHERE od.osce IN (" +
  				" SELECT id FROM Osce WHERE semester = " + semesterId + "))" +
  				" AND a.patientInRole = pr.id" +
  				" AND pr.patientInSemester = ps.id" +
  				" AND pr.oscePost = op.id " +
  				" AND op.standardizedRole = sr.id" +
  				" GROUP BY ps.standardizedPatient, a.osceDay, pr.oscePost, sr.roleType";
    	 
    	 TypedQuery<StandardizedPatient> query = em.createQuery(sql, StandardizedPatient.class);
    	 
    	 return (long) query.getResultList().size();
     }
     
     public static List<StandardizedPatient> findStandardizedPatientBySemester(int start, int max, String colName, Sorting sortType, Long semesterId)
     {
    	 EntityManager em = entityManager();
    	 
    	 String sql = "SELECT DISTINCT ps.standardizedPatient" +
  				" FROM Assignment AS a, PatientInRole AS pr, PatientInSemester AS ps, OscePost AS op, StandardizedRole AS sr " +
  				" WHERE a.type = 1 AND a.osceDay IN (SELECT od FROM OsceDay od WHERE od.osce IN (" +
  				" SELECT id FROM Osce WHERE semester = " + semesterId + "))" +
  				" AND a.patientInRole = pr.id" +
  				" AND pr.patientInSemester = ps.id" +
  				" AND pr.oscePost = op.id " +
  				" AND op.standardizedRole = sr.id" +
  				" GROUP BY ps.standardizedPatient, a.osceDay, pr.oscePost, sr.roleType" +
  				" ORDER BY ps.standardizedPatient." + colName + " " + sortType + " ";
    	 
    	 TypedQuery<StandardizedPatient> query = em.createQuery(sql, StandardizedPatient.class);    	 
    	 query.setFirstResult(start);
    	 query.setMaxResults(max);    	 
    	 return query.getResultList();
     }
     //payment change
     
   //deactivate student change
     public static Boolean deactivateStudentFromAssignment(StudentOsces stud)
     {
    	 EntityManager em = entityManager();
    	 String sql = "SELECT a FROM Assignment a WHERE a.osceDay.osce.id = " + stud.getOsce().getId() + " AND a.student.id = " + stud.getStudent().getId();
    	 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
    	 List<Assignment> assList = query.getResultList();
    	 
    	 for (Assignment ass : assList)
    	 {
    		 ass.setStudent(null);
    		 ass.persist();
    	 }
    	 
    	 return true;
     }
     public static Boolean activateStudentFromAssignment(StudentOsces stud)
     {
    	 EntityManager em = entityManager();
    	 String strSql = "SELECT DISTINCT a.sequenceNumber FROM Assignment a WHERE a.osceDay.osce.id = " + stud.getOsce().getId() + " AND a.type = 0 " + " AND a.student IS NULL";
    	 TypedQuery<Integer> query1 = em.createQuery(strSql,Integer.class);
    	 List<Integer> seqNumList = query1.getResultList();
    	 
    	 if (seqNumList.size() > 0)
    	 {
    		 Integer seqNum = seqNumList.get(0);
    		 String sql = "SELECT a FROM Assignment a WHERE a.osceDay.osce.id = " + stud.getOsce().getId() + " AND a.type = 0 AND a.sequenceNumber = " + seqNum; 
        	 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
        	 List<Assignment> assList = query.getResultList();
        	 
        	 for (Assignment ass : assList)
        	 {
        		 ass.setStudent(stud.getStudent());
        		 ass.persist();
        	 }
        	 
        	 return true;
    	 }
    	 else
    	 {
    		 return false;
    	 } 
     }
   //deactivate student change
     
     public static List<Date> clearExaminerAssignment(Long osceDayId,Long oscePostId,Long courseId)
     {
    	 Log.info("clearExaminerAssignment :");
         EntityManager em = entityManager();
         try
         {
         String queryString = "SELECT  a FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=2 and a.oscePostRoom in(select opr.id from OscePostRoom as opr where opr.oscePost=" + oscePostId + " and opr.course=" + courseId + " ) order by a.timeStart asc ";
         TypedQuery<Assignment> query = em.createQuery(queryString, Assignment.class);
         List<Assignment> assignmentList = query.getResultList();
         
         for(Assignment a:assignmentList)
         {
        	 a.remove();
        	 
         }
         Log.info("retrieveAssignmenstOfTypeExaminer query String :" + queryString);
         Log.info("Assignment List Size :" + assignmentList.size());
         
         String queryString1 = "SELECT  max(timeStart) FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=0 and a.oscePostRoom in(select opr.id from OscePostRoom as opr where opr.oscePost=" + oscePostId + " and opr.course=" + courseId + " ) ";
         TypedQuery<Date> query1 = em.createQuery(queryString1, Date.class);
         List<Date> dates=new ArrayList<Date>();
         dates.add(query1.getResultList().get(0));
         
         String queryString2 = "SELECT  max(timeEnd) FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=0 and a.oscePostRoom in(select opr.id from OscePostRoom as opr where opr.oscePost=" + oscePostId + " and opr.course=" + courseId + " ) ";
         TypedQuery<Date> query2 = em.createQuery(queryString2, Date.class);
         
         dates.add(query2.getResultList().get(0));
    	 return dates;
         }
         catch (Exception e) {
			e.printStackTrace();
			return null;
		}
     }
     
     public static void shiftLongBreak(Assignment currOsceDayId, int nextPrevFlag)
     {
    	 //for shift long break in previous rotation
    	 if (nextPrevFlag == 0)
    	 {
    		 if (currOsceDayId.getRotationNumber() > 0)
    		 {
    			 Date preRotOsceDayEndTime = getPreviousNextRotationEndTime(currOsceDayId.getOsceDay().getId(), (currOsceDayId.getRotationNumber() - 1));
    			if (preRotOsceDayEndTime != null)
    			{
    				int diff = currOsceDayId.getOsceDay().getOsce().getMiddleBreak().intValue() - currOsceDayId.getOsceDay().getOsce().getLongBreak().intValue(); 
        			updateAssignmentByDiff(currOsceDayId.getOsceDay().getId(), diff, currOsceDayId.timeEnd, false);
        			diff = currOsceDayId.getOsceDay().getOsce().getLongBreak().intValue() - currOsceDayId.getOsceDay().getOsce().getMiddleBreak().intValue();
        			updateAssignmentByDiff(currOsceDayId.getOsceDay().getId(), diff, preRotOsceDayEndTime, false);
    			}
    		 }	
    		
    	 }
    	//for shift long break in next rotation
    	 else if (nextPrevFlag == 1)
    	 {
    		int maxRotation = getMaxRotationNumber(currOsceDayId.getOsceDay().getId());
    		
    		if ((currOsceDayId.getRotationNumber() + 1) < maxRotation)
    		{
    			Date nextRotOsceDayEndTime = getPreviousNextRotationEndTime(currOsceDayId.getOsceDay().getId(), (currOsceDayId.getRotationNumber() + 1));
    			if (nextRotOsceDayEndTime != null)
    			{
    				int  diff = currOsceDayId.getOsceDay().getOsce().getLongBreak().intValue() - currOsceDayId.getOsceDay().getOsce().getMiddleBreak().intValue();
              		updateAssignmentByDiff(currOsceDayId.getOsceDay().getId(), diff, nextRotOsceDayEndTime, false); 
            		diff = currOsceDayId.getOsceDay().getOsce().getMiddleBreak().intValue() - currOsceDayId.getOsceDay().getOsce().getLongBreak().intValue(); 
             		updateAssignmentByDiff(currOsceDayId.getOsceDay().getId(), diff, currOsceDayId.timeEnd, false);
    			}    		
    		}    		
    	 }
    	 
    	 OsceDay osceDay = currOsceDayId.getOsceDay();
     	 osceDay.setIsTimeSlotShifted(false);
     	 osceDay.persist();
    	 
     }

     public static List<Assignment> findAssignmentOfLogicalBreakPostPerRotation(Long osceDayId, Long courseId, Integer rotationNumber)
     {
    	 EntityManager em = entityManager();
    	 String query="SELECT  a FROM Assignment as a where a.osceDay="+osceDayId+"  and type=0 and a.rotationNumber = " + rotationNumber + " and oscePostRoom is null and sequenceNumber in (select distinct (sequenceNumber) from Assignment where type=0 and osceDay="+osceDayId+" and oscePostRoom in (select id from OscePostRoom where course="+courseId+")) order by a.timeStart asc";
    	 TypedQuery<Assignment> typedQuery = em.createQuery(query, Assignment.class);
    	 List<Assignment> assignments=typedQuery.getResultList();
    	 Log.info("retrieveLogicalStudentInBreak query :" +query);
    	 
    	 return assignments;
     }
     
     public static List<Assignment> findAssignmentByOscePostAndOsceDay(Long osceDayId, Long oscePostId)
     {
    	 EntityManager em = entityManager();
    	 String sql = "SELECT a FROM Assignment a WHERE type = 1 AND a.osceDay = " + osceDayId + " AND a.oscePostRoom.oscePost.id = " + oscePostId + " GROUP BY a.timeStart ORDER BY a.timeStart";
    	 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
    	 return query.getResultList();
     }
     
     public static List<Assignment> findAssignmentByOscePostAndOsceDayAndTimeStartAndTimeEnd(Long osceDayId, Long oscePostId, Date timeStart, Date timeEnd, Integer sequenceNumber, int spCountForPost)
     {
    	 EntityManager em = entityManager();
    	 //String sql = "SELECT a FROM Assignment a WHERE type = 1 AND osce_day = " + osceDayId + " AND a.oscePostRoom.oscePost.id = " + oscePostId + " AND ( (a.timeStart = '" + timeStart + "' AND a.timeEnd = '" + timeEnd + "') OR a.sequenceNumber = " + sequenceNumber + ") ORDER BY a.timeStart";
    	 String sql = "SELECT a FROM Assignment a WHERE type = 1 AND a.osceDay = " + osceDayId + " AND a.oscePostRoom.oscePost.id = " + oscePostId + " AND a.sequenceNumber = " + sequenceNumber + " ORDER BY a.oscePostRoom.course.id, a.id";
    	 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
    	 
    	 Assignment assignmentSpBreakSlot = findAssignmentForSPBreak(osceDayId, timeStart, timeEnd);
    	 
    	 List<Assignment> listAssignmentSPslot = query.getResultList();
    	 
    	 if(assignmentSpBreakSlot != null)
    	 {
    		 listAssignmentSPslot.add(assignmentSpBreakSlot);
    	 }	 
    	 else if (assignmentSpBreakSlot == null)
    	 {
    		if (listAssignmentSPslot.size() != 0 && spCountForPost > listAssignmentSPslot.size())
    		{
    			int diff = spCountForPost - listAssignmentSPslot.size();
    			
    			for (int i=0; i<diff; i++)
    			{
    				// create break assignment
					Assignment assignment;
    				Assignment spAss = findAssignmentByOscePostAndOsceDayOrderByTimeEndDesc(osceDayId, oscePostId, sequenceNumber);
    				if (spAss == null)
    					assignment = listAssignmentSPslot.get(0);
    				else
    					assignment = spAss;
    				
					Assignment ass = new Assignment();
					ass.setType(AssignmentTypes.PATIENT);
					ass.setOscePostRoom(null);
					ass.setOsceDay(assignment.getOsceDay());
					ass.setTimeStart(assignment.getTimeStart());
					ass.setTimeEnd(assignment.getTimeEnd());
					ass.setSequenceNumber(assignment.getSequenceNumber());
					ass.persist();
					
					listAssignmentSPslot.add(ass);
    			}
    		}
    	 }
    	 
    	 return listAssignmentSPslot;
     }
     
     public static Assignment findAssignmentByOscePostAndOsceDayOrderByTimeEndDesc(Long osceDayId, Long oscePostId, Integer sequenceNumber)
     {
    	 EntityManager em = entityManager();
    	 String sql = "SELECT a FROM Assignment a WHERE type = 1 AND a.osceDay = " + osceDayId + " AND a.oscePostRoom.oscePost.id = " + oscePostId + " AND a.sequenceNumber = " + sequenceNumber + " ORDER BY a.timeEnd desc";
    	 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
    	 if (query.getResultList().isEmpty() == false)
    	 {
    		 return query.getResultList().get(0);
    	 }
    	 
    	 return null;
     }
     
     public static Assignment findAssignmentForSPBreak(Long osceDayId, Date timeStart, Date timeEnd)
     {
    	 EntityManager em = entityManager();
    	 String sql = "SELECT a FROM Assignment a WHERE type = 1 AND a.osceDay = " + osceDayId + " AND a.oscePostRoom IS NULL AND a.patientInRole IS NULL AND a.timeStart = '" + timeStart + "' AND a.timeEnd = '" + timeEnd + "' ORDER BY a.timeStart";
    	 //String sql = "SELECT a FROM Assignment a WHERE type = 1 AND a.osceDay = " + osceDayId + " AND a.oscePostRoom IS NULL AND a.timeStart = '" + timeStart + "' AND a.timeEnd = '" + timeEnd + "' ORDER BY a.timeStart";
    	 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
    	 
    	 if (query.getResultList().size() > 0)
    		 return query.getResultList().get(0);
    	 else
    		 return null;
     }
     
     public static Date getPreviousNextRotationEndTime(Long osceDayId, int rotationNumber)
     {
    	 EntityManager em = entityManager();
    	 String sql = "SELECT a FROM Assignment a WHERE a.type = 0 AND a.osceDay.id = " + osceDayId + " AND a.rotationNumber = " + rotationNumber + " ORDER BY timeEnd DESC";
    	 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
    	 
    	 if (query.getResultList().size() > 0)
    		 return query.getResultList().get(0).getTimeEnd();
    	 else 
    		 return null;
     }
     
     public static int getMaxRotationNumber(Long osceDayId)
     {
    	 EntityManager em = entityManager();
    	 String sql = "SELECT a FROM Assignment a WHERE a.type = 0 AND a.osceDay.id = " + osceDayId + " ORDER BY a.rotationNumber DESC";
    	 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
    
    	 if (query.getResultList().size() > 0)
    		 return query.getResultList().get(0).getRotationNumber();
    	 else
    		 return 0;
     }
     
     public static OsceDay updateTimeForOsceDay(Long osceDayId, int newStartDiffTime, int newLunchDiffTime)
     {
    	 OsceDay osceDay = OsceDay.findOsceDay(osceDayId);
    	 
    	 if (newStartDiffTime != 0)
    	 {
    		 updateAssignmentByDiff(osceDay.getId(), newStartDiffTime, osceDay.getTimeStart(), true);
    		 osceDay.setTimeStart(dateAddMin(osceDay.getTimeStart(), newStartDiffTime));
    		 osceDay.setTimeEnd(dateAddMin(osceDay.getTimeEnd(), newStartDiffTime));
    		 osceDay.setLunchBreakStart(dateAddMin(osceDay.getLunchBreakStart(), newStartDiffTime));
    		 osceDay.setIsTimeSlotShifted(false);
    		 osceDay.persist();
    	 }
    	 
    	 if (newLunchDiffTime != 0)
    	 {
    		 if(osceDay.getLunchBreakStart() != null)
    		 {
    			 //this is done because to get timeslot after lunch break.
    			 int lunchTime = (osceDay.getOsce().getLunchBreak().intValue() + osceDay.getLunchBreakAdjustedTime()) / 2;
    			 Date lunchBreakTime = dateAddMin(osceDay.getLunchBreakStart(), lunchTime);
    			 updateAssignmentByDiff(osceDay.getId(), newLunchDiffTime, lunchBreakTime, false);
    			 
    			 int oldAdjustedTime = osceDay.getLunchBreakAdjustedTime();
    			 
    			 osceDay.setTimeEnd(dateAddMin(osceDay.getTimeEnd(), newLunchDiffTime));
    			 osceDay.setIsTimeSlotShifted(false);
    			 osceDay.setLunchBreakAdjustedTime(oldAdjustedTime + newLunchDiffTime);
    			 //osceDay.setLunchBreakStart(lunchBreakTime);
        		 osceDay.persist();    			 
    		 }
    	 }
    	 
    	 return osceDay;
     }       
     
     //flag has two value :
     //1 : For Move Rotation Up From Lunch Break
     //2 : For Move Rotation Down From Lunch Break
     @Transactional(readOnly=false, rollbackFor=Exception.class)
     public static void moveLunchBreakOsceDay(int flag, Long osceDayId)
     {
    	 try
    	 {
    		 OsceDay osceDay = OsceDay.findOsceDay(osceDayId);
    		
			 if (osceDay.getOsceSequences().size() == 1)
        	 {
     			if (flag == 1) // up rotation
        		{
     				//add lunch break after rotation
     				String rotationStr[] = osceDay.getBreakByRotation().split("-");
     				int rotationNumber = 0;
     				
     				for (int i=0; i<rotationStr.length; i++)
     				{
     					if (rotationStr[i].contains(osceDay.getOsce().getLunchBreak().toString()))
 	    				{
     						String s[] = rotationStr[i].split(":");
     						rotationNumber = Integer.parseInt(s[0]);
     						break;
 	    				}
     				}
     				
     				Long newLunchValue = findLunchBreakBetRot(osceDayId, rotationNumber, (rotationNumber + 1));
     				
     				newLunchValue = newLunchValue - osceDay.getOsce().getMiddleBreak().intValue();
  	     			
  	     			updateAssignemntByDiffForShiftLunchBreakForOsceDay(osceDay, newLunchValue.intValue(), (rotationNumber+1), 1);	       			
     					
 	    			//update breakByRotation String
 	    			for (int i=0; i<rotationStr.length; i++)
 	    			{
 	    				if (rotationStr[i].contains(osceDay.getOsce().getLunchBreak().toString()))
 	    				{
 	    					if ((i+1) < rotationStr.length)
 	    					{	
 	    						String nextS[] = rotationStr[(i+1)].split(":");
 	    								
 	    						String s[] = rotationStr[i].split(":");
 	    						
 	    						if (s.length > 1 && nextS.length > 1)
 	    						{
 	    							String tmp = s[1];
 	    							s[1] = nextS[1];
 	    							nextS[1] = tmp;    									
 	    						}
 	    					
 	    						rotationStr[i] = StringUtils.join(s,":");
 	    						rotationStr[i+1]  = StringUtils.join(nextS,":");
 	    					}
 	    					break;	
 	    				}
 	    			}
 	    			
 	    			//persist new value of osceday
 	    			Assignment ass = findAssignmentByRotationAndOsceDay(osceDayId, (rotationNumber + 1));
 	    			String newStr = StringUtils.join(rotationStr, "-");
 	    			osceDay.setBreakByRotation(newStr);
 	    			osceDay.setIsTimeSlotShifted(true);
 	    			osceDay.setLunchBreakStart(ass.getTimeEnd());
 	    			osceDay.persist();
      				    			
        		}
        		else if (flag == 2) // down rotation
        		{
        			String rotationStr[] = osceDay.getBreakByRotation().split("-");
     				int rotationNumber = 0;
     				
     				for (int i=0; i<rotationStr.length; i++)
     				{
     					if (rotationStr[i].contains(osceDay.getOsce().getLunchBreak().toString()))
 	    				{
     						String s[] = rotationStr[i].split(":");
     						rotationNumber = Integer.parseInt(s[0]);
     						break;
 	    				}
     				}
     				
     				Long newLunchValue = findLunchBreakBetRot(osceDayId, rotationNumber, (rotationNumber + 1));
     				
     				newLunchValue = newLunchValue - osceDay.getOsce().getMiddleBreak().intValue();
  	     			
  	     			updateAssignemntByDiffForShiftLunchBreakForOsceDay(osceDay, newLunchValue.intValue(), rotationNumber, 2);				
    	    			
	    			for (int i=0; i<rotationStr.length; i++)
	    			{
	    				if (rotationStr[i].contains(osceDay.getOsce().getLunchBreak().toString()))
	    				{
	    					if ((i-1) >= 0)
	    					{	
	    						String nextS[] = rotationStr[(i-1)].split(":");
	    								
	    						String s[] = rotationStr[i].split(":");
	    						
	    						if (s.length > 1 && nextS.length > 1)
	    						{
	    							String tmp = s[1];
	    							s[1] = nextS[1];
	    							nextS[1] = tmp;    									
	    						}
	    					
	    						rotationStr[i] = StringUtils.join(s,":");
	    						rotationStr[i-1]  = StringUtils.join(nextS,":");
	    					}
	    					break;	
	    				}
	    			}
	    			
	    			String newStr = StringUtils.join(rotationStr, "-");
	    			Assignment ass = findAssignmentByRotationAndOsceDay(osceDayId, (rotationNumber - 1));
	    			osceDay.setBreakByRotation(newStr);
					osceDay.setLunchBreakStart(ass.getTimeEnd());
					osceDay.setIsTimeSlotShifted(true);
					osceDay.persist();            				
        		}        		
        	 }
        	 else if (osceDay.getOsceSequences().size() == 2)
        	 {
        		OsceSequence firstSeq = osceDay.getOsceSequences().get(0);
        		OsceSequence secondSeq = osceDay.getOsceSequences().get(1);
        		
        		Date lunchBreakTime = osceDay.getLunchBreakStart();
      			
        		if (flag == 1) // up rotation
         		{
      				//add lunch break after rotation
      				String rotationStr[] = osceDay.getBreakByRotation().split("-");
      				
      				int rotationNumber = 0;
     				
     				for (int i=0; i<rotationStr.length; i++)
     				{
     					if (rotationStr[i].contains(osceDay.getOsce().getLunchBreak().toString()))
 	    				{
     						String s[] = rotationStr[i].split(":");
     						rotationNumber = Integer.parseInt(s[0]);
     						break;
 	    				}
     				}
     					
     				Long newLunchValue = findLunchBreakBetRot(osceDayId, rotationNumber, (rotationNumber + 1));
     				
     				newLunchValue = newLunchValue - osceDay.getOsce().getMiddleBreak().intValue();
  	     			
  	     			updateAssignemntByDiffForShiftLunchBreak(osceDayId, newLunchValue.intValue(), (rotationNumber+1), firstSeq, secondSeq);
  	     			
  	     			Date newLunchBreakTime = dateAddMin(lunchBreakTime, osceDay.getOsce().getMiddleBreak().intValue());
  	     			
  	     			updateExaminer(osceDay, firstSeq, secondSeq, (rotationNumber+1), newLunchValue);
  	     			
  	     			//update breakByRotation String     			
  	     			for (int i=0; i<rotationStr.length; i++)
  	     			{
  	     				if (rotationStr[i].contains(osceDay.getOsce().getLunchBreak().toString()))
  	     				{
  	     					if ((i+1) < rotationStr.length)
  	     					{	
  	     						String nextS[] = rotationStr[(i+1)].split(":");
  	     								
  	     						String s[] = rotationStr[i].split(":");
  	     						
  	     						if (s.length > 1 && nextS.length > 1)
  	     						{
  	     							String tmp = s[1];
  	     							s[1] = nextS[1];
  	     							nextS[1] = tmp;    									
  	     						}
  	     					
  	     						rotationStr[i] = StringUtils.join(s,":");
  	     						rotationStr[i+1]  = StringUtils.join(nextS,":");
  	     					}
  	     					break;	
  	     				}
  	     			}
  	     			
  	     			//persist new value of osceday
  	     			String newStr = StringUtils.join(rotationStr, "-");
  	     			Assignment ass = findAssignmentByRotationAndOsceDay(osceDayId, (rotationNumber + 1));
  	     			osceDay.setBreakByRotation(newStr);
  	     			osceDay.setIsTimeSlotShifted(true);
  	     			osceDay.setLunchBreakStart(ass.getTimeEnd());
  	     			osceDay.persist();
  	     			
  	     			if(secondSeq.getNumberRotation() > 0)
  	     			{
  	     				secondSeq.setNumberRotation((secondSeq.getNumberRotation() - 1));
  	     				secondSeq.persist();
  	     			}
  	     			
  	     			firstSeq.setNumberRotation((firstSeq.getNumberRotation()+1));
  	     			firstSeq.persist();
  	     			
  	     		}
         		else if (flag == 2) // down rotation
         		{
         			String rotationStr1[] = osceDay.getBreakByRotation().split("-");
      				
      				int rotationNumber = 0;
     				
     				for (int i=0; i<rotationStr1.length; i++)
     				{
     					if (rotationStr1[i].contains(osceDay.getOsce().getLunchBreak().toString()))
 	    				{
     						String s[] = rotationStr1[i].split(":");
     						rotationNumber = Integer.parseInt(s[0]);
     						break;
 	    				}
     				}
     				
     				Long newLunchValue = findLunchBreakBetRot(osceDayId, rotationNumber, (rotationNumber + 1));
     				
     				newLunchValue = newLunchValue - osceDay.getOsce().getMiddleBreak().intValue();
 					
 					updateAssignemntByDiffForShiftLunchBreakForDownRot(osceDayId, newLunchValue.intValue(), rotationNumber, firstSeq, secondSeq);
 					
 					updateExaminerForDownRotation(osceDay, firstSeq, secondSeq, rotationNumber, newLunchValue);
 					
 					String rotationStr[] = osceDay.getBreakByRotation().split("-");
 	    			
 	    			for (int i=0; i<rotationStr.length; i++)
 	    			{
 	    				if (rotationStr[i].contains(osceDay.getOsce().getLunchBreak().toString()))
 	    				{
 	    					if ((i-1) >= 0)
 	    					{	
 	    						String nextS[] = rotationStr[(i-1)].split(":");
 	    								
 	    						String s[] = rotationStr[i].split(":");
 	    						
 	    						if (s.length > 1 && nextS.length > 1)
 	    						{
 	    							String tmp = s[1];
 	    							s[1] = nextS[1];
 	    							nextS[1] = tmp;    									
 	    						}
 	    					
 	    						rotationStr[i] = StringUtils.join(s,":");
 	    						rotationStr[i-1]  = StringUtils.join(nextS,":");
 	    					}
 	    					break;	
 	    				}
 	    			}
 	    			
 	    			String newStr = StringUtils.join(rotationStr, "-");
 	    			Assignment ass = findAssignmentByRotationAndOsceDay(osceDayId, (rotationNumber-1));
 	    			osceDay.setBreakByRotation(newStr);
 					osceDay.setLunchBreakStart(ass.getTimeEnd());
 					osceDay.setIsTimeSlotShifted(true);
 					osceDay.persist();
 					
 					if (firstSeq.getNumberRotation() > 0)
 					{
 						firstSeq.setNumberRotation((firstSeq.getNumberRotation()-1));
 						firstSeq.persist();
 					}
 					
 					secondSeq.setNumberRotation((secondSeq.getNumberRotation()+1));
 					secondSeq.persist();
         		}
        	 }
    	 }
    	 catch(Exception e)
    	 {
    		 e.printStackTrace();
    	 }
     }
     
     public static Assignment findEndTimeAfterLunchBreakRotation(Long osceDayId, Date afterLunchBreakTime)
     {
    	 EntityManager em = entityManager();
    	 String sql = "SELECT a FROM Assignment a WHERE a.osceDay.id = " + osceDayId + " AND a.type = 0 AND a.rotationNumber IN (SELECT DISTINCT a.rotationNumber FROM Assignment a WHERE a.osceDay.id = " + osceDayId + " AND a.timeStart = :timeStart AND a.type = 0) GROUP BY a.timeEnd ORDER BY a.timeEnd DESC ";
    	 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
    	 query.setParameter("timeStart", afterLunchBreakTime);
    	 
    	 if (query.getResultList().size() > 0)
    		 return query.getResultList().get(0);
    	 else
    		 return null;
     }
     
     public static Assignment findRotationNumberOfLunchBreakStart(Long osceDayId, Date lunchBreakStartTime)
     {
    	 EntityManager em = entityManager();
    	 String sql = "SELECT a FROM Assignment a WHERE a.osceDay.id = " + osceDayId + " AND a.timeEnd = :timeend AND a.type = 0 ORDER BY a.timeStart";
    	 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
    	 query.setParameter("timeend", lunchBreakStartTime);
    	 
    	 if (query.getResultList().size() > 0)
    		 return query.getResultList().get(0);
    	 else
    		 return null;
     }
     
     public static Assignment findAssignmentByRotationAndOsceDay(Long osceDayId, int rotationNumber)
     {
    	 EntityManager em = entityManager();
    	 String sql = "SELECT a FROM Assignment a WHERE a.osceDay.id = " + osceDayId + " AND a.type = 0 AND a.rotationNumber = " + rotationNumber + " GROUP BY a.timeEnd ORDER BY a.timeEnd DESC";
    	 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
    	 
    	 if (query.getResultList().size() > 0)
    		 return query.getResultList().get(0);
    	 else
    		 return null;
     }
     
     public static List<Assignment> findExaminerByLunchBreakEnd(Long osceDayId, Date lunchBreakTime)
     {
    	 EntityManager em = entityManager();
    	 String sql = "SELECT a FROM Assignment a WHERE a.type = 2 AND a.osceDay.id = " + osceDayId + " AND a.timeStart > :timeend AND a.timeEnd < :timeend ORDER BY a.timeStart";
    	 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
    	 query.setParameter("timeend", lunchBreakTime);
    	 
    	 return query.getResultList();
     }
     
     //flag is used to know for up or down rotation.
     //1 : for up rotation, 2: for down rotation
     public static void updateOscePostRoom(Long osceDayId, Date startTime, Date endTime, Long firstSeqId, Long secondSeqId, int flag)
     {
    	 EntityManager em = entityManager();
    	 boolean chkBreakPost = findBreakPostInOsceSequence(secondSeqId);
    	 
    	 if (chkBreakPost == true)
    	 {
    		 if (flag == 1)
    		 {
    			 OsceSequence firstOsceSeq = OsceSequence.findOsceSequence(firstSeqId);
    			 OsceSequence secondOsceSeq = OsceSequence.findOsceSequence(secondSeqId);
    			 
    			 for (int i=0; i<secondOsceSeq.getCourses().size(); i++)
    			 {
    				 Course secondSeqCourse = secondOsceSeq.getCourses().get(i);
    				 Course firstSeqCourse = firstOsceSeq.getCourses().get(i);
    					
    				 String sql = "SELECT a FROM Assignment a WHERE a.oscePostRoom.course.id = " + secondSeqCourse.getId() + " AND a.osceDay.id = " + osceDayId + " AND a.timeStart > :startTime AND a.timeEnd <= :endtime ORDER BY a.timeStart";
    	        	 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
    	        	 query.setParameter("startTime", startTime);
    	        	 query.setParameter("endtime", endTime);
    	        	 
    	        	 List<Assignment> assList = query.getResultList();
    	        	 
    	        	 for (Assignment ass : assList)
    	        	 {
    	        		 if (ass.getOscePostRoom() != null)
    	        		 {
    	        			OscePostRoom oscePostRoom = ass.getOscePostRoom();
    	        			String oprSql = "";
    	        			
    	        			if (oscePostRoom.getRoom() == null)
    	        				oprSql = "SELECT opr FROM OscePostRoom opr WHERE opr.room IS NULL AND opr.course.id = " + firstSeqCourse.getId() + " AND opr.oscePost.oscePostBlueprint.id = " + oscePostRoom.getOscePost().getOscePostBlueprint().getId() + " AND opr.oscePost.osceSequence.id = " + firstSeqId;
    	        			else
    	        				oprSql = "SELECT opr FROM OscePostRoom opr WHERE opr.room.id = " + oscePostRoom.getRoom().getId() + " AND opr.oscePost.oscePostBlueprint.id = " + oscePostRoom.getOscePost().getOscePostBlueprint().getId() + " AND opr.oscePost.osceSequence.id = " + firstSeqId;
    	        			
    	        			TypedQuery<OscePostRoom> oprQuery = em.createQuery(oprSql, OscePostRoom.class);
    	        			
    	        			if (oprQuery.getResultList().size() > 0)
    	        			{
    	        				ass.setOscePostRoom(oprQuery.getResultList().get(0));
    	        				ass.persist();
    	        			}
    	        		 }
    	        	 }
    			 }
    		 }
    		 else if (flag == 2)
    		 {
    			 OsceSequence firstOsceSeq = OsceSequence.findOsceSequence(firstSeqId);
    			 OsceSequence secondOsceSeq = OsceSequence.findOsceSequence(secondSeqId);
    			 
    			 for (int i=0; i<firstOsceSeq.getCourses().size(); i++)
    			 {
    				 Course secondSeqCourse = secondOsceSeq.getCourses().get(i);
    				 Course firstSeqCourse = firstOsceSeq.getCourses().get(i);
    					
    				 String sql = "SELECT a FROM Assignment a WHERE a.oscePostRoom.course.id = " + firstSeqCourse.getId() + " AND a.osceDay.id = " + osceDayId + " AND a.timeStart > :startTime AND a.timeEnd <= :endtime ORDER BY a.timeStart";
    	        	 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
    	        	 query.setParameter("startTime", startTime);
    	        	 query.setParameter("endtime", endTime);
    	        	 
    	        	 List<Assignment> assList = query.getResultList();
    	        	 
    	        	 for (Assignment ass : assList)
    	        	 {
    	        		 if (ass.getOscePostRoom() != null)
    	        		 {
    	        			OscePostRoom oscePostRoom = ass.getOscePostRoom();
    	        			String oprSql = "";
    	        			
    	        			if (oscePostRoom.getRoom() == null)
    	        				oprSql = "SELECT opr FROM OscePostRoom opr WHERE opr.room IS NULL AND opr.course.id = " + secondSeqCourse.getId() + " AND opr.oscePost.oscePostBlueprint.id = " + oscePostRoom.getOscePost().getOscePostBlueprint().getId() + " AND opr.oscePost.osceSequence.id = " + secondSeqId;
    	        			else
    	        				oprSql = "SELECT opr FROM OscePostRoom opr WHERE opr.room.id = " + oscePostRoom.getRoom().getId() + " AND opr.oscePost.oscePostBlueprint.id = " + oscePostRoom.getOscePost().getOscePostBlueprint().getId() + " AND opr.oscePost.osceSequence.id = " + secondSeqId;
    	        			
    	        			TypedQuery<OscePostRoom> oprQuery = em.createQuery(oprSql, OscePostRoom.class);
    	        			
    	        			if (oprQuery.getResultList().size() > 0)
    	        			{
    	        				ass.setOscePostRoom(oprQuery.getResultList().get(0));
    	        				ass.persist();
    	        			}
    	        		 }
    	        	 }
    			 }
    		 }
    	 }
    	 else if (chkBreakPost == false)
    	 {
    		 Long seqId = 0l;
    		 
    		 if (flag == 1)
    			 seqId = firstSeqId;
    		 else if (flag == 2)
    			 seqId = secondSeqId;
    		 
        	 String sql = "SELECT a FROM Assignment a WHERE a.osceDay.id = " + osceDayId + " AND a.timeStart > :startTime AND a.timeEnd <= :endtime ORDER BY a.timeStart";
        	 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
        	 query.setParameter("startTime", startTime);
        	 query.setParameter("endtime", endTime);
        	 
        	 List<Assignment> assList = query.getResultList();
        	 
        	 for (Assignment ass : assList)
        	 {
        		 if (ass.getOscePostRoom() != null)
        		 {
        			OscePostRoom oscePostRoom = ass.getOscePostRoom();
        			String oprSql = "";
        			
        			if (oscePostRoom.getRoom() == null)
        				oprSql = "SELECT opr FROM OscePostRoom opr WHERE opr.room IS NULL AND opr.oscePost.oscePostBlueprint.id = " + oscePostRoom.getOscePost().getOscePostBlueprint().getId() + " AND opr.oscePost.osceSequence.id = " + seqId;
        			else
        				oprSql = "SELECT opr FROM OscePostRoom opr WHERE opr.room.id = " + oscePostRoom.getRoom().getId() + " AND opr.oscePost.oscePostBlueprint.id = " + oscePostRoom.getOscePost().getOscePostBlueprint().getId() + " AND opr.oscePost.osceSequence.id = " + seqId;
        			
        			TypedQuery<OscePostRoom> oprQuery = em.createQuery(oprSql, OscePostRoom.class);
        			
        			if (oprQuery.getResultList().size() > 0)
        			{
        				ass.setOscePostRoom(oprQuery.getResultList().get(0));
        				ass.persist();
        			}
        		 }
        	 }
    	 }
     }
     
     public static boolean findBreakPostInOsceSequence(Long osceSeqId)
     {
    	 EntityManager em = entityManager();
    	 String sql = "SELECT op FROM OscePost op WHERE op.osceSequence.id = " + osceSeqId + " AND op.standardizedRole IS NULL";
    	 TypedQuery<OscePost> query = em.createQuery(sql, OscePost.class);
    	 if (query.getResultList().size() > 0)
    		 return true;
    	 else
    		 return false;
     }
     
     public static void updateExaminer(OsceDay osceDay, OsceSequence firstSeq, OsceSequence secondSeq, int rotationNumber, Long newLunchValue)
     {
    	 EntityManager em = entityManager();
    	 for (int i=0; i<secondSeq.getCourses().size(); i++)
    	 {
    		 Course secondSeqCourse = secondSeq.getCourses().get(i);
    		 Course firstSeqCourse = firstSeq.getCourses().get(i);
    		 
    		 List<OscePost> oscePostList = secondSeq.getOscePosts();
    		 List<OscePost> otherOscePostList = firstSeq.getOscePosts();
		 
			 for (int j=0; j<oscePostList.size(); j++)
			 {
				 OscePost oscePost = oscePostList.get(j);
				 OscePost otherOscePost = otherOscePostList.get(j);				 
				 
				 List<Assignment> tempAssList = maxEndTimeSlot(rotationNumber, osceDay.getId(), oscePost.getId(), secondSeqCourse.getId());
				 
				 Date startTimeSlot = new Date();
				 Date endTimeSlot = new Date();
				 if (tempAssList != null && tempAssList.size() > 0)
				 {
					 startTimeSlot = tempAssList.get(0).getTimeStart();
					 endTimeSlot = tempAssList.get((tempAssList.size() - 1)).getTimeEnd();
				 }
				 
				 String sql = "SELECT a FROM Assignment AS a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + firstSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + otherOscePost.getId() + " AND a.osceDay = " + osceDay.getId() + " AND a.timeStart = :timeStart AND a.timeEnd = :timeEnd";
				 TypedQuery<Assignment> q = em.createQuery(sql, Assignment.class);	
				 q.setParameter("timeStart", startTimeSlot);
				 q.setParameter("timeEnd", endTimeSlot);

    		/* String sql = "SELECT a FROM Assignment a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + firstSeqCourse.getId() + " AND a.osceDay.id = " + osceDay.getId() + " AND a.timeStart = :timeStart AND a.timeEnd = :timeEnd ORDER BY a.timeStart";
    		 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
        	 query.setParameter("timeStart", timeStart);
        	 query.setParameter("timeEnd", timeEnd);*/
        	 
	        	 if (q.getResultList().size() > 0)
	        	 {
	        		 for (Assignment ass : q.getResultList())
	        		 {
	        			 if (ass.getOscePostRoom() != null)
	        			 {
	        				 
	        				 List<Assignment> preRorAssList = maxEndTimeSlot((rotationNumber - 1), osceDay.getId(), oscePost.getId(), secondSeqCourse.getId());
         					
        					 Date tmpEndTimeSlot = new Date();
           					 if (preRorAssList != null && preRorAssList.size() > 0)
           					 {
           						 tmpEndTimeSlot = preRorAssList.get((preRorAssList.size() - 1)).getTimeEnd();
           					 }
           					 
           					 String sql2 = "";
           					 if (ass.getExaminer() != null)
           						 sql2 = "SELECT a FROM Assignment AS a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + firstSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + otherOscePost.getId() + " AND a.osceDay = " + osceDay.getId() + " AND a.timeEnd = :timeEnd AND a.examiner.id = " + ass.getExaminer().getId();
           					 else
           						 sql2 = "SELECT a FROM Assignment AS a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + firstSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + otherOscePost.getId() + " AND a.osceDay = " + osceDay.getId() + " AND a.timeEnd = :timeEnd";
       						
           					 
           					// String sql2 = "SELECT a FROM Assignment AS a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + firstSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + otherOscePost.getId() + " AND a.osceDay = " + osceDay.getId() + " AND a.timeEnd = :timeEnd";
           					 TypedQuery<Assignment> q2 = em.createQuery(sql2, Assignment.class);	
           					 q2.setParameter("timeEnd", tmpEndTimeSlot);
           					 
           					// boolean exminerUpdate = false;
           					 
           					 if (q2.getResultList().size() > 0)
           					 {
           						 for (Assignment tempass : q2.getResultList())
           						 {
           							tempass.setTimeEnd(endTimeSlot);
               						tempass.persist();
           						 }
           						 
           						 ass.remove();
           						
           						 updateSequenceNumberOfExaminer(osceDay.getId(), endTimeSlot, -1, secondSeqCourse.getId(), oscePost.getId());
           					 }
           					 else
           					 {
           						 OscePostRoom oscePostRoom = ass.getOscePostRoom();
	   	        				 OscePostRoom opr = findOscePostRoomByCourseAndOscePostBluePrint(firstSeq.getId(), oscePostRoom, firstSeqCourse.getId());
	   	        				 Integer maxSeqNo = getMaxSequenceNumberOfExaminer(osceDay.getId(), firstSeqCourse.getId(), oscePostRoom.getOscePost().getOscePostBlueprint().getId());
	   	        				 
	   	        				 if (opr != null && maxSeqNo > 0)
	   	        				 {	
	   	        					 ass.setOscePostRoom(opr);
	   	        					 ass.setSequenceNumber((maxSeqNo + 1));
	   	        					 ass.persist();
	   	        				 }
           					 }
           					 
	        			 }
	        		 }
	        	 }
	        	 else
	        	 { 
					 String sql1 = "SELECT a FROM Assignment a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + secondSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + oscePost.getId() + " AND a.osceDay.id = " + osceDay.getId() + " AND a.timeStart >= :timeStart AND a.timeEnd > :timeEnd ORDER BY a.timeStart";
	            	 TypedQuery<Assignment> query1 = em.createQuery(sql1, Assignment.class);
	            	 query1.setParameter("timeStart", startTimeSlot);
	            	 query1.setParameter("timeEnd", endTimeSlot);
	            	 
	            	 if (query1.getResultList().size() > 0)
	            	 { 
	            		 //for (Assignment ass : query1.getResultList())
	            		 {
	            			Assignment ass = query1.getResultList().get(0);
	            			
	            			if (ass.getOscePostRoom() != null)
	            			{
	            				OscePostRoom oscePostRoom = ass.getOscePostRoom();
	            				Integer maxSeqNo = getMaxSequenceNumberOfExaminer(osceDay.getId(), firstSeqCourse.getId(), oscePostRoom.getOscePost().getOscePostBlueprint().getId());
	            				OscePostRoom opr = findOscePostRoomByCourseAndOscePostBluePrint(firstSeq.getId(), oscePostRoom, firstSeqCourse.getId());
	            				
	            				if (maxSeqNo == null)
	            					maxSeqNo = 0;
	            				
	            				if (opr != null)
	            				{
	            					 List<Assignment> preRorAssList = maxEndTimeSlot((rotationNumber - 1), osceDay.getId(), oscePost.getId(), secondSeqCourse.getId());
	            					
	            					 Date tmpEndTimeSlot = new Date();
		           					 if (preRorAssList != null && preRorAssList.size() > 0)
		           					 {
		           						 tmpEndTimeSlot = preRorAssList.get((preRorAssList.size() - 1)).getTimeEnd();
		           					 }
		           					 
		           					 String sql2 = "";
		           					 if (ass.getExaminer() != null)
		           						 sql2 = "SELECT a FROM Assignment AS a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + firstSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + otherOscePost.getId() + " AND a.osceDay = " + osceDay.getId() + " AND a.timeEnd = :timeEnd AND a.examiner.id = " + ass.getExaminer().getId();
		           					 else
		           						 sql2 = "SELECT a FROM Assignment AS a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + firstSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + otherOscePost.getId() + " AND a.osceDay = " + osceDay.getId() + " AND a.timeEnd = :timeEnd";
		           					 
		           					 //String sql2 = "SELECT a FROM Assignment AS a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + firstSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + otherOscePost.getId() + " AND a.osceDay = " + osceDay.getId() + " AND a.timeEnd = :timeEnd";
		           					 TypedQuery<Assignment> q2 = em.createQuery(sql2, Assignment.class);	
		           					 q2.setParameter("timeEnd", tmpEndTimeSlot);
		           					 
		           					 //boolean examinerUpdate = false;
		           					 
		           					 if (q2.getResultList().size() > 0)
		           					 {
		           						 for (Assignment tempass : q2.getResultList())
		           						 {
		           							 tempass.setTimeEnd(endTimeSlot);
		           							 tempass.persist();		 
		           							 
		           							 List<Assignment> tempAssList1 = maxEndTimeSlot((rotationNumber+1), osceDay.getId(), oscePost.getId(), secondSeqCourse.getId());
		         							 
		           							 Date startTimeSlot1 = new Date();
		           							 Date endTimeSlot1 = new Date();
		           							 if (tempAssList1 != null && tempAssList1.size() > 0)
		           							 {
		           								 startTimeSlot1 = tempAssList1.get(0).getTimeStart();
		           								 endTimeSlot1 = tempAssList1.get((tempAssList1.size() - 1)).getTimeEnd();
		           							 }	
		           							 
		           							 ass.setTimeStart(startTimeSlot1);
		           							 ass.persist();
		           						 }
		           					 }
		           					 else
		           					 {
		           						Assignment newAss = new Assignment();
		            					newAss.setSequenceNumber((maxSeqNo + 1));
		                				newAss.setTimeStart(startTimeSlot);
		                				newAss.setTimeEnd(endTimeSlot);
		                				newAss.setType(AssignmentTypes.EXAMINER);
		                				newAss.setExaminer(ass.getExaminer());
		                				newAss.setOsceDay(osceDay);
		                				newAss.setOscePostRoom(opr);
		                				newAss.persist();
		                				
		                				//create new examiner slot in first sequence because there is no examiner is there
		                				{
		                					 String preRotSql = "SELECT a FROM Assignment AS a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + firstSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + otherOscePost.getId() + " AND a.osceDay = " + osceDay.getId() + " AND a.timeEnd = :timeEnd";
				           					 TypedQuery<Assignment> preRotQue = em.createQuery(preRotSql, Assignment.class);	
				           					 preRotQue.setParameter("timeEnd", tmpEndTimeSlot);
				           					 
				           					 if (preRotQue.getResultList().size() == 0)
				           					 {
				           						Assignment tmpAss = findFirstLastAssignmentByOsceDay(osceDay.getId(), 1);
				           						Assignment preRotAss = new Assignment();
				            					preRotAss.setSequenceNumber(1);
				                				preRotAss.setTimeStart(tmpAss.getTimeStart());
				                				preRotAss.setTimeEnd(tmpEndTimeSlot);
				                				preRotAss.setType(AssignmentTypes.EXAMINER);
				                				preRotAss.setOsceDay(osceDay);
				                				preRotAss.setOscePostRoom(opr);
				                				preRotAss.persist();
				                				
				                				newAss.setSequenceNumber(2);
				                				newAss.persist();
				           					 }
		                				}
		                				
		                				 Date newTimeStart = dateAddMin(endTimeSlot, (newLunchValue + osceDay.getOsce().getMiddleBreak().intValue()));
			           					 ass.setTimeStart(newTimeStart);
			           					 ass.persist();
		           					 }
	            				}
	            			}
	            		 }
	            	 }
	            	 else
	            	 {
	            		 //update examiner end time because there is no examiner in second sequence;
	            		 List<Assignment> preRorAssList = maxEndTimeSlot((rotationNumber - 1), osceDay.getId(), oscePost.getId(), secondSeqCourse.getId());
     					
    					 Date tmpEndTimeSlot = new Date();
    					 Date tmpStartTimeSlot = new Date();
       					 if (preRorAssList != null && preRorAssList.size() > 0)
       					 {
       						 tmpEndTimeSlot = preRorAssList.get((preRorAssList.size() - 1)).getTimeEnd();
       						 tmpStartTimeSlot = preRorAssList.get(0).getTimeStart();
       					 }
       					 
       					 String sql2 = "SELECT a FROM Assignment AS a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + firstSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + otherOscePost.getId() + " AND a.osceDay = " + osceDay.getId() + " AND a.timeEnd = :timeEnd";
       					 TypedQuery<Assignment> q2 = em.createQuery(sql2, Assignment.class);	
       					 q2.setParameter("timeEnd", tmpEndTimeSlot);
       					 
       					 if (q2.getResultList().size() > 0)
       					 {
       						 for (Assignment tempass : q2.getResultList())
       						 {
       							 tempass.setTimeEnd(endTimeSlot);
       							 tempass.persist();		           									           							 
       						 }
       					 }	 
	            	 }
					
	        	 }
	        	 
	        	 //updateSequenceNumberOfExaminer(osceDay.getId(), timeEnd, -1);
			 }
    	 }
    	 
    	 
     }
     
     public static Integer getMaxSequenceNumberOfExaminer(Long osceDayId, Long courseId, Long oscePostBluePrintId)
     {
    	 EntityManager em = entityManager();
    	 String sql = "SELECT MAX(a.sequenceNumber) FROM Assignment a WHERE a.type = 2 AND a.osceDay.id = " + osceDayId + " AND a.oscePostRoom.course.id = " + courseId + " AND a.oscePostRoom.oscePost.oscePostBlueprint.id = " + oscePostBluePrintId + " ORDER BY a.timeStart";
    	 TypedQuery<Integer> query = em.createQuery(sql, Integer.class);
    	 
    	 if (query.getResultList().size() > 0)
    		 return query.getResultList().get(0);
    	 else
    		 return 0;
     }
     
     public static OscePostRoom findOscePostRoomByCourseAndOscePostBluePrint(Long osceSeqId, OscePostRoom oscePostRoom, Long courseId)
     {
    	 EntityManager em = entityManager();
    	 String oprSql = "";
			
    	 if (oscePostRoom.getRoom() == null)
    		 oprSql = "SELECT opr FROM OscePostRoom opr WHERE opr.room IS NULL AND opr.course.id = " + courseId + " AND opr.oscePost.oscePostBlueprint.id = " + oscePostRoom.getOscePost().getOscePostBlueprint().getId() + " AND opr.oscePost.osceSequence.id = " + osceSeqId;
    	 else
    		 oprSql = "SELECT opr FROM OscePostRoom opr WHERE opr.room.id = " + oscePostRoom.getRoom().getId() + " AND opr.oscePost.oscePostBlueprint.id = " + oscePostRoom.getOscePost().getOscePostBlueprint().getId() + " AND opr.oscePost.osceSequence.id = " + osceSeqId;
    	 
    	 TypedQuery<OscePostRoom> oprQuery = em.createQuery(oprSql, OscePostRoom.class);
    	 
    	 if (oprQuery.getResultList().size() > 0)
    		 return oprQuery.getResultList().get(0);
    	 else
    		 return null;
    }
     
     public static void updateExaminerForDownRotation(OsceDay osceDay, OsceSequence firstSeq, OsceSequence secondSeq, int rotationNumber, Long newLunchValue)
     {
    	 EntityManager em = entityManager();
    	 for (int i=0; i<firstSeq.getCourses().size(); i++)
    	 {
    		 Course secondSeqCourse = secondSeq.getCourses().get(i);
    		 Course firstSeqCourse = firstSeq.getCourses().get(i);
    		 
    		 List<OscePost> oscePostList = firstSeq.getOscePosts();
    		 List<OscePost> otherOscePostList = secondSeq.getOscePosts();
    		 
			 for (int j=0; j<oscePostList.size(); j++)
			 {
				 OscePost oscePost = oscePostList.get(j);
				 OscePost otherOscePost = otherOscePostList.get(j);				 
				 
				 List<Assignment> tempAssList = maxEndTimeSlot(rotationNumber, osceDay.getId(), oscePost.getId(), firstSeqCourse.getId());
				 
				 Date startTimeSlot = new Date();
				 Date endTimeSlot = new Date();
				 if (tempAssList != null && tempAssList.size() > 0)
				 {
					 startTimeSlot = tempAssList.get(0).getTimeStart();
					 endTimeSlot = tempAssList.get((tempAssList.size() - 1)).getTimeEnd();
				 }
				 
				 String sql = "SELECT a FROM Assignment AS a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + secondSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + otherOscePost.getId() + " AND a.osceDay = " + osceDay.getId() + " AND a.timeStart = :timeStart AND a.timeEnd = :timeEnd";
				 TypedQuery<Assignment> q = em.createQuery(sql, Assignment.class);	
				 q.setParameter("timeStart", startTimeSlot);
				 q.setParameter("timeEnd", endTimeSlot);
    	 	 
	        	 if (q.getResultList().size() > 0)
	        	 {
	        		 for (Assignment ass : q.getResultList())
	        		 {
	        			 if (ass.getOscePostRoom() != null)
	        			 {
	        				 List<Assignment> nextRotAssList = maxEndTimeSlot((rotationNumber+1), osceDay.getId(), otherOscePost.getId(), secondSeqCourse.getId());
        					 
        					 Date nextRotStartTimeSlot = new Date();
        					 if (nextRotAssList != null && nextRotAssList.size() > 0)
        					 {
        						 nextRotStartTimeSlot = nextRotAssList.get(0).getTimeStart();	            						 
        					 }
        					 
        					 String nextRotql = "";
        					 if (ass.getExaminer() != null)
        						 nextRotql = "SELECT a FROM Assignment AS a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + secondSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + otherOscePost.getId() + " AND a.osceDay = " + osceDay.getId() + " AND a.timeStart = :timeStart AND a.examiner.id = " + ass.getExaminer().getId();
        					 else
        						 nextRotql = "SELECT a FROM Assignment AS a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + secondSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + otherOscePost.getId() + " AND a.osceDay = " + osceDay.getId() + " AND a.timeStart = :timeStart";
    						 
        					 //String nextRotql = "SELECT a FROM Assignment AS a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + secondSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + otherOscePost.getId() + " AND a.osceDay = " + osceDay.getId() + " AND a.timeStart = :timeStart";
        					 TypedQuery<Assignment> q2 = em.createQuery(nextRotql, Assignment.class);	
        					 q2.setParameter("timeStart", nextRotStartTimeSlot);	  
        					 //boolean examinerUpdate = false;
        					 
        					 if (q2.getResultList().size() > 0)
        					 {
        						 for (Assignment assignment : q2.getResultList())
        						 {
        							  assignment.setTimeStart(startTimeSlot);
        							  assignment.persist();        
        							  
        							  List<Assignment> tempAssList1 = maxEndTimeSlot((rotationNumber-1), osceDay.getId(), oscePost.getId(), firstSeqCourse.getId());
         							 
        							  Date startTimeSlot1 = new Date();
        							  Date endTimeSlot1 = new Date();
        							  if (tempAssList1 != null && tempAssList1.size() > 0)
        							  {
        								  startTimeSlot1 = tempAssList1.get(0).getTimeStart();
        								  endTimeSlot1 = tempAssList1.get((tempAssList1.size() - 1)).getTimeEnd();
        							  }	
         							 
        							  ass.setTimeEnd(endTimeSlot1);
        							  ass.persist();
        							  
        						 }
        						 ass.remove();
        					 }
        					 else
        					 {
        						 OscePostRoom oscePostRoom = ass.getOscePostRoom();
    	        				 OscePostRoom opr = findOscePostRoomByCourseAndOscePostBluePrint(secondSeq.getId(), oscePostRoom, secondSeqCourse.getId());
    	        				 
    	        				 ass.setSequenceNumber(1);
    	        				 ass.setOscePostRoom(opr);
    	        				 ass.persist();
        					 }
        					 
	        			 }
	        		 }
	        	 }
	        	 else
	        	 {
	        		 String sql1 = "SELECT a FROM Assignment a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + firstSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + oscePost.getId() + " AND a.osceDay.id = " + osceDay.getId() + " AND a.timeStart < :timeStart AND a.timeEnd <= :timeEnd ORDER BY a.timeEnd DESC";
	        		 TypedQuery<Assignment> query1 = em.createQuery(sql1, Assignment.class);
	            	 query1.setParameter("timeStart", startTimeSlot);
	            	 query1.setParameter("timeEnd", endTimeSlot);
	            	 
	            	 if (query1.getResultList().size() > 0)
	            	 { 
	            		 //for (Assignment ass : query1.getResultList())
	            		 {
	            			Assignment ass = query1.getResultList().get(0);
	            			if (ass.getOscePostRoom() != null)
	            			{
	            				OscePostRoom oscePostRoom = ass.getOscePostRoom();
	            				OscePostRoom opr = findOscePostRoomByCourseAndOscePostBluePrint(secondSeq.getId(), oscePostRoom, secondSeqCourse.getId());
	            				
	            				if (opr != null)
	            				{
	            					/*int middleBreak = osceDay.getOsce().getMiddleBreak().intValue();
	            					Date newTimeEnd = dateAddMin(startTimeSlot, -middleBreak);*/
	            					Date newEndTime = dateAddMin(startTimeSlot, -(newLunchValue.intValue() + osceDay.getOsce().getMiddleBreak().intValue()));
	            					ass.setTimeEnd(newEndTime);
	            					ass.persist();
	            					
	            					/*Date newTimeStart = dateAddMin(startTimeSlot, -osceDay.getOsce().getMiddleBreak().intValue());
	            					newTimeStart = dateAddMin(newTimeStart, osceDay.getOsce().getLunchBreak().intValue());*/
	            					
	            					 List<Assignment> nextRotAssList = maxEndTimeSlot((rotationNumber+1), osceDay.getId(), otherOscePost.getId(), secondSeqCourse.getId());
	            					 
	            					 Date nextRotStartTimeSlot = new Date();
	            					 Date nextRotEndTimeSlot = new Date();
	            					 if (nextRotAssList != null && nextRotAssList.size() > 0)
	            					 {
	            						 nextRotStartTimeSlot = nextRotAssList.get(0).getTimeStart();	           
	            						 nextRotEndTimeSlot = nextRotAssList.get((nextRotAssList.size() - 1)).getTimeEnd();
	            					 }
	            					 
	            					 String nextRotql = "";
	            					 if (ass.getExaminer() != null)
	            						 nextRotql = "SELECT a FROM Assignment AS a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + secondSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + otherOscePost.getId() + " AND a.osceDay = " + osceDay.getId() + " AND a.timeStart = :timeStart AND a.examiner.id = " + ass.getExaminer().getId();
	            					 else
	            						 nextRotql = "SELECT a FROM Assignment AS a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + secondSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + otherOscePost.getId() + " AND a.osceDay = " + osceDay.getId() + " AND a.timeStart = :timeStart";
	            						 
	            					 //String nextRotql = "SELECT a FROM Assignment AS a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + secondSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + otherOscePost.getId() + " AND a.osceDay = " + osceDay.getId() + " AND a.timeStart = :timeStart";
	            					 TypedQuery<Assignment> q2 = em.createQuery(nextRotql, Assignment.class);	
	            					 q2.setParameter("timeStart", nextRotStartTimeSlot);	  
	            					 
	            					 //boolean examinerUpdate = false;
	            					 
	            					 if (q2.getResultList().size() > 0)
	            					 {
	            						 for (Assignment assignment : q2.getResultList())
	            						 {
	            							 assignment.setTimeStart(startTimeSlot);
	            							 assignment.persist();	
	            							 
	            							 List<Assignment> tempAssList1 = maxEndTimeSlot((rotationNumber-1), osceDay.getId(), oscePost.getId(), firstSeqCourse.getId());
	            							 
	            							 Date startTimeSlot1 = new Date();
	            							 Date endTimeSlot1 = new Date();
	            							 if (tempAssList1 != null && tempAssList1.size() > 0)
	            							 {
	            								 startTimeSlot1 = tempAssList1.get(0).getTimeStart();
	            								 endTimeSlot1 = tempAssList1.get((tempAssList1.size() - 1)).getTimeEnd();
	            							 }
	            							 
	            							 ass.setTimeEnd(endTimeSlot1);
	            							 ass.persist();
	            						 }
	            					 }
	            					 else
	            					 {
	            						 Assignment newAss = new Assignment();
		            					 newAss.setSequenceNumber(1);
		            					 newAss.setTimeStart(startTimeSlot);
		            					 newAss.setTimeEnd(endTimeSlot);
		            					 newAss.setType(AssignmentTypes.EXAMINER);
		            					 newAss.setExaminer(ass.getExaminer());
		            					 newAss.setOsceDay(osceDay);
		            					 newAss.setOscePostRoom(opr);
		            					 newAss.persist();
		            					 
		            					 //check and create examiner slot with empty examiner
		            					 {
		            						 String newExSql = "SELECT a FROM Assignment AS a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + secondSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + otherOscePost.getId() + " AND a.osceDay = " + osceDay.getId() + " AND a.timeStart >= :timeStart";
			            					 //String nextRotql = "SELECT a FROM Assignment AS a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + secondSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + otherOscePost.getId() + " AND a.osceDay = " + osceDay.getId() + " AND a.timeStart = :timeStart";
			            					 TypedQuery<Assignment> newExQ = em.createQuery(newExSql, Assignment.class);	
			            					 newExQ.setParameter("timeStart", nextRotStartTimeSlot);
			            					 
			            					 if (newExQ.getResultList().size() == 0)
			            					 {
			            						 Assignment lastAss = findFirstLastAssignmentByOsceDay(osceDay.getId(), 2);
			            						 Assignment newEmptyAss = new Assignment();
			            						 newEmptyAss.setSequenceNumber(1);
			            						 newEmptyAss.setTimeStart(nextRotStartTimeSlot);
			            						 newEmptyAss.setTimeEnd(lastAss.getTimeEnd());
			            						 newEmptyAss.setType(AssignmentTypes.EXAMINER);
			            						 newEmptyAss.setOsceDay(osceDay);
				            					 newEmptyAss.setOscePostRoom(opr);
				            					 newEmptyAss.persist();
			            					 }
		            					 }
		            					 
		            					 updateSequenceNumberOfExaminer(osceDay.getId(), endTimeSlot, +1, secondSeqCourse.getId(), otherOscePost.getId());
	            					 }
	            					 
	            					 
	            				}        				
	            			}
	            		 }
	            	 }
	            	 else
	            	 {
	            		 //update start time of examiner because in first sequence there is no examiner.
	            		 List<Assignment> nextRotAssList = maxEndTimeSlot((rotationNumber+1), osceDay.getId(), otherOscePost.getId(), secondSeqCourse.getId());
    					 
    					 Date nextRotStartTimeSlot = new Date();
    					 if (nextRotAssList != null && nextRotAssList.size() > 0)
    					 {
    						 nextRotStartTimeSlot = nextRotAssList.get(0).getTimeStart();	            						 
    					 }
    					 
    					 String nextRotql = "SELECT a FROM Assignment AS a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + secondSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + otherOscePost.getId() + " AND a.osceDay = " + osceDay.getId() + " AND a.timeStart = :timeStart";
    					 //String nextRotql = "SELECT a FROM Assignment AS a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + secondSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + otherOscePost.getId() + " AND a.osceDay = " + osceDay.getId() + " AND a.timeStart = :timeStart";
    					 TypedQuery<Assignment> q2 = em.createQuery(nextRotql, Assignment.class);	
    					 q2.setParameter("timeStart", nextRotStartTimeSlot);
    					 
    					 if (q2.getResultList().size() > 0)
    					 {
    						 for (Assignment assignment : q2.getResultList())
    						 {
    							  assignment.setTimeStart(startTimeSlot);
    							  assignment.persist();        
    							  
    						 }
    					 }
	            	 }
	        	 }
			 }
    	 }
    	 
    	 //updateSequenceNumberOfExaminer(osceDay.getId(), timeEnd, +1);
     }
     
     public static void updateSequenceNumberOfExaminer(Long osceDayID, Date timeStart, int val, Long courseId, Long oscePostId)
     {
    	 EntityManager em = entityManager();
    	 String sql = "SELECT a FROM Assignment a WHERE a.type = 2 AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + courseId + " AND a.oscePostRoom.oscePost.id = " + oscePostId + " AND a.osceDay.id = " + osceDayID + " AND a.timeStart >= :timeStart ORDER BY a.timeStart";
    	 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
    	 query.setParameter("timeStart", timeStart);
    	 
    	 if (query.getResultList().size() > 0)
    	 {
    		 for (Assignment ass : query.getResultList())
    		 {
    			 if (val > 0)
    			 {
    				 ass.setSequenceNumber((ass.getSequenceNumber().intValue() + val));
    				 ass.persist();
    			 }
    			 else if (val < 0)
    			 {
    				 if (ass.getSequenceNumber().intValue() > 1)
    				 {
    					 ass.setSequenceNumber((ass.getSequenceNumber().intValue() + val));
    					 ass.persist();
    				 }
    			 }
    		 }
    	 }
     }
     
     public static void updateAssignemntByDiffForShiftLunchBreak(Long osceDayId, int diff, int rotationNumber, OsceSequence firstSeq, OsceSequence secondSeq)
     {
    	 EntityManager em = entityManager();
		 for (int i=0; i<secondSeq.getCourses().size(); i++)
		 {
			 Course secondSeqCourse = secondSeq.getCourses().get(i);
			 Course firstSeqCourse = firstSeq.getCourses().get(i);
			 
			 List<OscePost> oscePostList = secondSeq.getOscePosts();
			 
			 for (OscePost oscePost : oscePostList)
			 {
				 List<Assignment> tempAssList = maxEndTimeSlot(rotationNumber, osceDayId, oscePost.getId(), secondSeqCourse.getId());
				 
				 Date startTimeSlot = new Date();
				 Date endTimeSlot = new Date();
				 if (tempAssList != null && tempAssList.size() > 0)
				 {
					 startTimeSlot = tempAssList.get(0).getTimeStart();
					 endTimeSlot = tempAssList.get((tempAssList.size() - 1)).getTimeEnd();
				 }
				  
				 String sql = "SELECT a FROM Assignment AS a WHERE a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + secondSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + oscePost.getId() + " AND a.osceDay = " + osceDayId + " AND a.timeStart >= :timeStart AND a.timeEnd <= :timeEnd";
				 TypedQuery<Assignment> q = em.createQuery(sql, Assignment.class);	
				 q.setParameter("timeStart", startTimeSlot);
				 q.setParameter("timeEnd", endTimeSlot);
				 
				 List<Assignment> assList = q.getResultList();
	        	 
	        	 for (Assignment ass : assList)
	        	 {
			  		 Date timeStartDt = dateAddMin(ass.getTimeStart(), -diff);
			  		 Date timeEndDt = dateAddMin(ass.getTimeEnd(), -diff);
			  		/* if(ass.getTimeStart().before(endTimeSlot) && ass.getType().equals(AssignmentTypes.EXAMINER))
			  		 {
			  		
			  	 	 }
			    	 else
			  	  	    ass.setTimeStart(timeStartDt);*/
			  		 
			  		 ass.setTimeStart(timeStartDt);
			  		 ass.setTimeEnd(timeEndDt);
			  		
			  		 ass.persist();    	
	        		 
			  		 if (ass.getOscePostRoom() != null)
	        		 {
	        			OscePostRoom oscePostRoom = ass.getOscePostRoom();
	        			String oprSql = "";
	        			
	        			if (oscePostRoom.getRoom() == null)
	        				oprSql = "SELECT opr FROM OscePostRoom opr WHERE opr.room IS NULL AND opr.course.id = " + firstSeqCourse.getId() + " AND opr.oscePost.oscePostBlueprint.id = " + oscePostRoom.getOscePost().getOscePostBlueprint().getId() + " AND opr.oscePost.osceSequence.id = " + firstSeq.getId();
	        			else
	        				oprSql = "SELECT opr FROM OscePostRoom opr WHERE opr.room.id = " + oscePostRoom.getRoom().getId() + " AND opr.oscePost.oscePostBlueprint.id = " + oscePostRoom.getOscePost().getOscePostBlueprint().getId() + " AND opr.oscePost.osceSequence.id = " + firstSeq.getId();
	        			
	        			TypedQuery<OscePostRoom> oprQuery = em.createQuery(oprSql, OscePostRoom.class);
	        			
	        			if (oprQuery.getResultList().size() > 0)
	        			{
	        				ass.setOscePostRoom(oprQuery.getResultList().get(0));
	        				ass.persist();
	        			}
	        		 }
	        	 }
	        	 
	        	 String sql1 = "SELECT a FROM Assignment AS a WHERE a.oscePostRoom IS NULL AND a.osceDay = " + osceDayId + " AND a.timeStart >= :timeStart AND a.timeEnd <= :timeEnd";
				 TypedQuery<Assignment> q1 = em.createQuery(sql1, Assignment.class);	
				 q1.setParameter("timeStart", startTimeSlot);
				 q1.setParameter("timeEnd", endTimeSlot);
				 
				 List<Assignment> assList1 = q1.getResultList();
	        	 
	        	 for (Assignment ass : assList1)
	        	 {
			  		 Date timeStartDt = dateAddMin(ass.getTimeStart(), -diff);
			  		 Date timeEndDt = dateAddMin(ass.getTimeEnd(), -diff);
			  		/* if(ass.getTimeStart().before(endTimeSlot) && ass.getType().equals(AssignmentTypes.EXAMINER))
			  		 {
			  		
			  	 	 }
			    	 else
			  	  	    ass.setTimeStart(timeStartDt);*/
			  		 ass.setTimeStart(timeStartDt);			  		 
			  		 ass.setTimeEnd(timeEndDt);
			  		
			  		 ass.persist();    	
	        	 }				
			 }
		}    	
   	 
     	OsceDay osceDay=OsceDay.findOsceDay(osceDayId);
     	osceDay.setIsTimeSlotShifted(true);
     	osceDay.persist();
     }
     
     public static void updateAssignemntByDiffForShiftLunchBreakForDownRot(Long osceDayId, int diff, int rotationNumber, OsceSequence firstSeq, OsceSequence secondSeq)
     {
    	 EntityManager em = entityManager();
		 for (int i=0; i<firstSeq.getCourses().size(); i++)
		 {
			 Course firstSeqCourse = firstSeq.getCourses().get(i);
			 Course secondSeqCourse = secondSeq.getCourses().get(i);
			 
			 List<OscePost> oscePostList = firstSeq.getOscePosts();
			 
			 for (OscePost oscePost : oscePostList)
			 {
				 List<Assignment> tempAssList = maxEndTimeSlot(rotationNumber, osceDayId, oscePost.getId(), firstSeqCourse.getId());
				 
				 Date startTimeSlot = new Date();
				 Date endTimeSlot = new Date();
				 if (tempAssList != null && tempAssList.size() > 0)
				 {
					 startTimeSlot = tempAssList.get(0).getTimeStart();
					 endTimeSlot = tempAssList.get((tempAssList.size() - 1)).getTimeEnd();
				 }
				  
				 String sql = "SELECT a FROM Assignment AS a WHERE a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + firstSeqCourse.getId() + " AND a.oscePostRoom.oscePost.id = " + oscePost.getId() + " AND a.osceDay = " + osceDayId + " AND a.timeStart >= :timeStart AND a.timeEnd <= :timeEnd";
				 TypedQuery<Assignment> q = em.createQuery(sql, Assignment.class);	
				 q.setParameter("timeStart", startTimeSlot);
				 q.setParameter("timeEnd", endTimeSlot);
				 
				 List<Assignment> assList = q.getResultList();
	        	 
	        	 for (Assignment ass : assList)
	        	 {
			  		 Date timeStartDt = dateAddMin(ass.getTimeStart(), diff);
			  		 Date timeEndDt = dateAddMin(ass.getTimeEnd(), diff);
			  		/* if(ass.getTimeStart().before(endTimeSlot) && ass.getType().equals(AssignmentTypes.EXAMINER))
			  		 {
			  		
			  	 	 }
			    	 else
			  	  	    ass.setTimeStart(timeStartDt);*/
			  		 ass.setTimeStart(timeStartDt);			  		 
			  		 ass.setTimeEnd(timeEndDt);
			  		
			  		 ass.persist();    	
	        		 
			  		 if (ass.getOscePostRoom() != null)
	        		 {
	        			OscePostRoom oscePostRoom = ass.getOscePostRoom();
	        			String oprSql = "";
	        			
	        			if (oscePostRoom.getRoom() == null)
	        				oprSql = "SELECT opr FROM OscePostRoom opr WHERE opr.room IS NULL AND opr.course.id = " + secondSeqCourse.getId() + " AND opr.oscePost.oscePostBlueprint.id = " + oscePostRoom.getOscePost().getOscePostBlueprint().getId() + " AND opr.oscePost.osceSequence.id = " + secondSeq.getId();
	        			else
	        				oprSql = "SELECT opr FROM OscePostRoom opr WHERE opr.room.id = " + oscePostRoom.getRoom().getId() + " AND opr.oscePost.oscePostBlueprint.id = " + oscePostRoom.getOscePost().getOscePostBlueprint().getId() + " AND opr.oscePost.osceSequence.id = " + secondSeq.getId();
	        			
	        			TypedQuery<OscePostRoom> oprQuery = em.createQuery(oprSql, OscePostRoom.class);
	        			
	        			if (oprQuery.getResultList().size() > 0)
	        			{
	        				ass.setOscePostRoom(oprQuery.getResultList().get(0));
	        				ass.persist();
	        			}
	        		 }
	        	 }
	        	 
	        	 String sql1 = "SELECT a FROM Assignment AS a WHERE a.oscePostRoom IS NULL AND a.osceDay = " + osceDayId + " AND a.timeStart >= :timeStart AND a.timeEnd <= :timeEnd";
				 TypedQuery<Assignment> q1 = em.createQuery(sql1, Assignment.class);	
				 q1.setParameter("timeStart", startTimeSlot);
				 q1.setParameter("timeEnd", endTimeSlot);
				 
				 List<Assignment> assList1 = q1.getResultList();
	        	 
	        	 for (Assignment ass : assList1)
	        	 {
			  		 Date timeStartDt = dateAddMin(ass.getTimeStart(), diff);
			  		 Date timeEndDt = dateAddMin(ass.getTimeEnd(), diff);
			  		/* if(ass.getTimeStart().before(endTimeSlot) && ass.getType().equals(AssignmentTypes.EXAMINER))
			  		 {
			  		
			  	 	 }
			    	 else
			  	  	    ass.setTimeStart(timeStartDt);*/
			  		 ass.setTimeStart(timeStartDt);			  		 
			  		 ass.setTimeEnd(timeEndDt);
			  		
			  		 ass.persist();    	
	        	 }
			 }
		}    	
     	
	 
     	OsceDay osceDay=OsceDay.findOsceDay(osceDayId);
     	osceDay.setIsTimeSlotShifted(true);
     	osceDay.persist();
     }
     
     public static List<Assignment> maxEndTimeSlot(int rotationNumber, Long osceDayId, Long oscePostId, Long courseId)
     {
    	 EntityManager em = entityManager();
    	 //String sql = "SELECT a FROM Assignment AS a WHERE a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + courseId + " AND a.oscePostRoom.oscePost.id = " + oscePostId + " AND a.osceDay = " + osceDayId + " AND a.rotationNumber = " + rotationNumber + " GROUP BY a.rotationNumber";
    	 
    	// OscePost oscePost = OscePost.findOscePost(oscePostId);
    	// String queryString = "SELECT  a FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=0 and a.rotationNumber = " + rotationNumber + " and a.oscePostRoom in(select opr.id from OscePostRoom as opr where (opr.room in (select rm.room from OscePostRoom as rm where rm.oscePost = " + oscePostId +  " and rm.course= " + courseId + " and rm.version<999 ) or (opr.room is null  and opr.oscePost.id="+oscePostId+" and opr.course=" + courseId + " and opr.oscePost.oscePostBlueprint.postType = " + OscePost.findOscePost(oscePostId).getOscePostBlueprint().getPostType().ordinal() + ")) and opr.course=" + courseId + ") order by a.timeStart asc";
    	 String queryString = "SELECT  a FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=0 and a.rotationNumber = " + rotationNumber + " and a.oscePostRoom in(select opr.id from OscePostRoom as opr where (opr.room in (select rm.room from OscePostRoom as rm where rm.oscePost = " + oscePostId +  " and rm.course= " + courseId + ") or (opr.room is null  and opr.oscePost.id="+oscePostId+" and opr.course=" + courseId + "))) order by a.timeStart asc";
    	 
    	 /*if (oscePost.getOscePostBlueprint().getPostType() == PostType.NORMAL || oscePost.getOscePostBlueprint().getPostType() == PostType.BREAK)
    	 {
    		 queryString = "SELECT  a FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=0 and a.rotationNumber = " + rotationNumber + " and a.oscePostRoom in(select opr.id from OscePostRoom as opr where (opr.room in (select rm.room from OscePostRoom as rm where rm.oscePost = " + oscePostId +  " and rm.course= " + courseId + ") or (opr.room is null  and opr.oscePost.id="+oscePostId+" and opr.course=" + courseId + "))) order by a.timeStart asc";
    	 }
    	 if (oscePost.getOscePostBlueprint().getPostType() == PostType.ANAMNESIS_THERAPY || oscePost.getOscePostBlueprint().getPostType() == PostType.PREPARATION)
    	 {
    		 if (oscePost.getOscePostBlueprint().getIsFirstPart())
    			 queryString = "SELECT  a FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=0 and a.rotationNumber = " + rotationNumber + " and a.oscePostRoom in(select opr.id from OscePostRoom as opr where (opr.room in (select rm.room from OscePostRoom as rm where rm.oscePost = " + oscePostId +  " and rm.course= " + courseId + " and rm.version<999 ) or (opr.room is null  and opr.oscePost.id="+oscePostId+" and opr.course=" + courseId + ")) and opr.course=" + courseId + ") order by a.timeStart asc";
    		 else
    			 queryString = "SELECT  a FROM Assignment as a where a.osceDay=" + osceDayId + "  and type=0 and a.rotationNumber = " + rotationNumber + " and a.oscePostRoom in(select opr.id from OscePostRoom as opr where (opr.room in (select rm.room from OscePostRoom as rm where rm.oscePost = " + oscePostId +  " and rm.course= " + courseId + " and rm.version != 999 ) or (opr.room is null  and opr.oscePost.id="+oscePostId+" and opr.course=" + courseId + "))) order by a.timeStart asc";
    	 }*/
    	 
    	 TypedQuery<Assignment> q = em.createQuery(queryString, Assignment.class);
		 
		 return q.getResultList();
     }
     
     public static Long findLunchBreakBetRot(Long osceDayId, int firstRotNo, int secondRotNo)
     {
    	 Long value = 0l;
    	 EntityManager em = entityManager();
    	 String sql = "SELECT a FROM Assignment a WHERE a.oscePostRoom IS NOT NULL AND a.osceDay.id = " + osceDayId + " AND a.rotationNumber = " + firstRotNo + " GROUP BY a.timeStart ORDER BY a.timeStart";
    	 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
    	 
    	 String sql1 = "SELECT a FROM Assignment a WHERE a.oscePostRoom IS NOT NULL AND a.osceDay.id = " + osceDayId + " AND a.rotationNumber = " + secondRotNo + " GROUP BY a.timeStart ORDER BY a.timeStart";
    	 TypedQuery<Assignment> query1 = em.createQuery(sql1, Assignment.class);
    	 
    	 if (query.getResultList() != null && query.getResultList().size() > 0 && query1.getResultList() != null && query1.getResultList().size() > 0)
    	 {
    		 Date dt1 = query.getResultList().get(query.getResultList().size() - 1).getTimeEnd();
    		 Date dt2 = query1.getResultList().get(0).getTimeStart();
    		 
    		 DateTime temp1 = new DateTime(dt1);
    		 DateTime temp2 = new DateTime(dt2);
    		 
    		 Duration duration = new Duration(temp1,temp2);
    		 value = Math.abs((duration.getStandardSeconds() / 60));
    	 }
    	 
    	 return value;
     }
     
     //flag for UP Rotation or Down Rotation, 1 : up rotation, 2 : down rotation
     public static void updateAssignemntByDiffForShiftLunchBreakForOsceDay(OsceDay osceDay, int diff, int rotationNumber, int flag)
     {

		 if (flag == 1)
			 diff = -diff;
		 
    	 EntityManager em = entityManager();
    	 
    	 List<OsceSequence> osceSeqList = osceDay.getOsceSequences();
    	 
    	 if (osceSeqList.size() == 1)
    	 {
    		 OsceSequence osceSeq = osceSeqList.get(0);
    		 
    		 for (Course course : osceSeq.getCourses())
    		 {
    			 for (OscePost oscePost : osceSeq.getOscePosts())
    			 {
    				 List<Assignment> tempAssList = maxEndTimeSlot(rotationNumber, osceDay.getId(), oscePost.getId(), course.getId());
    				 
    				 Date startTimeSlot = new Date();
    				 Date endTimeSlot = new Date();
    				 if (tempAssList != null && tempAssList.size() > 0)
    				 {
    					 startTimeSlot = tempAssList.get(0).getTimeStart();
    					 endTimeSlot = tempAssList.get((tempAssList.size() - 1)).getTimeEnd();
    				 }
    				 
    				 String sql = "SELECT a FROM Assignment AS a WHERE a.oscePostRoom IS NOT NULL AND a.oscePostRoom.course.id = " + course.getId() + " AND a.oscePostRoom.oscePost.id = " + oscePost.getId() + " AND a.osceDay = " + osceDay.getId() + " AND a.timeStart >= :timeStart AND a.timeEnd <= :timeEnd";
    				 TypedQuery<Assignment> q = em.createQuery(sql, Assignment.class);	
    				 q.setParameter("timeStart", startTimeSlot);
    				 q.setParameter("timeEnd", endTimeSlot);
    				 
    				 List<Assignment> assList = q.getResultList();
    	        	 
    	        	 for (Assignment ass : assList)
    	        	 {
    	        		 Date timeStartDt = dateAddMin(ass.getTimeStart(), diff);
    			  		 Date timeEndDt = dateAddMin(ass.getTimeEnd(), diff);
    			  		 if(ass.getTimeStart().before(endTimeSlot) && ass.getType().equals(AssignmentTypes.EXAMINER))
    			  		 {
    			  		
    			  	 	 }
    			    	 else
    			  	  	    ass.setTimeStart(timeStartDt);
    			  		 
    			  		 //ass.setTimeStart(timeStartDt);
    			  		 ass.setTimeEnd(timeEndDt);
    			  		
    			  		 ass.persist();    	    			  		
    	        	 }
    				 
    	        	 String sql1 = "SELECT a FROM Assignment AS a WHERE a.oscePostRoom IS NULL AND a.osceDay = " + osceDay.getId() + " AND a.timeStart >= :timeStart AND a.timeEnd <= :timeEnd";
    				 TypedQuery<Assignment> q1 = em.createQuery(sql1, Assignment.class);	
    				 q1.setParameter("timeStart", startTimeSlot);
    				 q1.setParameter("timeEnd", endTimeSlot);
    				 
    				 List<Assignment> assList1 = q1.getResultList();
    	        	 
    	        	 for (Assignment ass : assList1)
    	        	 { 
    			  		 Date timeStartDt = dateAddMin(ass.getTimeStart(), diff);
    			  		 Date timeEndDt = dateAddMin(ass.getTimeEnd(), diff);
    			  		/* if(ass.getTimeStart().before(endTimeSlot) && ass.getType().equals(AssignmentTypes.EXAMINER))
    			  		 {
    			  		
    			  	 	 }
    			    	 else
    			  	  	    ass.setTimeStart(timeStartDt);*/
    			  		 ass.setTimeStart(timeStartDt);			  		 
    			  		 ass.setTimeEnd(timeEndDt);
    			  		
    			  		 ass.persist();    	
    	        	 }	
    			 }
    		 }
    	 }
		
     }
     
     public static List<Assignment> findAssignmentByOsceDay(Long osceDayId)
     {
    	 EntityManager em = entityManager();
    	 String sql = "SELECT a FROM Assignment a WHERE a.type = 0 AND a.osceDay.id = " + osceDayId + " GROUP BY a.timeStart ORDER BY a.timeStart";
    	 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
    	 return query.getResultList();
     }
     
     public static Assignment findFirstLastAssignmentByOsceDay(Long osceDayId, int flag)
     {
    	 EntityManager em = entityManager();
    	 String sql = "";
    	 if (flag == 1)
    		 sql = "SELECT a FROM Assignment a WHERE a.osceDay.id = " + osceDayId + " AND a.type = 0 GROUP BY a.timeStart ORDER BY a.timeStart";
    	 else if (flag == 2)
    	 	sql = "SELECT a FROM Assignment a WHERE a.osceDay.id = " + osceDayId + " AND a.type = 0 GROUP BY a.timeEnd ORDER BY a.timeEnd DESC";
    	 
    	 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
    	 
    	 if (query.getResultList().size() > 0)
    		 return query.getResultList().get(0);
    	 else
    		 return null;
     }

	public static Boolean clearSPAssignmentByOsceDay(Long osceDayId) {
		try
		{
			EntityManager em = entityManager();
			String sql = "SELECT a FROM Assignment a WHERE a.type = 1 and a.osceDay.id = " + osceDayId;
			TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
			
			for (Assignment ass : query.getResultList())
			{
				ass.setPatientInRole(null);
				ass.persist();				
			}
			
			String breakSpSql = "SELECT a FROM Assignment a WHERE a.type = 1 AND a.oscePostRoom IS NULL AND a.patientInRole IS NULL AND a.osceDay.id = " + osceDayId;
			TypedQuery<Assignment> breakSpQuery = em.createQuery(breakSpSql, Assignment.class);
			
			for (Assignment ass : breakSpQuery.getResultList())
			{
				ass.remove();
			}
			
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public static void updateLunchBreak(Long osceDayId, int diff)
	{
		OsceDay osceDay = OsceDay.findOsceDay(osceDayId);
		if (osceDay.getLunchBreakStart() != null)
		{
			Date lunchBreak = dateAddMin(osceDay.getLunchBreakStart(), diff);
			osceDay.setLunchBreakStart(lunchBreak);
			osceDay.persist();
		}	
	}
	
	public static List<Date> findDistinctSPSlotPerSeq(OsceSequence seq)
	{
		 EntityManager em = entityManager();
    	 String sql = "SELECT distinct a.timeStart FROM Assignment a WHERE a.type = 1  and a.oscePostRoom.oscePost.osceSequence = :seq   ORDER BY a.timeStart";
    	 TypedQuery<Date> query = em.createQuery(sql, Date.class);
    	 query.setParameter("seq", seq);
    	 return query.getResultList();
	}
     /*order by post,exlude break post, include logical sp break post*/
	public static List<Assignment> findAssignmentBySplot(OsceDay osceDay,Date timeStart)
	{
		 EntityManager em = entityManager();
    	 String sql = "SELECT a FROM Assignment a WHERE a.type = 1  and a.osceDay = :osceDay and a.timeStart = :timeStart   ";
    	 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
    	 query.setParameter("osceDay", osceDay);
    	 query.setParameter("timeStart", timeStart);
    	 
    	 List<Assignment> assignments=query.getResultList();
    	 
    	 for(Assignment a:assignments)
    	 {
    		 OscePostRoom oscePostRoom=a.getOscePostRoom();
    		 if(oscePostRoom !=null)
    		 {
    			 if(oscePostRoom.getOscePost().getOscePostBlueprint().getPostType()==PostType.BREAK)
    			 {
    				 assignments.remove(a);
    			 }
    		 }
    	 }
    	 return query.getResultList();
	}
	
	public static int findRotationNumberFromSPSlot(OsceDay osceDay,Date timeStart,Date timeEnd)
	{
		EntityManager em = entityManager();
   	 String sql = "select distinct(rotationNumber) from Assignment where osceDay=:osceDay and type=0 and timeStart>=:timeStart and timeEnd<=:timeEnd";
   	 TypedQuery<Integer> query = em.createQuery(sql, Integer.class);
   	 query.setParameter("osceDay", osceDay);
   	query.setParameter("timeStart", timeStart);
   	query.setParameter("timeEnd", timeEnd);
   	
   	if(query.getResultList().size()>0)   	
   	 return query.getResultList().get(0);
   	else
   		return 0;
	}

	public static List<Doctor> findAssignmentExamnierByOsce(Long osceId) {
		EntityManager em = entityManager();
    	String query = "SELECT distinct a.examiner FROM Assignment a WHERE a.type = 2 AND a.osceDay.osce.id = " + osceId + " ORDER BY a.timeStart,a.examiner";
    	TypedQuery<Doctor> q = em.createQuery(query, Doctor.class);
    	return q.getResultList();
	}

	public static List<Assignment> findAssignmentStudentsByOsce(Long osceId) {
		EntityManager em = entityManager();
    	String query = "SELECT distinct a FROM Assignment a WHERE a.type = 0 AND a.osceDay.osce.id = " + osceId + " ORDER BY a.timeStart";
    	TypedQuery<Assignment> q = em.createQuery(query, Assignment.class);
    	return q.getResultList();
	}
	
	public static List<Assignment> findAssignmentOfLogicalBreakPost(Long osceId)
    {
		EntityManager em = entityManager();
		String query="SELECT distinct a FROM Assignment as a where a.osceDay.osce.id="+osceId+"  and type=0 and oscePostRoom is null and sequenceNumber in (select distinct (sequenceNumber) from Assignment where type=0 and osceDay.osce.id="+osceId+" and oscePostRoom in (select id from OscePostRoom where course.osce.id="+osceId+")) order by a.timeStart asc";
		TypedQuery<Assignment> typedQuery = em.createQuery(query, Assignment.class);
		List<Assignment> assignments=typedQuery.getResultList();
		Log.info("retrieveLogicalStudentInBreak query :" +query);
		 
		return assignments;
    }

	public static Long countAssignmentOfLogicalBreakPostPerOsce(Long osceId) {
		EntityManager em = entityManager();
    	String query = "SELECT count(a) FROM Assignment a WHERE a.type = 0 AND a.osceDay.osce.id = " + osceId + " ORDER BY a.timeStart";
    	TypedQuery<Long> q = em.createQuery(query, Long.class);
    	return q.getSingleResult();
	}

	 public static List<Date> findDistinctSPTimeStartRotationWise(Long osceDayId,int rotation)
     {
    	 Log.info("findDistinctTimeStartRotationWise");
    	 
    	 String queryString="select distinct (timeStart) from Assignment a where type=1 and osceDay="+osceDayId+" and timeStart >= (select min(timeStart) from Assignment where type=0 and osceDay = " + osceDayId + " and rotationNumber = " + rotation + ")" 
    				 + " and timeEnd <= (select max(timeEnd) from Assignment where type=0 and osceDay = " + osceDayId + " and rotationNumber = " + rotation + ") order by timeStart";
    	    	 
    	 EntityManager em = entityManager();
    	 TypedQuery<Date> query = em.createQuery(queryString, Date.class);
         List<Date> assignmentList = query.getResultList();
         Log.info("findDistinctTimeStartRotationWise query String :" + queryString);
         Log.info("Assignment List Size :" + assignmentList);
         return assignmentList;
    	
     }
	 
	 public static List<Date> findDistinctSPTimeEndCourseAndRotationWise(Long courseId, Long osceDayId, int rotation)
     {
    	 Log.info("findDistinctTimeStartRotationWise");
    	 
    	 String queryString = "select distinct (timeEnd) from Assignment a where type=1 and osceDay="+osceDayId+" and timeStart >= (select min(timeStart) from Assignment where type=0 and osceDay = " + osceDayId + " and rotationNumber = " + rotation + ")" 
    				 + " and timeEnd <= (select max(timeEnd) from Assignment where type=0 and osceDay = " + osceDayId + " and rotationNumber = " + rotation + ")"
    				 + " and a.oscePostRoom.id IN (SELECT opr.id FROM OscePostRoom opr WHERE opr.course = " + courseId + ") order by timeStart";
    		 //queryString="select distinct (timeEnd) from Assignment a where type=0 and osceDay="+osceDayId+" and rotationNumber = "+rotation+" order by timeStart";
    	 
    	 
    	 EntityManager em = entityManager();
    	 TypedQuery<Date> query = em.createQuery(queryString, Date.class);
         List<Date> assignmentList = query.getResultList();
         Log.info("findDistinctTimeStartRotationWise query String :" + queryString);
         Log.info("Assignment List Size :" + assignmentList);
         return assignmentList;
    	
     }
	 
	 public static List<Assignment> findSPAssignmentByOsce(Long osceId)
	 {
		 EntityManager em = entityManager();
		 String sql = "SELECT a FROM Assignment a WHERE a.type = 1 AND a.osceDay.osce.id = " + osceId + " ORDER BY a.sequenceNumber";
		 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
		 return query.getResultList();
	 }
	 
	 public static List<OscePostRoom> findDistinctOscePostRoomAssignmentByOsceDay(Long osceDayId)
	 {
		 EntityManager em = entityManager();
		 String sql = "SELECT DISTINCT a.oscePostRoom FROM Assignment a WHERE a.type = 1 AND a.osceDay.id = " + osceDayId + " ORDER BY a.sequenceNumber";
		 TypedQuery<OscePostRoom> query = em.createQuery(sql, OscePostRoom.class);
		 return query.getResultList();
	 }
	 
	 public static List<OscePostRoom> findDistinctOscePostRoomOfStudentAssignment(Long osceId)
	 {
		 EntityManager em = entityManager();
		 String sql = "SELECT DISTINCT a.oscePostRoom FROM Assignment a WHERE a.type = 0 AND a.osceDay.osce.id = " + osceId + " ORDER BY a.sequenceNumber";
		 TypedQuery<OscePostRoom> query = em.createQuery(sql, OscePostRoom.class);
		 return query.getResultList();
	 }
	 
	 public static List<Integer> findDistinctSPSequenceNumberByOscePostRoomByOsceDay(Long oscePostRoomId, Long osceDayId)
	 {
		 EntityManager em = entityManager();
		 String sql = "";
		 if (oscePostRoomId != null)
			 sql = "SELECT DISTINCT a.sequenceNumber FROM Assignment a WHERE a.type = 1 AND a.osceDay.id = " + osceDayId + " AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.id = " + oscePostRoomId + " ORDER BY a.sequenceNumber";
		 else			 
			 sql = "SELECT DISTINCT a.sequenceNumber FROM Assignment a WHERE a.type = 1 AND a.osceDay.id = " + osceDayId + " AND a.oscePostRoom IS NULL ORDER BY a.sequenceNumber";
			 
		 TypedQuery<Integer> query = em.createQuery(sql, Integer.class);
		 return query.getResultList();
	 }
	 
	 public static List<PatientInRole> findSPByOscePostRoomAndSequenceNumberByOsceDay(Long oscePostRoomId, Long osceDayId, Integer sequenceNumber)
	 {
		 List<PatientInRole> patientInRoleList = new ArrayList<PatientInRole>();
		 EntityManager em = entityManager();
		 String sql = "";
		 if (oscePostRoomId != null)
			 sql = "SELECT a FROM Assignment a WHERE a.type = 1 AND a.sequenceNumber = " + sequenceNumber + " AND a.osceDay.id = " + osceDayId + " AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.id = " + oscePostRoomId;
		 else			 
			 sql = "SELECT a FROM Assignment a WHERE a.type = 1 AND a.sequenceNumber = " + sequenceNumber + " AND a.osceDay.id = " + osceDayId + " AND a.oscePostRoom IS NULL";
			 
		 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
		 
		 for (Assignment a : query.getResultList())
		 {
			 patientInRoleList.add(a.getPatientInRole());
		 }
		 
		 return patientInRoleList;
	 }
	 
	 public static List<Assignment> findSpAssignmentByOscePostRoomAndSequenceNumberByOsceDay(Long oscePostRoomId, Long osceDayId, Integer sequenceNumber)
	 {
		 EntityManager em = entityManager();
		 String sql = "";
		 if (oscePostRoomId != null)
			 sql = "SELECT a FROM Assignment a WHERE a.type = 1 AND a.sequenceNumber = " + sequenceNumber + " AND a.osceDay.id = " + osceDayId + " AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.id = " + oscePostRoomId;
		 else			 
			 sql = "SELECT a FROM Assignment a WHERE a.type = 1 AND a.sequenceNumber = " + sequenceNumber + " AND a.osceDay.id = " + osceDayId + " AND a.oscePostRoom IS NULL";
			 
		 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
		 return query.getResultList();
	 }
	 
	 public static Assignment findSpTimeBySequenceNumberAndOsceDay(Long osceDayId, Integer sequenceNumber)
	 {
		 EntityManager em = entityManager();
		 String sql = "SELECT a FROM Assignment a WHERE a.type = 1 AND a.sequenceNumber = " + sequenceNumber + " AND a.osceDay.id = " + osceDayId;
		 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
		 if (query.getResultList().size() > 0)
			 return query.getResultList().get(0);
		 else
			 return null;				 
	 }	 
	 
	 public static List<Assignment> findExaminerAssignmentByOsce(Long osceId)
	 {
		 EntityManager em = entityManager();
		 String sql = "SELECT a FROM Assignment a WHERE a.type = 2 AND a.osceDay.osce.id = " + osceId + " ORDER BY a.timeStart";
		 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
		 return query.getResultList();
	 }
	 
	 public static List<Assignment> findAssignmentByOsce(Long osceId)
	 {
		 EntityManager em = entityManager();
		 String sql = "SELECT a FROM Assignment a WHERE a.osceDay.osce.id = " + osceId + " ORDER BY a.timeStart";
		 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
		 return query.getResultList();
	 }
	 
	 public static Map<Integer, Date> findTimeStartByOscePostRoom(Long osceId, OscePostRoom opr)
	 {
		 Map<Integer, Date> map = new LinkedHashMap<Integer, Date>();
		 if (opr.getCourse() != null && opr.getRoom() != null)
		 {
			 EntityManager em = entityManager();
			 String sql = "SELECT a.rotationNumber, MIN(a.timeStart) FROM Assignment a WHERE a.type = 0 AND a.osceDay.osce.id = " + osceId + " AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.id IN (SELECT opr.id FROM OscePostRoom opr WHERE opr.course.id = " + opr.getCourse().getId() + " AND opr.room.id = " + opr.getRoom().getId() + ") GROUP BY a.rotationNumber ORDER BY a.timeStart";
			 List<Object[]> resultList = em.createQuery(sql).getResultList();
			 
			 for (Object[] result : resultList)
			 {
				map.put((Integer)result[0], (Date)result[1]);
			 }
		 }
		 return map;
	 }
	 
	 public static Map<Integer, Date> findTimeEndByOscePostRoom(Long osceId, OscePostRoom opr)
	 {
		 Map<Integer, Date> map = new LinkedHashMap<Integer, Date>();
		 if (opr.getCourse() != null && opr.getRoom() != null)
		 {
			 EntityManager em = entityManager();
			 String sql = "SELECT a.rotationNumber, MAX(a.timeEnd) FROM Assignment a WHERE a.type = 0 AND a.osceDay.osce.id = " + osceId + " AND a.oscePostRoom IS NOT NULL AND a.oscePostRoom.id IN (SELECT opr.id FROM OscePostRoom opr WHERE opr.course.id = " + opr.getCourse().getId() + " AND opr.room.id = " + opr.getRoom().getId() + ") GROUP BY a.rotationNumber ORDER BY a.timeStart";
			 List<Object[]> resultList = em.createQuery(sql).getResultList();
			
			 for (Object[] result : resultList)
			 {
				 map.put((Integer)result[0], (Date)result[1]);
			 }
		 }
		 
		 return map;
	 }
	 
	 public static List<Date> findMinTimeStartAndMaxTimeEndByOscePost(Long oscePostId)
	 {
		 List<Date> timeList = new ArrayList<Date>();
		 EntityManager em = entityManager();
		 String sql = "SELECT MIN(a.timeStart), MAX(a.timeEnd) FROM Assignment a WHERE a.type = 0 AND a.oscePostRoom.oscePost.id = " + oscePostId;
		 List<Object[]> resultList = em.createQuery(sql).getResultList();
		 
		 if (resultList.size() > 0)
		 {
			 Object[] result = resultList.get(0);
			 timeList.add(((Date)result[0]));
			 timeList.add(((Date)result[1]));
		 }
		 
		 return timeList;
	 }
	 
	 public static List<Assignment> findAssignmentByPatientInSemesterAndTimeStartAndTimeEnd(Date timeStart, Date timeEnd, Long osceDayId, Long patientInSemesterId)
	 {
		 EntityManager em = entityManager();
		 String sql = "SELECT a FROM Assignment a WHERE a.osceDay.id = " + osceDayId + " AND a.type = 1 AND a.timeStart >= :timeStart AND timeStart <= :timeEnd AND a.patientInRole.patientInSemester.id = " + patientInSemesterId;
		 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
		 query.setParameter("timeStart", timeStart);
		 query.setParameter("timeEnd", timeEnd);
		 List<Assignment> assignmentList = query.getResultList(); 
		 return assignmentList;
	 }
	 
	 @Transactional
	 public void insertAssignment(String sql) {
		 javax.persistence.Query query = entityManager().createNativeQuery(sql); 
		 query.executeUpdate();
	 }
	 
	 public static Osce removeManualOsceAssignmentByOsceId(Long osceId)
	 {
		 new Assignment().removeAssignment(osceId);
		 
		 Osce osce = Osce.findOsce(osceId);
		 osce.setOsceStatus(OsceStatus.OSCE_FIXED);
		 osce.persist();
		 
		 return osce;
	 }
	 
	 @Transactional
	 public void removeAssignment(Long osceId)
	 {
		 //remove student assignment
		 String studentSql = "delete from assignment where type = 0 and osce_day in (select id from osce_day where osce = " + osceId +")";
		 javax.persistence.Query studentQuery = entityManager().createNativeQuery(studentSql); 
		 studentQuery.executeUpdate();
		 
		 //remove SP assignment
		 String spSql = "delete from assignment where type = 1 and osce_day in (select id from osce_day where osce = " + osceId +")";
		 javax.persistence.Query spQuery = entityManager().createNativeQuery(spSql); 
		 spQuery.executeUpdate();
		 
		 //remove examiner assignment
		 String examinerSql = "delete from assignment where type = 2 and osce_day in (select id from osce_day where osce = " + osceId +")";
		 javax.persistence.Query examinerQuery = entityManager().createNativeQuery(examinerSql); 
		 examinerQuery.executeUpdate();
	 }
	 
	 public static List<Assignment> findAssignmentBySPAndOsceDay(Long osceId, Long spId)
	 {
		 EntityManager em = entityManager();
		 String sql = "select a from Assignment a where a.type = 1 and a.osceDay.osce.id = " + osceId + " and a.patientInRole != null and a.patientInRole.patientInSemester.standardizedPatient.id = " + spId + " order by a.timeStart";
		 TypedQuery<Assignment> query = em.createQuery(sql, Assignment.class);
		 return query.getResultList();
	 }
	 
	 public static List<Date> findMinTimeStartAndMaxTimeEndByOsceDayAndRotationNumber(Long osceDayId, int rotationNumber)
	 {
		 List<Date> timeList = new ArrayList<Date>();
		 EntityManager em = entityManager();
		 String sql = "SELECT MIN(a.timeStart), MAX(a.timeEnd) FROM Assignment a WHERE a.type = 0 AND a.osceDay.id = " + osceDayId + " AND a.rotationNumber = " + rotationNumber + " ORDER BY a.timeStart";
		 List<Object[]> resultList = em.createQuery(sql).getResultList();
		 
		 if (resultList.size() > 0)
		 {
			 Object[] result = resultList.get(0);
			 timeList.add(((Date)result[0]));
			 timeList.add(((Date)result[1]));
		 }
		 
		 return timeList;
	 }
} 
