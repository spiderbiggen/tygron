package tygronenv.translators;
 
 
 import static org.mockito.Mockito.mock;
 import static org.mockito.Mockito.when;
 import static org.mockito.Mockito.verify;

import java.util.ArrayList;
 import org.junit.Before;
 import org.junit.Test;

import com.vividsolutions.jts.geom.MultiPolygon;

import eis.eis2java.exception.TranslationException;
import nl.tytech.data.engine.item.Function;
import nl.tytech.data.engine.item.Zone;
import nl.tytech.data.engine.serializable.Category;
 
 /**
  * Test class for the J2Stakeholder translator
  * @author Tom
  *
  */
 public class J2ZoneTest {
     /**
      * Translator for the Zone class.
      */
     private J2Zone translator;
     
     /**
      * Zone instance to translate.
      */
     private Zone z;
     
     /**
      * Initialization method called before every test.
      */
     @Before
     public void init() {
         translator = new J2Zone();
         
         z = mock(Zone.class);
         MultiPolygon mp = mock(MultiPolygon.class);

  		Function f = mock(Function.class);
  		
  		ArrayList<nl.tytech.data.engine.item.Function> func = new ArrayList<nl.tytech.data.engine.item.Function>();
  		func.add(f);
  		
 		when(z.getfunctions()).thenReturn(func);
 		when(z.getMultiPolygon()).thenReturn(mp);
 		when(mp.getArea()).thenReturn(5.0);
     }
     
     /**
      * Test method which verifies that methods that are called.
      * The other methods cannot be verified, since they are final methods.
      * That does not work with mockito.
      * @throws TranslationException thrown if translating fails.
      */
     @Test
     public void testTranslate() throws TranslationException {
         translator.translate(z);
         //verify(z).getName();
         
     }
     
     /*package tygronenv.translators;

     import org.junit.Test;
     import static org.mockito.Mockito.*;

     import java.util.ArrayList;

     import eis.eis2java.exception.TranslationException;
     import eis.eis2java.translation.Java2Parameter;
     import eis.iilang.Parameter;
     import nl.tytech.data.engine.item.Building;
     import nl.tytech.data.engine.item.Function;
     import nl.tytech.data.engine.item.Zone;
     import nl.tytech.data.engine.serializable.Category;

     public class J2ZoneTest{

     	@Test
     	public void test() throws TranslationException {
     		J2Zone translator = new J2Zone();
     		
     		Zone z = mock(Zone.class);
     		Function f = mock(Function.class);
     		//Category c = mock(Category.class);
     		
     		ArrayList<nl.tytech.data.engine.item.Function> func = new ArrayList<nl.tytech.data.engine.item.Function>();
     		func.add(f);
     		
     		ArrayList<Category> cat = new ArrayList<>();
     		cat.add(null);
     		
     		when(z.getID()).thenReturn(1);
     		when(z.getName()).thenReturn("name");
     		when(z.getMaxAllowedFloors()).thenReturn(2);
     		when(z.getAllowedCategories()).thenReturn(cat);
     		when(f.getID()).thenReturn(10);
     		when(z.getfunctions()).thenReturn(func);
     		when(z.getMultiPolygon().getArea()).thenReturn(3.0);
     		
     		Parameter[] p = translator.translate(z);
     		
     		System.out.println(p[0]);
     		assert(p[0].equals("Zone"));
     	}
     	
     	
     }
*/
 
 }