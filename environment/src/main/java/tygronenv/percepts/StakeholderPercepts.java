package tygronenv.percepts;

import java.util.ArrayList;

import nl.tytech.data.engine.item.Stakeholder;

/**
 * Creates extra percepts for stakeholders.
 * @author Rico
 *
 */
public class StakeholderPercepts extends ExtraPercepts {

	/**
	 * List of all the stakeholders.
	 */
	private ArrayList<Stakeholder> stakeholders;
	/**
	 * The {@link Stakeholder} you are.
	 */
	private Stakeholder yourStakeholder;
	/**
	 * The {@linkplain Stakeholder.Type type} of
	 * stakeholder you are.
	 */
	private Stakeholder.Type yourTypeStakeholder;
	
	/**
	 * 
	 * @param items List of all new updates of the stakeholders.
	 * @param yourTypeOfStakeholder The {@link Type} of 
	 * {@link Stakeholder} you are in the game.
	 */
	public StakeholderPercepts(final ArrayList<Stakeholder> items,
			final Stakeholder.Type yourTypeOfStakeholder) {	
		stakeholders = items;
		yourTypeStakeholder = yourTypeOfStakeholder; 
		yourStakeholder = getYourStakeholder();
		
		registerPercept(new SelfPercept(this.yourStakeholder));
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
