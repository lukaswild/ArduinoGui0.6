package observer;

import generic.ComObject;

/**
 * @author Simon
 *
 */
public interface IObserver {

	public void update(Observable senderClass, ComObject comObject);
}
