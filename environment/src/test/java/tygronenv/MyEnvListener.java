package tygronenv;

import java.util.Collection;

import eis.EnvironmentListener;
import eis.iilang.EnvironmentState;

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

	public void waitForEntity() throws InterruptedException {
		while (entity == null) {
			Thread.sleep(200);
		}
	}

}
