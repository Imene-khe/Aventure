package data.item;

public class Equipment {
    private String name;
    private int price;
    
    public Equipment(String name) {
        this.name = name;
    }
    

    public String getName() {
        return name;
    }
    
    public int getPrice() {
        return price;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setPrice(int price) {
        this.price = price;
    }
    
    @Override
    public String toString() {
        return "Equipment{" +
                "name='" + name + '\'' +
                '}';
    }
    
}
