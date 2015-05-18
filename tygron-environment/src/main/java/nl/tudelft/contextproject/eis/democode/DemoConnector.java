package nl.tudelft.contextproject.eis.democode;

import nl.tudelft.contextproject.tygron.Connector;
import nl.tudelft.contextproject.tygron.Session;
import nl.tudelft.contextproject.tygron.SessionManager;
import nl.tudelft.contextproject.tygron.StakeholderList;

public class DemoConnector {

  /**
   * Demo Connector setup.
   * @param args Main Arguments
   */
  public static void main(String[] args) {
    // General setup for http
    Connector con = new Connector();
    
    // Session Manager
    SessionManager sesM = con.getSessionManager();
    int sessionSlot = sesM.createSession("testmap");
    System.out.println("Creating session in slot " + sessionSlot);
    
    System.out.println("Joining session...");
    Session sess = new Session(con.getConnectionManager());
    sesM.joinSession(sess, sessionSlot);
    
   
    System.out.println("Compatible API Data/Functions:");
    System.out.println(sess.getCompatibleOperations());
    
    System.out.println("Loading stake holders:");
    System.out.println(sess.loadStakeHolders());
   
    boolean sessionKill = sesM.killSession(sessionSlot);
    System.out.println("Killing session, result: " + sessionKill);
  }
}
