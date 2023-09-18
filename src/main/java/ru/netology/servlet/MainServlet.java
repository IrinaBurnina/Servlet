package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.config.JavaConfig;
import ru.netology.controller.PostController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MainServlet extends HttpServlet {
    private PostController controller;
    private static final String methodGet = "GET";
    private static final String methodPost = "POST";
    private static final String methodDelete = "DELETE";
    private static final String API_POSTS = "/api/posts";
    private static final String dWithSlash = "/\\d+";


    @Override
    public void init() {
        final var context = new AnnotationConfigApplicationContext(JavaConfig.class);
        controller = (PostController) context.getBean("postController");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // если деплоились (развернулись) в root context, то достаточно этого
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();
            if (method.equals(methodGet)) {
                if (path.equals(API_POSTS)) {
                    all(resp);
                    return;
                } else if (path.matches(API_POSTS + dWithSlash)) {
                    getById(parseId(path), resp);
                    return;
                }
            } else if (method.equals(methodPost)) {
                if (path.equals(API_POSTS)) {
                    save(resp, req);
                    return;
                }
            } else if (method.equals(methodDelete)) {
                if (path.matches(API_POSTS + dWithSlash)) {
                    removeById(parseId(path), resp);
                    return;
                }
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public PostController getController() {
        return controller;
    }

    public void all(HttpServletResponse resp) throws IOException {
        getController().all(resp);
    }

    public void save(HttpServletResponse resp, HttpServletRequest req) throws IOException {
        getController().save(req.getReader(), resp);
    }

    public Long parseId(String path) { //распознает из пути запроса id
        return Long.parseLong(path.substring(path.lastIndexOf("/")));
    }

    public void getById(Long id, HttpServletResponse resp) throws IOException {
        controller.getById(id, resp);
    }

    public void removeById(Long id, HttpServletResponse resp) throws IOException {
        controller.removeById(id, resp);
    }
}


