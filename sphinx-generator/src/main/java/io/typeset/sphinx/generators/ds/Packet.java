package io.typeset.sphinx.generators.ds;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

public class Packet {

	ScaffolingData sdata;
	JDefinedClass definedClass;
	JCodeModel codeModel;

	public Packet(ScaffolingData sdata, JDefinedClass definedClass, JCodeModel codeModel) {
		super();
		this.sdata = sdata;
		this.definedClass = definedClass;
		this.codeModel = codeModel;
	}

	public ScaffolingData getSdata() {
		return sdata;
	}

	public JDefinedClass getDefinedClass() {
		return definedClass;
	}

	public JCodeModel getCodeModel() {
		return codeModel;
	}

}
