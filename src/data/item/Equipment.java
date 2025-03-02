package data.item;

public  class Equipment {
	private String name;
	private int price;
	
	public Equipment(String name, int price) {
		this.name=name;
		this.price=price;
	}
	
	public Equipment(String string) {
		// TODO Auto-generated constructor stub
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
}
