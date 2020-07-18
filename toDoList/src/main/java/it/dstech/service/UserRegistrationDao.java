package it.dstech.service;

import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.springframework.web.multipart.MultipartFile;

public class UserRegistrationDao {

    @NotEmpty
    private String password;
    
    @Email
    @NotEmpty
    private String email;
    
    @Transient
    private MultipartFile imageDAO;    

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

	public MultipartFile getImageDAO() {
		return imageDAO;
	}

	public void setImageDAO(MultipartFile imageDAO) {
		this.imageDAO = imageDAO;
	}
}
