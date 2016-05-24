package tygronenv.percepts;

import java.util.LinkedList;
import java.util.List;

import eis.iilang.Percept;

/**
 * 
 * @author Rico
 *
 */
public abstract class ExtraPercepts {
	
	/**
	 * List of {@link SimlplePercept percepts}
	 * that are registered.
	 */
	private LinkedList<SimplePercept> registered = 
			new LinkedList<SimplePercept>();

	/**
	 * Get a list of all the percepts that are registered.
	 * @return 
	 * 			List of {@linkplain Percept percepts}.
	 */
	public final List<Percept> getPercepts() {
		List<Percept> percepts = new LinkedList<Percept>();
		for (SimplePercept percept: registered) {
			percepts.add(percept.getPercept());
		}
		return percepts;
	}
	
	/**
	 * Register a {@link SimplePercept}.
	 * @param sp
	 * 			The {@link SimplePercept} that
	 * 			is being registered.
	 */
	public void registerPercept(SimplePercept sp) {
		registered.add(sp);
	}
		
}
