package com.ankara_gelinlik.dto;

public class PasswordForm {

    private String email; // opsiyonel, form'da kullanmak istersen
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

    public PasswordForm() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
