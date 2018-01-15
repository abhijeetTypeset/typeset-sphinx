package io.typeset.generators.util;

import java.sql.Timestamp;

import io.typeset.model.NodeType;


/**
 * The Class GeneratorUtilities.
 * A class to hold various class generation utilities
 */
public class GeneratorUtilities {
	
	/**
	 * First letter captial.
	 *
	 * @param name the name
	 * @return the string
	 */
	public static String firstLetterCaptial(String name) {
		if (name.length() <= 1) {
			return name.toUpperCase();
		} else {
			return name.substring(0, 1).toUpperCase() + name.substring(1);
		}
	}

	/**
	 * Gets the getter name.
	 *
	 * @param name the name
	 * @return the getter name
	 */
	public static String getGetterName(String name) {
		return "get" + firstLetterCaptial(name);
	}

	/**
	 * Gets the node type.
	 *
	 * @param nodeType the node type
	 * @return the node type
	 */
	public static int getNodeType(NodeType nodeType) {
		if(nodeType == NodeType.PAGE) {
			return 0;
		}else if(nodeType == NodeType.SCREEN) {
			return 1;
		}else if(nodeType == NodeType.APP) {
			return 2;
		}else if(nodeType == NodeType.WIDGET) {
			return 3;
		}else if(nodeType == NodeType.CONTROL) {
			return 4;
		}else {
			return -1;
		}
		
	}

	/**
	 * Gets the timestamp.
	 *
	 * @return the timestamp
	 */
	public static String getTimestamp() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		return timestamp.getTime() + "";
	}
}
