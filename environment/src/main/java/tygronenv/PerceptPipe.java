package tygronenv;

import eis.iilang.Percept;

/**
 * Pipe where percepts can be pushed in (FIFO)for notification to EIS.
 */
public interface PerceptPipe {
	/**
	 * 
	 * @param percept
	 *            the percept to push into the pipe.
	 */
	void push(Percept percept);
}
