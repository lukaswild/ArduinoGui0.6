package generic;

import elements.Element;

/**
 * Created by Simon on 22.03.2015.
 */
public class ComObjectSingle {

    private Element model;
    private int position;
    private int projectId;
    private int actionNr;

    public Element getModel() {
        return model;
    }

    public void setModel(Element model) {
        this.model = model;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getActionNr() {
        return actionNr;
    }

    public void setActionNr(int actionNr) {
        this.actionNr = actionNr;
    }

    public ComObjectSingle(Element model, int position, int projectId, int actionNr) {
        this.model = model;
        this.position = position;
        this.projectId = projectId;
        this.actionNr = actionNr;
    }

    public ComObjectSingle() {}
}
