package functions;

public class EnvConfig {
    public String getValue(String key) {
       return System.getenv(key);
    }
}
