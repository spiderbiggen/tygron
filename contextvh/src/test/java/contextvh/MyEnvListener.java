package contextvh;

import eis.EnvironmentListener;
import eis.iilang.EnvironmentState;

import java.util.Collection;

/**
 * 
 * To check/wait for appearing entities.
 */
public class MyEnvListener implements EnvironmentListener {

	private String entity;

	@Override
	public void handleStateChange(EnvironmentState newState) {
	}

	@Override
	public void handleFreeEntity(String entity, Collection<String> agents) {
	}

	@Override
	public void handleDeletedEntity(String entity, Collection<String> agents) {
	}

	@Override
	public void handleNewEntity(String entity) {
		this.entity = entity;
	}

	public String waitForEntity() throws InterruptedException {
		while (entity == null) {
			Thread.sleep(200);
		}
		return entity;
	}

}
