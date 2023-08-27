package ru.netology.servlet;

import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MainServlet extends HttpServlet {
    private PostController controller;
    private static final String methodGet = "GET";
    private static final String methodPost = "POST";
    private static final String methodDelete = "DELETE";
    private static final String pieceOfPath = "/api/posts";
    private static final String dWithSlash = "/\\d+";


    @Override
    public void init() {
        final var repository = new PostRepository();
        final var service = new PostService(repository);
        controller = new PostController(service);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // если деплоились (развернулись) в root context, то достаточно этого
        init();
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();
            if (method.equals(methodGet)) {
                if (path.equals(pieceOfPath)) {
                    all(resp);
                    return;
                } else if (path.matches(pieceOfPath + dWithSlash)) {
                    getById(parseId(path), resp);
                    return;
                }
            } else if (method.equals(methodPost)) {
                if (path.equals(pieceOfPath)) {
                    save(resp, req);
                    return;
                }
            } else if (method.equals(methodDelete)) {
                if (path.matches(pieceOfPath + dWithSlash)) {
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

    public Long parseId(String path) {
        return Long.parseLong(path.substring(path.lastIndexOf("/")));
    }

    public void getById(Long id, HttpServletResponse resp) throws IOException {
        controller.getById(id, resp);
    }

    public void removeById(Long id, HttpServletResponse resp) throws IOException {
        controller.removeById(id, resp);
    }
}


