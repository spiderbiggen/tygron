/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.serializable.Category;

/**
 * Economy
 * <p>
 * This class keeps track of the this Economy of one customertype
 * </p>
 * @version $Revision: 1.3 $ <br>
 * @author Maxim Knepfle
 */
public class Economy extends Item {

    public enum State {
        GOOD(1.0f), NORMAL(0.95f), BAD(0.8f);

        private double effect;

        private State(double effect) {
            this.effect = effect;
        }

        public double getEffect() {
            return effect;
        }
    }

    private static final long serialVersionUID = 5186541964688923834L;

    @XMLValue
    private Category category = Category.OTHER;

    @XMLValue
    private State state = State.NORMAL;

    public Economy() {
    }

    public Economy(Category category, State state) {
        this.category = category;
        this.state = state;
    }

    public Category getFunctionCategory() {
        return category;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return this.getFunctionCategory().toString();
    }

}
