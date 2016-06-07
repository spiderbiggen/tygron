package tygronenv;

import eis.exceptions.EntityException;

public interface EntityEventListener {

	/**
	 * reports that entity is ready for use.
	 * 
	 * @param entity
	 *            the entity name
	 * @throws EntityException
	 */
	public void notifyReady(String entity) throws EntityException;

}
