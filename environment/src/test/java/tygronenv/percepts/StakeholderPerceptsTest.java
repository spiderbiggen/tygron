package tygronenv.percepts;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.data.engine.item.Stakeholder.Type;

/**
 * Tests the stakeholder percepts.
 * @author Rico
 *
 */
public class StakeholderPerceptsTest {
	
	private final Type typeEducation = Type.EDUCATION;
	private final Type typeExpert = Type.EXPERT;
	private final Type typeFarmer = Type.FARMER;
	private final Type typeMunicipality = Type.MUNICIPALITY;
	
	
	private Stakeholder stakeEducation = new Stakeholder();
	private Stakeholder stakeExpert = new Stakeholder();;
	private Stakeholder stakeFarmer = new Stakeholder();;
	private Stakeholder stakeMunicipality = new Stakeholder();;
	
	private StakeholderPercepts stakeObject;
	private ArrayList<Stakeholder> stakeholders;
	
	
	/**
	 * Setup the tests.
	 */
	@Before
	public void setUp() {
		//We can't use mockito because getType is final.
		stakeEducation.setType(typeEducation);
		stakeExpert.setType(typeExpert);
		stakeFarmer.setType(typeFarmer);
		stakeMunicipality.setType(typeMunicipality);
		
		stakeholders = new ArrayList<Stakeholder>();
		addStakeholders();
		
	}
	
	/**
	 * Add all stakeholders to the list.
	 */
	private void addStakeholders() {
		stakeholders.add(stakeEducation);
		stakeholders.add(stakeExpert);
		stakeholders.add(stakeFarmer);
		stakeholders.add(stakeMunicipality);
	}
	
	
	/**
	 * Test if the farmer stakeholder
	 * is in the list.
	 */
	@Test
	public void testCorrectStakeholder() {
		stakeObject = new StakeholderPercepts(stakeholders, Type.FARMER);
		Stakeholder.Type type = stakeObject.getYourStakeholder().getType();
		assertTrue(type.equals(Type.FARMER));
	}
	
	/**
	 * Test if the municipality stakeholder
	 * is in the list.
	 */
	@Test
	public void testCorrecStakeholder2() {
		stakeObject = new StakeholderPercepts(stakeholders, Type.MUNICIPALITY);
		Stakeholder.Type type = stakeObject.getYourStakeholder().getType(); 
		assertTrue(type.equals(Type.MUNICIPALITY));
	}
	
	/**
	 * Test if it throws a exception
	 * when the media stakeholder is
	 * not in the list of stakeholders.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testWrongStakeholder() {
		stakeObject = new StakeholderPercepts(stakeholders, Type.MEDIA);
	}

}
