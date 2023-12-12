package lk.javainstitute.techstoreadmin.model;

public class AuthRequest {
    private String email;

    public AuthRequest() {
    }

    public AuthRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
