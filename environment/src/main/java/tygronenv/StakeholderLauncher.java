package tygronenv;

import eis.exceptions.ManagementException;
import login.Login;
import nl.tytech.core.net.serializable.ProjectData;
import org.apache.commons.collections.map.HashedMap;
import tygronenv.configuration.Configuration;
import tygronenv.connection.ProjectFactory;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Joshua Slik
 */
public class StakeholderLauncher {

//    public static void main(String[] args) throws ManagementException, LoginException {
//
//        Configuration config = new Configuration(new HashedMap());
//
//        Login.main(new String[]{"joshua.b.slik@gmail.com", "J0shuaSl1k"});
//
//        ProjectFactory projectFactory = new ProjectFactory();
//
//        ProjectData projectData = new ProjectFactory().getProject("vhproject_codefox", "tudelft");
//        System.out.println(projectData.);
//
////        TygronEntity stakeholder = new TygronEntity(new EisEnv(), );
//
//    }

    public static void main(String[] args) {
        val stringList = new ArrayList<>();

        stringList.add("aa");
        stringList.add("b");
        stringList.add("cc");

        stringList.stream().parallel().map(x-> x+" lol").filter(x-> x.length() > 4).max((x,y)->x.length()-y.length()).;
    }

}
