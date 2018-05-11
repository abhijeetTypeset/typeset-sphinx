package io.typeset.sphinx;

public class Params {
	private String configFile;
	private String enabledSpec;
	private String slackChannel;
	
	public String getConfigFile() {
		return configFile;
	}
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}
	public String getEnabledSpec() {
		return enabledSpec;
	}
	public void setEnabledSpec(String enabledSpec) {
		this.enabledSpec = enabledSpec;
	}
	public String getSlackChannel() {
		return slackChannel;
	}
	public void setSlackChannel(String slackChannel) {
		this.slackChannel = slackChannel;
	}

}
