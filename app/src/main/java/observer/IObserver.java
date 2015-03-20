package observer;

/**
 * @author Simon
 *
 */
public interface IObserver {

//    public void update(Observable senderClass, Element modelInput, Element modelToUpdate, int inputElementPosition, int outputElementPosition, int projectId, int actionNr);

    public void update(Observable senderClass, Object msg);
}
