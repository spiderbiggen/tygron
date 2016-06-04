package login;

import nl.tytech.core.client.net.ServicesManager;
import nl.tytech.core.net.serializable.ProjectData;

import static nl.tytech.core.net.event.IOServiceEventType.GET_DOMAIN_STARTABLE_PROJECTS;
import static nl.tytech.core.net.event.IOServiceEventType.GET_PROJECT_DATA;

/**
 * Factory to fetch existing and create new projects.
 *
 * @author Stefan Breetveld
 */
public class ContextProjectFactory extends ProjectFactory {

    /**
     * Join existing project.
     *
     * @param name   the project name to join/make.
     * @param domain the domain that you want to find projects on.
     * @return project with given name on the given domain, or null if no project with given name
     * exists.
     */
    public ProjectData getProject(final String name, final String domain) {
        ProjectData[] projects = ServicesManager.fireServiceEvent(GET_DOMAIN_STARTABLE_PROJECTS, domain);
        if (projects != null) {
            for (ProjectData existing : projects) {
                if (existing.getFileName().equals(name)) {
                    return ServicesManager.fireServiceEvent(GET_PROJECT_DATA, name);
                }
            }
        }

        return getProject(name);
    }
}
