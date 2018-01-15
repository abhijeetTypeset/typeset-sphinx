package io.typeset.generators.ds;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

/**
 * The Class ScaffolingData.
 * A data structure used during test generation
 */
public class ScaffolingData {
	
	/** The method. */
	JMethod method;
	
	/** The block. */
	JBlock block;
	
	/** The assert var. */
	JVar assertVar;

	/**
	 * Instantiates a new scaffoling data.
	 *
	 * @param method the method
	 * @param block the block
	 * @param assertVar the assert var
	 */
	public ScaffolingData(JMethod method, JBlock block, JVar assertVar) {
		super();
		this.method = method;
		this.block = block;
		this.assertVar = assertVar;
	}

	/**
	 * Gets the method.
	 *
	 * @return the method
	 */
	public JMethod getMethod() {
		return method;
	}

	/**
	 * Sets the method.
	 *
	 * @param method the new method
	 */
	public void setMethod(JMethod method) {
		this.method = method;
	}

	/**
	 * Gets the block.
	 *
	 * @return the block
	 */
	public JBlock getBlock() {
		return block;
	}

	/**
	 * Sets the block.
	 *
	 * @param block the new block
	 */
	public void setBlock(JBlock block) {
		this.block = block;
	}

	/**
	 * Gets the assert var.
	 *
	 * @return the assert var
	 */
	public JVar getAssertVar() {
		return assertVar;
	}

	/**
	 * Sets the assert var.
	 *
	 * @param assertVar the new assert var
	 */
	public void setAssertVar(JVar assertVar) {
		this.assertVar = assertVar;
	}

}
