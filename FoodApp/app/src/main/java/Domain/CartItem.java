package Domain;

import com.google.firebase.database.Exclude;
public class CartItem implements java.io.Serializable {
    private int foodID;
    private int quantity;


    @Exclude
    private Foods foodDetails;


    public CartItem(int id, int quan)
    {
        foodID = id;
        quantity = quan;
    }

    public CartItem()
    {

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
        return foodDetails;
    }

    public void setFoodDetails(Foods foodDetails) {
        this.foodDetails = foodDetails;
    }

}
