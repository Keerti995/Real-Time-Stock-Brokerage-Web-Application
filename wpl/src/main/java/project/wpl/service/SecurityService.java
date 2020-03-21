package project.wpl.service;

public interface SecurityService {
    String findLoggedInUsername();

    void autoLogin(String username, String passwd);
}
