package functions.api.dynamicmailer;

import functions.api.FormLabelValue;

import java.util.List;

public class EventEmailTemplate {
  public String eventTypeDisplayName;
  public List<FormLabelValue> formValues;
  public String getEventTypeDisplayName() {
    return this.eventTypeDisplayName;
  }
  public void setEventTypeDisplayName(String e) {
    this.eventTypeDisplayName = e;
  }
  public List<FormLabelValue> getFormValues() {
    return this.formValues;
  }
  public void setFormValues(List<FormLabelValue> formValues) {
    this.formValues = formValues;
  }
}
