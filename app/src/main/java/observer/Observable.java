package observer;

import java.util.ArrayList;

import elements.ComObject;
import android.view.View;

public class Observable {

	private ArrayList<IObserver> observers;


    public Observable() {
        observers = new ArrayList<IObserver>();
    }

    public void add(IObserver o) {
        observers.add(o);
    }

    public void remove(IObserver o) {
        observers.remove(o);
    }


    public void notify(ComObject comObject) {
		for (IObserver o : observers) {
			o.update(this, comObject);
		}
	}
	
	
//	public void notify(Object msg) {
//		for (IObserver o : observers) {
//			o.update(this, msg);
//		}
//	}
}
