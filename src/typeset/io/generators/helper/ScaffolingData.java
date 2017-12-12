package typeset.io.generators.helper;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class ScaffolingData {
	JMethod method;
	JBlock block;
	JVar assertVar;

	public ScaffolingData(JMethod method, JBlock block, JVar assertVar) {
		super();
		this.method = method;
		this.block = block;
		this.assertVar = assertVar;
	}

	public JMethod getMethod() {
		return method;
	}

	public void setMethod(JMethod method) {
		this.method = method;
	}

	public JBlock getBlock() {
		return block;
	}

	public void setBlock(JBlock block) {
		this.block = block;
	}

	public JVar getAssertVar() {
		return assertVar;
	}

	public void setAssertVar(JVar assertVar) {
		this.assertVar = assertVar;
	}

}
