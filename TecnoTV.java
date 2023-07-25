class TecnoTV{
  private List<ProductProfile> productProfiles = new ArrayList<>();
  
  public static void main(String[] args) {
        String url = "http://tecnotv.club/plus/" + movie;
        List<String> links = extractLinksFromHTML(url);
        for (String link : links) {
            System.out.println(link);
        }
    }
  
private List<String> extractLinksFromHTML(String url) {
        List<String> links = new ArrayList<>();
        try {
            URLConnection conn = new URL(url).openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder htmlContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                htmlContent.append(line);
            }
            reader.close();

            // Parsear el HTML utilizando JSoup
            Document doc = Jsoup.parse(htmlContent.toString());

            // Buscar todos los elementos div con atributo data-item
            Elements divElements = doc.select("div[data-item]");

            // Expresiones regulares para extraer los enlaces de video y de imagen
            Pattern pattern = Pattern.compile("\"src\":\"(https?:\\\\/\\\\/[^\"]+\\.mp4)\"|\"splash\":\"(https?:\\\\/\\\\/[^\"]+)\"");
            //Pattern pattern = Pattern.compile("\"src\":\"(.*?)\",\"type\":\"video\\/mp4\"|\"splash\":\"(.*?)\"");

            // Iterar sobre los elementos div para extraer los enlaces
            for (Element div : divElements) {
                String dataItem = div.attr("data-item");

                // Extraer los enlaces utilizando la expresión regular
                Matcher matcher = pattern.matcher(dataItem);
                ProductProfile mProfile = new ProductProfile("Movie", "", "", "");;
                while (matcher.find()) {
                    if (matcher.group(1) != null) {
                        String video = URLDecoder.decode(matcher.group(1), "UTF-8").replace("\\/", "/");
                        System.out.println("URL del video: " + video);
                        mProfile.setProductUrl(video);
                    } else if (matcher.group(2) != null) {
                        String splash = URLDecoder.decode(matcher.group(2), "UTF-8").replace("\\/", "/");
                        System.out.println("URL de la imagen: " + splash);
                        mProfile.setImageUrl(splash);
                    }
                }
                productProfiles.add(mProfile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return links;
    }

  private static class MovieProfile {

        private String title;
        private String imageUrl;
        private String productUrl;

        public MovieProfile(String title, String imageUrl, String productUrl) {
            this.title = title;
            this.imageUrl = imageUrl;
            this.productUrl = productUrl;
        }

        public String getTitle() {
            return title;
        }
        public String getImageUrl() {
            return imageUrl;
        }

        public String getProductUrl() {
            return productUrl;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public void setProductUrl(String productUrl) {
            this.productUrl = productUrl;
        }

    }
}
