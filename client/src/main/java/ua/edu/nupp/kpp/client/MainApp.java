package ua.edu.nupp.kpp.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;

public class MainApp extends Application {
    private static final Logger LOG = Logger.getLogger(MainApp.class.getName());
    private final HttpProjectClient api = new HttpProjectClient();

    private final TableView<Student> students = new TableView<>();
    private final TableView<Project> projects = new TableView<>();
    private final TableView<Submission> submissions = new TableView<>();
    private final TableView<Review> reviews = new TableView<>();

    private ImportProjectsTask activeImportTask;

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane(
                new Tab("Студенти", studentPane()),
                new Tab("Проєкти", projectPane(stage)),
                new Tab("Подання", submissionPane()),
                new Tab("Рецензії", reviewPane())
        );
        tabs.getTabs().forEach(t -> t.setClosable(false));

        stage.setScene(new Scene(tabs, 1120, 680));
        stage.setTitle("Інформаційна система подання та перевірки студентських програмних проєктів");
        stage.show();
        refreshAll();
    }

    private Pane studentPane() {
        students.getColumns().setAll(
                col("ID", "id"),
                col("Код студента", "studentCode"),
                col("ПІБ", "fullName"),
                col("Група", "groupName"),
                col("Email", "email")
        );

        TextField code = new TextField();
        TextField fullName = new TextField();
        TextField group = new TextField();
        TextField email = new TextField();

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addStudent(new Student(null, code.getText(), fullName.getText(), group.getText(), email.getText()));
            refreshStudents();
        }));
        update.setOnAction(e -> safe(() -> {
            Student s = selected(students);
            s.setStudentCode(code.getText());
            s.setFullName(fullName.getText());
            s.setGroupName(group.getText());
            s.setEmail(email.getText());
            api.updateStudent(s);
            refreshStudents();
        }));
        delete.setOnAction(e -> safe(() -> {
            api.deleteStudent(selected(students).getId());
            refreshAll();
        }));
        refresh.setOnAction(e -> refreshStudents());

        students.getSelectionModel().selectedItemProperty().addListener((o, a, s) -> {
            if (s != null) {
                code.setText(s.getStudentCode());
                fullName.setText(s.getFullName());
                group.setText(s.getGroupName());
                email.setText(s.getEmail());
            }
        });

        VBox form = new VBox(8,
                new Label("Код студента"), code,
                new Label("ПІБ"), fullName,
                new Label("Група"), group,
                new Label("Email"), email,
                new HBox(8, add, update, delete, refresh)
        );
        return layout(students, form);
    }

    private Pane projectPane(Stage stage) {
        projects.getColumns().setAll(
                col("ID", "id"),
                col("ID студента", "studentId"),
                col("Код", "code"),
                col("Назва проєкту", "title"),
                col("Технологія", "technology"),
                col("Вимоги", "totalRequirements"),
                col("Перевірки", "totalChecks")
        );

        TextField studentId = new TextField();
        TextField code = new TextField();
        TextField title = new TextField();
        TextField tech = new TextField();
        TextField totalReq = new TextField();
        TextField totalChk = new TextField();

        ProgressBar progress = new ProgressBar(0);
        progress.setMaxWidth(Double.MAX_VALUE);
        Label status = new Label("CSV-імпорт не запущено");

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");
        Button importDefault = new Button("Імпорт sample CSV");
        Button importChoose = new Button("Обрати CSV...");
        Button cancelImport = new Button("Скасувати");
        cancelImport.setDisable(true);

        add.setOnAction(e -> safe(() -> {
            api.addProject(readProject(null, studentId, code, title, tech, totalReq, totalChk));
            refreshProjects();
        }));
        update.setOnAction(e -> safe(() -> {
            Project pr = selected(projects);
            api.updateProject(readProject(pr.getId(), studentId, code, title, tech, totalReq, totalChk));
            refreshProjects();
        }));
        delete.setOnAction(e -> safe(() -> {
            api.deleteProject(selected(projects).getId());
            refreshAll();
        }));
        refresh.setOnAction(e -> refreshProjects());

        importDefault.setOnAction(e -> startCsvImport(Path.of("sample-data", "projects.csv"), progress, status, cancelImport));
        importChoose.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Оберіть CSV-файл із переліком проєктів");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV файли", "*.csv"));
            File chosen = chooser.showOpenDialog(stage);
            if (chosen != null) {
                startCsvImport(chosen.toPath(), progress, status, cancelImport);
            }
        });
        cancelImport.setOnAction(e -> {
            if (activeImportTask != null && activeImportTask.isRunning()) {
                activeImportTask.cancel();
            }
        });

        projects.getSelectionModel().selectedItemProperty().addListener((o, a, pr) -> {
            if (pr != null) {
                studentId.setText(String.valueOf(pr.getStudentId()));
                code.setText(pr.getCode());
                title.setText(pr.getTitle());
                tech.setText(pr.getTechnology());
                totalReq.setText(String.valueOf(pr.getTotalRequirements()));
                totalChk.setText(String.valueOf(pr.getTotalChecks()));
            }
        });

        VBox form = new VBox(6,
                new Label("ID студента"), studentId,
                new Label("Код проєкту"), code,
                new Label("Назва проєкту"), title,
                new Label("Технологія"), tech,
                new Label("Усього вимог (для F1)"), totalReq,
                new Label("Усього перевірок (для F2)"), totalChk,
                new HBox(8, add, update, delete, refresh),
                new HBox(8, importDefault, importChoose, cancelImport),
                progress,
                status
        );
        return layout(projects, form);
    }

    private void startCsvImport(Path csvPath, ProgressBar progress, Label status, Button cancelBtn) {
        activeImportTask = new ImportProjectsTask(csvPath, api);
        progress.progressProperty().bind(activeImportTask.progressProperty());
        status.textProperty().bind(activeImportTask.messageProperty());
        cancelBtn.setDisable(false);

        activeImportTask.setOnSucceeded(x -> {
            progress.progressProperty().unbind();
            status.textProperty().unbind();
            cancelBtn.setDisable(true);
            status.setText(activeImportTask.getValue());
            refreshProjects();
        });
        activeImportTask.setOnCancelled(x -> {
            progress.progressProperty().unbind();
            status.textProperty().unbind();
            cancelBtn.setDisable(true);
            status.setText("Імпорт скасовано користувачем");
            refreshProjects();
        });
        activeImportTask.setOnFailed(x -> {
            progress.progressProperty().unbind();
            status.textProperty().unbind();
            cancelBtn.setDisable(true);
            status.setText("Помилка імпорту CSV");
            showError(activeImportTask.getException());
        });

        Thread worker = new Thread(activeImportTask, "csv-projects-import");
        worker.setDaemon(true);
        worker.start();
    }

    private Pane submissionPane() {
        submissions.getColumns().setAll(
                col("ID", "id"),
                col("Проєкт", "projectId"),
                col("Версія", "versionTag"),
                col("Стан", "state"),
                col("F1", "f1"),
                col("F2", "f2"),
                col("F3", "f3"),
                col("I", "qualityIndex")
        );

        TextField projectId = new TextField();
        TextField versionTag = new TextField();
        TextField completedReq = new TextField();
        TextField passedChk = new TextField();
        TextField docScore = new TextField();
        ComboBox<SubmissionState> nextState = new ComboBox<>(FXCollections.observableArrayList(SubmissionState.values()));

        Button add = new Button("Створити");
        Button update = new Button("Перерахувати");
        Button changeState = new Button("Змінити стан");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addSubmission(
                    Long.parseLong(projectId.getText().trim()),
                    versionTag.getText().trim(),
                    Integer.parseInt(completedReq.getText().trim()),
                    Integer.parseInt(passedChk.getText().trim()),
                    Double.parseDouble(docScore.getText().trim())
            );
            refreshSubmissions();
        }));
        update.setOnAction(e -> safe(() -> {
            Submission s = selected(submissions);
            api.updateSubmission(
                    s.getId(),
                    Long.parseLong(projectId.getText().trim()),
                    versionTag.getText().trim(),
                    Integer.parseInt(completedReq.getText().trim()),
                    Integer.parseInt(passedChk.getText().trim()),
                    Double.parseDouble(docScore.getText().trim())
            );
            refreshSubmissions();
        }));
        changeState.setOnAction(e -> safe(() -> {
            api.changeSubmissionState(selected(submissions).getId(), nextState.getValue());
            refreshSubmissions();
        }));
        delete.setOnAction(e -> safe(() -> {
            api.deleteSubmission(selected(submissions).getId());
            refreshAll();
        }));
        refresh.setOnAction(e -> refreshSubmissions());

        submissions.getSelectionModel().selectedItemProperty().addListener((o, a, s) -> {
            if (s != null) {
                projectId.setText(String.valueOf(s.getProjectId()));
                versionTag.setText(s.getVersionTag());
                completedReq.setText(String.valueOf(s.getCompletedRequirements()));
                passedChk.setText(String.valueOf(s.getPassedChecks()));
                docScore.setText(String.valueOf(s.getDocumentationScore()));
                nextState.setValue(s.getState());
            }
        });

        VBox form = new VBox(6,
                new Label("ID проєкту"), projectId,
                new Label("Версія подання (напр. v1.0)"), versionTag,
                new Label("Виконано вимог"), completedReq,
                new Label("Пройдено перевірок"), passedChk,
                new Label("Якість документації F3 (0..100)"), docScore,
                new Label("Новий стан життєвого циклу"), nextState,
                new HBox(8, add, update, changeState),
                new HBox(8, delete, refresh)
        );
        return layout(submissions, form);
    }

    private Pane reviewPane() {
        reviews.getColumns().setAll(
                col("ID", "id"),
                col("ID подання", "submissionId"),
                col("Рецензент", "reviewerName"),
                col("Коментар", "comment"),
                col("Дата", "reviewDate")
        );

        TextField submissionId = new TextField();
        TextField reviewer = new TextField();
        TextField comment = new TextField();
        TextField date = new TextField();

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addReview(new Review(null, Long.parseLong(submissionId.getText().trim()),
                    reviewer.getText(), comment.getText(), date.getText()));
            refreshReviews();
        }));
        update.setOnAction(e -> safe(() -> {
            Review r = selected(reviews);
            api.updateReview(new Review(r.getId(), Long.parseLong(submissionId.getText().trim()),
                    reviewer.getText(), comment.getText(), date.getText()));
            refreshReviews();
        }));
        delete.setOnAction(e -> safe(() -> {
            api.deleteReview(selected(reviews).getId());
            refreshReviews();
        }));
        refresh.setOnAction(e -> refreshReviews());

        reviews.getSelectionModel().selectedItemProperty().addListener((o, a, r) -> {
            if (r != null) {
                submissionId.setText(String.valueOf(r.getSubmissionId()));
                reviewer.setText(r.getReviewerName());
                comment.setText(r.getComment());
                date.setText(r.getReviewDate());
            }
        });

        VBox form = new VBox(8,
                new Label("ID подання"), submissionId,
                new Label("Рецензент"), reviewer,
                new Label("Коментар"), comment,
                new Label("Дата (YYYY-MM-DD)"), date,
                new HBox(8, add, update, delete, refresh)
        );
        return layout(reviews, form);
    }

    private Project readProject(Long id, TextField studentId, TextField code, TextField title,
                                TextField tech, TextField totalReq, TextField totalChk) {
        return new Project(
                id,
                Long.parseLong(studentId.getText().trim()),
                code.getText().trim(),
                title.getText().trim(),
                tech.getText().trim(),
                Integer.parseInt(totalReq.getText().trim()),
                Integer.parseInt(totalChk.getText().trim())
        );
    }

    private void refreshAll() {
        refreshStudents();
        refreshProjects();
        refreshSubmissions();
        refreshReviews();
    }

    private void refreshStudents() {
        safe(() -> students.setItems(FXCollections.observableArrayList(api.getStudents())));
    }

    private void refreshProjects() {
        safe(() -> projects.setItems(FXCollections.observableArrayList(api.getProjects())));
    }

    private void refreshSubmissions() {
        safe(() -> submissions.setItems(FXCollections.observableArrayList(api.getSubmissions())));
    }

    private void refreshReviews() {
        safe(() -> reviews.setItems(FXCollections.observableArrayList(api.getReviews())));
    }

    private <T> T selected(TableView<T> table) {
        T value = table.getSelectionModel().getSelectedItem();
        if (value == null) {
            throw new IllegalArgumentException("Виберіть запис у таблиці");
        }
        return value;
    }

    private void safe(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            LOG.warning(e.getMessage());
            showError(e);
        }
    }

    private void showError(Throwable e) {
        Alert dialog = new Alert(Alert.AlertType.ERROR, e == null ? "Невідома помилка" : e.getMessage(), ButtonType.OK);
        dialog.setHeaderText("Помилка виконання операції");
        dialog.showAndWait();
    }

    private Pane layout(TableView<?> table, Pane form) {
        form.setPadding(new Insets(10));
        SplitPane split = new SplitPane(table, form);
        split.setDividerPositions(0.66);
        BorderPane root = new BorderPane(split);
        root.setPadding(new Insets(10));
        return root;
    }

    private <S, T> TableColumn<S, T> col(String title, String property) {
        TableColumn<S, T> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
