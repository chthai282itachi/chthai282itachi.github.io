// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package ch.unibas.medizin.osce.domain;

import ch.unibas.medizin.osce.domain.MediaContent;
import ch.unibas.medizin.osce.domain.MediaContentType;
import ch.unibas.medizin.osce.domain.MediaContentTypeDataOnDemand;
import ch.unibas.medizin.osce.domain.StandardizedPatient;
import ch.unibas.medizin.osce.domain.StandardizedPatientDataOnDemand;
import java.lang.String;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

privileged aspect MediaContentDataOnDemand_Roo_DataOnDemand {
    
    declare @type: MediaContentDataOnDemand: @Component;
    
    private Random MediaContentDataOnDemand.rnd = new SecureRandom();
    
    private List<MediaContent> MediaContentDataOnDemand.data;
    
    @Autowired
    private MediaContentTypeDataOnDemand MediaContentDataOnDemand.mediaContentTypeDataOnDemand;
    
    @Autowired
    private StandardizedPatientDataOnDemand MediaContentDataOnDemand.standardizedPatientDataOnDemand;
    
    public MediaContent MediaContentDataOnDemand.getNewTransientMediaContent(int index) {
        MediaContent obj = new MediaContent();
        setComment(obj, index);
        setContentType(obj, index);
        setLink(obj, index);
        setStandardizedPatient(obj, index);
        return obj;
    }
    
    public void MediaContentDataOnDemand.setComment(MediaContent obj, int index) {
        String comment = "comment_" + index;
        if (comment.length() > 512) {
            comment = comment.substring(0, 512);
        }
        obj.setComment(comment);
    }
    
    public void MediaContentDataOnDemand.setContentType(MediaContent obj, int index) {
        MediaContentType contentType = mediaContentTypeDataOnDemand.getRandomMediaContentType();
        obj.setContentType(contentType);
    }
    
    public void MediaContentDataOnDemand.setLink(MediaContent obj, int index) {
        String link = "link_" + index;
        if (link.length() > 512) {
            link = link.substring(0, 512);
        }
        obj.setLink(link);
    }
    
    public void MediaContentDataOnDemand.setStandardizedPatient(MediaContent obj, int index) {
        StandardizedPatient standardizedPatient = standardizedPatientDataOnDemand.getRandomStandardizedPatient();
        obj.setStandardizedPatient(standardizedPatient);
    }
    
    public MediaContent MediaContentDataOnDemand.getSpecificMediaContent(int index) {
        init();
        if (index < 0) index = 0;
        if (index > (data.size() - 1)) index = data.size() - 1;
        MediaContent obj = data.get(index);
        return MediaContent.findMediaContent(obj.getId());
    }
    
    public MediaContent MediaContentDataOnDemand.getRandomMediaContent() {
        init();
        MediaContent obj = data.get(rnd.nextInt(data.size()));
        return MediaContent.findMediaContent(obj.getId());
    }
    
    public boolean MediaContentDataOnDemand.modifyMediaContent(MediaContent obj) {
        return false;
    }
    
    public void MediaContentDataOnDemand.init() {
        data = MediaContent.findMediaContentEntries(0, 10);
        if (data == null) throw new IllegalStateException("Find entries implementation for 'MediaContent' illegally returned null");
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<ch.unibas.medizin.osce.domain.MediaContent>();
        for (int i = 0; i < 10; i++) {
            MediaContent obj = getNewTransientMediaContent(i);
            try {
                obj.persist();
            } catch (ConstraintViolationException e) {
                StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> it = e.getConstraintViolations().iterator(); it.hasNext();) {
                    ConstraintViolation<?> cv = it.next();
                    msg.append("[").append(cv.getConstraintDescriptor()).append(":").append(cv.getMessage()).append("=").append(cv.getInvalidValue()).append("]");
                }
                throw new RuntimeException(msg.toString(), e);
            }
            obj.flush();
            data.add(obj);
        }
    }
    
}
