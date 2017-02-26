package sample;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import org.json.JSONArray;
import org.json.JSONObject;

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
    public String register(@RequestBody UserProfile body, HttpSession httpSession) {
        JSONObject response = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if ((body.getEmail() == null) ||  body.getEmail().isEmpty()) {
            jsonArray.put("field email is empty");
        }

        if ((body.getPassword() == null) ||  body.getPassword().isEmpty()) {
            jsonArray.put("field password is empty");
        }

        if ((body.getLogin() == null) ||  body.getLogin().isEmpty()) {
            jsonArray.put("field login is empty");
        }

        if (jsonArray.length() > 0) {
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
    public String login(@RequestBody UserProfile body, HttpSession httpSession) {
        JSONObject response = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if ((body.getEmail() == null) || body.getEmail().isEmpty()) {
            jsonArray.put("Field email is empty");
        }

        if ((body.getPassword() == null) || body.getPassword().isEmpty()) {
            jsonArray.put("Field password is empty");
        }

        if (jsonArray.length() > 0) {
            response.put("error", jsonArray);
            return response.toString();
        }

        if(accountService.login(body.getEmail(), body.getPassword())) {
            httpSession.setAttribute("email", body.getEmail());
            response.put("status", "success");
        }
        else response.put("error", "invalidate email or password");
        return response.toString();
    }

    @RequestMapping(path = "/api/user/logout", method = RequestMethod.POST, consumes = "application/json")
    public String logout(HttpSession httpSession) {
        JSONObject response = new JSONObject();
        if(httpSession.getAttribute("email") != null) {
            response.put("status", "success");
            httpSession.invalidate();
        } else {
            response.put("error", "user didn\'t login");
        }
        return response.toString();
    }

    @RequestMapping(path = "/api/user/getuser", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public UserProfile getUser(HttpSession httpSession) {
        String email = httpSession.getAttribute("email").toString();
        UserProfile userProfile = accountService.getUser(email);
        if(userProfile != null) {
            return userProfile;
        }
        return null;
    }

    @RequestMapping(path = "/api/user/update", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String updateUser(@RequestBody UserProfile changedUser, HttpSession httpSession) {
        JSONObject response = new JSONObject();
        if(httpSession.getAttribute("email") != null) {
            if(changedUser.getEmail() != null && changedUser.getLogin() != null && changedUser.getPassword() != null) {
                UserProfile userProfile = accountService.update(httpSession.getAttribute("email").toString(), changedUser);
                httpSession.setAttribute("email", userProfile.getEmail());
                response = userProfileToJSON(userProfile);
                return response.toString();
            } else {
                response.put("error", "there is no data to update");
            }
        } else {
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

    public UserController(@NotNull AccountService accountService) {
        this.accountService = accountService;
    }

}



