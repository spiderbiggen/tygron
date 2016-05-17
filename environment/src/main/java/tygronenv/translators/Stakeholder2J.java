package tygronenv.translators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Parameter2Java;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.data.engine.item.Stakeholder.Type;

public class Stakeholder2J implements Parameter2Java<Stakeholder> {

	public Stakeholder2J() {
		// Used for testing
	}

	@Override
	public Stakeholder translate(Parameter parameter) throws TranslationException {
		if (!(parameter instanceof Identifier)) {
			throw new TranslationException("expected identifier but got " + parameter);
		}
		String id = ((Identifier) parameter).getValue();

		List<Stakeholder> stakeholders = new ArrayList<>(
				EventManager.<Stakeholder> getItemMap(MapLink.STAKEHOLDERS).values());
		for (Stakeholder stakeholder : stakeholders) {
			if (id.equals(stakeholder.getName()))
				return stakeholder;
		}
		throw new TranslationException(
				"unknown stakeholder type " + id + ". Allowed are:" + Arrays.asList(Type.values()));

	}

	@Override
	public Class<Stakeholder> translatesTo() {
		return Stakeholder.class;
	}

}
