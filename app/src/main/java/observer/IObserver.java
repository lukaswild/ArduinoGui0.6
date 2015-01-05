package observer;

import elements.ComObject;
import android.view.View;
import android.widget.ViewSwitcher;

/**
 * @author Simon
 *
 */
public interface IObserver {

	public void update(Observable senderClass, ComObject comObject);
}
