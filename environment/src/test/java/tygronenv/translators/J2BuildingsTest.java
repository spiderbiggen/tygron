package tygronenv.translators;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;
import org.mockito.Mockito;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Translator;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import nl.tytech.data.engine.item.Building;
import nl.tytech.data.engine.serializable.Category;

/**
 * Created by Stefan Breetveld on 23-5-2016.
 * In package tygronenv.translators.
 */
public class J2BuildingsTest {

    private Translator translator = Translator.getInstance();

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

        translator.registerJava2ParameterTranslator(new J2Building());
        translator.registerJava2ParameterTranslator(new J2Category());

        Parameter[] params = translator.translate2Parameter(spyBuilding);
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
