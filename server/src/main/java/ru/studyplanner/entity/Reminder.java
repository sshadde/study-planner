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
import java.time.Instant;

@Entity
@Table(name = "reminders")
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @Column(name = "remind_at", nullable = false)
    private Instant remindAt;

    @Column(length = 500)
    private String message;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    protected Reminder() {
    }

    public Reminder(Assignment assignment, Instant remindAt, String message) {
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment is required");
        }
        if (remindAt == null) {
            throw new IllegalArgumentException("Reminder time is required");
        }
        this.assignment = assignment;
        this.remindAt = remindAt;
        this.message = message == null || message.isBlank() ? null : message.trim();
    }

    public void disable() {
        enabled = false;
    }

    public void markSent(Instant sentAt) {
        this.sentAt = sentAt == null ? Instant.now() : sentAt;
    }

    public Long getId() {
        return id;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public Instant getRemindAt() {
        return remindAt;
    }

    public String getMessage() {
        return message;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
