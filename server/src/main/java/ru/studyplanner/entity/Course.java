package ru.studyplanner.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(
        name = "courses",
        uniqueConstraints = @UniqueConstraint(name = "uk_courses_user_title", columnNames = {"user_id", "title"})
)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(name = "teacher_name", length = 160)
    private String teacherName;

    private Short semester;

    @Column(length = 16)
    private String color;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected Course() {
    }

    public Course(User user, String title, String teacherName, Short semester, String color) {
        this.user = user;
        this.title = requireText(title, "Course title is required");
        this.teacherName = blankToNull(teacherName);
        this.semester = semester;
        this.color = blankToNull(color);
    }

    public void update(String title, String teacherName, Short semester, String color) {
        rename(title);
        this.teacherName = blankToNull(teacherName);
        this.semester = semester;
        this.color = blankToNull(color);
        touch();
    }

    public void rename(String title) {
        this.title = requireText(title, "Course title is required");
        touch();
    }

    private void touch() {
        updatedAt = Instant.now();
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public Short getSemester() {
        return semester;
    }

    public String getColor() {
        return color;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
