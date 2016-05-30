package tygronenv.actions;

import java.util.HashMap;

/**
 * 
 * @author Max_G
 *
 */
public class ActionContainer extends HashMap<String, CustomAction>{
	private static final long serialVersionUID = -6578689136054121705L;
	
	/**
	 * Creates an ActionContainer object filled with all existing CustomActions defined.
	 * Thus use the ActionContainer.put function to add custom actions to the list.
	 * They will automatically be used in TygronEntity.
	 */
	public ActionContainer() {
		super();
		
		addAction(new CounterTestAction());
	}
	
	public void addAction(CustomAction action) {
		this.put(action.getName(), action);
	}
}
