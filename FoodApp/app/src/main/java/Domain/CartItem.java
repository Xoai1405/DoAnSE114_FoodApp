package Domain;

import com.google.firebase.database.Exclude;
public class CartItem {
    private int foodID;
    private int quantity;

    private Foods foodDetail;


    public CartItem(int id, int quan)
    {
        foodID = id;
        quantity = quan;
    }
    public int getFoodID()
    {
        return  foodID;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setFoodID(int id)
    {
        foodID = id;
    }

    public void setQuantity(int quan)
    {
        quantity = quan;
    }

    @Exclude
    public Foods getFoodDetails() {
        return foodDetail;
    }

    public void setFoodDetails(Foods foodDetails) {
        this.foodDetail = foodDetails;
    }

}
