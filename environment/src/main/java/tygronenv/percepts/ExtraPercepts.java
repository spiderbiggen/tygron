package tygronenv.percepts;

import java.util.LinkedList;
import java.util.List;

import eis.iilang.Percept;

public abstract class ExtraPercepts {
	
	private LinkedList<SimplePercept> registered = new LinkedList<SimplePercept>();

	public final List<Percept> getPercepts() {
		List<Percept> percepts = new LinkedList<Percept>();
		for (SimplePercept percept: registered) {
			percepts.add(percept.getPercept());
		}
		return percepts;
	}
	
	public void registerPercept(SimplePercept sp) {
		registered.add(sp);
	}
		
}
