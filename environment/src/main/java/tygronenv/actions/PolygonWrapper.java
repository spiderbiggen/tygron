package tygronenv.actions;

import com.vividsolutions.jts.geom.MultiPolygon;

import nl.tytech.core.net.serializable.PolygonItem;

/**
 * Class for wrapping MultiPolygons in a PolygonItem.
 * This way they can be passed around in GetRelevantAreas.
 * @author Max Groenenboom
 */
public class PolygonWrapper implements PolygonItem {

	private MultiPolygon multiPolygon;

	public PolygonWrapper(MultiPolygon mp) {
		multiPolygon = mp;
	}

	@Override
	public Integer getID() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public MultiPolygon[] getQTMultiPolygons() {
		MultiPolygon[] result = {multiPolygon};
		return result;
	}

}
