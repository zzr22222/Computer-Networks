public class BookInfo {
    private String title;
    private String link;
    private String author;
    private String price;

    public BookInfo(String title, String link, String author, String price) {
        this.title = title;
        this.link = link;
        this.author = author;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Title: " + title + "\nLink: " + link + "\nAuthor: " + author + "\nPrice: " + price + "\n";
    }
}
