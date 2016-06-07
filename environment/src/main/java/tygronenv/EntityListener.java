package tygronenv;

/**
 * Listener for an entity
 * 
 * @author W.Pasman
 *
 */
public interface EntityListener {

	/**
	 * Called by Entity when it is ready to give first percepts
	 * 
	 * @param entityName
	 *            name for this entity.
	 */
	void entityReady(String entityName);

}
