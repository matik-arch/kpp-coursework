package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SubmissionServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(SubmissionServlet.class.getName());
    private final SubmissionService service = new SubmissionService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String action = req.getParameter("action");
            if ("add".equals(action)) {
                service.add(
                        Long.parseLong(req.getParameter("projectId")),
                        req.getParameter("versionTag"),
                        Integer.parseInt(req.getParameter("completedRequirements")),
                        Integer.parseInt(req.getParameter("passedChecks")),
                        Double.parseDouble(req.getParameter("documentationScore"))
                );
            } else if ("update".equals(action)) {
                service.update(
                        parseId(req),
                        Long.parseLong(req.getParameter("projectId")),
                        req.getParameter("versionTag"),
                        Integer.parseInt(req.getParameter("completedRequirements")),
                        Integer.parseInt(req.getParameter("passedChecks")),
                        Double.parseDouble(req.getParameter("documentationScore"))
                );
            } else if ("transition".equals(action)) {
                String stateValue = req.getParameter("state");
                if (stateValue == null || stateValue.isBlank()) {
                    throw new IllegalArgumentException("Не задано новий стан подання");
                }
                service.changeState(parseId(req), SubmissionState.valueOf(stateValue));
            } else if ("delete".equals(action)) {
                service.delete(parseId(req));
            } else {
                throw new IllegalArgumentException("Невідома операція: " + action);
            }
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    private void writeJson(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), data);
    }

    private void writeError(HttpServletResponse resp, Exception e) throws IOException {
        boolean clientError = e instanceof IllegalArgumentException;
        int status = clientError ? HttpServletResponse.SC_BAD_REQUEST : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        if (!clientError) {
            LOG.log(Level.SEVERE, "Внутрішня помилка сервера у SubmissionServlet", e);
        }
        String message = clientError
                ? (e.getMessage() == null ? "Некоректний запит" : e.getMessage())
                : "Внутрішня помилка сервера";
        resp.setStatus(status);
        writeJson(resp, Map.of("error", message));
    }

    private long parseId(HttpServletRequest req) {
        return Long.parseLong(req.getParameter("id"));
    }
}
