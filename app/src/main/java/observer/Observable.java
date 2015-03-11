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

    // TODO mit nur zwei Parameter, wobei der zweite ein Object ist --> Für unterschiedlich benötigte Daten Klassen erzeugen
    public void notify(Observable senderClass, Element modelInput, Element modelToUpdate, int inputElementPosition, int outputElementPosition, int projectId, int actionNr) {
        for (IObserver o : observers) {
            o.update(senderClass, modelInput, modelToUpdate, inputElementPosition, outputElementPosition, projectId, actionNr);
        }
    }
	
	
//	public void notify(Object msg) {
//		for (IObserver o : observers) {
//			o.update(this, msg);
//		}
//	}
}
