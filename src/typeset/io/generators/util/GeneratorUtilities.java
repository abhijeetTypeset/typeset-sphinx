package typeset.io.generators.util;

import java.sql.Timestamp;

import typeset.io.model.NodeType;

public class GeneratorUtilities {
	public static String firstLetterCaptial(String name) {
		if (name.length() <= 1) {
			return name.toUpperCase();
		} else {
			return name.substring(0, 1).toUpperCase() + name.substring(1);
		}
	}

	public static String getGetterName(String name) {
		return "get" + firstLetterCaptial(name);
	}

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

	public static String getTimestamp() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		return timestamp.getTime() + "";
	}
}
