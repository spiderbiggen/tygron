package tygronenv.percepts;

import eis.iilang.Numeral;
import eis.iilang.Percept;
import nl.tytech.data.engine.item.Stakeholder;

/**
 * Creates the self {@link Percept} in the form of:
 * my_stakeholder_id(<ID>).
 * @author Rico
 *
 */
public class SelfPercept implements SimplePercept {
	
	/**
	 * The {@link Stakeholder} you are in the game.
	 */
	private final Stakeholder stakeholder;

	/**
	 * Create a {@link SelfPercept} from your {@link Stakeholder}.
	 * @param holder The {@link Stakeholder} you are in the game.
	 */
	public SelfPercept(final Stakeholder holder) {
		stakeholder = holder;
	}
	
	
	@Override
	public Percept getPercept() {
		return new Percept(
				getPerceptName(),
				new Numeral(stakeholder.getID()));
	}

	@Override
	public String getPerceptName() {
		return "my_stakeholder_id";
	}

}
