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
@RestController
public class UserController {
    @NotNull
    private final AccountService accountService;
    /**
     * Данный метод вызывается с помощью reflection'a, поэтому Spring позволяет инжектить в него аргументы.
     * Подробнее можно почитать в сорцах к аннотации {@link RequestMapping}. Там описано как заинжектить различные атрибуты http-запроса.
     * Возвращаемое значение можно так же варьировать. Н.п. Если отдать InputStream, можно стримить музыку или видео
     */

    @RequestMapping(path = "/api/user/registration", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String register(@RequestBody UserProfile body, HttpSession httpSession, HttpServletResponse responseCode) {
        JSONObject response = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        if(isEmptyField(body.getEmail())) {
            jsonArray.put(getEmptyFieldError("email"));
        }

        if(isEmptyField(body.getPassword())) {
            jsonArray.put(getEmptyFieldError("password"));
        }

        if(isEmptyField(body.getLogin())) {
            jsonArray.put(getEmptyFieldError("login"));
        }

        if(body == null) {
            jsonArray.put("empty body");
        }

        if (jsonArray.length() > 0) {
            responseCode.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.put("errors", jsonArray);
            return response.toString();
        }

        UserProfile userProfile = accountService.register(body.getEmail(), body.getLogin(), body.getPassword());
        if(userProfile != null) {
            response = userProfileToJSON(userProfile);
            httpSession.setAttribute("email", body.getEmail());
        } else {
            response.put("error", "this email is occupied");
        }
        return response.toString();
    }

    @RequestMapping(path = "/api/user/login", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String login(@RequestBody UserProfile body, HttpSession httpSession, HttpServletResponse responseCode) {
        JSONObject response = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        if(isEmptyField(body.getEmail())) {
            jsonArray.put(getEmptyFieldError("email"));
        }

        if(isEmptyField(body.getPassword())) {
            jsonArray.put(getEmptyFieldError("password"));
        }

        if (jsonArray.length() > 0) {
            responseCode.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.put("error", jsonArray);
            return response.toString();
        }

        if(accountService.login(body.getEmail(), body.getPassword())) {
            httpSession.setAttribute("email", body.getEmail());
            response.put("status", "success");
        }
        else response.put("error", "invalid email or password");
        return response.toString();
    }

    @RequestMapping(path = "/api/user/logout", method = RequestMethod.POST, consumes = "application/json")
    public String logout(HttpSession httpSession, HttpServletResponse responseCode) {
        JSONObject response = new JSONObject();
        if(httpSession.getAttribute("email") != null) {
            response.put("status", "success");
            httpSession.invalidate();
        } else {
            responseCode.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.put("error", "user didn\'t login");
        }
        return response.toString();
    }

    @RequestMapping(path = "/api/user/getuser", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String getUser(HttpSession httpSession , HttpServletResponse responseCode) {
        JSONObject response = new JSONObject();
        if(httpSession.getAttribute("email") != null) {
            UserProfile userProfile = accountService.getUser(httpSession.getAttribute("email").toString());
            response = userProfileToJSON(userProfile);
        } else {
            responseCode.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.put("error", "user didn\'t login");
        }
        return response.toString();
    }

    @RequestMapping(path = "/api/user/update", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String updateUser(@RequestBody UserProfile changedUserProfile, HttpSession httpSession, HttpServletResponse responseCode) {
        JSONObject response = new JSONObject();
        if(httpSession.getAttribute("email") != null) {
            if(!isEmptyField(changedUserProfile.getEmail()) || !isEmptyField(changedUserProfile.getPassword()) ||
                    !isEmptyField(changedUserProfile.getLogin())) {
                UserProfile oldUserProfile = accountService.getUser(httpSession.getAttribute("email").toString());
                UserProfile userProfile = accountService.update(oldUserProfile, changedUserProfile);
                if (userProfile == null) {
                    response.put("error", "this email is occupied");
                    return response.toString();
                }
                httpSession.setAttribute("email", userProfile.getEmail());
                response = userProfileToJSON(userProfile);
                return response.toString();
            } else {
                responseCode.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.put("error", "data to update is empty");
            }
        } else {
            responseCode.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.put("error", "user didn't login");
        }
        return response.toString();
    }


    public JSONObject userProfileToJSON (UserProfile userProfile) {
        JSONObject userProfileJSON = new JSONObject();
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



