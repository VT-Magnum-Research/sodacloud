package org.magnum.soda.aop;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;


public class DefaultInvocationProcessorFactory implements
		InvocationProcessorFactory {

	private Map<Class<?>, Class<? extends InvocationProcessor<?>>> processors_ = new HashMap<Class<?>, Class<? extends InvocationProcessor<?>>>();

	public DefaultInvocationProcessorFactory() {}

	public <Type extends Annotation> void addProcessor(
			Class<Type> anno, Class<? extends InvocationProcessor<Type>> proc) {
		processors_.put(anno, proc);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <PreProc extends Annotation> InvocationProcessor<PreProc> getProcessor(
			PreProc proc) {
		Class<? extends InvocationProcessor<PreProc>> processortype = (Class<? extends InvocationProcessor<PreProc>>) processors_
				.get(proc.annotationType());

		InvocationProcessor<PreProc> processor = null;

		if (processortype != null) {
			try {
				processor = processortype.newInstance();
				processor.setAnnotation(proc);
			} catch (Exception e) {
				throw new RuntimeException("Error creating processor of type: "
						+ processortype + " for processing annotation:" + proc);
			}
		}
		return processor;
	}
}
