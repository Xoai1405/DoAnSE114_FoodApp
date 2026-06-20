package Domain;

public class User {

    private String UserID;
    private String UserName;

    private String UserPhoneNumber;

    private String UserEmail;
    public User(){

    }

    public User(String id, String name , String phone, String email)
    {
        UserID = id;
        UserName = name;
        UserPhoneNumber = phone;
        UserEmail = email;
    }

    public String getUserID()
    {
        return UserID;
    }

    public void setUserID(String id)
    {
        UserID = id;
    }

    public String getUserName()
    {
        return UserName;
    }

    public void setUserName(String user)
    {
        UserName = user;
    }

    public String getUserPhoneNumber()
    {
        return UserPhoneNumber;
    }

    public void setUserPhoneNumber(String number)
    {
        UserPhoneNumber = number;
    }

    public String getUserEmail()
    {
        return UserEmail;
    }

    public void setUserEmail(String email)
    {
        UserEmail = email;
    }
}
