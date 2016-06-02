package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Parameter2Java;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import nl.tytech.data.engine.item.Building.GroundLayerType;

public class GroundLayerType2J implements Parameter2Java<GroundLayerType> {

	@Override
	public GroundLayerType translate(Parameter parameter) throws TranslationException {
		Identifier name = (Identifier) parameter;
		if (name.equals("SURFACE")) {
			return GroundLayerType.SURFACE;
		} else {
			return GroundLayerType.UNDERGROUND;
		}
	}

	@Override
	public Class<GroundLayerType> translatesTo() {
		return GroundLayerType.class;
	}
}