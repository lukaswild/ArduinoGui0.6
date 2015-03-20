package observer;

import java.util.ArrayList;

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

    public void notify(Observable senderClass, Object msg) {
        for(IObserver o : observers) {
            o.update(senderClass, msg);
        }
    }
}
