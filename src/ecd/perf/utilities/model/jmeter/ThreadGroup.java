package ecd.perf.utilities.model.jmeter;

import java.util.ArrayList;
import java.util.List;

import ecd.perf.utilities.model.jmeter.listener.AbstractJmeterListener;
import ecd.perf.utilities.model.jmeter.logic.LoopController;
public class ThreadGroup extends AbstractModelElement{

	private static final long serialVersionUID = 1L;
	private final AbstractGroupElement loopCtrl;
	private final ArrayList<Object> children = new ArrayList<Object>();
	protected ThreadGroup() {
		super("Thread Group");
		this.put("ThreadGroup.on_sample_error", "continue");
		this.put("ThreadGroup.num_threads", "${THREADS}");
		this.put("ThreadGroup.ramp_time", "${RAMPTIME}");
		this.put("ThreadGroup.start_time", System.currentTimeMillis());
		this.put("ThreadGroup.end_time", System.currentTimeMillis()+10000);
		this.put("ThreadGroup.scheduler", false);
		this.put("ThreadGroup.duration", "");
		this.put("ThreadGroup.delay", "");
		this.put("ThreadGroup.main_controller", new LoopController());
		loopCtrl = new LoopGroupController("${LOOPS}");
		children.add(loopCtrl);
	}
	
	protected ThreadGroup(String threadName, boolean isEnabled) {
		super(threadName);
		this.setEnable(isEnabled);
		this.put("ThreadGroup.on_sample_error", "continue");
		this.put("ThreadGroup.num_threads", "${THREADS}");
		this.put("ThreadGroup.ramp_time", "${RAMPTIME}");
		this.put("ThreadGroup.start_time", System.currentTimeMillis());
		this.put("ThreadGroup.end_time", System.currentTimeMillis()+10000);
		this.put("ThreadGroup.scheduler", false);
		this.put("ThreadGroup.duration", "");
		this.put("ThreadGroup.delay", "");
		this.put("ThreadGroup.main_controller", new LoopController());
		loopCtrl = new LoopGroupController("${LOOPS}");
		children.add(loopCtrl);
	}

	@Override
	public List<Object> getChildren() {
		return children;
	}

	@Override
	public String getTagName() {
		return "ThreadGroup";
	}

	@Override
	public String getTestclass() {
		return "ThreadGroup";
	}

	@Override
	public String getUIClass() {
		return "ThreadGroupGui";
	}

	@Override
	public boolean isPrintEmptyHashTree() {
		return true;
	}
	
	public AbstractGroupElement getMainController(){
		return this.loopCtrl;
	}

	public void addListener(AbstractJmeterListener listener) {
		if(!children.contains(listener))
			children.add(listener);
	}
}
