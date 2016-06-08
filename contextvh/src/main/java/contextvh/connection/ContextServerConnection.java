package contextvh.connection;

import eis.exceptions.ManagementException;
import login.Login;
import contextvh.login.ContextProjectFactory;
import login.ProjectException;
import nl.tytech.core.client.net.ServicesManager;
import nl.tytech.core.net.serializable.ProjectData;
import nl.tytech.core.net.serializable.User;
import nl.tytech.core.net.serializable.User.AccessLevel;
import tygronenv.configuration.Configuration;
import tygronenv.connection.Session;

import javax.security.auth.login.LoginException;

/**
 * The top level connection with the server. Creates connection, checks
 * credentials, creates new project (if requested name is not yet available).
 *
 * @author W.Pasman
 */
public class ContextServerConnection {

    /**
     * True if project is created by us. False if someone else created the
     * project.
     */
    private boolean createdProject = false;
    private ProjectData project;

    /**
     * Session can be used by all team members.
     */
    private Session session = null;
    private ContextProjectFactory factory = new ContextProjectFactory();

    /**
     * Connect with theserver, and create Session with the
     * {@link Configuration}.
     *
     * @param config the {@link Configuration} to use
     * @throws ManagementException thrown if connection failed for various reasons.
     */
    public ContextServerConnection(final Configuration config) throws ManagementException {
        User user;
        Login login;
        try {
            login = new Login();
            user = login.doLogin();
        } catch (LoginException e) {
            throw new ManagementException("login failed", e);
        }

        if (user.getMaxAccessLevel().ordinal() < AccessLevel.EDITOR.ordinal()) {
            throw new ManagementException("You need to have at least EDITOR access level");
        }
        project = factory.getProject(config.getProject(), user.getDomain());
        if (project == null) {
            try {
                project = factory.createProject(config.getProject(), user.getDomain());
            } catch (ProjectException e) {
                throw new ManagementException("failed to create project", e);
            }
            createdProject = true;
        }
        session = new Session(config, project);
    }

    /**
     * Disconnect from server. After calling this, this object can not be used
     * anymore.
     *
     * @throws ManagementException thrown if project couldn't be deleted.
     */
    public void disconnect() throws ManagementException {
        if (session != null) {
            session.close();
            session = null;
        }
        if (createdProject) {
            try {
                factory.deleteProject(project);
            } catch (final ProjectException e) {
                throw new ManagementException("Failed to remove temp project", e);
            } finally {
                project = null;
                createdProject = false;
            }
        }
        ServicesManager.removeLoginCredentials();
    }

    /**
     * @return the session that we are working on. All team members can use this
     * session
     */
    public Session getSession() {
        return session;
    }
}
