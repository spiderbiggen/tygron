package nl.tytech.sdk.e2eTests;

import javax.security.auth.login.LoginException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import login.ProjectException;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.SpecialOption.Type;
import nl.tytech.data.engine.item.Stakeholder;

/**
 * Test to check that buyer receives the land sold by the seller. E2E test.
 * 
 * @author W.Pasman
 *
 */
public class BuyerReceivesLandTest {

	private GameField gameField;

	/**
	 * login, create Game field.
	 */
	@Before
	public void before() throws LoginException, ProjectException {
		gameField = new GameField();
	}

	@After
	public void after() throws InterruptedException, ProjectException {

		gameField.close();
		gameField = null;
	}

	@Test
	public void buyerReceivedLand() throws InterruptedException {
		MyStakeholder municipality = gameField.addStakeholder(Stakeholder.Type.MUNICIPALITY);
		MyStakeholder inhabitant = gameField.addStakeholder(Stakeholder.Type.CIVILIAN); // inhabitant

		municipality.waitForAppearance();
		inhabitant.waitForAppearance();

		inhabitant.getEventHandler().resetUpdate(MapLink.LANDS, MapLink.POPUPS);
		municipality.getEventHandler().resetUpdate(MapLink.LANDS, MapLink.POPUPS);

		// sell land to inhabitant.
		municipality.sellLand(municipality.getSellableLand(), inhabitant.getStakeholder().getID());

		inhabitant.confirmLandTransaction(Type.BUY_LAND);

		municipality.confirmLandTransaction(Type.SELL_LAND);
		// check that the LANDS is sold
		municipality.getEventHandler().waitFor(MapLink.LANDS);

		municipality.close();
		inhabitant.close();
	}

}
