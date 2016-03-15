package tygronenv;

import nl.tytech.core.net.serializable.ProjectData;
import tygronenv.configuration.Configuration;
import tygronenv.connection.Session;

/**
 * the 'participant' , but close to the EIS level.
 * 
 * @author W.Pasman
 *
 */
public class TygronEntity {
	private Session session;

	public TygronEntity(Configuration config, ProjectData project) {
		session = new Session(config, project);
	}

	public void close() {
		session.close();
	}
}
