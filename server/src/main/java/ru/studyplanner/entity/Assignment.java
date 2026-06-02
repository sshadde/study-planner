package ru.studyplanner.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "due_at", nullable = false)
    private Instant dueAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssignmentPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssignmentStatus status = AssignmentStatus.NEW;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "completed_at")
    private Instant completedAt;

    protected Assignment() {
    }

    public Assignment(
            User user,
            Course course,
            String title,
            String description,
            Instant dueAt,
            AssignmentPriority priority,
            AssignmentStatus status
    ) {
        this.user = user;
        this.course = course;
        this.title = requireText(title, "Assignment title is required");
        this.description = blankToNull(description);
        this.dueAt = requireDueAt(dueAt);
        this.priority = priority == null ? AssignmentPriority.MEDIUM : priority;
        changeStatus(status == null ? AssignmentStatus.NEW : status);
    }

    public void update(
            Course course,
            String title,
            String description,
            Instant dueAt,
            AssignmentPriority priority
    ) {
        this.course = course;
        rename(title);
        this.description = blankToNull(description);
        this.dueAt = requireDueAt(dueAt);
        this.priority = priority == null ? AssignmentPriority.MEDIUM : priority;
        touch();
    }

    public void changeStatus(AssignmentStatus nextStatus) {
        if (nextStatus == null) {
            throw new IllegalArgumentException("Assignment status is required");
        }
        status = nextStatus;
        if (nextStatus == AssignmentStatus.DONE) {
            completedAt = completedAt == null ? Instant.now() : completedAt;
        } else {
            completedAt = null;
        }
        touch();
    }

    public void markDone(Instant completedAt) {
        status = AssignmentStatus.DONE;
        this.completedAt = completedAt == null ? Instant.now() : completedAt;
        touch();
    }

    public boolean isOverdue(Instant now) {
        Instant reference = now == null ? Instant.now() : now;
        return dueAt.isBefore(reference) && status != AssignmentStatus.DONE && status != AssignmentStatus.ARCHIVED;
    }

    public void rename(String title) {
        this.title = requireText(title, "Assignment title is required");
        touch();
    }

    public void reschedule(Instant dueAt) {
        this.dueAt = requireDueAt(dueAt);
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

    private static Instant requireDueAt(Instant dueAt) {
        if (dueAt == null) {
            throw new IllegalArgumentException("Due date is required");
        }
        return dueAt;
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

    public Course getCourse() {
        return course;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Instant getDueAt() {
        return dueAt;
    }

    public AssignmentPriority getPriority() {
        return priority;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}
