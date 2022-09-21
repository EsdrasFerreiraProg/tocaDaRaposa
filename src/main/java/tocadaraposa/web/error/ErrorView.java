package tocadaraposa.web.error;

import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
public class ErrorView implements ErrorViewResolver {
    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> map) {

        ModelAndView model = new ModelAndView("error");

        model.addObject("status", status.value());
        switch (status.value()){
            case 404:
                prepareMap(model, "Página não encontrada", "A página não foi encontrada no servidor!");
                break;
            case 500:
                prepareMap(model, "Erro no servidor", "Ocorreu um erro inesperado, tente mais tarde!");
                break;
            case 503:
                prepareMap(model, "Acesso negado", "Você não tem permissão de acesso para esse recurso!");
                break;
            default:
                prepareMap(model, map.get("error").toString(), map.get("message").toString());
                break;
        }

        return model;
    }

    private void prepareMap(ModelAndView model,  String errorTitle, String errorDescription){
        model.addObject("errorTitle", errorTitle);
        model.addObject("errorDescription", errorDescription);
    }

}