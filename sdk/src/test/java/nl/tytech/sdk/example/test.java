package nl.tytech.sdk.example;

import java.util.List;

import nl.tytech.core.net.event.IOServiceEventType;
import nl.tytech.data.engine.event.ParticipantEventType;

public class test {
	public static void main(String[] args) {
		checkIOServiceEventType();
		System.out.println("--------");
		checkParticipantEventType();
	}

	public static void checkIOServiceEventType() {
		IOServiceEventType o = IOServiceEventType.CHANGE_PROJECT_DOMAIN;
		System.out.println(o);
		List<Class<?>> c = o.getClasses();
		System.out.println(c);
		Class<?> r = o.getResponseClass();
		System.out.println(r);
		System.out.println(o.getAccessLevel());

	}

	public static void checkParticipantEventType() {

		ParticipantEventType o = ParticipantEventType.BUILDING_PLAN_DEMOLISH;
		System.out.println(o);
		List<Class<?>> c = o.getClasses();
		System.out.println(c);
		Class<?> r = o.getResponseClass();
		System.out.println(r);

	}
}
