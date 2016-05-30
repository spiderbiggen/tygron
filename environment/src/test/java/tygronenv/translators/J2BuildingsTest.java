package tygronenv.translators;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;
import org.mockito.Mockito;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Translator;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import nl.tytech.data.engine.item.Building;
import nl.tytech.data.engine.serializable.Category;
import nl.tytech.data.engine.serializable.MapType;

import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by Stefan Breetveld on 23-5-2016.
 * In package tygronenv.translators.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Building.class)
public class J2BuildingsTest {

    private Translator translator = Translator.getInstance();

    @Test
    public void J2BuildingTest() throws TranslationException {
        int buildingID = 10;
        String name = "testBuilding";
        int ownerID = 10;
        int buildYr = 1950;
        GeometryFactory gf = new GeometryFactory();
        Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(10, 10);
        coordinates[1] = new Coordinate(10, 20);
        coordinates[2] = new Coordinate(20, 20);
        coordinates[3] = new Coordinate(20, 10);
        coordinates[4] = new Coordinate(10, 10);
        Polygon[] polygonArray = {gf.createPolygon(coordinates)};
		MultiPolygon mp = gf.createMultiPolygon(polygonArray);
        Collection<Category> categories = new ArrayList<>();
        Category cat1 = Category.EDUCATION;
        Category cat2 = Category.BRIDGE;
        categories.add(cat1);
        categories.add(cat2);
        int floors = 5;

        translator.registerJava2ParameterTranslator(new J2Building());
        translator.registerJava2ParameterTranslator(new J2Category());
        translator.registerJava2ParameterTranslator(new J2MultiPolygon());

        Building b = PowerMockito.mock(Building.class);

        PowerMockito.when(b.getName()).thenReturn(name);
        PowerMockito.when(b.getID()).thenReturn(buildingID);
        PowerMockito.when(b.getOwnerID()).thenReturn(ownerID);
        PowerMockito.when(b.getConstructionYear()).thenReturn(buildYr);
        PowerMockito.when(b.getFloors()).thenReturn(floors);
        PowerMockito.when(b.getCategories()).thenReturn(categories);
        PowerMockito.when(b.getMultiPolygon(MapType.MAQUETTE)).thenReturn(mp);

        Parameter[] params = translator.translate2Parameter(b);
        Function func = (Function) params[0];
        LinkedList<Parameter> parameters = func.getParameters();

        ParameterList paramCategories = new ParameterList();
        paramCategories.add(new Identifier(cat1.name()));
        paramCategories.add(new Identifier(cat2.name()));
        Parameter multiPolygon = new Function("multipolygon", new Identifier(mp.toText()));

        System.out.println(func);
        assertEquals("building", func.getName());
        assertEquals(new Numeral(buildingID), parameters.get(0));
        assertEquals(new Identifier(name), parameters.get(1));
        assertEquals(new Numeral(ownerID), parameters.get(2));
        assertEquals(new Numeral(floors), parameters.get(3));
        assertEquals(new Numeral(buildYr), parameters.get(4));
        assertEquals(paramCategories, parameters.get(5));
        assertEquals(multiPolygon, parameters.get(6));

    }

}