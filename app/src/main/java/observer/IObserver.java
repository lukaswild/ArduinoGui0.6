package observer;

import elements.Element;

/**
 * @author Simon
 *
 */
public interface IObserver {

//	public void update(Observable senderClass, ComObject comObject);

    public void update(Observable senderClass, Element modelToUpdate, int position);
}
