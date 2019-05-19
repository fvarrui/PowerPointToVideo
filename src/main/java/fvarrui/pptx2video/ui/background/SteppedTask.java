package fvarrui.pptx2video.ui.background;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.concurrent.Task;

public class SteppedTask extends Task<Void> {
	
	private Map<String, Object> data;
	private List<Step> steps = new ArrayList<>();
	
	public SteppedTask(Map<String, Object> data) {
		super();
		this.data = data;
	}
	
	public SteppedTask() {
		this(new HashMap<String, Object>());
	}
	
	public List<Step> getSteps() {
		return steps;
	}
	
	public Step addStep(String description, StepTask task) {
		Step s = new Step(description, task);
		steps.add(s);
		return s;
	}
	
	public Step addStep(String description) {
		return addStep(description, data -> {});
	}
	
	public Map<String, Object> getData() {
		return data;
	}

	@Override
	protected Void call() throws Exception {
		long workDone = 1;
		for (Step s : steps) {
			if (isCancelled()) {
				updateMessage("Process cancelled");
				break;
			}
			updateMessage(s.getDescription());
			updateProgress(workDone++, steps.size());
			s.getTask().doWork(data);
		}
		return null;
	}

}
