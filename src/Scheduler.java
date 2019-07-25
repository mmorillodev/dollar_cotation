import com.google.gson.Gson;
import mine.utils.HttpRequest;
import mine.utils.CSVFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Scheduler extends TimerTask {

    private HttpRequest         request;
    private CSVFactory          factory;
    private Map<String, String> dollarCotation;
    private DateFormat          simpleDateFormat;
    private Calendar            currentDate;
    private int                 count;
    private int                 maxRequests;

    private final String KEY;
    private final String URL;

    {
        KEY = "b885cfe1";
        URL = "https://api.hgbrasil.com/finance";
    }

    @SuppressWarnings("all")
    public Scheduler() {
        this.request            = new HttpRequest(URL + "?key=" + KEY, "GET");
        this.factory            = new CSVFactory("C:\\Users\\Nescara\\Desktop\\Dollar_cotation.csv");
        this.simpleDateFormat   = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        factory.setHeaders("Moeda", "Valor de compra", "Valor de venda", "variação", "Data/Hora da cotação");
    }

    public Scheduler(int maxRequests) {
        this();
        this.maxRequests = maxRequests;
    }

    @Override
    public void run() {
        if((maxRequests > 0 && count == maxRequests)) {
            super.cancel();
        }

        this.currentDate = Calendar.getInstance();

        if(currentDate.get(Calendar.HOUR_OF_DAY) < 9 || currentDate.get(Calendar.HOUR_OF_DAY) > 16 || (currentDate.get(Calendar.HOUR_OF_DAY) == 16 && currentDate.get(Calendar.MINUTE) >= 15)) {
            System.out.println("Market closed! " + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE));
            return;
        }

        try {
            dollarCotation = getDollarCotation(request);

            if(dollarCotation == null)
                return;

            factory.addRecord(
                dollarCotation  .get("name"),
                dollarCotation  .get("buy"),
                dollarCotation  .get("sell"),
                dollarCotation  .get("variation"),
                simpleDateFormat.format(this.currentDate.getTime())
            );
            factory.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getDollarCotation(HttpRequest request) throws IOException {
        request.fireRequest();
        count++;

        System.out.println(request.getResponse().toString());

        if (request.getResponse().http_code != 200)
            return null;

        String s = request.getResponse().response_body;
        UOLResponse parsed = new Gson().fromJson(s, UOLResponse.class);

        if (parsed.results.currencies.get("USD") instanceof Map)
            return (Map<String, String>) parsed.results.currencies.get("USD");

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
