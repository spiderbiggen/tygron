package nl.tytech.sdk.e2eTests;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginException;

import login.Login;
import login.ProjectException;
import login.ProjectFactory;
import nl.tytech.core.net.serializable.ProjectData;
import nl.tytech.data.engine.item.Stakeholder;

/**
 * Game field where general testing of interactions can be done for testing.
 * 
 * @author W.Pasman
 *
 */
public class GameField {

	private ProjectData project;
	private Map<Stakeholder.Type, MyStakeholder> stakeholders = new HashMap<Stakeholder.Type, MyStakeholder>();

	public GameField() throws LoginException, ProjectException {
		Login login = new Login();
		login.doLogin();

		ProjectFactory factory = new ProjectFactory();
		String projectName = "test" + System.currentTimeMillis();
		project = factory.createProject(projectName);
		if (project == null) {
			throw new IllegalStateException("createProject returned null");
		}
	}

	/**
	 * Call this once for each expected stakeholder. You may then use the
	 * returned stakeholder immediately
	 * 
	 */
	public MyStakeholder addStakeholder(Stakeholder.Type type) {
		MyStakeholder stakeholder = new MyStakeholder(type, project);
		stakeholders.put(type, stakeholder);
		return stakeholder;
	}

	public void close() throws ProjectException {
		new ProjectFactory().deleteProject(project);
		project = null;
	}

}
