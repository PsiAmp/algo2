import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WebCrawler {

    private Queue<String> toVisit;
    private Set<String> visitedUrls;

    public WebCrawler() {
        toVisit = new LinkedList<>();
        visitedUrls = new HashSet<>();
    }

    public void crawl(String root) {
        toVisit.add(root);

        while (!toVisit.isEmpty() && visitedUrls.size() < 1000) {
            String toVisitUrl = toVisit.poll();
            String s = null;

            try {
                URL url = new URL(toVisitUrl);
                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();

                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                s = in.lines().collect(Collectors.joining());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (s != null) {
                String regexp = "(http|https)://(\\w+\\.)+(edu|com|gov|org)";
                Pattern pattern = Pattern.compile(regexp);
                Matcher matcher = pattern.matcher(s);

                while (matcher.find()) {
                    String matchedUrl = matcher.group();
                    if (!visitedUrls.contains(matchedUrl)) {
                        toVisit.add(matchedUrl);
                        visitedUrls.add(matchedUrl);
                        System.out.println(matchedUrl);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        WebCrawler webCrawler = new WebCrawler();
        webCrawler.crawl("https://www.reddit.com/");
    }
}
