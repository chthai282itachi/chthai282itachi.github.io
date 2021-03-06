package ch.unibas.medizin.osce.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.constraints.Size;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity
public class Skill {

	@PersistenceContext(unitName="persistenceUnit")
    transient EntityManager entityManager;
	
	
	private Integer shortcut;
	
	@Size(max = 1024)
	private String description;
	
	@ManyToOne
	private Topic topic;
	
	@ManyToOne
	private SkillLevel skillLevel;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "skill")
	private Set<SkillHasAppliance> skillHasAppliances = new HashSet<SkillHasAppliance>();
	
	public static List<Skill> findSkillByTopic(Topic val)
	{
		EntityManager em = entityManager();
		String s="SELECT o FROM Skill AS o WHERE o.topic = "+val.getId() ;
		System.out.println("Query: " + s.toString());
		//TypedQuery<Skill> q = em.createQuery("SELECT o FROM Skill o JOIN o.skillLevel s WHERE o.topic LIKE :val", Skill.class);    		
		TypedQuery<Skill> q = em.createQuery(s, Skill.class);
     	//q.setParameter("val", val);
     	
     	return q.getResultList();
	}
	
	public static List<Skill> findSkillByTopicAndSkillLevel(Topic val, SkillLevel skillval)
	{
		EntityManager em = entityManager();
    	TypedQuery<Skill> q = em.createQuery("SELECT o FROM Skill o WHERE o.topic LIKE :val AND o.skillLevel LIKE :skillval", Skill.class);
     	q.setParameter("val", val);
     	q.setParameter("skillval", skillval);
     	return q.getResultList();
	}
	
	public static List<Skill> findSkillByTopicIDAndSkillLevelID(long topicId, long skillLevelId)
	{
		EntityManager em = entityManager();
		String q = "";
		if (skillLevelId == 0)
			q = "SELECT s FROM Skill AS s WHERE s.topic =" + topicId + " AND s.skillLevel = null";
		else
			q = "SELECT s FROM Skill AS s WHERE s.topic =" + topicId + " AND s.skillLevel ="+ skillLevelId;
		
		System.out.println("~~QUERY : " + q.toString());
				
    	TypedQuery<Skill> result = em.createQuery(q, Skill.class);
     	return result.getResultList();
	}	
	
	public static List<Skill> findAllSkillByLimit(int start, int length)
	{
		EntityManager em = entityManager();
		String sql = "SELECT s FROM Skill As s";
		TypedQuery<Skill> result = em.createQuery(sql, Skill.class);
		result.setFirstResult(start);
		result.setMaxResults(length);
		List<Skill> response = result.getResultList();
		return response;
	}
	
	public static List<Skill> findSkillBySearchCriteria(int start, int max, Long mainClassificationId, Long classificationTopicId, Long topicId, Long skillLevlId, Long applianceId)
	{
		EntityManager em = entityManager();
		String sql = "SELECT s FROM Skill As s where";
		
		if(mainClassificationId != null) // main classification
			sql = sql + " s.topic.classificationTopic.mainClassification.id = " + mainClassificationId +" AND  " ;
		
		if(classificationTopicId != null) //ClassificationTopic
			sql = sql + " s.topic.classificationTopic.id = " + classificationTopicId +" AND  " ;
		
		if(topicId != null) //Topic
			sql = sql + " s.topic.id = " + topicId +" AND  " ;
		
		if (skillLevlId != null)
			sql = sql + " s.skillLevel.id = " + skillLevlId + " AND  ";
		
		if (applianceId != null)
			sql = sql + " s.id in (select a.skill from SkillHasAppliance as a where a.appliance.id = " + applianceId + ") AND  ";

		sql = sql.substring(0, sql.length()-5);
		
		//System.out.println("QUERY : " + sql);
		
		TypedQuery<Skill> result = em.createQuery(sql, Skill.class);
		result.setFirstResult(start);
		result.setMaxResults(max);
		List<Skill> response = result.getResultList();
		return response;
	}
	
	public static Integer countSkillBySearchCriteria(Long mainClassificationId, Long classificationTopicId, Long topicId, Long skillLevlId, Long applianceId)
	{
		EntityManager em = entityManager();
		String sql = "SELECT s FROM Skill As s where";
		
		if(mainClassificationId != null) // main classification
			sql = sql + " s.topic.classificationTopic.mainClassification.id = " + mainClassificationId +" AND  " ;
		
		if(classificationTopicId != null) //ClassificationTopic
			sql = sql + " s.topic.classificationTopic.id = " + classificationTopicId +" AND  " ;
		
		if(topicId != null) //Topic
			sql = sql + " s.topic.id = " + topicId +" AND  " ;
		
		if (skillLevlId != null)
			sql = sql + " s.skillLevel.id = " + skillLevlId + " AND  ";
		
		if (applianceId != null)
			sql = sql + " s.id in (select a.skill from SkillHasAppliance as a where a.appliance.id = " + applianceId + ") AND  ";

		sql = sql.substring(0, sql.length()-5);
		
		//System.out.println("QUERY : " + sql);
		
		TypedQuery<Skill> result = em.createQuery(sql, Skill.class);
		List<Skill> response = result.getResultList();
		return response.size();
	}
	
	public static List<Skill> findSkillByTopicAndShortcut(Long classificationId, String value)
	{
		EntityManager em = entityManager();
		String s="SELECT s FROM Skill AS s WHERE s.shortcut = " + value + " AND s.topic.id IN (SELECT t.id FROM Topic t WHERE t.classificationTopic.id = " + classificationId +")";
		TypedQuery<Skill> q = em.createQuery(s, Skill.class);
		return q.getResultList();
	}
}
