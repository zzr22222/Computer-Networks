import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DoubanMovieScraper {

    public static void main(String[] args) {
        String url = "https://movie.douban.com/subject/1292052/reviews";
        String folderName = "reviews";

        // 创建一个名为 "reviews" 的文件夹
        File folder = new File(folderName);
        if (!folder.exists()) {
            folder.mkdir();
        }

        try {
            //select()方法提取信息
            Document document = Jsoup.connect(url).get();
            Elements reviewElements = document.select(".review-list .main.review-item");

            //将提取到的信息按类别切分
            for (Element reviewElement : reviewElements) {
                String username = reviewElement.select(".name").text();
                String rating = reviewElement.select(".main-title-rating").attr("title");
                String time = reviewElement.select(".main-meta").text();
                String content = reviewElement.select(".review-short").text();

                // 替换用户名中的非法文件名字符
                String sanitizedUsername = username.replaceAll("[\\\\/:*?\"<>|]", "_");

                // 将影评信息写入以用户名命名的txt文件中，并将其保存在 "reviews" 文件夹下
                File outputFile = new File(folder, sanitizedUsername + ".txt");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                    writer.write("用户名: " + username);
                    writer.newLine();
                    writer.write("评价级别: " + rating);
                    writer.newLine();
                    writer.write("评价时间: " + time);
                    writer.newLine();
                    writer.write("内容: " + content);
                    writer.newLine();
                } catch (IOException e) {
                    System.err.println("无法写入文件: " + outputFile.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
