package tygronenv.percepts;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.data.engine.item.Stakeholder.Type;

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
	
	private void addStakeholders() {
		stakeholders.add(stakeEducation);
		stakeholders.add(stakeExpert);
		stakeholders.add(stakeFarmer);
		stakeholders.add(stakeMunicipality);
	}
	
	
	@Test
	public void testCorrectStakeholder() {
		stakeObject = new StakeholderPercepts(stakeholders, Type.FARMER);
		assertTrue(stakeObject.getYourStakeholder().getType().equals(Type.FARMER));
	}
	
	@Test
	public void testCorrecStakeholder2() {
		stakeObject = new StakeholderPercepts(stakeholders, Type.MUNICIPALITY);
		assertTrue(stakeObject.getYourStakeholder().getType().equals(Type.MUNICIPALITY));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testWrongStakeholder() {
		stakeObject = new StakeholderPercepts(stakeholders, Type.MEDIA);
	}

}
