package functions.services;


public class FormLabelValue {
    public String label;
    public String value;

    public FormLabelValue(String l, String v){
        label = l;
        value = v;
    }

    String getLabel(){
        return this.label;
    }

    String getValue(){
        return this.value;
    }

}
