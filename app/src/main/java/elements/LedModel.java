package elements;


import com.example.arduinogui.R;

public class LedModel extends BoolElement implements OutputElement {

    int resourceLedOff = R.drawable.lamp_on;
    int resourceLedOn = R.drawable.lamp_off;

    public LedModel(String name, boolean statusHigh) {
        super.name = name;
        super.statusHigh = statusHigh;
        super.kind = "Led";
        if(statusHigh)
            super.setResource(R.drawable.lamp_on);
        else
            super.setResource(R.drawable.lamp_off);

    }

    public LedModel() {
        super.kind = "Led";
    }

    @Override
    public void setResource(boolean status) {
        if(status)
            super.setResource(resourceLedOn);
        else
            super.setResource(resourceLedOff);
    }

    public int getResourceLedOn() {
        return resourceLedOn;
    }

    public void setResourceLedOn(int resourceLedOn) {
        this.resourceLedOn = resourceLedOn;
    }

    public int getResourceLedOff() {
        return resourceLedOff;
    }

    public void setResourceLedOff(int resourceLedOff) {
        this.resourceLedOff = resourceLedOff;
    }

}
