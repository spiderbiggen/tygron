/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.engine.serializable;

import java.io.Serializable;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.StringUtils;

/**
 *
 * @author Frank Baars
 *
 */
public class GraphInformation implements Serializable {

    public enum GraphType {
        NONE, LINE_GRAPH, BAR_GRAPH, STACKED_BAR_GRAPH, XY_LOGSCALED_XY_LINE_GRAPH
    }

    private static final long serialVersionUID = -7649108830044046315L;

    @XMLValue
    private GraphType graphType = GraphType.LINE_GRAPH;

    @XMLValue
    private GraphVariableData graphVariableData;

    @XMLValue
    private String horizontalAxisTerm = StringUtils.EMPTY;

    @XMLValue
    private String verticalAxisTerm = StringUtils.EMPTY;

    @XMLValue
    private String scoringFactorsGraphName = StringUtils.EMPTY;

    public GraphInformation() {
        graphVariableData = new GraphVariableData();
    }

    public GraphVariableData getGraphData() {
        return this.graphVariableData;
    }

    public String getGraphName() {
        return scoringFactorsGraphName;
    }

    public GraphType getGraphType() {
        return this.graphType;
    }

    public String getHorizontalAxisTerm() {
        return horizontalAxisTerm;
    }

    public void getHorizontalAxisTerm(String horizontalAxisTerm) {
        this.horizontalAxisTerm = horizontalAxisTerm;
    }

    public String getVerticalAxisTerm() {
        return verticalAxisTerm;
    }

    public void getVerticalAxisTerm(String verticalAxisTerm) {
        this.verticalAxisTerm = verticalAxisTerm;
    }
}
