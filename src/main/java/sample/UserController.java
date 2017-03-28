package sample;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import sample.services.account.AccountServiceDB;
import sample.services.account.AccountServiceInterface;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergey on 24.02.17.
 */

@CrossOrigin    //Enables CORS from all origins
@RestController
public class UserController {
    @NotNull
    private final AccountServiceInterface accountService;


    @PostMapping(path = "/api/user/registration")
    public ResponseEntity<ResponseWrapper<UserProfile>> register(@RequestBody UserProfile body, HttpSession httpSession, HttpServletResponse response) {
        final List<String> errorList = new ArrayList<String>();
        if(isEmptyField(body.getEmail())) {
            errorList.add(getEmptyFieldError("email"));
        }

        if(isEmptyField(body.getPassword())) {
            errorList.add(getEmptyFieldError("password"));
        }

        if(isEmptyField(body.getLogin())) {
            errorList.add(getEmptyFieldError("login"));
        }

        if (!errorList.isEmpty()) {
            return new ResponseEntity<>(new ResponseWrapper<>(errorList, null), HttpStatus.BAD_REQUEST);
        }

        body.setPassword(passwordEncoder().encode(body.getPassword()));

        final UserProfile userProfile = accountService.register(body);
        if(userProfile != null) {
            httpSession.setAttribute("email", body.getEmail());
            return new ResponseEntity<>(new ResponseWrapper<>(null, userProfile), HttpStatus.OK);
        } else {
            errorList.add("this email is occupied");
            return new ResponseEntity<>(new ResponseWrapper<>(errorList, userProfile), HttpStatus.CONFLICT);
        }
    }

    @PostMapping(path = "/api/user/login")
    public ResponseEntity<ResponseWrapper> login(@RequestBody UserProfile body, HttpSession httpSession, HttpServletResponse response) {
        final List<String> errorList = new ArrayList<>();

        if(isEmptyField(body.getEmail())) {
            errorList.add(getEmptyFieldError("email"));
        }

        if(isEmptyField(body.getPassword())) {
            errorList.add(getEmptyFieldError("password"));
        }

        if (!errorList.isEmpty()) {
            return new ResponseEntity<>(new ResponseWrapper<>(errorList, null), HttpStatus.BAD_REQUEST);
        }

        if(accountService.login(body.getEmail(), body.getPassword())) {
            httpSession.setAttribute("email", body.getEmail());
            return new ResponseEntity<>(new ResponseWrapper<>(null, null), HttpStatus.OK);
        }
        else {
            errorList.add("invalid email or password");
            return new ResponseEntity<>(new ResponseWrapper<>(errorList, null), HttpStatus.OK);
        }
    }

    @PostMapping(path = "/api/user/logout")
    public ResponseEntity<ResponseWrapper> logout(HttpSession httpSession, HttpServletResponse response) {
        final List<String> errorList = new ArrayList<String>();
        if(httpSession.getAttribute("email") != null) {
            httpSession.invalidate();
            return new ResponseEntity<>(new ResponseWrapper(null, null), HttpStatus.OK);
        } else {
            errorList.add("user didn\'t login");
            return new ResponseEntity<>(new ResponseWrapper(errorList, null), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(path = "/api/user/getuser")
    public ResponseEntity<ResponseWrapper<UserProfile>> getUser(HttpSession httpSession , HttpServletResponse response) {
        if(httpSession.getAttribute("email") != null) {
            final UserProfile userProfile = accountService.getUser(httpSession.getAttribute("email").toString());
            return new ResponseEntity<>(new ResponseWrapper<>(null, userProfile), HttpStatus.OK);
        } else {
            final List<String> errorList = new ArrayList<>();
            errorList.add("user didn\'t login");
            return new ResponseEntity<>(new ResponseWrapper<>(errorList, null), HttpStatus.OK);
        }
    }


    @PostMapping(path = "/api/user/update")
    public ResponseEntity<ResponseWrapper<UserProfile>> updateUser(@RequestBody UserProfile changedUserProfile, HttpSession httpSession, HttpServletResponse response) {
        final List<String> errorList = new ArrayList<>();

        if(httpSession.getAttribute("email") != null) {
            if(!isEmptyField(changedUserProfile.getEmail()) || !isEmptyField(changedUserProfile.getPassword()) ||
                    !isEmptyField(changedUserProfile.getLogin())) {
                final UserProfile oldUserProfile = accountService.getUser(httpSession.getAttribute("email").toString());
                final UserProfile updatedUserProfile = accountService.update(oldUserProfile, changedUserProfile);

                if (updatedUserProfile == null) {
                    errorList.add("this email is occupied");
                    return new ResponseEntity<>(new ResponseWrapper<>(errorList, null), HttpStatus.CONFLICT);
                }

                httpSession.setAttribute("email", updatedUserProfile.getEmail());
                return new ResponseEntity<>(new ResponseWrapper<>(null, updatedUserProfile), HttpStatus.OK);
            } else {
                errorList.add("data to update is empty");
                return new ResponseEntity<>(new ResponseWrapper<>(errorList, null), HttpStatus.BAD_REQUEST);
            }
        } else {
            errorList.add("user didn't login");
            return new ResponseEntity<>(new ResponseWrapper<UserProfile>(errorList, null), HttpStatus.FORBIDDEN);
        }
    }


    @PostMapping(path = "/api/user/score")
    public ResponseEntity<ResponseWrapper> setScore(@RequestBody ObjectNode score, HttpSession httpSession, HttpServletResponse response) {
        final List<String> errorList = new ArrayList<String>();
        if(isEmptyField(score.get("score").toString())) {
            errorList.add(getEmptyFieldError("score"));
            return new ResponseEntity<>(new ResponseWrapper(errorList, null), HttpStatus.BAD_REQUEST);
        }
        if(httpSession.getAttribute("email") != null) {
            final UserProfile userProfile = accountService.getUser(httpSession.getAttribute("email").toString());
            userProfile.setScore(score.get("score").intValue());
            accountService.updateScore(userProfile);
            return new ResponseEntity<>(new ResponseWrapper(null, null), HttpStatus.OK);
        } else {
            errorList.add("user didn\'t login");
            return new ResponseEntity<>(new ResponseWrapper(errorList, null), HttpStatus.FORBIDDEN);

        }
    }

    @PostMapping(path = "/api/user/leaders/{count}")
    public ResponseEntity<ResponseWrapper<List<UserProfile>>> getLeaders(@PathVariable("count") Integer count, HttpSession httpSession , HttpServletResponse response) {
        int usersCounter;
        if(count == null || count < 1) {
            count = 1;
        }
        final List<UserProfile> userProfileArrayList = accountService.getSortedUsersByScore(count);
        return new ResponseEntity<ResponseWrapper<List<UserProfile>>>(new ResponseWrapper<>(null, userProfileArrayList), HttpStatus.OK);
    }

    @GetMapping(path = "/api/user/islogin")
    public ResponseEntity<ResponseWrapper<String>> isLogin(HttpSession httpSession, HttpServletResponse response) {
        if(httpSession.getAttribute("email") != null) {
            return new ResponseEntity<>(new ResponseWrapper<>(null, "true"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseWrapper<>(null, "false"), HttpStatus.OK);
        }
    }

    private boolean isEmptyField(String field) {
        return ((field == null) || field.isEmpty());
    }

    private String getEmptyFieldError(String fieldName) {
        return ("field " + fieldName + " is empty");
    }

    public UserController(@NotNull AccountServiceDB accountService) {
        this.accountService = accountService;
    }

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}



