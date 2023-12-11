import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SZUCrawler {
    // 定义一个静态变量，用于记录下载文件的总大小
    private static long totalSize = 0;

    public static void main(String[] args) {
        String url = "https://www.szu.edu.cn/";

        try {
            // 通过Jsoup连接网址，并获取网页内容
            Document document = Jsoup.connect(url).get();

            // 处理文件的相对路径，并下载资源
            document = fixRelativePathsAndDownloadResources(document, url);

            // 将处理后的网页内容保存到文件中
            saveToFile(document.toString(), "szu_homepage.html");

            // 输出下载文件的总大小
            System.out.println("下载完成，总文件大小为：" + totalSize + " 字节");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 将网页内容保存到文件中
     *
     * @param content  要保存的网页内容
     * @param fileName 保存到的文件名
     */
    private static void saveToFile(String content, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);

            // 获取文件大小，并将其添加到totalSize变量中
            long fileSize = Files.size(Paths.get(fileName));
            totalSize += fileSize;

            System.out.println("网页内容已保存到文件: " + fileName + "，大小为：" + fileSize + " 字节");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理相对路径和下载资源
     *
     * @param document 网页文档
     * @param baseUrl  网址
     * @return 处理后的网页文档
     * @throws IOException
     */
    private static Document fixRelativePathsAndDownloadResources(Document document, String baseUrl) throws IOException {
        URL base = new URL(baseUrl);
        Elements links = document.select("link[href], script[src], img[src]");
        for (Element element : links) {
            if (element.hasAttr("href")) {
                String absPath = element.attr("abs:href");
                element.attr("href", absPath);
                downloadResource(absPath, "css");
            }
            if (element.hasAttr("src")) {
                String absPath = element.attr("abs:src");
                element.attr("src", absPath);
                String extension = absPath.substring(absPath.lastIndexOf('.') + 1);
                if (extension.equalsIgnoreCase("js")) {
                    downloadResource(absPath, "js");
                } else if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")
                        || extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("gif")) {
                    downloadResource(absPath, "images");
                }
            }
        }

        // 内联样式处理代码
        //使用Jsoup 的 select 方法从网页文档中选择具有 style 属性的元素
        Elements elementsWithStyle = document.select("[style]");
        //正则表达式匹配
        Pattern pattern = Pattern.compile("url\\(['\"]?([^'\"\\)]+)['\"]?\\)", Pattern.CASE_INSENSITIVE);
        for (Element element : elementsWithStyle) {
            String originalStyle = element.attr("style");
            Matcher matcher = pattern.matcher(originalStyle);
            StringBuffer newStyle = new StringBuffer();
            //查找所有匹配样式
            while (matcher.find()) {
                String relativePath = matcher.group(1);
                String absolutePath = new URL(base, relativePath).toString();
                //用绝对路径替换相对路径
                matcher.appendReplacement(newStyle, "url('" + absolutePath + "')");
            }
            matcher.appendTail(newStyle);
            element.attr("style", newStyle.toString());
        }
        return document;
    }

/**
 * 下载资源并保存到指定文件夹
 *
 * @param url    资源的网址
 * @param folder 保存资源的文件夹名称
 * */
    private static void downloadResource(String url, String folder) {
        try {
            URL resourceUrl = new URL(url);

            // 从URL中提取文件名
            String fileName = url.substring(url.lastIndexOf('/') + 1);

            // 构建完整的文件路径，包括文件夹和文件名
            String fullPath = folder + File.separator + fileName;

            // 确保文件夹存在，如果不存在，则创建
            Files.createDirectories(Paths.get(folder));

            // 从URL中复制文件内容到本地文件中
            Files.copy(resourceUrl.openStream(), Paths.get(fullPath), StandardCopyOption.REPLACE_EXISTING);

            // 获取文件大小，并将其添加到totalSize变量中
            long fileSize = Files.size(Paths.get(fullPath));
            totalSize += fileSize;

            System.out.println("已下载资源：" + url + " 保存到：" + fullPath + "，大小为：" + fileSize + " 字节");
        } catch (IOException e) {
            System.err.println("无法下载资源：" + url);
            e.printStackTrace();
        }
    }
}