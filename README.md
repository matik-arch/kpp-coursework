# Інформаційна система подання та перевірки студентських програмних проєктів

- **Дисципліна:** Кросплатформене програмування
- **Варіант:** №5
- **Виконав:** Варламов Ілля Юрійович, група 201-ТН
- **Керівник:** Заїка С. О.
- **Університет:** Національний університет «Полтавська політехніка імені Юрія Кондратюка»

## 1. Архітектура та технологічний стек
- **Мова та складання:** Java 21, Apache Maven
- **Клієнт:** JavaFX 21 (`client/`)
- **Взаємодія:** HTTP (`java.net.http.HttpClient`) + JSON (`Jackson ObjectMapper`)
- **Сервер:** Servlet API 4.0.1 (`javax.servlet`), Apache Tomcat 9.x (`server/`)
- **Зберігання даних:** MySQL 8.x + JDBC (`PreparedStatement`, `try-with-resources`)
- **Тестування:** JUnit 5 (12 модульних тестів)

Маршрут обробки запиту:
`JavaFX-клієнт → HttpProjectClient → HTTP/JSON → Servlet → Service → DAO → JDBC → MySQL`

## 2. Предметна модель (Варіант №5)
- **Сутності:** `Student` (Студент), `Project` (Проєкт), `Submission` (Подання), `Review` (Рецензія).
- **Життєвий цикл `Submission`:** `DRAFT` (ЧЕРНЕТКА) → `SUBMITTED` (ПОДАНО) → `IN_REVIEW` (ПЕРЕВІРКА) → `ACCEPTED` (ПРИЙНЯТО).
- **Інтегральний показник якості:** `I = 0,40·F1 + 0,35·F2 + 0,25·F3`, де:
  - `F1` — повнота реалізації (`min(100, (completedRequirements / totalRequirements) * 100)`);
  - `F2` — результат перевірок (`min(100, (passedChecks / totalChecks) * 100)`);
  - `F3` — якість документації (`0..100`).
- **Фоновий CSV-імпорт:** імпорт переліку проєктів із `client/sample-data/projects.csv` через `JavaFX Task` (`ImportProjectsTask`) із `ProgressBar`, обробкою помилок по рядках і можливістю скасування (`isCancelled()`).

## 3. Структура репозиторію
```text
student-projects-coursework/
├─ database.sql
├─ application-example.properties
├─ .gitignore
├─ README.md
├─ server/
│  ├─ pom.xml
│  └─ src/
│     ├─ main/java/ua/edu/nupp/kpp/server/
│     │  ├─ Student.java, Project.java, Submission.java, SubmissionState.java, Review.java
│     │  ├─ DBUtil.java
│     │  ├─ StudentDAO.java, ProjectDAO.java, SubmissionDAO.java, ReviewDAO.java
│     │  ├─ QualityCalculator.java
│     │  ├─ StudentService.java, ProjectService.java, SubmissionService.java, ReviewService.java
│     │  └─ StudentServlet.java, ProjectServlet.java, SubmissionServlet.java, ReviewServlet.java
│     ├─ main/webapp/WEB-INF/web.xml
│     └─ test/java/ua/edu/nupp/kpp/server/CourseworkTest.java
└─ client/
   ├─ pom.xml
   ├─ sample-data/projects.csv
   └─ src/main/java/ua/edu/nupp/kpp/client/
      ├─ Student.java, Project.java, Submission.java, SubmissionState.java, Review.java
      ├─ HttpProjectClient.java
      ├─ ImportProjectsTask.java
      └─ MainApp.java
```

## 4. Інструкція запуску

### Крок 1. Підготовка бази даних MySQL
Виконайте `database.sql` у MySQL (через phpMyAdmin або CLI):
```bash
mysql -u root -p < database.sql
```
За потреби задайте змінні середовища перед запуском Tomcat:
- `DB_URL` (за замовчуванням `jdbc:mysql://localhost:3306/student_projects?useUnicode=true&characterEncoding=UTF-8`)
- `DB_USER` (за замовчуванням `root`)
- `DB_PASSWORD` (за замовчуванням порожній рядок)

### Крок 2. Збірка та запуск тестів серверної частини
```bash
cd server
mvn clean test
mvn clean package
```
Скопіюйте `server/target/student-projects-server.war` у каталог `webapps/` **Apache Tomcat 9.x** та запустіть Tomcat (`startup.bat` або `startup.sh`).
Перевірте роботу сервера у браузері: `http://localhost:8080/student-projects-server/students`.

### Крок 3. Запуск JavaFX-клієнта
```bash
cd client
mvn javafx:run
```

## 5. Формат CSV-файлу (`client/sample-data/projects.csv`)
Роздільник — `;`, кодування — UTF-8:
```csv
student_id;code;title;technology;total_requirements;total_checks
1;PRJ-03;Модуль статичного аналізу коду;Java 21 / Maven;16;20
```

## 6. Сценарій перевірки основних функцій
1. Перевірити CRUD на вкладках «Студенти», «Проєкти», «Подання», «Рецензії».
2. На вкладці «Проєкти» запустити «Імпорт sample CSV» і переконатися у роботі `ProgressBar`.
3. На вкладці «Подання» створити подання, перевірити автоматичний розрахунок `F1`, `F2`, `F3`, `I` та послідовний перехід `DRAFT → SUBMITTED → IN_REVIEW → ACCEPTED` (спроба `DRAFT → ACCEPTED` має бути відхилена сервером).
