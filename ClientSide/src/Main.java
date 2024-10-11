import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {

    private static String baseURL = "http://localhost:8080/";
    private static String token;

    public static void main(String[] args) {

        HttpClient httpClient = HttpClient.newHttpClient();
        Scanner scanner = new Scanner(System.in);


       while (true){
           System.out.println("Välj: ");
           System.out.println("1. registrera");
           System.out.println("2. logga in");
           System.out.println("3. skapa meddelande");
           System.out.println("4. se meddelande");
           System.out.println("5. avsluta");

           int selected = scanner.nextInt();
           scanner.nextLine();

           switch (selected){
               case 1:
                   register(httpClient, scanner);
                   break;
               case 2:
                   login(httpClient,scanner);
                   break;
               case 3:
                   postMessage(httpClient,scanner);
                   break;
               case 4:
                   seeMessage(httpClient);
                   break;
               case 5:
                   System.out.println("utloggad");
                   return;

               default:
                   System.out.println("finns inget alternativ, försök igen!");
           }
       }

    }


    //returnerar true om token inte är null
    public static boolean loggedIn(){
       return token != null;
    }
    private static void seeMessage(HttpClient httpClient) {
        //ser till att du måste vara inloggad innan du kan fortsätta
        if (!loggedIn()){
            System.out.println("logga in");
            return;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseURL + "message/see"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> res = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("meddelandet: " +  res.body());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void postMessage(HttpClient httpClient, Scanner scanner) {
        //ser till att du måste vara inloggad innan du kan fortsätta
        if (!loggedIn()){
            System.out.println("logga in");
            return;
        }
        System.out.println("Skriv in meddelande");
        String message = scanner.nextLine();

        JSONObject json = new JSONObject();
        json.put("message", message);

        //authorization med static token som man får vid login och kopplat till serverside
        //så man endast kan se och skapa sitt egna message genom token
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseURL + "message/post"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(json.toJSONString()))
                    .build();

            HttpResponse<String> res = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("nytt meddelande inlagt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void login(HttpClient httpClient, Scanner scanner) {
        System.out.println("Email: ");
        String email = scanner.nextLine();
        System.out.println("password: ");
        String password = scanner.nextLine();

        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("password", password);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseURL + "login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toJSONString()))
                    .build();

            HttpResponse<String> res = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("inloggad: " + email);

            //om allt är ok och statuskod 200 logga in. felhantering så man ser när man är inloggad
            if (res.statusCode() == 200){
                token = res.body();
            } else {
                System.out.println("något gick fel");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void register(HttpClient httpClient, Scanner scanner) {
        System.out.println("Email: ");
        String email = scanner.nextLine();
        System.out.println("password: ");
        String password = scanner.nextLine();

        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("password", password);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseURL + "register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toJSONString()))
                    .build();

            HttpResponse<String> res = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("registrerad");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}