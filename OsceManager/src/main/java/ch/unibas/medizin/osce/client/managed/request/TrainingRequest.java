// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.

package ch.unibas.medizin.osce.client.managed.request;

import com.google.gwt.requestfactory.shared.InstanceRequest;
import com.google.gwt.requestfactory.shared.Request;
import com.google.gwt.requestfactory.shared.RequestContext;
import com.google.gwt.requestfactory.shared.ServiceName;
import org.springframework.roo.addon.gwt.RooGwtMirroredFrom;

@RooGwtMirroredFrom("ch.unibas.medizin.osce.domain.Training")
@ServiceName("ch.unibas.medizin.osce.domain.Training")
public interface TrainingRequest extends RequestContext {

    abstract InstanceRequest<ch.unibas.medizin.osce.client.managed.request.TrainingProxy, java.lang.Void> persist();

    abstract InstanceRequest<ch.unibas.medizin.osce.client.managed.request.TrainingProxy, java.lang.Void> remove();

    abstract Request<java.lang.Long> countTrainings();

    abstract Request<ch.unibas.medizin.osce.client.managed.request.TrainingProxy> findTraining(Integer id);

    abstract Request<java.util.List<ch.unibas.medizin.osce.client.managed.request.TrainingProxy>> findAllTrainings();

    abstract Request<java.util.List<ch.unibas.medizin.osce.client.managed.request.TrainingProxy>> findTrainingEntries(int firstResult, int maxResults);
}
