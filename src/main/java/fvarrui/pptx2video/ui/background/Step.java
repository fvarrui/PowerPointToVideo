package fvarrui.pptx2video.ui.background;

public class Step {
	
	private String description;
	private StepTask task;
	
	public Step(String description, StepTask task) {
		super();
		this.description = description;
		this.task = task;
	}
	
	public String getDescription() {
		return description;
	}
	
	public StepTask getTask() {
		return task;
	}

}
