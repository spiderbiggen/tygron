package tygronenv.percepts;

import eis.iilang.Percept;

public class PerceptTest implements SimplePercept{

	@Override
	public Percept getPercept() {
		return new Percept("testpercept");
	}

	@Override
	public String getPerceptName() {
		return null;
	}

}
