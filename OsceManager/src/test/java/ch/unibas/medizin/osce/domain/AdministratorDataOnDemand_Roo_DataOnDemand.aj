// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package ch.unibas.medizin.osce.domain;

import ch.unibas.medizin.osce.domain.Administrator;
import java.lang.String;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.stereotype.Component;

privileged aspect AdministratorDataOnDemand_Roo_DataOnDemand {
    
    declare @type: AdministratorDataOnDemand: @Component;
    
    private Random AdministratorDataOnDemand.rnd = new SecureRandom();
    
    private List<Administrator> AdministratorDataOnDemand.data;
    
    public Administrator AdministratorDataOnDemand.getNewTransientAdministrator(int index) {
        Administrator obj = new Administrator();
        setEmail(obj, index);
        setName(obj, index);
        setPreName(obj, index);
        return obj;
    }
    
    public void AdministratorDataOnDemand.setEmail(Administrator obj, int index) {
        String email = "email_" + index;
        if (email.length() > 40) {
            email = new Random().nextInt(10) + email.substring(1, 40);
        }
        obj.setEmail(email);
    }
    
    public void AdministratorDataOnDemand.setName(Administrator obj, int index) {
        String name = "name_" + index;
        if (name.length() > 40) {
            name = name.substring(0, 40);
        }
        obj.setName(name);
    }
    
    public void AdministratorDataOnDemand.setPreName(Administrator obj, int index) {
        String preName = "preName_" + index;
        if (preName.length() > 40) {
            preName = preName.substring(0, 40);
        }
        obj.setPreName(preName);
    }
    
    public Administrator AdministratorDataOnDemand.getSpecificAdministrator(int index) {
        init();
        if (index < 0) index = 0;
        if (index > (data.size() - 1)) index = data.size() - 1;
        Administrator obj = data.get(index);
        return Administrator.findAdministrator(obj.getId());
    }
    
    public Administrator AdministratorDataOnDemand.getRandomAdministrator() {
        init();
        Administrator obj = data.get(rnd.nextInt(data.size()));
        return Administrator.findAdministrator(obj.getId());
    }
    
    public boolean AdministratorDataOnDemand.modifyAdministrator(Administrator obj) {
        return false;
    }
    
    public void AdministratorDataOnDemand.init() {
        data = Administrator.findAdministratorEntries(0, 10);
        if (data == null) throw new IllegalStateException("Find entries implementation for 'Administrator' illegally returned null");
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<ch.unibas.medizin.osce.domain.Administrator>();
        for (int i = 0; i < 10; i++) {
            Administrator obj = getNewTransientAdministrator(i);
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
