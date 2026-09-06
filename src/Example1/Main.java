package Example1;

import java.time.LocalDate;

class Client {
    private final int id_client;
    private String full_name;
    private String phone;
    private String gender;
    private LocalDate birthday;
    private String passport;
    private String comment;

    public Client(int id_client, String full_name, String phone, String gender, LocalDate birthday, String passport, String comment){
        this.id_client = id_client;
        this.full_name = full_name;
        this.gender = gender;
        this.phone = phone;
        this.birthday = birthday;
        this.passport = passport;
        this.comment = comment;
    }

    public int getId_client(){
        return id_client;
    }
    public String getFull_name(){
        return full_name;
    }
    public String getGender(){
        return gender;
    }
    public String getPhone(){
        return phone;
    }
    public LocalDate getBirthday(){
        return birthday;
    }
    public String getPassport(){
        return passport;
    }
    public String getComment(){
        return comment;
    }


}