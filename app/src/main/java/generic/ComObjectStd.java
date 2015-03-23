package generic;

import elements.Element;

public class ComObjectStd {

//    public void notify(Observable senderClass, Element modelInput, Element modelToUpdate, int inputElementPosition, int outputElementPosition, int projectId, int actionNr) {
//        for (IObserver o : observers) {
//            o.update(senderClass, modelInput, modelToUpdate, inputElementPosition, outputElementPosition, projectId, actionNr);
//        }
//    }


    private Element modelInput;
    private Element modelOutput;
    private int inputElementPosition;
    private int outputElementPosition;
    private int projectId;
    private int actionNr;


    public Element getModelInput() {
        return modelInput;
    }

    public void setModelInput(Element modelInput) {
        this.modelInput = modelInput;
    }

    public Element getModelOutput() {
        return modelOutput;
    }

    public void setModelOutput(Element modelOutput) {
        this.modelOutput = modelOutput;
    }

    public int getInputElementPosition() {
        return inputElementPosition;
    }

    public void setInputElementPosition(int inputElementPosition) {
        this.inputElementPosition = inputElementPosition;
    }

    public int getOutputElementPosition() {
        return outputElementPosition;
    }

    public void setOutputElementPosition(int outputElementPosition) {
        this.outputElementPosition = outputElementPosition;
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

    public ComObjectStd(Element modelInput, Element modelOutput, int inputElementPosition, int outputElementPosition, int projectId, int actionNr) {
        this.modelInput = modelInput;
        this.modelOutput = modelOutput;
        this.inputElementPosition = inputElementPosition;
        this.outputElementPosition = outputElementPosition;
        this.projectId = projectId;
        this.actionNr = actionNr;
    }

    public ComObjectStd() {}
}
