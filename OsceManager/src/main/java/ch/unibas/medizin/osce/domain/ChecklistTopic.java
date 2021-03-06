package ch.unibas.medizin.osce.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.constraints.Size;

import org.apache.log4j.Logger;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@RooJavaBean
@RooToString
@RooEntity
public class ChecklistTopic {
	
	@PersistenceContext(unitName="persistenceUnit")
    transient EntityManager entityManager;
	
	private static Logger Log = Logger.getLogger(ChecklistTopic.class);
	
	private Integer sort_order;
	
	@Size(max=50)
	private String title;
	
	@ManyToOne
	private CheckList checkList;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "checkListTopic")
	@OrderBy("sequenceNumber")
	private List<ChecklistQuestion> checkListQuestions = new ArrayList<ChecklistQuestion>();
	
	@Size(max=50)
	private String description;
	
	@Transactional
	public ChecklistTopic save()
	{
		 if (this.entityManager == null) this.entityManager = entityManager();
	        this.entityManager.persist(this);
	        
	      return this;
	}
	
	public static Integer findMaxSortOrder()
	{
		EntityManager em = entityManager();
		TypedQuery<Integer> query = em.createQuery("SELECT MAX(sort_order) FROM ChecklistTopic o",Integer.class);
		System.out.println("findMaxSortOrder query = "+query.getSingleResult());
        return query.getSingleResult();
	}
	public void topicMoveUp(long checklistID) {
		if (this.entityManager == null) {
			this.entityManager = entityManager();
		}
		ChecklistTopic topics = findTopicsByOrderSmaller(this.sort_order - 1,checklistID);
		if (topics == null) {
			return;
		}
		topics.setSort_order(this.sort_order);
		topics.persist();
		setSort_order(sort_order - 1);
		this.persist();
	}

	public void topicMoveDown(long checklistID) {
		if (this.entityManager == null) {
			this.entityManager = entityManager();
		}
		ChecklistTopic topic = findTopicsByOrderGreater(this.sort_order + 1,checklistID);
		if (topic == null) {
			return;
		}
		topic.setSort_order(this.sort_order);
		topic.persist();
		setSort_order(sort_order + 1);
		this.persist();
	}
	
	public static ChecklistTopic findTopicsByOrderSmaller(int sort_order,long checklistID) {
		EntityManager em = entityManager();
		TypedQuery<ChecklistTopic> query = em
				.createQuery(
						"SELECT o FROM ChecklistTopic AS o WHERE o.sort_order <= :sort_order and o.checkList = "+checklistID+" ORDER BY o.sort_order DESC",
						ChecklistTopic.class);
		query.setParameter("sort_order", sort_order);
		List<ChecklistTopic> resultList = query.getResultList();
		Log.info("in OrderSamller "+resultList.size());
		if (resultList == null || resultList.size() == 0)
			return null;
		Log.info("in OrderSamller "+resultList.size());
		return resultList.get(0);
	}
	
	public static ChecklistTopic findTopicsByOrderGreater(int sort_order,long checklistID) {
		EntityManager em = entityManager();
		TypedQuery<ChecklistTopic> query = em
				.createQuery(
						"SELECT o FROM ChecklistTopic AS o WHERE o.sort_order >= :sort_order and o.checkList = "+checklistID+" ORDER BY o.sort_order ASC",
						ChecklistTopic.class);
		query.setParameter("sort_order", sort_order);
		List<ChecklistTopic> resultList = query.getResultList();
		Log.info("in OrderGreater "+resultList.size());
		if (resultList == null || resultList.size() == 0)
			return null;
		Log.info("in OrderGreater "+resultList.size());
		return resultList.get(0);
	}
	
	public static Boolean updateSequence(List<ChecklistTopic> topicid) {
		
		try{
			EntityManager em = entityManager();
			
			for(int i=0;i<topicid.size();i++)
			{
				ChecklistTopic topic =topicid.get(i);
				topic.setSort_order(i);
				topic.persist();
			}
			
			
			
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	   
	   
}
