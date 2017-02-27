package sample;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by sergey on 24.02.17.
 */

@CrossOrigin    //Enables CORS from all origins
@RestController
public class UserController {
    @NotNull
    private final AccountService accountService;

    @RequestMapping(path = "/api/user/registration", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String register(@RequestBody UserProfile body, HttpSession httpSession, HttpServletResponse response) {
        JSONObject responseJSON = new JSONObject();
        final JSONArray errorList = new JSONArray();

        if(isEmptyField(body.getEmail())) {
            errorList.put(getEmptyFieldError("email"));
        }

        if(isEmptyField(body.getPassword())) {
            errorList.put(getEmptyFieldError("password"));
        }

        if(isEmptyField(body.getLogin())) {
            errorList.put(getEmptyFieldError("login"));
        }

        if (errorList.length() > 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseJSON.put("errors", errorList);
            return responseJSON.toString();
        }

        final UserProfile userProfile = accountService.register(body.getEmail(), body.getLogin(), body.getPassword());
        if(userProfile != null) {
            responseJSON = userProfileToJSON(userProfile);
            httpSession.setAttribute("email", body.getEmail());
        } else {
            responseJSON.put("error", "this email is occupied");
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }
        return responseJSON.toString();
    }

    @RequestMapping(path = "/api/user/login", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String login(@RequestBody UserProfile body, HttpSession httpSession, HttpServletResponse response) {
        final JSONObject responseJSON = new JSONObject();
        final JSONArray errorList = new JSONArray();

        if(isEmptyField(body.getEmail())) {
            errorList.put(getEmptyFieldError("email"));
        }

        if(isEmptyField(body.getPassword())) {
            errorList.put(getEmptyFieldError("password"));
        }

        if (errorList.length() > 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseJSON.put("error", errorList);
            return responseJSON.toString();
        }

        if(accountService.login(body.getEmail(), body.getPassword())) {
            httpSession.setAttribute("email", body.getEmail());
            responseJSON.put("status", "success");
        }
        else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            responseJSON.put("error", "invalid email or password");
        }
        return responseJSON.toString();
    }

    @RequestMapping(path = "/api/user/logout", method = RequestMethod.POST, consumes = "application/json")
    public String logout(HttpSession httpSession, HttpServletResponse response) {
        final JSONObject responseJSON = new JSONObject();
        if(httpSession.getAttribute("email") != null) {
            responseJSON.put("status", "success");
            httpSession.invalidate();
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            responseJSON.put("error", "user didn\'t login");
        }
        return responseJSON.toString();
    }

    @RequestMapping(path = "/api/user/getuser", method = RequestMethod.GET, produces = "application/json", consumes = "application/json")
    public String getUser(HttpSession httpSession , HttpServletResponse response) {
        JSONObject responseJSON = new JSONObject();
        if(httpSession.getAttribute("email") != null) {
            final UserProfile userProfile = accountService.getUser(httpSession.getAttribute("email").toString());
            responseJSON = userProfileToJSON(userProfile);
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            responseJSON.put("error", "user didn\'t login");
        }
        return responseJSON.toString();
    }

    @RequestMapping(path = "/api/user/update", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String updateUser(@RequestBody UserProfile changedUserProfile, HttpSession httpSession, HttpServletResponse response) {
        JSONObject responseJSON = new JSONObject();

        if(httpSession.getAttribute("email") != null) {
            if(!isEmptyField(changedUserProfile.getEmail()) && !isEmptyField(changedUserProfile.getPassword()) &&
                    !isEmptyField(changedUserProfile.getLogin())) {
                final UserProfile oldUserProfile = accountService.getUser(httpSession.getAttribute("email").toString());
                final UserProfile updatedUserProfile = accountService.update(oldUserProfile, changedUserProfile);

                if (updatedUserProfile == null) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    responseJSON.put("error", "this email is occupied");
                    return responseJSON.toString();
                }

                httpSession.setAttribute("email", updatedUserProfile.getEmail());
                responseJSON = userProfileToJSON(updatedUserProfile);
                return responseJSON.toString();
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseJSON.put("error", "data to update is empty");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            responseJSON.put("error", "user didn't login");
        }
        return responseJSON.toString();
    }


    public JSONObject userProfileToJSON (UserProfile userProfile) {
        final JSONObject userProfileJSON = new JSONObject();
        //userProfileJSON.put("password", userProfile.getPassword());
        userProfileJSON.put("id", userProfile.getId());
        userProfileJSON.put("login", userProfile.getLogin());
        userProfileJSON.put("email", userProfile.getEmail());
        return userProfileJSON;
    }

    private boolean isEmptyField(String field) {
        return ((field == null) || field.isEmpty());
    }

    private String getEmptyFieldError(String fieldName) {
        return ("field " + fieldName + " is empty");
    }

    public UserController(@NotNull AccountService accountService) {
        this.accountService = accountService;
    }

}



