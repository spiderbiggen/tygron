package tygronenv.percepts;

import eis.iilang.Percept;

/**
 * Interface for a SimplePercept which is used in 
 * {@link ExtraPercepts} to get receive all 
 * the extra's percepts.
 * @author Rico
 *
 */
public interface SimplePercept {
	
	/**
	 * Get the {@link Percept}.
	 * @return Returns the {@link Percept}. 
	 */
	public abstract Percept getPercept();
	
	/**
	 * Get the name of this {@link Percept}.
	 * @return Name of this {@link Percept}.
	 */
	public abstract String getPerceptName();

}
