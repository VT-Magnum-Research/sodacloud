// CHECKSTYLE:OFF
/**
 * Source code generated by Fluent Builders Generator
 * Do not modify this file
 * See generator home page at: http://code.google.com/p/fluent-builders-generator-eclipse-plugin/
 */

package org.magnum.soda.svc;

import org.magnum.soda.proxy.ObjRef;

public class ObjAdvertisementMsgBuilder extends
		ObjAdvertisementMsgBuilderBase<ObjAdvertisementMsgBuilder> {
	public static ObjAdvertisementMsgBuilder objAdvertisementMsg() {
		return new ObjAdvertisementMsgBuilder();
	}

	public ObjAdvertisementMsgBuilder() {
		super(new ObjAdvertisementMsg());
	}

	public ObjAdvertisementMsg build() {
		return getInstance();
	}
}

class ObjAdvertisementMsgBuilderBase<GeneratorT extends ObjAdvertisementMsgBuilderBase<GeneratorT>> {
	private ObjAdvertisementMsg instance;

	protected ObjAdvertisementMsgBuilderBase(ObjAdvertisementMsg aInstance) {
		instance = aInstance;
	}

	protected ObjAdvertisementMsg getInstance() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withServer(String aValue) {
		instance.setServer(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withTopicId(String aValue) {
		instance.setTopicId(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withObjectId(ObjRef aValue) {
		instance.setObjectId(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withType(String aValue) {
		instance.setType(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withId(String aValue) {
		instance.setId(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withResponseTo(String aValue) {
		instance.setResponseTo(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withSource(String aValue) {
		instance.setSource(aValue);

		return (GeneratorT) this;
	}
}