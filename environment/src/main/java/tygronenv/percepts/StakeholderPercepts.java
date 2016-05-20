package tygronenv.percepts;

import java.util.ArrayList;

import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.data.engine.item.Stakeholder.Type;

/**
 * Creates extra percepts for stakeholders.
 * @author Rico
 *
 */
public class StakeholderPercepts extends ExtraPercepts {

	private ArrayList<Stakeholder> stakeholders;
	private Stakeholder yourStakeholder;
	private Type yourTypeStakeholder;
	
	/**
	 * 
	 * @param items List of all new updates of the stakeholders.
	 * @param yourTypeOfStakeholder The {@link Type} of 
	 * {@link Stakeholder} you are in the game.
	 */
	public StakeholderPercepts(final ArrayList<Stakeholder> items,
			final Type yourTypeOfStakeholder) {
		stakeholders = items;
		yourTypeStakeholder = yourTypeOfStakeholder; 
		yourStakeholder = getYourStakeholder();
		
		registerPercept(new SelfPercept(this.yourStakeholder));
		registerPercept(new PerceptTest());
	}
	
	/**
	 * Get the {@link Stakeholder} you are in the game.
	 * @return The {@link Stakeholder} you 
	 * are when your {@link Type} can be
	 * found. Otherwhise 
	 * this throws a {@link IllegalArgumentException} 
	 */
	public Stakeholder getYourStakeholder() {
		for (Stakeholder stakeholder : stakeholders) {
			if (stakeholder.getType().equals(yourTypeStakeholder)) {
				return stakeholder;
			}
		}
		throw new IllegalArgumentException("Stakeholder of type " 
				+ yourTypeStakeholder + " is not available");
	}

}
