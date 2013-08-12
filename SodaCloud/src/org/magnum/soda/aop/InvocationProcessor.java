package org.magnum.soda.aop;

import org.magnum.soda.svc.InvocationInfo;


public interface InvocationProcessor<AnnoType> {

	public void preProcess(InvocationInfo i);
	public Object postProcess(InvocationInfo i, Object rslt);
	public void setAnnotation(AnnoType anno);
	
}
