package observer;

import java.util.ArrayList;

import elements.Element;

public class Observable {

	private ArrayList<IObserver> observers;


    public Observable() {
        observers = new ArrayList<IObserver>();
    }

    public void addToObservers(IObserver o) {
        observers.add(o);
    }

    public void removeFromObservers(IObserver o) {
        observers.remove(o);
    }


//    public void notify(ComObject comObject) {
//        for (IObserver o : observers) {
//            o.update(this, comObject);
//        }
//    }

    public void notify(Observable senderClass, Element modelToUpdate, int inputElementPosition, int outputElementPosition) {
        for (IObserver o : observers) {
            o.update(senderClass, modelToUpdate, inputElementPosition, outputElementPosition);
        }
    }
	
	
//	public void notify(Object msg) {
//		for (IObserver o : observers) {
//			o.update(this, msg);
//		}
//	}
}
