package io.typeset.sphinx.model;


/**
 * The Enum NodeType; all types of nodes that we have in W.A.S.P. model
 */
public enum NodeType {
	
	/** The control. */
	CONTROL ("control"), 
	
	/** The widget. */
	WIDGET ("widget"), 
	
	/** The app. */
	APP("app"), 
	
	/** The screen. */
	SCREEN("screen"), 
	
	/** The page. */
	PAGE("page"), 
	
	/** The dummy. */
	DUMMY("dummy");
	
	/** The name. */
	private String name;       

    /**
     * Instantiates a new node type.
     *
     * @param s the s
     */
    private NodeType(String s) {
        name = s;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    public String toString() {
        return this.name;
     }
}
