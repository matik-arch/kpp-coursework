DROP DATABASE IF EXISTS student_projects;
CREATE DATABASE student_projects CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE student_projects;

CREATE TABLE student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_code VARCHAR(40) NOT NULL,
    full_name VARCHAR(160) NOT NULL,
    group_name VARCHAR(40) NOT NULL,
    email VARCHAR(120) NOT NULL
);

CREATE TABLE project (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    code VARCHAR(40) NOT NULL,
    title VARCHAR(200) NOT NULL,
    technology VARCHAR(80) NOT NULL,
    total_requirements INT NOT NULL,
    total_checks INT NOT NULL,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE
);

CREATE TABLE submission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    version_tag VARCHAR(40) NOT NULL,
    state VARCHAR(30) NOT NULL,
    completed_requirements INT NOT NULL,
    passed_checks INT NOT NULL,
    documentation_score DOUBLE NOT NULL,
    f1 DOUBLE NOT NULL,
    f2 DOUBLE NOT NULL,
    f3 DOUBLE NOT NULL,
    quality_index DOUBLE NOT NULL,
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);

CREATE TABLE review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    submission_id BIGINT NOT NULL,
    reviewer_name VARCHAR(160) NOT NULL,
    comment VARCHAR(500) NOT NULL,
    review_date VARCHAR(10) NOT NULL,
    FOREIGN KEY (submission_id) REFERENCES submission(id) ON DELETE CASCADE
);

INSERT INTO student(student_code, full_name, group_name, email) VALUES
('ST-201-05', 'Варламов Ілля Юрійович', '201-ТН', 'varlamov@nupp.edu.ua'),
('ST-201-01', 'Коваленко Андрій Олегович', '201-ТН', 'kovalenko@nupp.edu.ua');

INSERT INTO project(student_id, code, title, technology, total_requirements, total_checks) VALUES
(1, 'PRJ-01', 'Система подання та перевірки проєктів', 'Java 21 / JavaFX', 20, 25),
(2, 'PRJ-02', 'Трекер лабораторних робіт', 'Java 21 / Servlets', 10, 20);

INSERT INTO submission(project_id, version_tag, state, completed_requirements, passed_checks, documentation_score, f1, f2, f3, quality_index) VALUES
(1, 'v1.0', 'DRAFT', 17, 22, 76.0, 85.0, 88.0, 76.0, 83.80),
(2, 'v1.0', 'SUBMITTED', 6, 14, 80.0, 60.0, 70.0, 80.0, 68.50);

INSERT INTO review(submission_id, reviewer_name, comment, review_date) VALUES
(1, 'Заїка С.О.', 'Архітектура відповідає вимогам, перевірити обробку некоректних рядків CSV', '2026-11-10'),
(2, 'Заїка С.О.', 'Додати модульні тести для перевірки переходів станів подання', '2026-11-12');
