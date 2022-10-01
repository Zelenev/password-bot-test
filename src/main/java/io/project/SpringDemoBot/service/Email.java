package io.project.SpringDemoBot.service;

public class Email {
    private String firstName;
    private String lastName;
    private String password;
    private int defaultPasswordLength = 10;
    private String email;
    private String companySuffix = "maringroup.com";

    //Конструктор, который получает первое имя и фамилию
    public  Email(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;

        //Вызываем метод, который генерирует пароль
        this.password = randomPassword(defaultPasswordLength);

        //Объединение элементов для генерации эл.почты
        email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@" + companySuffix;
    }

    //Сгенерировать рандомный пароль
    private String randomPassword(int length){
        String passwordSet = "abcdefghijklmopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%";
        char[] password = new char[length];
        for(int i=0; i<length;i++ ){
            int rand = (int)(Math.random()* passwordSet.length());
            password[i] = passwordSet.charAt(rand);
        }
        return new String(password);
    }

    //Изменить пароль
    public void changePassword(String password){
        this.password = password;
    }

    public String getPassword(){return password;}

    public String getInfo(){
        return "Name: " + firstName + " " + lastName +
                "\nEmail: " + email +
                "\nPassword: " + password;
    }
}
