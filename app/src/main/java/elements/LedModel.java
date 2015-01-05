package elements;


import com.example.arduinogui.R;

public class LedModel extends BoolElement implements OutputElement {

	
	public LedModel(String name, boolean statusHigh) {
		super.name = name;
		super.statusHigh = statusHigh;
        super.setRessource(R.drawable.lamp_off);
	}
	
	public LedModel() {} 
	
	@Override
	public void receiveData() {
		// TODO Auto-generated method stub; überhaupt notwendig?
		System.out.println("Nicht ausprogrammiert - Nichts geschieht");
	}
	
}
