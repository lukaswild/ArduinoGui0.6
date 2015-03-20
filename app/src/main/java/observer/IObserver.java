package observer;

/**
 * @author Simon
 *
 */
public interface IObserver {

    public void update(Observable senderClass, Object msg);
}
