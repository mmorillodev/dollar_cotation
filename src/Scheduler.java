import com.google.gson.Gson;
import mine.utils.HttpRequest;
import mine.utils.CSVFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class Scheduler extends TimerTask {

    private HttpRequest         request;
    private CSVFactory          factory;
    private Map<String, String> dollarCotation;
    private int                 count;
    private DateFormat          simpleDateFormat;

    private final String KEY    = "b885cfe1";
    private final String URL    = "https://api.hgbrasil.com/finance";

    @SuppressWarnings("all")
    public Scheduler() {
        this.request = new HttpRequest(URL + "?key=" + KEY, "GET");
        this.factory = new CSVFactory("C:\\Users\\Nescara\\Desktop");
        this.simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        factory.setHeaders("Moeda", "Valor de compra", "Valor de venda", "variação", "Data/Hora da cotação");
    }

    @Override
    public void run() {
        if(count++ == 10)
            System.exit(1);

        try {
            dollarCotation = execute(request);

            if(dollarCotation == null)
                return;

            factory.addRecord(
                    dollarCotation  .get("name"),
                    dollarCotation  .get("buy"),
                    dollarCotation  .get("sell"),
                    dollarCotation  .get("variation"),
                    simpleDateFormat.format(new Date())

            );
            factory.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> execute(HttpRequest request) throws IOException {
        request.fireRequest();

        System.out.println(request.getResponse().toString());

        if(request.getResponse().http_code != 200)
            return null;

        String s = request.getResponse().response_body;
        UOLResponse parsed = new Gson().fromJson(s, UOLResponse.class);

        if(parsed.results.currencies.get("USD") instanceof Map)
            return (Map<String, String>)parsed.results.currencies.get("USD");

        return null;
    }
}

@SuppressWarnings("unused")
class UOLResponse {
    String by;
    String valid_key;
    Results results;
    double execution_time;
    boolean from_cache;
}

@SuppressWarnings("unused")
class Results {
    Map<String, Object> currencies;
    Map<String, Map<String, String>> stocks;
    List<String> available_sources;
    Map<String, Object> bitcoin;
    List<Object> taxes;
}
