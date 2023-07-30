import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WebFetcher {

    public static void main(String[] args) {
        List<String> capture = new ArrayList<>();
        List<String> pics = new ArrayList<>();
        String nurl = "http://tecnotv.club/plus";

        while (nurl != null) {
            System.out.println("Current URL: " + nurl);
            nurl = gatheringData(capture, pics, nurl);
        }

        System.out.println("---- Gathered videos ----");
        for (String rmov : capture) {
            String rmov1 = rmov.substring(rmov.indexOf("http"), rmov.indexOf(".mp4") + 4).replace("\\/", "/").strip();
            System.out.println("video link: " + rmov1);
        }

        for (String pic : pics) {
            String splash = pic.substring(pic.indexOf("http"), pic.indexOf(".jpg") + 4).replace("\\/", "/").strip();
            System.out.println("splash: " + splash);
        }
    }

    public static String gatheringData(List<String> capture, List<String> pics, String nurl) {
        try {
            URL url = new URL(nurl);
            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            urlc.setRequestMethod("GET");
            try (BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.contains("<a class=\"elementor-post__thumbnail__link\"")) {
                        String urlFromLine = urlExtractor(line);
                        gatherDataFromUrl(urlFromLine, capture, pics);
                    }
                    if (line.contains("data-next-page=")) {
                        String segm = "data-next-page=", segm1 = "\"></div>";
                        nurl = line.substring(line.indexOf(segm) + segm.length() + 1, line.indexOf(segm1));
                        return nurl;
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public static String urlExtractor(String murl) {
        String idx0 = "<a class=\"elementor-post__thumbnail__link\" href=\"";
        String lastIdx = "\" >";
        int startIdx = murl.indexOf(idx0) + idx0.length();
        int endIdx = murl.indexOf(lastIdx, startIdx);
        return murl.substring(startIdx, endIdx);
    }

    public static void gatherDataFromUrl(String nurl, List<String> capture, List<String> pics) throws IOException {
        URL url = new URL(nurl);
        HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
        urlc.setRequestMethod("GET");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream()))) {
            String nline;
            while ((nline = br.readLine()) != null) {
                if (nline.contains(".mp4")) {
                    capture.add(nline);
                } else if (nline.contains("featuredImage")) {
                    pics.add(extractImageUrl(nline));
                }
            }
        }
    }

    public static String extractImageUrl(String fragmento) {
        String startTag = "\"featuredImage\":\"";
        String endTag = "\"}";
        int startIndex = fragmento.indexOf(startTag);
        if (startIndex != -1) {
            int endIndex = fragmento.indexOf(endTag, startIndex + startTag.length());
            if (endIndex != -1) {
                String imageUrlWithParams = fragmento.substring(startIndex + startTag.length(), endIndex);
                // Eliminate the parameters "?fit=600%2C900" from the URL
                int paramsIndex = imageUrlWithParams.indexOf("?");
                if (paramsIndex != -1) {
                    return imageUrlWithParams.substring(0, paramsIndex);
                }
                return imageUrlWithParams;
            }
        }
        return null;
    }
}
