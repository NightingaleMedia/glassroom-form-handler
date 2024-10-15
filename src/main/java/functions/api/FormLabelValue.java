package functions.api;


public class FormLabelValue {
    public String label;
    public String value;

    public FormLabelValue(String l, String v){
        label = l;
        value = v;
    }

    public String getLabel(){
        return this.label;
    }

    public String getValue(){
        return this.value;
    }

}
