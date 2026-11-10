package service;

import dao.UserDao;
import model.User;
import exceptions.AuthorizationException;
import exceptions.AuthenticationException;

public class AuthorizationService{
    private final UserDao userDao;

    public AuthorizationService(UserDao userDao){
        this.userDao = userDao;
    }

    public static void authorized(String role, String requiredRole){
        if(!role.equals(requiredRole)){
            throw new AuthorizationException("This user is not allowed to access this feature");
        }
    }

    public User login(String username, String password){
        User user = userDao.findByLogin(username, password);
        if(user == null) throw new AuthenticationException("Invalid username or password");
        if(!user.role().equals("manager")) throw new AuthorizationException("This account does not have access to this app");
        return user;
    }
}