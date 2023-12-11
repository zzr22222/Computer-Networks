import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dangdang1 {
    private static final String SEARCH_URL = "http://search.dangdang.com/?key=%B6%F9%CD%AF%B0%D9%BF%C6%C8%AB%CA%E9%D7%A2%D2%F4%B0%E6&act=input";
    private static final String OUTPUT_FILE = "books.txt";
    private static final int NUM_THREADS = 10;
    private static final int MAX_DEPTH = 3;

    private static LinkedBlockingQueue<UrlDepthPair> urlQueue = new LinkedBlockingQueue<>();
    private static ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
    private static Set<String> visitedUrls = new HashSet<>();

    public static void main(String[] args) throws IOException {
        // 添加初始URL到队列
        urlQueue.add(new UrlDepthPair(SEARCH_URL, 1));

        // 启动多线程爬虫
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.execute(() -> {
                try {
                    crawl();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();
    }


    public static void crawl() throws IOException {
        while (!urlQueue.isEmpty()) {
            UrlDepthPair urlDepthPair = urlQueue.poll();
            if (urlDepthPair != null) {
                String url = urlDepthPair.getUrl();
                int depth = urlDepthPair.getDepth();

                // 检查URL是否已被访问过
                if (visitedUrls.contains(url)) {
                    continue;
                }
                //url未访问过，则入队
                visitedUrls.add(url);

                // 检查URL是否包含协议
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    continue;
                }
                //HttpURLConnection类解析url
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                //ByteArrayOutputStream和ByteArrayInputStream输入输出流读取网页内容
                byte[] buf = new byte[4096];
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try (InputStream inputStream = connection.getInputStream()) {
                    int nread;
                    while ((nread = inputStream.read(buf)) > 0) {
                        byteArrayOutputStream.write(buf, 0, nread);
                    }
                }
                String encoding = "gb2312";//网页编码

                // 解析网页
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                Document document = Jsoup.parse(byteArrayInputStream, encoding, url);
                parseBooks(document);

                //指定深度
                if (depth < MAX_DEPTH) {
                    Elements nextPage = document.select(".next");
                    for (Element link : nextPage) {
                        urlQueue.add(new UrlDepthPair(link.absUrl("href"), depth + 1));
                    }
                }
            }
        }
    }


    public static void parseBooks(Document document) throws IOException {
        Elements bookElements = document.select(".bigimg li");

        // 使用gb2312显示中文
        Charset gbkCharset = Charset.forName("gb2312");

        // 使用try-with-resources语句来自动关闭BufferedWriter
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(OUTPUT_FILE, true), gbkCharset))) {
            //select()方法提取指定信息
            for (Element element : bookElements) {
                String bookTitle = element.select("a[dd_name]").attr("title");
                String bookUrl = element.select("a[dd_name]").attr("href");
                String bookPriceText = element.select(".search_now_price").text();
                //正则表达式匹配价格（因为¥会乱码）
                Pattern pattern = Pattern.compile("(\\d+(\\.\\d{1,2})?)");
                Matcher matcher = pattern.matcher(bookPriceText);
                String bookPrice = "";
                if (matcher.find()) {
                    bookPrice = matcher.group(1);
                }
                System.out.println(bookPrice);

                writer.write("书名: " + bookTitle + "\n");
                writer.write("链接: " + bookUrl + "\n");
                writer.write("价格: " + bookPrice +"元"+ "\n");
                writer.write("\n");
            }
        }
    }


    static class UrlDepthPair {
        private final String url;
        private final int depth;

        public UrlDepthPair(String url, int depth) {
            this.url = url;
            this.depth = depth;
        }

        public String getUrl() {
            return url;
        }

        public int getDepth() {
            return depth;
        }
    }
}

