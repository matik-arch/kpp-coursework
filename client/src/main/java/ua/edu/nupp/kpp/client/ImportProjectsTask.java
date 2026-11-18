package ua.edu.nupp.kpp.client;

import javafx.concurrent.Task;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class ImportProjectsTask extends Task<String> {
    private static final Logger LOG = Logger.getLogger(ImportProjectsTask.class.getName());
    private final Path file;
    private final HttpProjectClient api;

    public ImportProjectsTask(Path file, HttpProjectClient api) {
        this.file = file;
        this.api = api;
    }

    @Override
    protected String call() throws Exception {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        int total = Math.max(1, lines.size() - 1);
        int imported = 0;
        int skipped = 0;

        for (int i = 1; i < lines.size(); i++) {
            if (isCancelled()) {
                LOG.info("CSV-імпорт скасовано користувачем на рядку " + (i + 1));
                updateMessage("Скасовано. Імпортовано: " + imported + ", пропущено: " + skipped);
                break;
            }
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] p = line.split(";");
            if (p.length != 6) {
                skipped++;
                LOG.warning("Пропущено рядок " + (i + 1) + ": очікується 6 полів");
                updateProgress(i, total);
                continue;
            }
            try {
                Project project = new Project(
                        null,
                        Long.parseLong(p[0].trim()),
                        p[1].trim(),
                        p[2].trim(),
                        p[3].trim(),
                        Integer.parseInt(p[4].trim()),
                        Integer.parseInt(p[5].trim())
                );
                api.addProject(project);
                imported++;
            } catch (Exception rowError) {
                skipped++;
                LOG.warning("Помилка в рядку " + (i + 1) + ": " + rowError.getMessage());
            }
            updateProgress(i, total);
            updateMessage("Опрацьовано " + i + " із " + total + " (імпортовано: " + imported + ", пропущено: " + skipped + ")");
        }

        String summary = "Готово. Імпортовано: " + imported + ", пропущено: " + skipped;
        LOG.info("Завершено CSV-імпорт проєктів: " + summary);
        return summary;
    }
}
