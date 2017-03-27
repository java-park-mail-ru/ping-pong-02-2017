package sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    private final AccountServiceHM accountService;
    private static final ObjectMapper mapper = new ObjectMapper();


    @PostMapping(path = "/api/user/registration")
    public ResponseEntity<ResponseWrapper> register(@RequestBody UserProfile body, HttpSession httpSession, HttpServletResponse response) {
        final List errorList = new ArrayList();
        if(isEmptyField(body.getEmail())) {
            errorList.add(getEmptyFieldError("email"));
        }

        if(isEmptyField(body.getPassword())) {
            errorList.add(getEmptyFieldError("password"));
        }

        if(isEmptyField(body.getLogin())) {
            errorList.add(getEmptyFieldError("login"));
        }

        if (errorList.size() > 0) {
            return new ResponseEntity<>(new ResponseWrapper(errorList, null), HttpStatus.BAD_REQUEST);
        }

        body.setPassword(passwordEncoder().encode(body.getPassword()));

        final UserProfile userProfile = accountService.register(body.getEmail(), body.getLogin(), body.getPassword());
        if(userProfile != null) {
            httpSession.setAttribute("email", body.getEmail());
            return new ResponseEntity<>(new ResponseWrapper(null, userProfile), HttpStatus.OK);
        } else {
            errorList.add("this email is occupied");
            return new ResponseEntity<>(new ResponseWrapper(errorList, userProfile), HttpStatus.CONFLICT);
        }
    }

    @PostMapping(path = "/api/user/login")
    public ResponseEntity<ResponseWrapper> login(@RequestBody UserProfile body, HttpSession httpSession, HttpServletResponse response) {
        final List errorList = new ArrayList();

        if(isEmptyField(body.getEmail())) {
            errorList.add(getEmptyFieldError("email"));
        }

        if(isEmptyField(body.getPassword())) {
            errorList.add(getEmptyFieldError("password"));
        }

        if (errorList.size() > 0) {
            return new ResponseEntity<>(new ResponseWrapper(errorList, null), HttpStatus.BAD_REQUEST);
        }

        if(accountService.login(body.getEmail(), body.getPassword())) {
            httpSession.setAttribute("email", body.getEmail());
            return new ResponseEntity<>(new ResponseWrapper(null, null), HttpStatus.OK);
        }
        else {
            errorList.add("invalid email or password");
            return new ResponseEntity<>(new ResponseWrapper(errorList, null), HttpStatus.OK);
        }
    }

    @PostMapping(path = "/api/user/logout")
    public ResponseEntity<ResponseWrapper> logout(HttpSession httpSession, HttpServletResponse response) {
        final List errorList = new ArrayList();
        if(httpSession.getAttribute("email") != null) {
            httpSession.invalidate();
            return new ResponseEntity<>(new ResponseWrapper(null, null), HttpStatus.OK);
        } else {
            errorList.add("user didn\'t login");
            return new ResponseEntity<>(new ResponseWrapper(errorList, null), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(path = "/api/user/getuser")
    public ResponseEntity<ResponseWrapper> getUser(HttpSession httpSession , HttpServletResponse response) {
        final List errorList = new ArrayList();
        if(httpSession.getAttribute("email") != null) {
            final UserProfile userProfile = accountService.getUser(httpSession.getAttribute("email").toString());
            return new ResponseEntity<>(new ResponseWrapper(null, userProfile), HttpStatus.OK);
        } else {
            errorList.add("user didn\'t login");
            return new ResponseEntity<>(new ResponseWrapper(errorList, null), HttpStatus.OK);
        }
    }


    @PostMapping(path = "/api/user/update")
    public ResponseEntity<ResponseWrapper> updateUser(@RequestBody UserProfile changedUserProfile, HttpSession httpSession, HttpServletResponse response) {
        final List errorList = new ArrayList();

        if(httpSession.getAttribute("email") != null) {
            if(!isEmptyField(changedUserProfile.getEmail()) || !isEmptyField(changedUserProfile.getPassword()) ||
                    !isEmptyField(changedUserProfile.getLogin())) {
                final UserProfile oldUserProfile = accountService.getUser(httpSession.getAttribute("email").toString());
                final UserProfile updatedUserProfile = accountService.update(oldUserProfile, changedUserProfile);

                if (updatedUserProfile == null) {
                    errorList.add("this email is occupied");
                    return new ResponseEntity<>(new ResponseWrapper(errorList, null), HttpStatus.CONFLICT);
                }

                httpSession.setAttribute("email", updatedUserProfile.getEmail());
                return new ResponseEntity<>(new ResponseWrapper(null, updatedUserProfile), HttpStatus.OK);
            } else {
                errorList.add("data to update is empty");
                return new ResponseEntity<>(new ResponseWrapper(errorList, null), HttpStatus.BAD_REQUEST);
            }
        } else {
            errorList.add("user didn't login");
            return new ResponseEntity<>(new ResponseWrapper(errorList, null), HttpStatus.FORBIDDEN);
        }
    }


    @PostMapping(path = "/api/user/score")
    public ResponseEntity<ResponseWrapper> setScore(@RequestBody ObjectNode score, HttpSession httpSession, HttpServletResponse response) {
        final List errorList = new ArrayList();
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

    @PostMapping(path = "/api/user/leaders")
    public ResponseEntity<ResponseWrapper> getLeaders(@RequestBody ObjectNode countJSON, HttpSession httpSession , HttpServletResponse response) {
        final ObjectNode responseJSON = mapper.createObjectNode();
        int usersCounter;
        if(countJSON.get("count") == null) {
            usersCounter = 1;
        } else {
            usersCounter = countJSON.get("count").intValue();
        }
        final ArrayNode leadersList = mapper.createArrayNode();
        final ArrayList<UserProfile> userProfileArrayList = accountService.getSortedUsersByScore();
        return new ResponseEntity<>(new ResponseWrapper(null, userProfileArrayList), HttpStatus.OK);
    }

    @GetMapping(path = "/api/user/islogin")
    public ObjectNode isLogin(HttpSession httpSession, HttpServletResponse response) {
        final ObjectNode responseJSON = mapper.createObjectNode();
        if(httpSession.getAttribute("email") != null) {
            responseJSON.put("isLoggedIn","true");
        } else {
            responseJSON.put("isLoggedIn","false");
        }
        return responseJSON;
    }

    @GetMapping(path = "/api/user/flush")
    public void flush() {
        accountService.flush();
    }


    private boolean isEmptyField(String field) {
        return ((field == null) || field.isEmpty());
    }

    private String getEmptyFieldError(String fieldName) {
        return ("field " + fieldName + " is empty");
    }

    public UserController(@NotNull AccountServiceHM accountService) {
        this.accountService = accountService;
    }

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}



