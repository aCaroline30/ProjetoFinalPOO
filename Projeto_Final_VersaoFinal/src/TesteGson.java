import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
public class TesteGson {
  public static void main(String[] args) {
    Gson g = new GsonBuilder().create();
    System.out.println("Gson ok: " + g);
  }
}