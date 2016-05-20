package tygronenv.translators;

import static org.junit.Assert.assertEquals;

import eis.iilang.*;
import nl.tytech.data.engine.item.Building;
import nl.tytech.data.engine.serializable.Category;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;

import eis.eis2java.exception.NoTranslatorException;
import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Translator;
import org.junit.runners.ParentRunner;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class TranslatorsTest {
	Translator translatorfactory = Translator.getInstance();

	@Test
	public void MultiPolygon2JTest() throws NoTranslatorException, TranslationException {
		translatorfactory.registerParameter2JavaTranslator(new MultiPolygon2J());

		Function parameter = new Function("square", new Numeral(1.0), new Numeral(2.0), new Numeral(3.0),
				new Numeral(4.0));

		MultiPolygon polygon = translatorfactory.translate2Java(parameter, MultiPolygon.class);
		// check first coordinate, should be the (1,2)
		Coordinate c = polygon.getCoordinate();
		assertEquals(1.0, c.getOrdinate(0), 0.0001);
		assertEquals(2.0, c.getOrdinate(1), 0.0001);
	}

	@Test(expected = TranslationException.class)
	public void MultiPolygon2JTestMissingNumber() throws NoTranslatorException, TranslationException {
		translatorfactory.registerParameter2JavaTranslator(new MultiPolygon2J());

		Function parameter = new Function("square", new Numeral(1.0), new Numeral(3.0), new Numeral(4.0));

		MultiPolygon polygon = translatorfactory.translate2Java(parameter, MultiPolygon.class);
		// check first coordinate, should be the (1,2)
		Coordinate c = polygon.getCoordinate();
		assertEquals(1.0, c.getOrdinate(0), 0.0001);
		assertEquals(2.0, c.getOrdinate(1), 0.0001);
	}

	@Test(expected = TranslationException.class)
	public void MultiPolygon2JTestWrongArgType() throws NoTranslatorException, TranslationException {
		translatorfactory.registerParameter2JavaTranslator(new MultiPolygon2J());

		Function parameter = new Function("square", new Numeral(1.0), new Identifier("2.0"), new Numeral(3.0),
				new Numeral(4.0));

		MultiPolygon polygon = translatorfactory.translate2Java(parameter, MultiPolygon.class);
		// check first coordinate, should be the (1,2)
		Coordinate c = polygon.getCoordinate();
		assertEquals(1.0, c.getOrdinate(0), 0.0001);
		assertEquals(2.0, c.getOrdinate(1), 0.0001);
	}

	@Test
    public void J2ExtBuildingTest() throws TranslationException {

        int buildingID = 10;
        String name = "testBuilding";
        int ownerID = 10;
        int buildYr = 1950;
        Collection<Category> categories = new ArrayList<>();
        Category cat1 = Category.EDUCATION;
        Category cat2 = Category.BRIDGE;
        categories.add(cat1);
        categories.add(cat2);
        int floors = 5;
        Building building = new Building(0, name);
        building.setId(buildingID);
        building.setOwnerID(ownerID);
        building.setConstructionYear(buildYr);
        Building spyBuilding = Mockito.spy(building);
        Mockito.doReturn(floors).when(spyBuilding).getFloors();
        Mockito.doReturn(categories).when(spyBuilding).getCategories();

        translatorfactory.registerJava2ParameterTranslator(new J2Building());
        translatorfactory.registerJava2ParameterTranslator(new J2Category());

        Parameter[] params = translatorfactory.translate2Parameter(spyBuilding);
        Function func = (Function) params[0];
        LinkedList<Parameter> parameters = func.getParameters();

        ParameterList paramCategories = new ParameterList();
        paramCategories.add(new Identifier(cat1.name()));
        paramCategories.add(new Identifier(cat2.name()));

		assertEquals("building", func.getName());
        assertEquals(new Numeral(buildingID), parameters.get(0));
        assertEquals(new Identifier(name), parameters.get(1));
        assertEquals(new Numeral(ownerID), parameters.get(2));
        assertEquals(new Numeral(buildYr), parameters.get(3));
        assertEquals(paramCategories, parameters.get(4));
        assertEquals(new Numeral(floors), parameters.get(5));

    }

}
