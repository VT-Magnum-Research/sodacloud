package org.magnum.soda.aop;

import java.lang.annotation.Annotation;

public interface InvocationProcessorFactory {

	public <PreProc extends Annotation> InvocationProcessor<PreProc> getProcessor(PreProc proc);
	
}
