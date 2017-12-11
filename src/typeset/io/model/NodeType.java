package typeset.io.model;

public enum NodeType {
	CONTROL ("control"), 
	WIDGET ("widget"), 
	APP("app"), 
	SCREEN("screen"), 
	PAGE("page"), 
	DUMMY("dummy");
	
	private String name;       

    private NodeType(String s) {
        name = s;
    }
    public String toString() {
        return this.name;
     }
}
