package cn.fanrunqi.materiallogin.bean;

/**
 * Created by horizon on 6/15/2017.
 */

public class BookDetailInfo {
    /**
     * String bookId = borrowInfo.getString("bookId");
     * String author = borrowInfo.getString("author");
     * String title = borrowInfo.getString("title");
     * String book_id = borrowInfo.getString("id");
     * double price = borrowInfo.getDouble("price");
     * int deposit = borrowInfo.getInt("deposit");
     * String refresh_token = result.getString("refresh_token");
     */
    public String author;
    public String title;
    public double price;
    public int deposit;
    public String id;


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDeposit() {
        return deposit;
    }

    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
